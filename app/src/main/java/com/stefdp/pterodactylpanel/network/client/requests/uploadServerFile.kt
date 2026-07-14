package com.stefdp.pterodactylpanel.network.client.requests

import android.content.Context
import com.google.gson.Gson
import com.stefdp.pterodactylpanel.Logger
import com.stefdp.pterodactylpanel.R
import com.stefdp.pterodactylpanel.network.ApiErrorResponse
import com.stefdp.pterodactylpanel.network.PterodactylApiClient
import com.stefdp.pterodactylpanel.network.client.models.SignedUrl
import com.stefdp.pterodactylpanel.transferservice.ProgressTracker
import com.stefdp.pterodactylpanel.transferservice.TransferServiceConnection
import com.stefdp.pterodactylpanel.utils.SecureStorage
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source
import java.io.File
import java.io.IOException
import java.util.concurrent.CancellationException

private const val TAG = "ClientApi[uploadServerFile]"

suspend fun uploadServerFile(
    context: Context,
    serverId: String,
    filePath: String,
    directory: String? = null,
    fileMimeType: String? = null,
    notificationTitle: String = "Uploading file",
    notificationContent: String = "Upload in progress",
    onProgress: (totalBytes: Long, bytesTransferred: Long, speedBytesPerSecond: Double) -> Unit = { _, _, _ -> },
): Result<Unit> {
    val secureStore = SecureStorage.getInstance(context)

    val serverUrl = secureStore.get(SecureStorage.STORAGE_SERVER_URL_KEY)
    val token = secureStore.get(SecureStorage.STORAGE_CLIENT_TOKEN_KEY)

    Logger.debug(TAG, "Server URL: $serverUrl, token: $token")

    if (token.isNullOrEmpty()) {
        return Result.failure(
            Exception("Missing client token")
        )
    }

    if (serverUrl.isNullOrEmpty()) {
        return Result.failure(
            Exception("Missing server URL")
        )
    }

    val service = TransferServiceConnection.getService(context)
    val transferId = service.registerTransfer(
        notificationTitle,
        notificationContent,
        onProgress
    )

    return try {
        val uploadUrlResponse = PterodactylApiClient.getClientApiService(serverUrl).getUploadServerFileUrl(
            authorization = "Bearer $token",
            serverId = serverId,
        )

        if (!uploadUrlResponse.isSuccessful) {
            service.failTransfer(transferId, "HTTP ${uploadUrlResponse.code()}")

            return Result.failure(Exception("Failed to get upload URL: HTTP ${uploadUrlResponse.code()}"))
        }

        val uploadUrlBody = uploadUrlResponse.body()

        if (uploadUrlBody !is SignedUrl) {
            service.failTransfer(transferId, "Invalid response for upload URL")

            return Result.failure(Exception("Failed to get upload URL: Invalid response"))
        }

        val file = File(filePath)
        val totalBytes = file.length()
        val tracker = ProgressTracker()

        val mimeType = fileMimeType
            ?: java.net.URLConnection.guessContentTypeFromName(file.name)
            ?: "application/octet-stream"

        val requestBody = object : RequestBody() {
            override fun contentType() = mimeType.toMediaTypeOrNull()
            override fun contentLength() = totalBytes

            override fun writeTo(sink: BufferedSink) {
                try {
                    var bytesWritten = 0L
                    var lastNotifyTime = 0L

                    file.inputStream().source().use { source ->
                        val buffer = okio.Buffer()
                        var read: Long

                        while (source.read(buffer, 8192).also { read = it } != -1L) {
                            if (service.isTransferCancelled(transferId)) {
                                throw CancellationException("Upload cancelled")
                            }

                            sink.write(buffer, read)
                            bytesWritten += read

                            val now = System.currentTimeMillis()

                            if (now - lastNotifyTime >= 250) {
                                val speed = tracker.update(bytesWritten)
                                service.updateProgress(
                                    transferId,
                                    totalBytes,
                                    bytesWritten,
                                    speed
                                )

                                onProgress(
                                    totalBytes,
                                    bytesWritten,
                                    speed
                                )

                                lastNotifyTime = now
                            }
                        }
                    }

                    val speed = tracker.update(bytesWritten)

                    service.updateProgress(
                        transferId, totalBytes,
                        bytesWritten,
                        speed
                    )

                    onProgress(
                        totalBytes,
                        bytesWritten,
                        speed
                    )

                } catch(e: CancellationException) {
                    throw IOException("Upload cancelled", e)
                }
            }
        }

        val part = MultipartBody.Part.createFormData("file", file.name, requestBody)

        val response = PterodactylApiClient.getClientApiService(serverUrl).uploadServerFile(
            uploadUrl = uploadUrlBody.attributes.url,
            file = part,
            directory = directory
        )

        if (!response.isSuccessful) {
            val statusCode = response.code()

            if (statusCode == 401) {
                return Result.failure(
                    Exception("Invalid Token")
                )
            }

            val errorBody = response.errorBody()?.string()
            val json = Gson().fromJson(errorBody, ApiErrorResponse::class.java)

            service.failTransfer(transferId, "HTTP ${response.code()}")

            if (json.errors.isNotEmpty()) {
                val errorMessages = json.errors.joinToString(separator = "; ") { it.detail }
                val errorCount = json.errors.size

                val errorMessage = if (errorCount > 1) {
                    "$errorCount errors: $errorMessages"
                } else {
                    "Error: $errorMessages"
                }

                Logger.error(TAG, errorMessage)

                return Result.failure(
                    Exception(errorMessage)
                )
            }

            return Result.failure(
                Exception("Something went wrong...")
            )
        }

        return Result.success(Unit)
    } catch (e: CancellationException) {
        Result.failure(Exception("Upload cancelled"))
    } catch (e: Exception) {
        service.failTransfer(transferId, e.message ?: "Unknown error")

        Result.failure(e)
    }
}
package com.stefdp.pterodactylpanel.network.client.requests

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import com.stefdp.pterodactylpanel.Logger
import com.stefdp.pterodactylpanel.network.models.ApiErrorResponse
import com.stefdp.pterodactylpanel.network.PterodactylApiClient
import com.stefdp.pterodactylpanel.network.client.models.SignedUrl
import com.stefdp.pterodactylpanel.transferservice.CountingRequestBody
import com.stefdp.pterodactylpanel.transferservice.ProgressTracker
import com.stefdp.pterodactylpanel.transferservice.TransferServiceConnection
import com.stefdp.pterodactylpanel.utils.SecureStorage
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source
import java.io.IOException
import java.util.concurrent.CancellationException

private const val TAG = "ClientApi[uploadServerFiles]"

data class UploadFile(
    val mimeType: String,
    val name: String,
    val uri: Uri
)

suspend fun uploadServerFiles(
    context: Context,
    serverId: String,
    files: List<UploadFile>,
    directory: String? = null,
    notificationTitle: String = "Uploading files",
    notificationContent: String = "Upload in progress",
    onProgress: (totalBytes: Long, bytesTransferred: Long, speedBytesPerSecond: Double) -> Unit = { _, _, _ -> },
): Result<Unit> {
    val secureStore = SecureStorage.getInstance(context)

    val serverUrl = secureStore.get(SecureStorage.STORAGE_SERVER_URL_KEY)
    val token = secureStore.get(SecureStorage.STORAGE_CLIENT_TOKEN_KEY)

    Logger.debug(TAG, "Server URL: $serverUrl, token: $token")

    if (token.isNullOrEmpty() || serverUrl.isNullOrEmpty()) {
        return Result.failure(Exception("Missing client token or server URL"))
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

        if (!uploadUrlResponse.isSuccessful || uploadUrlResponse.body() !is SignedUrl) {
            val reason = "Failed to get upload URL: HTTP ${uploadUrlResponse.code()}"
            service.failTransfer(transferId, reason)
            return Result.failure(Exception(reason))
        }

        val uploadUrlBody = uploadUrlResponse.body() as SignedUrl

        val multipartBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)

        for (serverFile in files) {
            val contentResolver = context.contentResolver

            val assetFileDescriptor = contentResolver.openAssetFileDescriptor(serverFile.uri, "r")
            val fileLength = assetFileDescriptor?.use { it.length } ?: -1L

            val mimeType = serverFile.mimeType.toMediaTypeOrNull() ?: "application/octet-stream".toMediaTypeOrNull()

            val fileRequestBody = object : RequestBody() {
                override fun contentType() = mimeType
                override fun contentLength() = fileLength

                override fun writeTo(sink: BufferedSink) {
                    val inputStream = contentResolver.openInputStream(serverFile.uri)
                        ?: throw IOException("Could not open input stream for URI: ${serverFile.uri}")

                    inputStream.source().use { source ->
                        sink.writeAll(source)
                    }
                }
            }

            multipartBuilder.addFormDataPart("files", serverFile.name, fileRequestBody)
        }

        val baseMultipartBody = multipartBuilder.build()

        val tracker = ProgressTracker()
        var lastNotifyTime = 0L

        val countingRequestBody = CountingRequestBody(baseMultipartBody) { bytesWritten, totalBytes ->
            if (service.isTransferCancelled(transferId)) {
                throw CancellationException("Upload cancelled")
            }

            val now = System.currentTimeMillis()
            if (now - lastNotifyTime >= 250 || bytesWritten == totalBytes) {
                val speed = tracker.update(bytesWritten)

                service.updateProgress(transferId, totalBytes, bytesWritten, speed)
                onProgress(totalBytes, bytesWritten, speed)

                lastNotifyTime = now
            }
        }

        val response = PterodactylApiClient.getClientApiService(serverUrl).uploadServerFiles(
            uploadUrl = uploadUrlBody.attributes.url,
            body = countingRequestBody,
            directory = directory
        )

        if (!response.isSuccessful) {
            val statusCode = response.code()
            if (statusCode == 401) return Result.failure(Exception("Invalid Token"))

            val errorBody = response.errorBody()?.string()
            val json = Gson().fromJson(errorBody, ApiErrorResponse::class.java)

            service.failTransfer(transferId, "HTTP ${response.code()}")

            if (json?.errors?.isNotEmpty() == true) {
                val errorMessages = json.errors.joinToString(separator = "; ") { it.detail }
                Logger.error(TAG, errorMessages)
                return Result.failure(Exception(errorMessages))
            }

            return Result.failure(Exception("Something went wrong..."))
        }

        Result.success(Unit)
    } catch (e: CancellationException) {
        Result.failure(Exception("Upload cancelled"))
    } catch (e: Exception) {
        service.failTransfer(transferId, e.message ?: "Unknown error")
        Result.failure(e)
    }
}
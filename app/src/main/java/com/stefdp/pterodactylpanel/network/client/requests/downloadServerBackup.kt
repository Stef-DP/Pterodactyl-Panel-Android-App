package com.stefdp.pterodactylpanel.network.client.requests

import android.content.Context
import com.stefdp.pterodactylpanel.network.PterodactylApiClient
import com.stefdp.pterodactylpanel.network.client.models.SignedUrl
import com.stefdp.pterodactylpanel.transferservice.TransferServiceConnection
import com.stefdp.pterodactylpanel.transferservice.util.copyStreamWithProgress
import com.stefdp.pterodactylpanel.utils.SecureStorage
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.CancellationException

suspend fun downloadServerBackup(
    context: Context,
    serverId: String,
    backupId: String,
    destinationPath: String,
    notificationTitle: String = "Downloading backup",
    notificationContent: String = "Download in progress",
    onProgress: (totalBytes: Long, bytesTransferred: Long, speedBytesPerSecond: Double) -> Unit = { _, _, _ -> },
): Result<String> {
    val secureStore = SecureStorage.getInstance(context)

    val serverUrl = secureStore.get(SecureStorage.STORAGE_SERVER_URL_KEY)
    val token = secureStore.get(SecureStorage.STORAGE_CLIENT_TOKEN_KEY)

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
    val transferId = service.registerTransfer(notificationTitle, notificationContent, onProgress)

    return try {
        val downloadUrlResponse = PterodactylApiClient.getClientApiService(serverUrl).getServerBackupDownloadUrl(
            authorization = "Bearer $token",
            serverId = serverId,
            backupId = backupId
        )

        if (!downloadUrlResponse.isSuccessful) {
            service.failTransfer(transferId, "HTTP ${downloadUrlResponse.code()}")

            return Result.failure(Exception("Failed to get download URL: HTTP ${downloadUrlResponse.code()}"))
        }

        val downloadUrlBody = downloadUrlResponse.body()

        if (downloadUrlBody !is SignedUrl) {
            service.failTransfer(transferId, "Invalid response for download URL")

            return Result.failure(Exception("Failed to get download URL: Invalid response"))
        }

        val response = PterodactylApiClient.getClientApiService(serverUrl).downloadServerBackup(
            fileUrl = downloadUrlBody.attributes.url
        )

        if (!response.isSuccessful) {
            service.failTransfer(transferId, "HTTP ${response.code()}")

            return Result.failure(Exception("Download failed: HTTP ${response.code()}"))
        }

        val body = response.body() ?: run {
            service.failTransfer(transferId, "Empty response")

            return Result.failure(Exception("Download failed: empty response body"))
        }

        val totalBytes = body.contentLength()
        val file = File(destinationPath)

        file.parentFile?.mkdirs()

        body.byteStream().use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                copyStreamWithProgress(
                    inputStream = inputStream,
                    outputStream = outputStream,
                    totalBytes = totalBytes,
                    transferId = transferId,
                    service = service,
                    onProgress = onProgress,
                )
            }
        }

        service.completeTransfer(transferId, destinationPath)
        Result.success(destinationPath)
    } catch (e: CancellationException) {
        File(destinationPath).delete()

        Result.failure(Exception("Download cancelled"))
    } catch (e: Exception) {
        service.failTransfer(transferId, e.message ?: "Unknown error")
        File(destinationPath).delete()

        Result.failure(e)
    }
}
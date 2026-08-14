package com.stefdp.pterodactylpanel.network.application.requests

import android.content.Context
import com.google.gson.Gson
import com.stefdp.pterodactylpanel.Logger
import com.stefdp.pterodactylpanel.network.ApiErrorResponse
import com.stefdp.pterodactylpanel.network.PterodactylApiClient
import com.stefdp.pterodactylpanel.network.application.models.ApplicationNode
import com.stefdp.pterodactylpanel.network.application.models.requests.UpdateNodeBody
import com.stefdp.pterodactylpanel.utils.SecureStorage

private const val TAG = "ApplicationApi[updateNode]"

suspend fun updateNode(
    context: Context,
    nodeId: Long,
    daemonListen: Int,
    daemonSftp: Int,
    disk: Long,
    diskOverallocate: Long,
    fqdn: String,
    locationId: Long,
    memory: Long,
    memoryOverallocate: Long,
    name: String,
    scheme: ApplicationNode.Attributes.Scheme,
    behindProxy: Boolean? = null,
    daemonBase: String? = null,
    description: String? = null,
    maintenanceMode: Boolean? = null,
    public: Boolean? = null,
    uploadSize: Long? = null,
): Result<ApplicationNode> {
    try {
        val secureStore = SecureStorage.getInstance(context)

        val serverUrl = secureStore.get(SecureStorage.STORAGE_SERVER_URL_KEY)
        val token = secureStore.get(SecureStorage.STORAGE_APPLICATION_TOKEN_KEY)

        if (token.isNullOrEmpty()) {
            return Result.failure(
                Exception("Missing application token")
            )
        }

        if (serverUrl.isNullOrEmpty()) {
            return Result.failure(
                Exception("Missing server URL")
            )
        }

        val requestBody = UpdateNodeBody(
            daemonListen = daemonListen,
            daemonSftp = daemonSftp,
            disk = disk,
            diskOverallocate = diskOverallocate,
            fqdn = fqdn,
            locationId = locationId,
            memory = memory,
            memoryOverallocate = memoryOverallocate,
            name = name,
            scheme = scheme,
            behindProxy = behindProxy,
            daemonBase = daemonBase,
            description = description,
            maintenanceMode = maintenanceMode,
            public = public,
            uploadSize = uploadSize
        )

        val response = PterodactylApiClient.getApplicationApiService(serverUrl).updateNode(
            authorization = "Bearer $token",
            nodeId = nodeId,
            data = requestBody
        )

        val body = response.body()

        if (!response.isSuccessful) {
            val statusCode = response.code()

            Logger.error(TAG, "Request failed with code: $statusCode and message: ${response.message()}")

            if (statusCode == 401) {
                return Result.failure(
                    Exception("Invalid Token")
                )
            }

            val errorBody = response.errorBody()?.string()

            val json = Gson().fromJson(errorBody, ApiErrorResponse::class.java)

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

        if (body is ApplicationNode) {
            return Result.success(body)
        }

        return Result.failure(
            Exception("Something went wrong...")
        )
    } catch(e: Exception) {
        Logger.error(TAG, "Exception occurred: ${e.message}", e)

        return Result.failure(
            Exception("Something went wrong...")
        )
    }
}
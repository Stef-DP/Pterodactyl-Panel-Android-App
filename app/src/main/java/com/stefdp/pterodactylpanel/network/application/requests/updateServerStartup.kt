package com.stefdp.pterodactylpanel.network.application.requests

import android.content.Context
import com.google.gson.Gson
import com.stefdp.pterodactylpanel.network.models.ApiErrorResponse
import com.stefdp.pterodactylpanel.network.PterodactylApiClient
import com.stefdp.pterodactylpanel.network.application.models.ApplicationServer
import com.stefdp.pterodactylpanel.network.application.models.requests.UpdateServerStartupBody
import com.stefdp.pterodactylpanel.utils.SecureStorage
import com.stefdp.pterodactylpanel.Logger

private const val TAG = "ApplicationApi[updateServerStartup]"

suspend fun updateServerStartup(
    context: Context,
    serverId: Long,
    startup: String? = null,
    environment: Map<String, Any?>? = null,
    egg: Long? = null,
    image: String? = null,
    skipEggInstallScript: Boolean? = null,
): Result<ApplicationServer> {
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

        val requestBody = UpdateServerStartupBody(
            startup = startup,
            environment = environment,
            egg = egg,
            image = image,
            skipEggInstallScript = skipEggInstallScript
        )

        val response = PterodactylApiClient.getApplicationApiService(serverUrl).updateServerStartup(
            authorization = "Bearer $token",
            serverId = serverId,
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

        if (body is ApplicationServer) {
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
package com.stefdp.pterodactylpanel.network.client.requests

import android.content.Context
import com.google.gson.Gson
import com.stefdp.pterodactylpanel.network.models.ApiErrorResponse
import com.stefdp.pterodactylpanel.network.PterodactylApiClient
import com.stefdp.pterodactylpanel.network.client.models.responses.GetAccount2FAQrCodeResponse
import com.stefdp.pterodactylpanel.utils.SecureStorage
import com.stefdp.pterodactylpanel.Logger

private const val TAG = "ClientApi[getAccount2FAQrCode]"

suspend fun getAccount2FAQrCode(
    context: Context,
): Result<GetAccount2FAQrCodeResult> {
    try {
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

        val response = PterodactylApiClient.getClientApiService(serverUrl).getAccount2FAQrCode(
            authorization = "Bearer $token",
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
                val alreadyEnabledError = json.errors.find {
                    it.status == "400" || it.detail == "Two-factor authentication is already enabled on this account."
                }

                if (alreadyEnabledError != null) {
                    return Result.success(
                        GetAccount2FAQrCodeResult.AlreadyEnabled
                    )
                }

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

        if (body is GetAccount2FAQrCodeResponse) {
            return Result.success(
                GetAccount2FAQrCodeResult.Success(body)
            )
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

sealed interface GetAccount2FAQrCodeResult {
    data class Success(val response: GetAccount2FAQrCodeResponse) : GetAccount2FAQrCodeResult
    data object AlreadyEnabled : GetAccount2FAQrCodeResult
}
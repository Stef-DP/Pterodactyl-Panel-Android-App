package com.stefdp.pterodactylpanel.network.application.requests

import android.content.Context
import com.google.gson.Gson
import com.stefdp.pterodactylpanel.network.ApiErrorResponse
import com.stefdp.pterodactylpanel.network.PterodactylApiClient
import com.stefdp.pterodactylpanel.network.application.models.ApplicationUser
import com.stefdp.pterodactylpanel.network.application.models.requests.CreateUserBody
import com.stefdp.pterodactylpanel.utils.SecureStorage
import com.stefdp.zipline.Logger

private const val TAG = "ApplicationApi[createUser]"

suspend fun createUser(
    context: Context,
    email: String,
    username: String,
    firstName: String,
    lastName: String,
    password: String? = null,
    language: String? = null,
    rootAdmin: Boolean? = null,
    externalId: String? = null,
): Result<ApplicationUser> {
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

        val requestBody = CreateUserBody(
            email = email,
            username = username,
            firstName = firstName,
            lastName = lastName,
            password = password,
            language = language,
            rootAdmin = rootAdmin,
            externalId = externalId
        )

        val response = PterodactylApiClient.getApplicationApiService(serverUrl).createUser(
            authorization = "Bearer $token",
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

        if (body is ApplicationUser) {
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
package com.stefdp.pterodactylpanel.network.client.requests

import android.content.Context
import com.google.gson.Gson
import com.stefdp.pterodactylpanel.network.ApiErrorResponse
import com.stefdp.pterodactylpanel.network.PterodactylApiClient
import com.stefdp.pterodactylpanel.network.client.models.ServerScheduleAction
import com.stefdp.pterodactylpanel.network.client.models.ServerScheduleTask
import com.stefdp.pterodactylpanel.network.client.models.requests.CreateServerScheduleTaskBody
import com.stefdp.pterodactylpanel.utils.SecureStorage
import com.stefdp.zipline.Logger

private const val TAG = "ClientApi[createServerScheduleTask]"

suspend fun createServerScheduleTask(
    context: Context,
    serverId: String,
    scheduleId: String,
    action: ServerScheduleAction,
    payload: String,
    timeOffset: Long,
    sequenceId: Long? = null,
    continueOnFailure: Boolean = false
): Result<ServerScheduleTask> {
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

        val requestBody = CreateServerScheduleTaskBody(
            action = action,
            payload = payload,
            timeOffset = timeOffset,
            sequenceId = sequenceId,
            continueOnFailure = continueOnFailure
        )

        val response = PterodactylApiClient.getClientApiService(serverUrl).createServerScheduleTask(
            authorization = "Bearer $token",
            serverId = serverId,
            scheduleId = scheduleId,
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

        if (body is ServerScheduleTask) {
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
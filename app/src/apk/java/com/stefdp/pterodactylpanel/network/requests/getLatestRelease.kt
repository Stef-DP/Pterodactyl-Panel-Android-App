package com.stefdp.pterodactylpanel.network.requests

import android.content.Context
import com.google.gson.Gson
import com.stefdp.pterodactylpanel.Logger
import com.stefdp.pterodactylpanel.network.UpdateClient
import com.stefdp.pterodactylpanel.network.models.ErrorResponse
import com.stefdp.pterodactylpanel.network.models.ForgejoRelease

private const val TAG = "Update[getLatestRelease]"

suspend fun getLatestRelease(
    context: Context,
    username: String,
    repo : String
): Result<ForgejoRelease> {
    try {
        val response = UpdateClient.getUpdateService().getLatestRelease(
            username = username,
            repo = repo
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
            val json = Gson().fromJson(errorBody, ErrorResponse::class.java)

            if (json.message.isNotEmpty()) {
                Logger.error(TAG, "Error message: ${json.message}")

                return Result.failure(
                    Exception(json.message)
                )
            }

            return Result.failure(
                Exception("Something went wrong...")
            )
        }

        if (body is ForgejoRelease) {
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
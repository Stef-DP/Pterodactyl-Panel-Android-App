package com.stefdp.pterodactylpanel.network.node.requests

import com.google.gson.Gson
import com.stefdp.pterodactylpanel.Logger
import com.stefdp.pterodactylpanel.network.models.ApiErrorResponse
import com.stefdp.pterodactylpanel.network.PterodactylApiClient
import com.stefdp.pterodactylpanel.network.node.models.responses.GetNodeSystemV2Response

private const val TAG = "NodeApi[getNodeSystemV2]"

suspend fun getNodeSystemV2(
    nodeUrl: String,
    token: String,
): Result<GetNodeSystemV2Response> {
    try {
        val response = PterodactylApiClient.getNodeApiService(nodeUrl).getNodeSystemV2(
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

        if (body is GetNodeSystemV2Response) {
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
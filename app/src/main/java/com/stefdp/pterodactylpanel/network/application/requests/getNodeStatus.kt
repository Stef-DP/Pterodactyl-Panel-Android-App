package com.stefdp.pterodactylpanel.network.application.requests

import android.content.Context
import com.google.gson.Gson
import com.stefdp.pterodactylpanel.network.ApiErrorResponse
import com.stefdp.pterodactylpanel.network.PterodactylApiClient
import com.stefdp.pterodactylpanel.network.application.models.ApplicationNode
import com.stefdp.pterodactylpanel.utils.SecureStorage
import com.stefdp.pterodactylpanel.Logger

private const val TAG = "ApplicationApi[getNodeStatus]"

suspend fun getNodeStatus(
    context: Context,
    url: String,
): Result<Unit> {
    try {
        val secureStore = SecureStorage.getInstance(context)

        val serverUrl = secureStore.get(SecureStorage.STORAGE_SERVER_URL_KEY)

        if (serverUrl.isNullOrEmpty()) {
            return Result.failure(
                Exception("Missing server URL")
            )
        }

        val response = PterodactylApiClient.getApplicationApiService(serverUrl).getNodeStatus(
            url = url
        )

        val status = response.code()

        if (status == 204) {
            return Result.success(Unit)
        }

        return Result.failure(
            Exception("")
        )
    } catch(e: Exception) {
        Logger.error(TAG, "Exception occurred: ${e.message}", e)

        return Result.failure(
            Exception("")
        )
    }
}
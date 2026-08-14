package com.stefdp.pterodactylpanel.network.node.requests

import com.stefdp.pterodactylpanel.Logger
import com.stefdp.pterodactylpanel.network.PterodactylApiClient

private const val TAG = "NodeApi[getNodeStatus]"

suspend fun getNodeStatus(
    nodeUrl: String,
): Result<Unit> {
    try {
        val response = PterodactylApiClient.getNodeApiService(nodeUrl).getNodeStatus()

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
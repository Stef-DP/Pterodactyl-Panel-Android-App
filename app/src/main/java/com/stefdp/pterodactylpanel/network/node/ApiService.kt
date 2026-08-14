package com.stefdp.pterodactylpanel.network.node

import com.stefdp.pterodactylpanel.network.node.models.responses.GetNodeSystemV1Response
import com.stefdp.pterodactylpanel.network.node.models.responses.GetNodeSystemV2Response
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.OPTIONS
import retrofit2.http.Query
import retrofit2.http.Url

interface PterodactylNodeApiService {
    @OPTIONS("system")
    suspend fun getNodeStatus(): Response<ResponseBody>

    @GET("system")
    suspend fun getNodeSystemV1(
        @Header("Authorization") authorization: String,
    ): Response<GetNodeSystemV1Response>

    @GET("system")
    suspend fun getNodeSystemV2(
        @Header("Authorization") authorization: String,
        @Query("v") version: Int = 2
    ): Response<GetNodeSystemV2Response>
}
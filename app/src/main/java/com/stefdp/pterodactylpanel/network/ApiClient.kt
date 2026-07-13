package com.stefdp.pterodactylpanel.network

import com.google.gson.GsonBuilder
import com.stefdp.pterodactylpanel.network.application.PterodactylApplicationApiService
import com.stefdp.pterodactylpanel.network.client.PterodactylClientApiService
import com.stefdp.pterodactylpanel.DEBUG_NETWORK
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object PterodactylApiClient {
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    fun getClientApiService(baseUrl: String, includeNull: Boolean = false): PterodactylClientApiService {
        val formattedUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"

        val fullUrl = formattedUrl + "api/client/"

        val retrofit = Retrofit.Builder()
            .baseUrl(fullUrl)

        if (DEBUG_NETWORK) {
            retrofit.client(okHttpClient)
        }

        retrofit.addConverterFactory(ScalarsConverterFactory.create())

        if (includeNull) {
            val gson = GsonBuilder()
                .serializeNulls()
                .create()

            retrofit.addConverterFactory(GsonConverterFactory.create(gson))
        } else {
            retrofit.addConverterFactory(GsonConverterFactory.create())
        }

        return retrofit
            .build()
            .create(PterodactylClientApiService::class.java)
    }

    fun getApplicationApiService(baseUrl: String, includeNull: Boolean = false): PterodactylApplicationApiService {
        val formattedUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"

        val fullUrl = formattedUrl + "api/application/"

        val retrofit = Retrofit.Builder()
            .baseUrl(fullUrl)

        if (DEBUG_NETWORK) {
            retrofit.client(okHttpClient)
        }

        retrofit.addConverterFactory(ScalarsConverterFactory.create())

        if (includeNull) {
            val gson = GsonBuilder()
                .serializeNulls()
                .create()

            retrofit.addConverterFactory(GsonConverterFactory.create(gson))
        } else {
            retrofit.addConverterFactory(GsonConverterFactory.create())
        }

        return retrofit
            .build()
            .create(PterodactylApplicationApiService::class.java)
    }
}
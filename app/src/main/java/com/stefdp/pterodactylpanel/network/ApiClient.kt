package com.stefdp.pterodactylpanel.network

import com.google.gson.GsonBuilder
import com.stefdp.zipline.DEBUG_NETWORK
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

    fun getPterodactylApiService(baseUrl: String, includeNull: Boolean): PterodactylApiService {
        val formattedUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"

        val fullUrl = formattedUrl + "api/"

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
            .create(PterodactylApiService::class.java)
    }
}
package com.stefdp.pterodactylpanel.network

import com.stefdp.pterodactylpanel.network.models.RecoveryCodes
import com.stefdp.pterodactylpanel.network.models.User
import com.stefdp.pterodactylpanel.network.models.requests.CreateClientApiKeyBody
import com.stefdp.pterodactylpanel.network.models.requests.DisableClient2FABody
import com.stefdp.pterodactylpanel.network.models.requests.EnableClient2FABody
import com.stefdp.pterodactylpanel.network.models.requests.UpdateClientEmailBody
import com.stefdp.pterodactylpanel.network.models.requests.UpdateClientPasswordBody
import com.stefdp.pterodactylpanel.network.models.responses.GetClient2FAQrCodeResponse
import com.stefdp.pterodactylpanel.network.models.responses.GetClientAvailablePermissionsResponse
import com.stefdp.pterodactylpanel.network.models.responses.ListClientApiKeysResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

// TODO: reminder for myself, api auth is "Bearer <API_KEY>"
// docs: https://old-api.redbanana.dev/

interface PterodactylApiService {
    @GET("client")
    fun getClientServers(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Query("per_page") perPage: Int? = null,
        @Query("page") page: Int? = null
    )

    @GET("client/permissions")
    fun getClientAvailablePermissions(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
    ): Response<GetClientAvailablePermissionsResponse>

    @GET("client/account")
    fun getClient(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json"
    ): Response<User>

    @GET("client/account/two-factor")
    fun getClient2FAQrCode(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json"
    ): Response<GetClient2FAQrCodeResponse>

    @POST("client/account/two-factor")
    fun enableClient2FA(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Body data: EnableClient2FABody
    ): Response<RecoveryCodes>

    @POST("client/account/two-factor")
    fun disableClient2FA(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Body data: DisableClient2FABody
    )

    @PUT("client/account/email")
    fun updateClientEmail(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Body data: UpdateClientEmailBody
    )

    @PUT("client/account/password")
    fun updateClientPassword(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Body data: UpdateClientPasswordBody
    )

    @GET("client/account/api-keys")
    fun listClientApiKeys(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
    ): Response<ListClientApiKeysResponse>

    @POST("client/account/api-keys")
    fun createClientApiKey(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Body data: CreateClientApiKeyBody
    )

    @DELETE("client/account/api-keys/{identifier}")
    fun deleteClientApiKey(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("identifier") identifier: String,
    )
}
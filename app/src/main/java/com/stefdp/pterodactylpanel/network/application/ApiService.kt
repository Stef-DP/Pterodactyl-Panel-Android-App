package com.stefdp.pterodactylpanel.network.application

import com.stefdp.pterodactylpanel.network.application.models.requests.GetApplicationUsersQuerySort
import com.stefdp.pterodactylpanel.network.application.models.requests.ListApplicationUsersQueryInclude
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface PterodactylApplicationApiService {
    @GET("application/users")
    fun listApplicationUsers(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Query("include") include: ListApplicationUsersQueryInclude,
        @Query("page") page: Int? = null,
        @Query("per_page") perPage: Int? = null,
        @Query("filter[email]") filterEmail: String? = null,
        @Query("filter[uuid]") filterUuid: String? = null,
        @Query("filter[username]") filterUsername: String? = null,
        @Query("filter[external_id]") filterExternalId: String? = null,
        @Query("sort") sort: GetApplicationUsersQuerySort? = null,
    )
}
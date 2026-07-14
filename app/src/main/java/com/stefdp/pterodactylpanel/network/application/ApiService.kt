package com.stefdp.pterodactylpanel.network.application

import com.stefdp.pterodactylpanel.network.application.models.ApplicationEgg
import com.stefdp.pterodactylpanel.network.application.models.ApplicationLocation
import com.stefdp.pterodactylpanel.network.application.models.ApplicationNest
import com.stefdp.pterodactylpanel.network.application.models.ApplicationNode
import com.stefdp.pterodactylpanel.network.application.models.ApplicationServer
import com.stefdp.pterodactylpanel.network.application.models.ApplicationServerDatabase
import com.stefdp.pterodactylpanel.network.application.models.ApplicationUser
import com.stefdp.pterodactylpanel.network.application.models.requests.CreateLocationBody
import com.stefdp.pterodactylpanel.network.application.models.requests.CreateNodeAllocationBody
import com.stefdp.pterodactylpanel.network.application.models.requests.CreateNodeBody
import com.stefdp.pterodactylpanel.network.application.models.requests.CreateServerBody
import com.stefdp.pterodactylpanel.network.application.models.requests.CreateServerDatabaseBody
import com.stefdp.pterodactylpanel.network.application.models.requests.CreateUserBody
import com.stefdp.pterodactylpanel.network.application.models.requests.GetUsersQuerySort
import com.stefdp.pterodactylpanel.network.application.models.requests.ListLocationsQuerySort
import com.stefdp.pterodactylpanel.network.application.models.requests.ListNodesQuerySort
import com.stefdp.pterodactylpanel.network.application.models.requests.ListServersQuerySort
import com.stefdp.pterodactylpanel.network.application.models.requests.UpdateNodeBody
import com.stefdp.pterodactylpanel.network.application.models.requests.UpdateServerBuildBody
import com.stefdp.pterodactylpanel.network.application.models.requests.UpdateServerDetailsBody
import com.stefdp.pterodactylpanel.network.application.models.requests.UpdateServerStartupBody
import com.stefdp.pterodactylpanel.network.application.models.requests.UpdateUserBody
import com.stefdp.pterodactylpanel.network.application.models.responses.GetDeployableNodesResponse
import com.stefdp.pterodactylpanel.network.application.models.responses.GetNodeConfigurationResponse
import com.stefdp.pterodactylpanel.network.application.models.responses.ListLocationsResponse
import com.stefdp.pterodactylpanel.network.application.models.responses.ListNestEggsResponse
import com.stefdp.pterodactylpanel.network.application.models.responses.ListNestsResponse
import com.stefdp.pterodactylpanel.network.application.models.responses.ListNodeAllocationsResponse
import com.stefdp.pterodactylpanel.network.application.models.responses.ListNodesResponse
import com.stefdp.pterodactylpanel.network.application.models.responses.ListServerDatabasesResponse
import com.stefdp.pterodactylpanel.network.application.models.responses.ListServersResponse
import com.stefdp.pterodactylpanel.network.application.models.responses.ListUsersResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface PterodactylApplicationApiService {
    @GET("users")
    suspend fun listUsers(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Query("include") include: String? = null, // list of ListUsersQueryInclude separated by ","
        @Query("page") page: Int? = null,
        @Query("per_page") perPage: Int? = null,
        @Query("filter[email]") filterEmail: String? = null,
        @Query("filter[uuid]") filterUuid: String? = null,
        @Query("filter[username]") filterUsername: String? = null,
        @Query("filter[external_id]") filterExternalId: String? = null,
        @Query("sort") sort: GetUsersQuerySort? = null,
    ): Response<ListUsersResponse>

    @GET("users/{userId}")
    suspend fun getUser(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("userId") userId: Long,
        @Query("include") include: String? = null, // list of ListUsersQueryInclude separated by ","
    ): Response<ApplicationUser>

    @GET("users/external/{externalId}")
    suspend fun getUserByExternalId(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("externalId") externalId: String,
        @Query("include") include: String? = null, // list of ListUsersQueryInclude separated by ","
    ): Response<ApplicationUser>

    @POST("users")
    suspend fun createUser(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Body data: CreateUserBody
    ): Response<ApplicationUser>

    @PATCH("users/{userId}")
    suspend fun updateUser(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("userId") userId: Long,
        @Body data: UpdateUserBody
    ): Response<ApplicationUser>

    @DELETE("users/{userId}")
    suspend fun deleteUser(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("userId") userId: Long
    ): Response<Unit>

    @GET("nodes")
    suspend fun listNodes(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Query("page") page: Int? = null,
        @Query("per_page") perPage: Int? = null,
        @Query("filter[uuid]") filterUuid: String? = null,
        @Query("filter[name]") filterName: String? = null,
        @Query("filter[fqdn]") filterFQDN: String? = null,
        @Query("filter[daemon_token_id]") filterDaemonTokenId: String? = null,
        @Query("sort") sort: ListNodesQuerySort? = null,
        @Query("include") include: String? = null // list of ListNodesQueryInclude separated by ","
    ): Response<ListNodesResponse>

    @GET("nodes/{nodeId}")
    suspend fun getNode(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("nodeId") nodeId: Long,
        @Query("include") include: String? = null // list of ListNodesQueryInclude separated by ","
    ): Response<ApplicationNode>

    @GET("nodes/{nodeId}/configuration")
    suspend fun getNodeConfiguration(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("nodeId") nodeId: Long
    ): Response<GetNodeConfigurationResponse>

    @GET("nodes/deployable")
    suspend fun getDeployableNodes(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Query("memory") memory: Long,
        @Query("disk") disk: Long,
        @Query("location_ids[]") locationIds: List<Long>? = null,
        @Query("page") page: Int? = null,
        @Query("per_page") perPage: Int? = null,
    ): Response<GetDeployableNodesResponse>

    @POST("nodes")
    suspend fun createNode(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Body data: CreateNodeBody
    ): Response<ApplicationNode>

    @PATCH("nodes/{nodeId}")
    suspend fun updateNode(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("nodeId") nodeId: Long,
        @Body data: UpdateNodeBody
    ): Response<ApplicationNode>

    @DELETE("nodes/{nodeId}")
    suspend fun deleteNode(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("nodeId") nodeId: Long
    ): Response<Unit>

    @GET("nodes/{nodeId}/allocations")
    suspend fun listNodeAllocations(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("nodeId") nodeId: Long,
        @Query("include") include: String? = null, //  list of ListNodeAllocationsQueryInclude separated by ","
        @Query("filter[ip]") filterIp: String? = null,
        @Query("filter[port]") filterPort: Int? = null,
        @Query("filter[ip_alias") filterIpAlias: String? = null,
        @Query("filter[server_id]") filterServerId: Long? = null,
        @Query("page") page: Int? = null,
        @Query("per_page") perPage: Int? = null,
    ): Response<ListNodeAllocationsResponse>

    @POST("nodes/{nodeId}/allocations")
    suspend fun createNodeAllocation(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("nodeId") nodeId: Long,
        @Body data: CreateNodeAllocationBody
    ): Response<Unit>

    @DELETE("nodes/{nodeId}/allocations/{allocationId}")
    suspend fun deleteNodeAllocation(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("nodeId") nodeId: Long,
        @Path("allocationId") allocationId: Long
    ): Response<Unit>

    @GET("locations")
    suspend fun listLocations(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Query("filter[short]") filterShort: String? = null,
        @Query("filter[long]") filterLong: String? = null,
        @Query("sort") sort: ListLocationsQuerySort? = null,
        @Query("include") include: String? = null, // list of ListLocationsQueryInclude separated by ","
        @Query("page") page: Int? = null,
        @Query("per_page") perPage: Int? = null
    ): Response<ListLocationsResponse>

    @GET("locations/{locationId}")
    suspend fun getLocation(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("locationId") locationId: Long,
        @Query("include") include: String? = null, // list of ListLocationsQueryInclude separated by ","
    ): Response<ApplicationLocation>

    @POST("locations")
    suspend fun createLocation(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Body data: CreateLocationBody
    ): Response<ApplicationLocation>

    @PATCH("locations/{locationId}")
    suspend fun updateLocation(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("locationId") locationId: Long,
        @Body data: CreateLocationBody
    ): Response<ApplicationLocation>

    @DELETE("locations/{locationId}")
    suspend fun deleteLocation(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("locationId") locationId: Long
    ): Response<Unit>

    @GET("servers")
    suspend fun listServers(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Query("include") include: String? = null, // list of ListServersQueryInclude separated by ","
        @Query("filter[uuid]") filterUuid: String? = null,
        @Query("filter[uuidShort]") filterUuidShort: String? = null,
        @Query("filter[name]") filterName: String? = null,
        @Query("filter[description]") filterDescription: String? = null,
        @Query("filter[image]") filterImage: String? = null,
        @Query("filter[external_id]") filterExternalId: String? = null,
        @Query("sort") sort: ListServersQuerySort? = null,
        @Query("page") page: Int? = null,
        @Query("per_page") perPage: Int? = null
    ): Response<ListServersResponse>

    @GET("servers/{serverId}")
    suspend fun getServer(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: Long,
        @Query("include") include: String? = null, // list of ListServersQueryInclude separated by ","
    ): Response<ApplicationServer>

    @GET("servers/external/{serverId}")
    suspend fun getServerByExternalId(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("externalId") externalId: String,
        @Query("include") include: String? = null, // list of ListServersQueryInclude separated by ","
    ): Response<ApplicationServer>

    @POST("servers")
    suspend fun createServer(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Body data: CreateServerBody
    ): Response<ApplicationServer>

    @PATCH("servers/{serverId}/details")
    suspend fun updateServerDetails(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: Long,
        @Body data: UpdateServerDetailsBody
    ): Response<ApplicationServer>

    @PATCH("servers/{serverId}/build")
    suspend fun updateServerBuild(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: Long,
        @Body data: UpdateServerBuildBody
    ): Response<ApplicationServer>

    @PATCH("servers/{serverId}/startup")
    suspend fun updateServerStartup(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: Long,
        @Body data: UpdateServerStartupBody
    ): Response<ApplicationServer>

    @POST("servers/{serverId}/suspend")
    suspend fun suspendServer(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: Long,
    ): Response<Unit>

    @POST("servers/{serverId}/unsuspend")
    suspend fun unsuspendServer(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: Long,
    ): Response<Unit>

    @POST("servers/{serverId}/reinstall")
    suspend fun reinstallServer(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: Long,
    ): Response<Unit>

    @DELETE("servers/{serverId}")
    suspend fun deleteServer(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: Long,
    ): Response<Unit>

    @DELETE("servers/{serverId}/force")
    suspend fun forceDeleteServer(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: Long,
    ): Response<Unit>

    @GET("servers/{serverId}/databases")
    suspend fun listServerDatabases(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: Long,
        @Query("include") include: String? = null, // list of ListServerDatabasesQueryInclude separated by ","
    ): Response<ListServerDatabasesResponse>

    @GET("servers/{serverId}/databases/{databaseId}")
    suspend fun getServerDatabase(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: Long,
        @Path("databaseId") databaseId: Long,
        @Query("include") include: String? = null, // list of ListServerDatabasesQueryInclude separated by ","
    ): Response<ApplicationServerDatabase>

    @POST("servers/{serverId}/databases")
    suspend fun createServerDatabase(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: Long,
        @Body data: CreateServerDatabaseBody
    ): Response<ApplicationServerDatabase>

    @POST("servers/{serverId}/databases/{databaseId}/reset-password")
    suspend fun resetServerDatabasePassword(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: Long,
        @Path("databaseId") databaseId: Long,
    ): Response<Unit>

    @DELETE("servers/{serverId}/databases/{databaseId}")
    suspend fun deleteServerDatabase(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: Long,
        @Path("databaseId") databaseId: Long,
    ): Response<Unit>

    @GET("nests")
    suspend fun listNests(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Query("include") include: String? = null, // list of ListNestsQueryInclude separated by ","
        @Query("page") page: Int? = null,
        @Query("per_page") perPage: Int? = null
    ): Response<ListNestsResponse>

    @GET("nests/{nestId}")
    suspend fun getNest(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("nestId") nestId: Long,
        @Query("include") include: String? = null, // list of ListNestsQueryInclude separated by ","
    ): Response<ApplicationNest>

    @GET("nests/{nestId}/eggs")
    suspend fun listNestEggs(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("nestId") nestId: Long,
        @Query("include") include: String? = null, // list of ListNestEggsQueryInclude separated by ","
    ): Response<ListNestEggsResponse>

    @GET("nests/{nestId}/eggs/{eggId}")
    suspend fun getNestEgg(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("nestId") nestId: Long,
        @Path("eggId") eggId: Long,
        @Query("include") include: String? = null, // list of ListNestEggsQueryInclude separated by ","
    ): Response<ApplicationEgg>
}
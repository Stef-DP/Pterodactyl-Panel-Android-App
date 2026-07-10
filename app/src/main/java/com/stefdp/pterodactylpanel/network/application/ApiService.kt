package com.stefdp.pterodactylpanel.network.application

import com.stefdp.pterodactylpanel.network.application.models.ApplicationLocation
import com.stefdp.pterodactylpanel.network.application.models.ApplicationNode
import com.stefdp.pterodactylpanel.network.application.models.ApplicationServer
import com.stefdp.pterodactylpanel.network.application.models.ApplicationUser
import com.stefdp.pterodactylpanel.network.application.models.requests.CreateLocationBody
import com.stefdp.pterodactylpanel.network.application.models.requests.CreateNodeAllocationBody
import com.stefdp.pterodactylpanel.network.application.models.requests.CreateNodeBody
import com.stefdp.pterodactylpanel.network.application.models.requests.CreateServerBody
import com.stefdp.pterodactylpanel.network.application.models.requests.CreateUserBody
import com.stefdp.pterodactylpanel.network.application.models.requests.GetUsersQuerySort
import com.stefdp.pterodactylpanel.network.application.models.requests.ListLocationsQuerySort
import com.stefdp.pterodactylpanel.network.application.models.requests.ListNodesQueryInclude
import com.stefdp.pterodactylpanel.network.application.models.requests.ListNodesQuerySort
import com.stefdp.pterodactylpanel.network.application.models.requests.ListServersQuerySort
import com.stefdp.pterodactylpanel.network.application.models.requests.ListUsersQueryInclude
import com.stefdp.pterodactylpanel.network.application.models.requests.UpdateNodeBody
import com.stefdp.pterodactylpanel.network.application.models.requests.UpdateServerBuildBody
import com.stefdp.pterodactylpanel.network.application.models.requests.UpdateServerDetailsBody
import com.stefdp.pterodactylpanel.network.application.models.requests.UpdateServerStartupBody
import com.stefdp.pterodactylpanel.network.application.models.requests.UpdateUserBody
import com.stefdp.pterodactylpanel.network.application.models.responses.GetDeployableNodesResponse
import com.stefdp.pterodactylpanel.network.application.models.responses.GetNodeConfigurationResponse
import com.stefdp.pterodactylpanel.network.application.models.responses.ListLocationsResponse
import com.stefdp.pterodactylpanel.network.application.models.responses.ListNodeAllocationsResponse
import com.stefdp.pterodactylpanel.network.application.models.responses.ListNodesResponse
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
    @GET("application/users")
    fun listUsers(
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

    @GET("application/users/{userId}")
    fun getUser(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("userId") userId: Long,
        @Query("include") include: String? = null, // list of ListUsersQueryInclude separated by ","
    ): Response<ApplicationUser>

    @GET("application/users/external/{externalId}")
    fun getUserByExternalId(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("externalId") externalId: String,
        @Query("include") include: String? = null, // list of ListUsersQueryInclude separated by ","
    ): Response<ApplicationUser>

    @POST("application/users")
    fun createUser(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Body data: CreateUserBody
    ): Response<ApplicationUser>

    @PATCH("application/users/{userId}")
    fun updateUser(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("userId") userId: Long,
        @Body data: UpdateUserBody
    ): Response<ApplicationUser>

    @DELETE("application/users/{userId}")
    fun deleteUser(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("userId") userId: Long
    )

    @GET("application/nodes")
    fun listNodes(
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

    @GET("application/nodes/{nodeId}")
    fun getNode(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("nodeId") nodeId: Long,
        @Query("include") include: String? = null // list of ListNodesQueryInclude separated by ","
    ): Response<ApplicationNode>

    @GET("application/nodes/{nodeId}/configuration")
    fun getNodeConfiguration(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("nodeId") nodeId: Long
    ): Response<GetNodeConfigurationResponse>

    @GET("application/nodes/deployable")
    fun getDeployableNodes(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Query("memory") memory: Long,
        @Query("disk") disk: Long,
        @Query("location_ids[]") locationIds: List<Long>? = null,
        @Query("page") page: Int? = null,
        @Query("per_page") perPage: Int? = null,
    ): Response<GetDeployableNodesResponse>

    @POST("application/nodes")
    fun createNode(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Body data: CreateNodeBody
    ): Response<ApplicationNode>

    @PATCH("application/nodes/{nodeId}")
    fun updateNode(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Body data: UpdateNodeBody
    ): Response<ApplicationNode>

    @DELETE("application/nodes/{nodeId}")
    fun deleteNode(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("nodeId") nodeId: Long
    )

    @GET("application/nodes/{nodeId}/allocations")
    fun listNodeAllocations(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("nodeId") nodeId: Long,
        @Query("include") include: String, //  list of ListNodeAllocationsQueryInclude separated by ","
        @Query("filter[ip]") filterIp: String? = null,
        @Query("filter[port]") filterPort: Int? = null,
        @Query("filter[ip_alias") filterIpAlias: String? = null,
        @Query("filter[server_id]") filterServerId: Long? = null,
        @Query("page") page: Int? = null,
        @Query("per_page") perPage: Int? = null,
    ): Response<ListNodeAllocationsResponse>

    @POST("application/nodes/{nodeId}/allocations")
    fun createNodeAllocation(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("nodeId") nodeId: Long,
        @Body data: CreateNodeAllocationBody
    )

    @DELETE("application/nodes/{nodeId}/allocations/{allocationId}")
    fun deleteNodeAllocation(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("nodeId") nodeId: Long,
        @Path("allocationId") allocationId: Long
    )

    @GET("application/locations")
    fun listLocations(
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

    @GET("application/locations/{locationId}")
    fun getLocation(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("locationId") locationId: Long,
        @Query("include") include: String? = null, // list of ListLocationsQueryInclude separated by ","
    ): Response<ApplicationLocation>

    @POST("application/locations")
    fun createLocation(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Body data: CreateLocationBody
    ): Response<ApplicationLocation>

    @PATCH("application/locations/{locationId}")
    fun updateLocation(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("locationId") locationId: Long,
        @Body data: CreateLocationBody
    ): Response<ApplicationLocation>

    @DELETE("application/locations/{locationId}")
    fun deleteLocation(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("locationId") locationId: Long
    )

    @GET("application/servers")
    fun listServers(
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

    @GET("application/servers/{serverId}")
    fun getServer(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: Long,
        @Query("include") include: String? = null, // list of ListServersQueryInclude separated by ","
    ): Response<ApplicationServer>

    @GET("application/servers/external/{serverId}")
    fun getServerByExternalId(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("externalId") etxernalId: Long,
        @Query("include") include: String? = null, // list of ListServersQueryInclude separated by ","
    ): Response<ApplicationServer>

    @POST("application/servers")
    fun createServer(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Body data: CreateServerBody
    ): Response<ApplicationServer>

    @PATCH("application/servers/{serverId}/details")
    fun updateServerDetails(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: Long,
        @Body data: UpdateServerDetailsBody
    ): Response<ApplicationServer>

    @PATCH("application/servers/{serverId}/build")
    fun updateServerBuild(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: Long,
        @Body data: UpdateServerBuildBody
    ): Response<ApplicationServer>

    @PATCH("application/servers/{serverId}/startup")
    fun updateServerStartup(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: Long,
        @Body data: UpdateServerStartupBody
    ): Response<ApplicationServer>

    @POST("application/servers/{serverId}/suspend")
    fun suspendServer(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: Long,
    )

    @POST("application/servers/{serverId}/unsuspend")
    fun unsuspendServer(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: Long,
    )

    @POST("application/servers/{serverId}/reinstall")
    fun reinstallServer(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: Long,
    )

    @DELETE("application/servers/{serverId}")
    fun deleteServer(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: Long,
    )

    @DELETE("application/servers/{serverId}/force")
    fun forceDeleteServer(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: Long,
    )
}
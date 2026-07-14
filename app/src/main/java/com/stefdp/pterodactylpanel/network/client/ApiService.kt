package com.stefdp.pterodactylpanel.network.client

import com.stefdp.pterodactylpanel.network.client.models.RecoveryCodes
import com.stefdp.pterodactylpanel.network.client.models.ServerAllocation
import com.stefdp.pterodactylpanel.network.client.models.ServerBackup
import com.stefdp.pterodactylpanel.network.client.models.ServerDatabase
import com.stefdp.pterodactylpanel.network.client.models.ServerEggVariable
import com.stefdp.pterodactylpanel.network.client.models.ServerFile
import com.stefdp.pterodactylpanel.network.client.models.ServerSchedule
import com.stefdp.pterodactylpanel.network.client.models.ServerScheduleTask
import com.stefdp.pterodactylpanel.network.client.models.ServerStats
import com.stefdp.pterodactylpanel.network.client.models.ServerSubuser
import com.stefdp.pterodactylpanel.network.client.models.SignedUrl
import com.stefdp.pterodactylpanel.network.client.models.SshKey
import com.stefdp.pterodactylpanel.network.client.models.User
import com.stefdp.pterodactylpanel.network.client.models.requests.AddAccountSshKeyBody
import com.stefdp.pterodactylpanel.network.client.models.requests.CompressServerFilesBody
import com.stefdp.pterodactylpanel.network.client.models.requests.CopyServerFileBody
import com.stefdp.pterodactylpanel.network.client.models.requests.CreateAccountApiKeyBody
import com.stefdp.pterodactylpanel.network.client.models.requests.CreateServerBackupBody
import com.stefdp.pterodactylpanel.network.client.models.requests.CreateServerDatabaseBody
import com.stefdp.pterodactylpanel.network.client.models.requests.CreateServerFolderBody
import com.stefdp.pterodactylpanel.network.client.models.requests.CreateServerScheduleBody
import com.stefdp.pterodactylpanel.network.client.models.requests.CreateServerScheduleTaskBody
import com.stefdp.pterodactylpanel.network.client.models.requests.CreateServerSubuserBody
import com.stefdp.pterodactylpanel.network.client.models.requests.DecompressServerFileBody
import com.stefdp.pterodactylpanel.network.client.models.requests.DeleteServerFilesBody
import com.stefdp.pterodactylpanel.network.client.models.requests.DisableAccount2FABody
import com.stefdp.pterodactylpanel.network.client.models.requests.EnableAccount2FABody
import com.stefdp.pterodactylpanel.network.client.models.requests.GetAccountActivityQuerySort
import com.stefdp.pterodactylpanel.network.client.models.requests.GetServersQueryType
import com.stefdp.pterodactylpanel.network.client.models.requests.RemoveAccountSshKeyBody
import com.stefdp.pterodactylpanel.network.client.models.requests.RenameServerBody
import com.stefdp.pterodactylpanel.network.client.models.requests.RenameServerFilesBody
import com.stefdp.pterodactylpanel.network.client.models.requests.RestoreServerBackupBody
import com.stefdp.pterodactylpanel.network.client.models.requests.SendCommandToServerBody
import com.stefdp.pterodactylpanel.network.client.models.requests.SendPowerSignalToServerBody
import com.stefdp.pterodactylpanel.network.client.models.requests.UpdateAccountEmailBody
import com.stefdp.pterodactylpanel.network.client.models.requests.UpdateAccountPasswordBody
import com.stefdp.pterodactylpanel.network.client.models.requests.UpdateServerAllocationNotesBody
import com.stefdp.pterodactylpanel.network.client.models.requests.UpdateServerDockerImageBody
import com.stefdp.pterodactylpanel.network.client.models.requests.UpdateServerFilesPermissionsBody
import com.stefdp.pterodactylpanel.network.client.models.requests.UpdateServerScheduleBody
import com.stefdp.pterodactylpanel.network.client.models.requests.UpdateServerScheduleTaskBody
import com.stefdp.pterodactylpanel.network.client.models.requests.UpdateServerStartupVariableBody
import com.stefdp.pterodactylpanel.network.client.models.requests.UpdateServerUserBody
import com.stefdp.pterodactylpanel.network.client.models.responses.CreateAccountApiKeyResponse
import com.stefdp.pterodactylpanel.network.client.models.responses.GetAccount2FAQrCodeResponse
import com.stefdp.pterodactylpanel.network.client.models.responses.GetAccountActivityResponse
import com.stefdp.pterodactylpanel.network.client.models.responses.GetAccountSshKeysResponse
import com.stefdp.pterodactylpanel.network.client.models.responses.GetAvailablePermissionsResponse
import com.stefdp.pterodactylpanel.network.client.models.responses.GetServerResponse
import com.stefdp.pterodactylpanel.network.client.models.responses.GetServerStartupVariablesResponse
import com.stefdp.pterodactylpanel.network.client.models.responses.GetServerWebsocketResponse
import com.stefdp.pterodactylpanel.network.client.models.responses.ListAccountApiKeysResponse
import com.stefdp.pterodactylpanel.network.client.models.responses.ListServerAllocationsResponse
import com.stefdp.pterodactylpanel.network.client.models.responses.ListServerBackupsResponse
import com.stefdp.pterodactylpanel.network.client.models.responses.ListServerDatabasesResponse
import com.stefdp.pterodactylpanel.network.client.models.responses.ListServerFilesResponse
import com.stefdp.pterodactylpanel.network.client.models.responses.ListServerSchedulesResponse
import com.stefdp.pterodactylpanel.network.client.models.responses.ListServerSubusersResponse
import com.stefdp.pterodactylpanel.network.client.models.responses.ListServersResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming
import retrofit2.http.Url

// TODO: reminder for myself, api auth is "Bearer <API_KEY>"
/* docs:
- https://registry.scalar.com/@default-team-68o6n/apis/pterodactyl-panel-api@1.0.0#tag/client-server-network
    - docs OpenAPI file generated by opencode and displayed via scalar.com
- https://old-api.redbanana.dev/docs/client-server-schedules/post-update-task
    - old docs backed up by a pterodactyl panel Discord server moderator
- https://pterodactyl-api-docs.netvpx.com/docs/api/schedules#update-schedule-task
    - official docs but according to a moderator of the discord, generated with AI and includes hallucinations

I legit don't know which one to follow now 😭
*/

interface PterodactylClientApiService {
    @GET(".")
    suspend fun listServers(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Query("per_page") perPage: Int? = null,
        @Query("page") page: Int? = null,
        @Query("filter[name]") filterName: String? = null,
        @Query("filter[uuid]") filterUuid: String? = null,
        @Query("filter[external_id]") filterExternalId: String? = null,
        @Query("filter[description]") filterDescription: String? = null,
        @Query("filter[*]") filterAny: String? = null,
        @Query("type") type: GetServersQueryType? = null,
        @Query("include") include: String? = null, // list of ListServersQueryInclude separated by ","
    ): Response<ListServersResponse>

    @GET("permissions")
    suspend fun getAvailablePermissions(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
    ): Response<GetAvailablePermissionsResponse>

    @GET("account")
    suspend fun getAccount(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json"
    ): Response<User>

    @GET("account/two-factor")
    suspend fun getAccount2FAQrCode(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json"
    ): Response<GetAccount2FAQrCodeResponse>

    @POST("account/two-factor")
    suspend fun enableAccount2FA(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Body data: EnableAccount2FABody
    ): Response<RecoveryCodes>

    @POST("account/two-factor")
    suspend fun disableAccount2FA(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Body data: DisableAccount2FABody
    ): Response<Unit>

    @PUT("account/email")
    suspend fun updateAccountEmail(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Body data: UpdateAccountEmailBody
    ): Response<Unit>

    @PUT("account/password")
    suspend fun updateAccountPassword(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Body data: UpdateAccountPasswordBody
    ): Response<Unit>

    @GET("account/api-keys")
    suspend fun listAccountApiKeys(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
    ): Response<ListAccountApiKeysResponse>

    @POST("account/api-keys")
    suspend fun createAccountApiKey(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Body data: CreateAccountApiKeyBody
    ): Response<CreateAccountApiKeyResponse>

    @DELETE("account/api-keys/{keyId}")
    suspend fun deleteAccountApiKey(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("keyId") keyId: String,
    ): Response<Unit>

    @GET("account/activity")
    suspend fun getAccountActivity(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Query("filter[event]") filterEvent: String? = null,
        @Query("sort") sort: GetAccountActivityQuerySort? = null,
        @Query("include") include: String? = null, // list of GetAccountActivityQueryInclude separated by ","
        @Query("per_page") perPage: Int? = null,
        @Query("page") page: Int? = null,
    ): Response<GetAccountActivityResponse>
    
    @GET("account/ssh-keys")
    suspend fun listAccountSshKeys(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
    ): Response<GetAccountSshKeysResponse>

    @POST("account/ssh-keys")
    suspend fun addAccountSshKey(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Body data: AddAccountSshKeyBody
    ): Response<SshKey>

    @POST("account/ssh-keys/remove")
    suspend fun removeAccountSshKey(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Body data: RemoveAccountSshKeyBody
    ): Response<Unit>

    @GET("servers/{serverId}")
    suspend fun getServer(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Query("include") include: String? = null, // list of ListServersQueryInclude separated by ","
    ): Response<GetServerResponse>

    @GET("servers/{serverId}/websocker")
    suspend fun getServerWebsocket(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
    ): Response<GetServerWebsocketResponse>

    @GET("servers/{serverId}/resources")
    suspend fun getServerResources(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
    ): Response<ServerStats>

    @POST("servers/{serverId}/command")
    suspend fun sendCommandToServer(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Body data: SendCommandToServerBody
    ): Response<Unit>

    @POST("servers/{serverId}/power")
    suspend fun sendPowerSignalToServer(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Body data: SendPowerSignalToServerBody
    ): Response<Unit>

    @GET("servers/{serverId}/databases")
    suspend fun listServerDatabases(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Query("include") include: String? = null, // list of ListServerDatabasesQueryInclude separated by ","
    ): Response<ListServerDatabasesResponse>

    @POST("servers/{serverId}/databases")
    suspend fun createServerDatabase(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Body data: CreateServerDatabaseBody
    ): Response<ServerDatabase>

    @POST("server/{serverId}/databases/{databaseId)/rotate-password")
    suspend fun rotateServerDatabasePassword(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Path("databaseId") databaseId: String,
    ): Response<ServerDatabase>

    @DELETE("server/{serverId}/databases/{databaseId}")
    suspend fun deleteServerDatabase(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Path("databaseId") databaseId: String,
    ): Response<Unit>

    @GET("servers/{serverId}/files/list")
    suspend fun listServerFiles(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Query("directory") directory: String? = null,
    ): Response<ListServerFilesResponse>

    @GET("servers/{serverId}/files/contents")
    suspend fun getServerFileContents(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Query("file") file: String, // for "/home/container/file.txt" it'd be "/file.txt"
    ): Response<String>

    @GET("servers/{serverId}/files/download")
    suspend fun getDownloadServerFileUrl(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Query("file") file: String
    ): Response<SignedUrl>

    @Streaming
    @GET
    suspend fun downloadServerFile(
//        @Header("Authorization") authorization: String, // not sure if this requires auth or not
        @Url fileUrl: String, // the url returned by getDownloadServerFileUrl's response
    ): Response<ResponseBody>

    @POST("servers/{serverId}/files/write")
    suspend fun writeServerFile(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Query("file") file: String,
        @Body fileContents: String,
    ): Response<Unit>

    @POST("servers/{serverId}/files/create-folder")
    suspend fun createServerFolder(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Body data: CreateServerFolderBody
    ): Response<Unit>

    @POST("servers/{serverId}/files/copy")
    suspend fun copyServerFile(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Body data: CopyServerFileBody
    ): Response<Unit>

    @PUT("servers/{serverId}/files/rename")
    suspend fun renameServerFiles(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Body data: RenameServerFilesBody
    ): Response<Unit>

    @POST("servers/{serverId}/files/compress")
    suspend fun compressServerFiles(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Body data: CompressServerFilesBody
    ): Response<ServerFile>

    @POST("servers/{serverId}/files/decompress")
    suspend fun decompressServerFile(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Body data: DecompressServerFileBody
    ): Response<Unit>

    @POST("servers/{serverId}/files/delete")
    suspend fun deleteServerFiles(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Body data: DeleteServerFilesBody
    ): Response<Unit>

    @GET("servers/{serverId}/files/upload")
    suspend fun getUploadServerFileUrl(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
    ): Response<SignedUrl>

    @POST
    suspend fun uploadServerFile(
        @Url uploadUrl: String,
        @Part file: MultipartBody.Part,
        @Query("directory") directory: String? = null,
    ): Response<Unit>

    @POST("servers/{serverId}/files/chmod")
    suspend fun updateServerFilesPermissions(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Body data: UpdateServerFilesPermissionsBody
    ): Response<Unit>

    @GET("servers/{serverId}/schedules")
    suspend fun listServerSchedules(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
    ): Response<ListServerSchedulesResponse>

    @GET("servers/{serverId}/schedules/{scheduleId}")
    suspend fun getServerSchedule(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Path("scheduleId") scheduleId: String,
    ): Response<ServerSchedule>

    @POST("servers/{serverId}/schedules")
    suspend fun createServerSchedule(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Body data: CreateServerScheduleBody
    ): Response<ServerSchedule>

    @POST("servers/{serverId}/schedules/{scheduleId}")
    suspend fun updateServerSchedule(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Path("scheduleId") scheduleId: String,
        @Body data: UpdateServerScheduleBody
    ): Response<ServerSchedule>

    @DELETE("servers/{serverId}/schedules/{scheduleId}")
    suspend fun deleteServerSchedule(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Path("scheduleId") scheduleId: String,
    ): Response<Unit>

    @POST("servers/{serverId}/schedules/{scheduleId}/tasks")
    suspend fun createServerScheduleTask(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Path("scheduleId") scheduleId: String,
        @Body data: CreateServerScheduleTaskBody
    ): Response<ServerScheduleTask>

    @POST("servers/{serverId}/schedules/{scheduleId}/tasks/{taskId}")
    suspend fun updateServerScheduleTask(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Path("scheduleId") scheduleId: String,
        @Path("taskId") taskId: String,
        @Body data: UpdateServerScheduleTaskBody
    ): Response<ServerScheduleTask>

    @DELETE("servers/{serverId}/schedules/{scheduleId}/tasks/{taskId}")
    suspend fun deleteServerScheduleTask(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Path("scheduleId") scheduleId: String,
        @Path("taskId") taskId: String,
    ): Response<Unit>

    @GET("servers/{serverId}/network/allocations")
    suspend fun listServerAllocations(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
    ): Response<ListServerAllocationsResponse>

    @POST("servers/{serverId}/network/allocations")
    suspend fun assignAutomaticAllocationToServer(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
    ): Response<ServerAllocation>

    @POST("servers/{serverId}/network/allocations/{allocationId}/primary")
    suspend fun setServerPrimaryAllocation(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Path("allocationId") allocationId: String,
    ): Response<ServerAllocation>

    @POST("servers/{serverId}/network/allocations/{allocationId}")
    suspend fun updateServerAllocationNotes(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Path("allocationId") allocationId: String,
        @Body data: UpdateServerAllocationNotesBody
    ): Response<ServerAllocation>

    @DELETE("servers/{serverId}/network/allocations/{allocationId}")
    suspend fun deleteServerAllocation(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Path("allocationId") allocationId: String,
    ): Response<Unit>

    @GET("servers/{serverId}/users")
    suspend fun listServerSubusers(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
    ): Response<ListServerSubusersResponse>

    @GET("servers/{serverId}/users/{userUuid}")
    suspend fun getServerSubuser(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Path("userUuid") userUuid: String,
    ): Response<ServerSubuser>

    @POST("servers/{serverId}/users")
    suspend fun createServerSubuser(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Body data: CreateServerSubuserBody
    ): Response<ServerSubuser>

    @POST("servers/{serverId}/users/{userUuid}")
    suspend fun updateServerSubuser(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Path("userUuid") userUuid: String,
        @Body data: UpdateServerUserBody
    ): Response<ServerSubuser>

    @DELETE("servers/{serverId}/users/{userUuid}")
    suspend fun deleteServerSubuser(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Path("userUuid") userUuid: String,
    ): Response<Unit>

    @GET("servers/{serverId}/backups")
    suspend fun listServerBackups(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Query("per_page") perPage: Int? = null,
        @Query("page") page: Int? = null,
    ): Response<ListServerBackupsResponse>

    @GET("servers/{serverId}/backups/{backupId}")
    suspend fun getServerBackup(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Path("backupId") backupId: String,
    ): Response<ServerBackup>

    @GET("servers/{serverId}/backups/{backupId}/download")
    suspend fun getServerBackupDownloadUrl(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Path("backupId") backupId: String,
    ): Response<SignedUrl>

    @Streaming
    @GET
    suspend fun downloadServerBackup(
//        @Header("Authorization") authorization: String, // not sure if this requires auth or not
        @Url fileUrl: String, // the url returned by getServerBackupDownloadUrl's response
    ): Response<ResponseBody>

    @POST("servers/{serverId}/backups")
    suspend fun createServerBackup(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Body data: CreateServerBackupBody
    ): Response<ServerBackup>

    @POST("servers/{serverId}/backups/{backupId}/restore")
    suspend fun restoreServerBackup(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Path("backupId") backupId: String,
        @Body data: RestoreServerBackupBody
    ): Response<Unit>

    @POST("servers/{serverId}/backups/{backupId}/lock")
    suspend fun toggleServerBackupLock(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Path("backupId") backupId: String,
    ): Response<ServerBackup>

    @GET("servers/{serverId}/backups/{backupId}")
    suspend fun deleteServerBackup(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Path("backupId") backupId: String,
    ): Response<Unit>

    @GET("servers/{serverId}/startup")
    suspend fun getServerStartupVariables(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
    ): Response<GetServerStartupVariablesResponse>

    @PUT("servers/{serverId}/startup/variable")
    suspend fun updateServerStartupVariable(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Body data: UpdateServerStartupVariableBody
    ): Response<ServerEggVariable>

    @POST("servers/{serverId}/settings/rename")
    suspend fun renameServer(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Body data: RenameServerBody
    ): Response<Unit>

    @POST("servers/{serverId}/settings/reinstall")
    suspend fun reinstallServer(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
    ): Response<Unit>

    @PUT("servers/{serverId}/settings/docker-image")
    suspend fun updateServerDockerImage(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Body data: UpdateServerDockerImageBody
    ): Response<Unit>
}
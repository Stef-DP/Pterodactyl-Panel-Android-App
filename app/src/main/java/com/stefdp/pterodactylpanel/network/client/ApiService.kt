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
    @GET("")
    fun listServers(
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
    fun getAvailablePermissions(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
    ): Response<GetAvailablePermissionsResponse>

    @GET("account")
    fun getAccount(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json"
    ): Response<User>

    @GET("account/two-factor")
    fun getAccount2FAQrCode(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json"
    ): Response<GetAccount2FAQrCodeResponse>

    @POST("account/two-factor")
    fun enableAccount2FA(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Body data: EnableAccount2FABody
    ): Response<RecoveryCodes>

    @POST("account/two-factor")
    fun disableAccount2FA(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Body data: DisableAccount2FABody
    ): Response<Unit>

    @PUT("account/email")
    fun updateAccountEmail(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Body data: UpdateAccountEmailBody
    ): Response<Unit>

    @PUT("account/password")
    fun updateAccountPassword(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Body data: UpdateAccountPasswordBody
    ): Response<Unit>

    @GET("account/api-keys")
    fun listAccountApiKeys(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
    ): Response<ListAccountApiKeysResponse>

    @POST("account/api-keys")
    fun createAccountApiKey(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Body data: CreateAccountApiKeyBody
    ): Response<CreateAccountApiKeyResponse>

    @DELETE("account/api-keys/{keyId}")
    fun deleteAccountApiKey(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("keyId") keyId: String,
    ): Response<Unit>

    @GET("account/activity")
    fun getAccountActivity(
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
    fun listAccountSshKeys(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
    ): Response<GetAccountSshKeysResponse>

    @POST("account/ssh-keys")
    fun addAccountSshKey(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Body data: AddAccountSshKeyBody
    ): Response<SshKey>

    @POST("account/ssh-keys/remove")
    fun removeAccountSshKey(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Body data: RemoveAccountSshKeyBody
    ): Response<Unit>

    @GET("servers/{serverId}")
    fun getServer(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Query("include") include: String? = null, // list of ListServersQueryInclude separated by ","
    ): Response<GetServerResponse>

    @GET("servers/{serverId}/websocker")
    fun getServerWebsocket(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
    ): Response<GetServerWebsocketResponse>

    @GET("servers/{serverId}/resources")
    fun getServerResources(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
    ): Response<ServerStats>

    @POST("servers/{serverId}/command")
    fun sendCommandToServer(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Body data: SendCommandToServerBody
    ): Response<Unit>

    @POST("servers/{serverId}/power")
    fun sendPowerSignalToServer(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Body data: SendPowerSignalToServerBody
    ): Response<Unit>

    @GET("servers/{serverId}/databases")
    fun listServerDatabases(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Query("include") include: String? = null, // list of ListServerDatabasesQueryInclude separated by ","
    ): Response<ListServerDatabasesResponse>

    @POST("servers/{serverId}/databases")
    fun createServerDatabase(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Body data: CreateServerDatabaseBody
    ): Response<ServerDatabase>

    @POST("server/{serverId}/databases/{databaseId)/rotate-password")
    fun rotateServerDatabasePassword(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Path("databaseId") databaseId: String,
    ): Response<ServerDatabase>

    @DELETE("server/{serverId}/databases/{databaseId}")
    fun deleteServerDatabase(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Path("databaseId") databaseId: String,
    ): Response<Unit>

    @GET("servers/{serverId}/files/list")
    fun listServerFiles(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Query("directory") directory: String? = null,
    ): Response<ListServerFilesResponse>

    @GET("servers/{serverId}/files/contents")
    fun getServerFileContents(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Query("file") file: String, // for "/home/container/file.txt" it'd be "/file.txt"
    ): Response<String>

    @GET("servers/{serverId}/files/download")
    fun getDownloadServerFileUrl(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Query("file") file: String
    ): Response<SignedUrl>

    @Streaming
    @GET
    fun downloadServerFile(
//        @Header("Authorization") authorization: String, // not sure if this requires auth or not
        @Url fileUrl: String, // the url returned by getDownloadServerFileUrl's response
    ): Response<ResponseBody>

    @POST("servers/{serverId}/files/write")
    fun writeServerFile(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Query("file") file: String,
        @Body fileContents: String,
    ): Response<Unit>

    @POST("servers/{serverId}/files/create-folder")
    fun createServerFolder(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Body data: CreateServerFolderBody
    ): Response<Unit>

    @POST("servers/{serverId}/files/copy")
    fun copyServerFile(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Body data: CopyServerFileBody
    ): Response<Unit>

    @PUT("servers/{serverId}/files/rename")
    fun renameServerFiles(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Body data: RenameServerFilesBody
    ): Response<Unit>

    @POST("servers/{serverId}/files/compress")
    fun compressServerFiles(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Body data: CompressServerFilesBody
    ): Response<ServerFile>

    @POST("servers/{serverId}/files/decompress")
    fun decompressServerFile(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Body data: DecompressServerFileBody
    ): Response<Unit>

    @POST("servers/{serverId}/files/delete")
    fun deleteServerFiles(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Body data: DeleteServerFilesBody
    ): Response<Unit>

    @GET("servers/{serverId}/files/upload")
    fun geUploadServerFileUrl(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Query("directory") directory: String? = null, // not sure if that actually works
    ): Response<SignedUrl>

    // TODO: if i can't get this to work, i'll just use use writeServerFile instead
    @POST
    fun uploadServerFile(
//        @Header("Authorization") authorization: String, // not sure if this requires auth or not
        @Url uploadUrl: String,
        @Part files: List<MultipartBody.Part>, // haven't tested that, idk if it actually works
        @Part("directory") directory: String, // idk if that'll work either
    )

    @POST("servers/{serverId}/files/chmod")
    fun updateServerFilesPermissions(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Body data: UpdateServerFilesPermissionsBody
    ): Response<Unit>

    @GET("servers/{serverId}/schedules")
    fun listServerSchedules(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
    ): Response<ListServerSchedulesResponse>

    @GET("servers/{serverId}/schedules/{scheduleId}")
    fun getServerSchedule(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Path("scheduleId") scheduleId: String,
    ): Response<ServerSchedule>

    @POST("servers/{serverId}/schedules")
    fun createServerSchedule(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Body data: CreateServerScheduleBody
    ): Response<ServerSchedule>

    @POST("servers/{serverId}/schedules/{scheduleId}")
    fun updateServerSchedule(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Path("scheduleId") scheduleId: String,
        @Body data: UpdateServerScheduleBody
    ): Response<ServerSchedule>

    @DELETE("servers/{serverId}/schedules/{scheduleId}")
    fun deleteServerSchedule(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Path("scheduleId") scheduleId: String,
    ): Response<Unit>

    @POST("servers/{serverId}/schedules/{scheduleId}/tasks")
    fun createServerScheduleTask(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Path("scheduleId") scheduleId: String,
        @Body data: CreateServerScheduleTaskBody
    ): Response<ServerScheduleTask>

    @POST("servers/{serverId}/schedules/{scheduleId}/tasks/{taskId}")
    fun updateServerScheduleTask(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Path("scheduleId") scheduleId: String,
        @Path("taskId") taskId: String,
        @Body data: UpdateServerScheduleTaskBody
    ): Response<ServerScheduleTask>

    @DELETE("servers/{serverId}/schedules/{scheduleId}/tasks/{taskId}")
    fun deleteServerScheduleTask(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Path("scheduleId") scheduleId: String,
        @Path("taskId") taskId: String,
    ): Response<Unit>

    @GET("servers/{serverId}/network/allocations")
    fun listServerAllocations(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
    ): Response<ListServerAllocationsResponse>

    @POST("servers/{serverId}/network/allocations")
    fun assignAutomaticAllocationToServer(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
    ): Response<ServerAllocation>

    @POST("servers/{serverId}/network/allocations/{allocationId}/primary")
    fun setServerPrimaryAllocation(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Path("allocationId") allocationId: String,
    ): Response<ServerAllocation>

    @POST("servers/{serverId}/network/allocations/{allocationId}")
    fun updateServerAllocationNotes(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Path("allocationId") allocationId: String,
        @Body data: UpdateServerAllocationNotesBody
    ): Response<ServerAllocation>

    @DELETE("servers/{serverId}/network/allocations/{allocationId}")
    fun deleteServerAllocation(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Path("allocationId") allocationId: String,
    ): Response<Unit>

    @GET("servers/{serverId}/users")
    fun listServerSubusers(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
    ): Response<ListServerSubusersResponse>

    @GET("servers/{serverId}/users/{userUuid}")
    fun getServerSubuser(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Path("userUuid") userUuid: String,
    ): Response<ServerSubuser>

    @POST("servers/{serverId}/users")
    fun createServerSubuser(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Body data: CreateServerSubuserBody
    ): Response<ServerSubuser>

    @POST("servers/{serverId}/users/{userUuid}")
    fun updateServerSubuser(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Path("userUuid") userUuid: String,
        @Body data: UpdateServerUserBody
    ): Response<ServerSubuser>

    @DELETE("servers/{serverId}/users/{userUuid}")
    fun deleteServerSubuser(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Path("userUuid") userUuid: String,
    ): Response<Unit>

    @GET("servers/{serverId}/backups")
    fun listServerBackups(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Query("per_page") perPage: Int? = null,
        @Query("page") page: Int? = null,
    ): Response<ListServerBackupsResponse>

    @GET("servers/{serverId}/backups/{backupId}")
    fun getServerBackup(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Path("backupId") backupId: String,
    ): Response<ServerBackup>

    @GET("servers/{serverId}/backups/{backupId}/download")
    fun getServerBackupDownloadUrl(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Path("backupId") backupId: String,
    ): Response<SignedUrl>

    @Streaming
    @GET
    fun downloadServerBackup(
//        @Header("Authorization") authorization: String, // not sure if this requires auth or not
        @Url fileUrl: String, // the url returned by getServerBackupDownloadUrl's response
    ): Response<ResponseBody>

    @POST("servers/{serverId}/backups")
    fun createServerBackup(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Body data: CreateServerBackupBody
    ): Response<ServerBackup>

    @POST("servers/{serverId}/backups/{backupId}/restore")
    fun restoreServerBackup(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Path("backupId") backupId: String,
        @Body data: RestoreServerBackupBody
    ): Response<Unit>

    @POST("servers/{serverId}/backups/{backupId}/lock")
    fun toggleServerBackupLock(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Path("backupId") backupId: String,
    ): Response<ServerBackup>

    @GET("servers/{serverId}/backups/{backupId}")
    fun deleteServerBackup(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Path("backupId") backupId: String,
    ): Response<Unit>

    @GET("servers/{serverId}/startup")
    fun getServerStartupVariables(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
    ): Response<GetServerStartupVariablesResponse>

    @PUT("servers/{serverId}/startup/variable")
    fun updateServerStartupVariable(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Body data: UpdateServerStartupVariableBody
    ): Response<ServerEggVariable>

    @POST("servers/{serverId}/settings/rename")
    fun renameServer(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Body data: RenameServerBody
    ): Response<Unit>

    @POST("servers/{serverId}/settings/reinstall")
    fun reinstallServer(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
    ): Response<Unit>

    @PUT("servers/{serverId}/settings/docker-image")
    fun updateServerDockerImage(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverId") serverId: String,
        @Body data: UpdateServerDockerImageBody
    ): Response<Unit>
}
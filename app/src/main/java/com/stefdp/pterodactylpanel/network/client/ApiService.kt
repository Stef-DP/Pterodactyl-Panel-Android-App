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
import com.stefdp.pterodactylpanel.network.client.models.requests.UpdateServerDockerImageBody
import com.stefdp.pterodactylpanel.network.client.models.User
import com.stefdp.pterodactylpanel.network.client.models.requests.AddAccountSshKeyBody
import com.stefdp.pterodactylpanel.network.client.models.requests.SendCommandToServerBody
import com.stefdp.pterodactylpanel.network.client.models.requests.SendPowerSignalToServerBody
import com.stefdp.pterodactylpanel.network.client.models.requests.CompressServerFilesBody
import com.stefdp.pterodactylpanel.network.client.models.requests.CopyServerFileBody
import com.stefdp.pterodactylpanel.network.client.models.requests.CreateAccountApiKeyBody
import com.stefdp.pterodactylpanel.network.client.models.requests.CreateServerBackupBody
import com.stefdp.pterodactylpanel.network.client.models.requests.CreateServerDatabaseBody
import com.stefdp.pterodactylpanel.network.client.models.requests.CreateServerFolderBody
import com.stefdp.pterodactylpanel.network.client.models.requests.CreateServerScheduleBody
import com.stefdp.pterodactylpanel.network.client.models.requests.CreateServerScheduleTaskBody
import com.stefdp.pterodactylpanel.network.client.models.requests.CreateServerUserBody
import com.stefdp.pterodactylpanel.network.client.models.requests.DecompressServerFileBody
import com.stefdp.pterodactylpanel.network.client.models.requests.DeleteServerFilesBody
import com.stefdp.pterodactylpanel.network.client.models.requests.DisableAccount2FABody
import com.stefdp.pterodactylpanel.network.client.models.requests.EnableAccount2FABody
import com.stefdp.pterodactylpanel.network.client.models.requests.GetAccountActivityQueryInclude
import com.stefdp.pterodactylpanel.network.client.models.requests.GetAccountActivityQuerySort
import com.stefdp.pterodactylpanel.network.client.models.requests.GetServersQueryType
import com.stefdp.pterodactylpanel.network.client.models.requests.RemoveAccountSshKeyBody
import com.stefdp.pterodactylpanel.network.client.models.requests.RenameServerBody
import com.stefdp.pterodactylpanel.network.client.models.requests.RenameServerFilesBody
import com.stefdp.pterodactylpanel.network.client.models.requests.RestoreServerBackupBody
import com.stefdp.pterodactylpanel.network.client.models.requests.UpdateAccountEmailBody
import com.stefdp.pterodactylpanel.network.client.models.requests.UpdateAccountPasswordBody
import com.stefdp.pterodactylpanel.network.client.models.requests.UpdateServerAllocationNotesBody
import com.stefdp.pterodactylpanel.network.client.models.requests.UpdateServerScheduleBody
import com.stefdp.pterodactylpanel.network.client.models.requests.UpdateServerFilePermissionsBody
import com.stefdp.pterodactylpanel.network.client.models.requests.UpdateServerScheduleTaskBody
import com.stefdp.pterodactylpanel.network.client.models.requests.UpdateServerStartupVariableBody
import com.stefdp.pterodactylpanel.network.client.models.requests.UpdateServerUserBody
import com.stefdp.pterodactylpanel.network.client.models.responses.CreateAccountApiKeyResponse
import com.stefdp.pterodactylpanel.network.client.models.responses.GetAccount2FAQrCodeResponse
import com.stefdp.pterodactylpanel.network.client.models.responses.GetAvailablePermissionsResponse
import com.stefdp.pterodactylpanel.network.client.models.responses.GetAccountActivityResponse
import com.stefdp.pterodactylpanel.network.client.models.responses.GetServerBackupsResponse
import com.stefdp.pterodactylpanel.network.client.models.responses.GetServerResponse
import com.stefdp.pterodactylpanel.network.client.models.responses.GetServerSchedulesResponse
import com.stefdp.pterodactylpanel.network.client.models.responses.GetServerStartupVariablesResponse
import com.stefdp.pterodactylpanel.network.client.models.responses.GetServersResponse
import com.stefdp.pterodactylpanel.network.client.models.responses.GetAccountSshKeysResponse
import com.stefdp.pterodactylpanel.network.client.models.responses.ListAccountApiKeysResponse
import com.stefdp.pterodactylpanel.network.client.models.responses.ListServerAllocationsResponse
import com.stefdp.pterodactylpanel.network.client.models.responses.ListServerDatabasesResponse
import com.stefdp.pterodactylpanel.network.client.models.responses.ListServerFilesResponse
import com.stefdp.pterodactylpanel.network.client.models.responses.ListServerUsersResponse
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
    - official docs but according to a moderator fo the discord, generated with AI and includes hallucinations

i legit don't know which one to follow now 😭
*/

interface PterodactylClientApiService {
    @GET("")
    fun getServers(
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
        @Query("type") type: GetServersQueryType,
        @Query("include") include: String? = null, // list of GetServersQueryInclude separated by ","
    ): Response<GetServersResponse>

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
    )

    @PUT("account/email")
    fun updateAccountEmail(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Body data: UpdateAccountEmailBody
    )

    @PUT("account/password")
    fun updateAccountPassword(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Body data: UpdateAccountPasswordBody
    )

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

    @DELETE("account/api-keys/{identifier}")
    fun deleteAccountApiKey(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("identifier") keyId: String,
    )

    @GET("account/activity")
    fun getAccountActivity(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Query("filter[event]") filterEvent: String? = null,
        @Query("sort") sort: GetAccountActivityQuerySort? = null,
        @Query("include") include: GetAccountActivityQueryInclude? = null,
        @Query("per_page") perPage: Int? = null,
        @Query("page") page: Int? = null,
    ): Response<GetAccountActivityResponse>
    
    @GET("account/ssh-keys")
    fun getAccountSshKeys(
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
    )

    @GET("servers/{identifier}")
    fun getServer(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("identifier") serverId: String,
    ): Response<GetServerResponse>

    @GET("servers/{identifier}/resources")
    fun getServerResources(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("identifier") serverId: String,
    ): Response<ServerStats>

    @POST("servers/{identifier}/command")
    fun clientSendCommandToServer(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("identifier") serverId: String,
        @Body data: SendCommandToServerBody
    )

    @POST("servers/{identifier}/power")
    fun clientSendPowerSignalToServer(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("identifier") serverId: String,
        @Body data: SendPowerSignalToServerBody
    )

    @GET("servers/{identifier}/databases")
    fun listServerDatabases(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("identifier") serverId: String,
    ): Response<ListServerDatabasesResponse>

    @POST("servers/{identifier}/databases")
    fun createServerDatabase(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("identifier") serverId: String,
        @Body data: CreateServerDatabaseBody
    ): Response<ServerDatabase>

    @POST("server/{serverIdentifier}/databases/{databaseIdentifier)/rotate-password")
    fun rotateServerDatabasePassword(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverIdentifier") serverId: String,
        @Path("databaseIdentifier") databaseId: String,
    ): Response<ServerDatabase>

    @DELETE("server/{serverIdentifier}/databases/{databaseIdentifier}")
    fun deleteServerDatabase(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverIdentifier") serverId: String,
        @Path("databaseIdentifier") databaseId: String,
    )

    @GET("servers/{identifier}/files/list")
    fun listServerFiles(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("identifier") serverId: String,
        @Query("directory") directory: String? = null,
    ): Response<ListServerFilesResponse>

    @GET("servers/{identifier}/files/contents")
    fun getServerFileContents(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("identifier") serverId: String,
        @Query("file") file: String, // for "/home/container/file.txt" it'd be "/file.txt"
    ): Response<String>

    @GET("servers/{identifier}/files/download")
    fun getDownloadServerFileUrl(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("identifier") serverId: String,
        @Query("file") file: String
    ): Response<SignedUrl>

    @Streaming
    @GET
    fun downloadServerFile(
//        @Header("Authorization") authorization: String, // not sure if this requires auth or not
        @Url fileUrl: String, // the url returned by getDownloadServerFileUrl's response
    ): Response<ResponseBody>

    @POST("servers/{identifier}/files/write")
    fun writeServerFile(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("identifier") serverId: String,
        @Query("file") file: String,
        @Body fileContents: String,
    )

    @POST("servers/{identifier}/files/create-folder")
    fun createServerFolder(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("identifier") serverId: String,
        @Body data: CreateServerFolderBody
    )

    @POST("servers/{identifier}/files/copy")
    fun copyServerFile(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("identifier") serverId: String,
        @Body data: CopyServerFileBody
    )

    @PUT("servers/{identifier}/files/rename")
    fun renameServerFiles(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("identifier") serverId: String,
        @Body data: RenameServerFilesBody
    )

    @POST("servers/{identifier}/files/compress")
    fun compressServerFiles(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("identifier") serverId: String,
        @Body data: CompressServerFilesBody
    ): Response<ServerFile>

    @POST("servers/{identifier}/files/decompress")
    fun decompressServerFile(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("identifier") serverId: String,
        @Body data: DecompressServerFileBody
    )

    @POST("servers/{identifier}/files/delete")
    fun deleteServerFiles(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("identifier") serverId: String,
        @Body data: DeleteServerFilesBody
    )

    @GET("servers/{identifier}/files/upload")
    fun geUploadServerFileUrl(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("identifier") serverId: String,
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

    @POST("servers/{identifier}/files/chmod")
    fun updateServerFilePermissions(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("identifier") serverId: String,
        @Body data: UpdateServerFilePermissionsBody
    )

    @GET("servers/{identifier}/schedules")
    fun getServerSchedules(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("identifier") serverId: String,
    ): Response<GetServerSchedulesResponse>

    @GET("servers/{serverIdentifier}/schedules/{scheduleIdentifier}")
    fun getServerSchedule(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverIdentifier") serverId: String,
        @Path("scheduleIdentifier") scheduleId: String,
    ): Response<ServerSchedule>

    @POST("servers/{identifier}/schedules")
    fun createServerSchedule(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("identifier") serverId: String,
        @Body data: CreateServerScheduleBody
    ): Response<ServerSchedule>

    @POST("servers/{serverIdentifier}/schedules/{scheduleIdentifier}")
    fun updateServerSchedule(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverIdentifier") serverId: String,
        @Path("scheduleIdentifier") scheduleId: String,
        @Body data: UpdateServerScheduleBody
    ): Response<ServerSchedule>

    @DELETE("servers/{serverIdentifier}/schedules/{scheduleIdentifier}")
    fun deleteServerSchedule(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverIdentifier") serverId: String,
        @Path("scheduleIdentifier") scheduleId: String,
    )

    @POST("servers/{serverIdentifier}/schedules/{scheduleIdentifier}/tasks")
    fun createServerScheduleTask(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverIdentifier") serverId: String,
        @Path("scheduleIdentifier") scheduleId: String,
    ): Response<CreateServerScheduleTaskBody>

    @POST("servers/{serverIdentifier}/schedules/{scheduleIdentifier}/tasks/{taskIdentifier}")
    fun updateServerScheduleTask(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverIdentifier") serverId: String,
        @Path("scheduleIdentifier") scheduleId: String,
        @Path("taskIdentifier") taskId: String,
        @Body data: UpdateServerScheduleTaskBody
    ): Response<ServerScheduleTask>

    @DELETE("servers/{serverIdentifier}/schedules/{scheduleIdentifier}/tasks/{taskIdentifier}")
    fun deleteServerScheduleTask(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverIdentifier") serverId: String,
        @Path("scheduleIdentifier") scheduleId: String,
        @Path("taskIdentifier") taskId: String,
    )

    @GET("servers/{identifier}/network/allocations")
    fun listServerAllocations(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("identifier") serverId: String,
    ): Response<ListServerAllocationsResponse>

    @POST("servers/{identifier}/network/allocations")
    fun assignAutomaticAllocationToServer(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("identifier") serverId: String,
    ): Response<ServerAllocation>

    @POST("servers/{serverIdentifier}/network/allocations/{allocationIdentifier}/primary")
    fun setServerPrimaryAllocation(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverIdentifier") serverId: String,
        @Path("allocationIdentifier") allocationId: String,
    ): Response<ServerAllocation>

    @POST("servers/{serverIdentifier}/network/allocations/{allocationIdentifier}")
    fun updateServerAllocationNotes(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverIdentifier") serverId: String,
        @Path("allocationIdentifier") allocationId: String,
        @Body data: UpdateServerAllocationNotesBody
    ): Response<ServerAllocation>

    @DELETE("servers/{serverIdentifier}/network/allocations/{allocationIdentifier}")
    fun deleteServerAllocation(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverIdentifier") serverId: String,
        @Path("allocationIdentifier") allocationId: String,
    )

    @GET("servers/{identifier}/users")
    fun listServerUsers(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("identifier") serverId: String,
    ): Response<ListServerUsersResponse>

    @GET("servers/{serverIdentifier}/users/{userIdentifier}")
    fun getServerUser(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverIdentifier") serverId: String,
        @Path("userIdentifier") userUuid: String,
    ): Response<ServerSubuser>

    @POST("servers/{identifier}/users")
    fun createServerUser(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("identifier") serverId: String,
        @Body data: CreateServerUserBody
    ): Response<ServerSubuser>

    @POST("servers/{serverIdentifier}/users/{userIdentifier}")
    fun updateServerUser(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverIdentifier") serverId: String,
        @Path("userIdentifier") userUuid: String,
        @Body data: UpdateServerUserBody
    ): Response<ServerSubuser>

    @DELETE("servers/{serverIdentifier}/users/{userIdentifier}")
    fun deleteServerUser(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverIdentifier") serverId: String,
        @Path("userIdentifier") userUuid: String,
    )

    @GET("servers/{identifier}/backups")
    fun getServerBackups(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("identifier") serverId: String,
        @Query("per_page") perPage: Int? = null,
        @Query("page") page: Int? = null,
    ): Response<GetServerBackupsResponse>

    @GET("servers/{serverIdentifier}/backups/{backupIdentifier}")
    fun getServerBackup(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverIdentifier") serverId: String,
        @Path("backupIdentifier") backupId: String,
    ): Response<ServerBackup>

    @GET("servers/{serverIdentifier}/backups/{backupIdentifier}/download")
    fun getServerBackupDownloadUrl(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverIdentifier") serverId: String,
        @Path("backupIdentifier") backupId: String,
    ): Response<SignedUrl>

    @Streaming
    @GET
    fun downloadServerBackup(
//        @Header("Authorization") authorization: String, // not sure if this requires auth or not
        @Url fileUrl: String, // the url returned by getServerBackupDownloadUrl's response
    ): Response<ResponseBody>

    @POST("servers/{identifier}/backups")
    fun createServerBackup(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("identifier") serverId: String,
        @Body data: CreateServerBackupBody
    ): Response<ServerBackup>

    @POST("servers/{serverIdentifier}/backups/{backupIdentifier}/restore")
    fun restoreServerBackup(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverIdentifier") serverId: String,
        @Path("backupIdentifier") backupId: String,
        @Body data: RestoreServerBackupBody
    )

    @POST("servers/{serverIdentifier}/backups/{backupIdentifier}/lock")
    fun toggleServerBackupLock(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverIdentifier") serverId: String,
        @Path("backupIdentifier") backupId: String,
    ): Response<ServerBackup>

    @GET("servers/{serverIdentifier}/backups/{backupIdentifier}")
    fun deleteServerBackup(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("serverIdentifier") serverId: String,
        @Path("backupIdentifier") backupId: String,
    )

    @GET("servers/{identifier}/startup")
    fun getServerStartupVariables(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("identifier") serverId: String,
    ): Response<GetServerStartupVariablesResponse>

    @PUT("servers/{identifier}/startup/variable")
    fun updateServerStartupVariable(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("identifier") serverId: String,
        @Body data: UpdateServerStartupVariableBody
    ): Response<ServerEggVariable>

    @POST("servers/{identifier}/settings/rename")
    fun renameServer(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("identifier") serverId: String,
        @Body data: RenameServerBody
    )

    @POST("servers/{identifier}/settings/reinstall")
    fun reinstallServer(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("identifier") serverId: String,
    )

    @PUT("servers/{identifier}/settings/docker-image")
    fun updateServerDockerImage(
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "Application/vnd.pterodactyl.v1+json",
        @Header("Content-Type") contentType: String = "application/json",
        @Path("identifier") serverId: String,
        @Body data: UpdateServerDockerImageBody
    )
}
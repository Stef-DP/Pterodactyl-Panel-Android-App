package com.stefdp.pterodactylpanel.network.application.models.responses

import com.google.gson.annotations.SerializedName

data class GetNodeConfigurationResponse(
    val debug: Boolean,
    val uuid: String,
    @SerializedName("token_id") val tokenId: String,
    val token: String,
    val api: GetNodeConfigurationResponseApi,
    val system: GetNodeConfigurationResponseSystem,
    @SerializedName("allowed_mounts") val allowedMounts: List<String>,
    val remote: String
)

data class GetNodeConfigurationResponseApi(
    val host: String,
    val port: Int,
    val ssl: GetNodeConfigurationResponseApiSSL,
    @SerializedName("upload_limit") val uploadLimit: Long
)

data class GetNodeConfigurationResponseApiSSL(
    val enabled: Boolean,
    val cert: String? = null,
    val key: String? = null
)

data class GetNodeConfigurationResponseSystem(
    val data: String,
    val sftp: GetNodeConfigurationResponseSystemSFTP,
)

data class GetNodeConfigurationResponseSystemSFTP(
    @SerializedName("bind_port") val bindPort: Int
)
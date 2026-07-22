package com.stefdp.pterodactylpanel.network.application.models.responses

import com.google.gson.annotations.SerializedName

data class GetNodeConfigurationResponse(
    val debug: Boolean,
    val uuid: String,
    @SerializedName("token_id") val tokenId: String,
    val token: String,
    val api: Api,
    val system: System,
    @SerializedName("allowed_mounts") val allowedMounts: List<String>,
    val remote: String
) {
    data class Api(
        val host: String,
        val port: Int,
        val ssl: Ssl,
        @SerializedName("upload_limit") val uploadLimit: Long
    ) {
        data class Ssl(
            val enabled: Boolean,
            val cert: String? = null,
            val key: String? = null
        )
    }

    data class System(
        val data: String,
        val sftp: Sftp,
    ) {
        data class Sftp(
            @SerializedName("bind_port") val bindPort: Int
        )
    }
}
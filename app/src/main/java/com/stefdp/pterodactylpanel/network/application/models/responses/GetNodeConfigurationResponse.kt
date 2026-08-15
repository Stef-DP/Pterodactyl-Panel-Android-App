package com.stefdp.pterodactylpanel.network.application.models.responses

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
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
    @Serializable
    data class Api(
        val host: String,
        val port: Int,
        val ssl: Ssl,
        @SerializedName("upload_limit") val uploadLimit: Long
    ) {
        @Serializable
        data class Ssl(
            val enabled: Boolean,
            val cert: String? = null,
            val key: String? = null
        )
    }

    @Serializable
    data class System(
        val data: String,
        val sftp: Sftp,
    ) {
        @Serializable
        data class Sftp(
            @SerializedName("bind_port") val bindPort: Int
        )
    }
}
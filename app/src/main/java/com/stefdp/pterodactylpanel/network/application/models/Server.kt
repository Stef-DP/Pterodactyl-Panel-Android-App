package com.stefdp.pterodactylpanel.network.application.models

import com.google.gson.annotations.SerializedName

data class ApplicationServer(
    val `object`: String = "server",
    val attributes: ApplicationServerAttributes
)

data class ApplicationServerAttributes(
    val id: Long,
    @SerializedName("external_id") val externalId: String? = null,
    val uuid: String,
    val identifier: String,
    val name: String,
    val description: String? = null,
    val status: String?,
    val suspended: Boolean,
    val limits: ApplicationServerAttributesLimits,
    @SerializedName("feature_limits") val featureLimits: ApplicationServerAttributesFeatureLimits,
    val user: Long,
    val node: Long,
    val allocation: Long,
    val nest: Long,
    val egg: Long,
    val container: ApplicationServerAttributesContainer,
    @SerializedName("updated_at") val updatedAt: String? = null,
    @SerializedName("created_at") val createdAt: String,
)

data class ApplicationServerAttributesLimits(
    val memory: Long,
    val swap: Long,
    val disk: Long,
    val io: Long,
    val cpu: Long,
    val threads: Long? = null,
    @SerializedName("oom_disabled") val oomDisabled: Boolean
)

data class ApplicationServerAttributesFeatureLimits(
    val databases: Long,
    val allocations: Long,
    val backups: Long
)

data class ApplicationServerAttributesContainer(
    @SerializedName("startup_command") val startupCommand: String,
    val image: String,
    val installed: Int,
    val environment: Map<String, String>
)
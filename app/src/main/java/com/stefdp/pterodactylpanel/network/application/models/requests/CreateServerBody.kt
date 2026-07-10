package com.stefdp.pterodactylpanel.network.application.models.requests

import com.google.gson.annotations.SerializedName

data class CreateServerBody(
    @SerializedName("docker_image") val dockerImage: String,
    val egg: Long,
    val environment: Map<String, Any?>,
    @SerializedName("feature_limits") val featureLimits: CreateServerBodyFeatureLimits,
    val limits: CreateServerBodyLimits,
    val allocation: CreateServerBodyAllocation,
    val name: String,
    val startup: String,
    val user: Long,
    @SerializedName("external_id") val externalId: String? = null,
    val description: String? = null,
    @SerializedName("skip_scripts") val skipEggInstallScript: Boolean? = null,
    @SerializedName("oom_disabled") val oomDisabled: Boolean? = null, // not sure if this goes here or in limits, i don't get it from the docs
    val deploy: CreateServerBodyDeploy? = null,
    @SerializedName("start_on_completion") val startOnCompletion: Boolean? = null
)

data class CreateServerBodyFeatureLimits(
    val allocations: Long,
    val backups: Long,
    val databases: Long
)

data class CreateServerBodyLimits(
    val cpu: Long,
    val disk: Long,
    val io: Long,
    val memory: Long,
    val swap: Long,
    val threads: String? = null,
//    @SerializedName("oom_disabled") val oomDisabled: Boolean,
)

data class CreateServerBodyAllocation(
    val default: Long,
    val additional: List<Long> = emptyList()
)

data class CreateServerBodyDeploy(
    val locations: List<Long> = emptyList(),
    @SerializedName("dedicated_ip") val dedicatedIp: Boolean,
    @SerializedName("port_range") val portRange: List<String> = emptyList()
)
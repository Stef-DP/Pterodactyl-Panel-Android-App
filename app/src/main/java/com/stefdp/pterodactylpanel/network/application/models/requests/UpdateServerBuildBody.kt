package com.stefdp.pterodactylpanel.network.application.models.requests

import com.google.gson.annotations.SerializedName

data class UpdateServerBuildBody(
    val allocation: Long? = null,
    @SerializedName("feature_limits") val featureLimits: UpdateServerBuildBodyFeatureLimits? = null,
    @SerializedName("add_allocations") val addAllocations: List<Long>? = null,
//    val limits: UpdateServerBuildBodyLimits? = null,
    @SerializedName("oom_disabled") val oomDisabled: Boolean? = null,
    @SerializedName("remove_allocations") val removeAllocations: List<Long>? = null,
    val cpu: Long? = null,
    val disk: Long? = null,
    val io: Long? = null,
    val memory: Long? = null,
    val swap: Long? = null,
    val threads: String? = null
)

data class UpdateServerBuildBodyFeatureLimits(
    val allocations: Long,
    val backups: Long,
    val databases: Long
)

//data class UpdateServerBuildBodyLimits(
//    val cpu: Long,
//    val disk: Long,
//    val io: Long,
//    val memory: Long,
//    val swap: Long,
//    val threads: String? = null,
////    @SerializedName("oom_disabled") val oomDisabled: Boolean? = null,
//)
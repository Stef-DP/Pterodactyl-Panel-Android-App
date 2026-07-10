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
    val relationships: ApplicationServerRelationships? = null
)

data class ApplicationServerAttributesLimits(
    val memory: Long,
    val swap: Long,
    val disk: Long,
    val io: Long,
    val cpu: Long,
    val threads: String? = null,
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
    val environment: Map<String, Any?>
)

data class ApplicationServerRelationships(
    val allocations: ApplicationServerRelationshipsAllocations? = null,
    val user: ApplicationUser? = null,
    val subusers: ApplicationServerRelationshipsSubusers? = null,
    val nest: ApplicationNest? = null,
    val egg: ApplicationEgg? = null,
    val variables: ApplicationEggRelationshipsVariables? = null,
    val location: ApplicationLocation? = null,
    val node: ApplicationNode? = null,
    val databases: ApplicationServerRelationshipsDatabases? = null,
)

data class ApplicationServerRelationshipsAllocations(
    val `object`: String = "list",
    val data: List<ApplicationAllocation>
)

data class ApplicationServerRelationshipsSubusers(
    val `object`: String = "list",
    val data: List<ApplicationServerSubuser>
)

data class ApplicationEggRelationshipsVariables(
    val `object`: String = "list",
    val data: List<ApplicationServerVariable>
)

data class ApplicationServerRelationshipsDatabases(
    val `object`: String = "list",
    val data: List<ApplicationServerDatabase>
)
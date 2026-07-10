package com.stefdp.pterodactylpanel.network.application.models

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

data class ApplicationEgg(
    val `object`: String = "egg",
    val attributes: ApplicationEggAttributes
)

data class ApplicationEggAttributes(
    val id: Long,
    val uuid: String,
    val name: String,
    val nest: Long,
    val author: String,
    val description: String,
    @SerializedName("docker_image") val dockerImage: String,
    @SerializedName("docker_images") val dockerImages: Map<String, String>,
    val config: ApplicationEggConfig,
    val startup: String,
    val script: ApplicationEggScript,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String? = null,
    val relationships: ApplicationEggRelationships? = null
)

data class ApplicationEggConfig(
    val files: Map<String, ApplicationEggConfigFile>,
    val startup: ApplicationEggConfigStartup,
    val stop: String,
    val logs: JsonElement, // "[]" or "{custom: Boolean, location: String}"
    @SerializedName("file_denylist") val fileDenylist: List<String>,
    val extends: String? = null
)

data class ApplicationEggConfigFile(
    val parses: String,
    val find: Map<String, Any?>
)

data class ApplicationEggConfigStartup(
    val done: String,
    val userInteraction: List<String>,
)

data class ApplicationEggScript(
    val privileged: Boolean,
    val install: String,
    val entry: String,
    val container: String,
    val extends: String? = null
)

data class ApplicationEggRelationships(
    val nest: ApplicationNest? = null,
    val servers: ApplicationEggRelationshipsServers? = null,
    val variables: ApplicationEggRelationshipsVariables? = null,
    val config: ApplicationEggRelationshipsNull? = null,
    val script: ApplicationEggRelationshipsNull? = null
)

data class ApplicationEggRelationshipsServers(
    val `object`: String = "list",
    val data: List<ApplicationServer>
)

data class ApplicationEggRelationshipsVariables(
    val `object`: String = "list",
    val data: List<ApplicationServerVariable>
)

// idk why this exists tbh
data class ApplicationEggRelationshipsNull(
    val `object`: String = "null_resource",
    val attributes: Any? = null
)
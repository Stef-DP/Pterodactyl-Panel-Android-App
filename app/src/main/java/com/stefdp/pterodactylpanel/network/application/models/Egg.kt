package com.stefdp.pterodactylpanel.network.application.models

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

data class ApplicationEgg(
    val `object`: String = "egg",
    val attributes: Attributes
) {
    data class Attributes(
        val id: Long,
        val uuid: String,
        val name: String,
        val nest: Long,
        val author: String,
        val description: String,
        @SerializedName("docker_image") val dockerImage: String,
        @SerializedName("docker_images") val dockerImages: Map<String, String>,
        val config: Config,
        val startup: String,
        val script: Script,
        @SerializedName("created_at") val createdAt: String,
        @SerializedName("updated_at") val updatedAt: String? = null,
        val relationships: Relationships? = null
    ) {
        data class Config(
            val files: Map<String, File>,
            val startup: Startup,
            val stop: String,
            val logs: JsonElement, // "[]" or "{custom: Boolean, location: String}"
            @SerializedName("file_denylist") val fileDenylist: List<String>,
            val extends: String? = null
        ) {
            data class File(
                val parses: String,
                val find: Map<String, Any?>
            )

            data class Startup(
                val done: String,
                val userInteraction: List<String>,
            )
        }

        data class Script(
            val privileged: Boolean,
            val install: String,
            val entry: String,
            val container: String,
            val extends: String? = null
        )

        data class Relationships(
            val nest: ApplicationNest? = null,
            val servers: Servers? = null,
            val variables: Variables? = null,
            val config: Null? = null,
            val script: Null? = null
        ) {
            data class Servers(
                val `object`: String = "list",
                val data: List<ApplicationServer>
            )

            data class Variables(
                val `object`: String = "list",
                val data: List<ApplicationServerVariable>
            )

            // idk why this exists tbh
            data class Null(
                val `object`: String = "null_resource",
                val attributes: Any? = null
            )
        }
    }
}
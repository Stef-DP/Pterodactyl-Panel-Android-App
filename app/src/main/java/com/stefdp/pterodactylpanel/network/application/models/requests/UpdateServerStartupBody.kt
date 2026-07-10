package com.stefdp.pterodactylpanel.network.application.models.requests

import com.google.gson.annotations.SerializedName

data class UpdateServerStartupBody(
    val startup: String? = null,
    val environment: Map<String, Any?>? = null,
    val egg: Long? = null,
    val image: String? = null,
    @SerializedName("skip_scripts") val skipEggInstallScript: Boolean? = null
)

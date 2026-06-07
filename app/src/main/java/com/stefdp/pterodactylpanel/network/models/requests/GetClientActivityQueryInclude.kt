package com.stefdp.pterodactylpanel.network.models.requests

import com.google.gson.annotations.SerializedName

enum class GetClientActivityQueryInclude(val value: String) {
    @SerializedName("actor")
    ACTOR("actor");

    override fun toString(): String = value
}
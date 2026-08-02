package com.stefdp.pterodactylpanel.network.client.models.requests

import com.google.gson.annotations.SerializedName

enum class GetServerActivityQueryInclude(val value: String) {
    @SerializedName("actor")
    ACTOR("actor");

    override fun toString(): String = value
}
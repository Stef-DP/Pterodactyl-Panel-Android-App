package com.stefdp.pterodactylpanel.network.client.models.requests

import com.google.gson.annotations.SerializedName

enum class GetAccountActivityQueryInclude(val value: String) {
    @SerializedName("actor")
    ACTOR("actor");

    override fun toString(): String = value
}
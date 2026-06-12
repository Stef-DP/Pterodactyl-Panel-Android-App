package com.stefdp.pterodactylpanel.network.client.models.requests

import com.google.gson.annotations.SerializedName

enum class GetClientServersQueryInclude(val value: String) {
    @SerializedName("egg")
    EGG("egg"),

    @SerializedName("subusers")
    SUBUSERS("subusers");

    override fun toString(): String = value
}
package com.stefdp.pterodactylpanel.network.client.models

import com.google.gson.annotations.SerializedName

enum class ServerStatus(val value: String) {
    @SerializedName("suspended")
    SUSPENDED("suspended");

    override fun toString(): String = value
}
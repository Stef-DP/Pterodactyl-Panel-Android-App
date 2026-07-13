package com.stefdp.pterodactylpanel.network.client.models.requests

import com.google.gson.annotations.SerializedName

enum class ListServerDatabasesQueryInclude(val value: String) {
    @SerializedName("password")
    PASSWORD("password");

    override fun toString(): String = value
}
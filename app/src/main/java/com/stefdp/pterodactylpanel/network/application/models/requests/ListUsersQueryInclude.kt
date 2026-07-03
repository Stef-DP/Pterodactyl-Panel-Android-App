package com.stefdp.pterodactylpanel.network.application.models.requests

import com.google.gson.annotations.SerializedName

enum class ListUsersQueryInclude(val value: String) {
    @SerializedName("servers")
    SERVERS("servers");

    override fun toString(): String = value
}
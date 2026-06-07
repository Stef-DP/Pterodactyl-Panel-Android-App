package com.stefdp.pterodactylpanel.network.models.requests

import com.google.gson.annotations.SerializedName

enum class GetClientServersQueryType(val value: String) {
     @SerializedName("owner")
    OWNER("owner"),

    @SerializedName("admin")
    ADMIN("admin"),

    @SerializedName("admin-all")
    ADMIN_ALL("admin-all");

    override fun toString(): String = value
}
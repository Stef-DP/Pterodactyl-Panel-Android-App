package com.stefdp.pterodactylpanel.network.application.models.requests

import com.google.gson.annotations.SerializedName

enum class ListServersQuerySort(val value: String) {
    @SerializedName("id")
    ID_ASC("id"),

    @SerializedName("-id")
    ID_DESC("-id"),

    @SerializedName("uuid")
    UUID_ASC("uuid"),

    @SerializedName("-uuid")
    UUID_DESC("-uuid");

    override fun toString(): String = value
}

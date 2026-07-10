package com.stefdp.pterodactylpanel.network.application.models.requests

import com.google.gson.annotations.SerializedName

enum class ListLocationsQuerySort(val value: String) {
    @SerializedName("id")
    ID_ASC("id"),

    @SerializedName("-id")
    ID_DESC("-id");

    override fun toString(): String = value
}

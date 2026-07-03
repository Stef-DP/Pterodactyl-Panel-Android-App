package com.stefdp.pterodactylpanel.network.application.models.requests

import com.google.gson.annotations.SerializedName

enum class GetUsersQuerySort(val value: String) {
    @SerializedName("id")
    ID("id"),

    @SerializedName("uuid")
    UUID("uuid");

    override fun toString(): String = value
}
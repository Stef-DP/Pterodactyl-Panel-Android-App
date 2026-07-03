package com.stefdp.pterodactylpanel.network.application.models.requests

import com.google.gson.annotations.SerializedName

enum class ListNodesQuerySort(val value: String) {
    @SerializedName("id")
    ID("id"),

    @SerializedName("uuid")
    UUID("uuid"),

    @SerializedName("memory")
    MEMORY("memory"),

    @SerializedName("disk")
    DISK("disk");

    override fun toString(): String = value
}
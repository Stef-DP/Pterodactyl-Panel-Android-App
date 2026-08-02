package com.stefdp.pterodactylpanel.network.client.models.requests

import com.google.gson.annotations.SerializedName

enum class GetServerActivityQuerySort(val value: String) {
    @SerializedName("timestamp")
    TIMESTAMP("timestamp"),

    @SerializedName("-timestamp")
    TIMESTAMP_DESC("-timestamp");

    override fun toString(): String = value
}
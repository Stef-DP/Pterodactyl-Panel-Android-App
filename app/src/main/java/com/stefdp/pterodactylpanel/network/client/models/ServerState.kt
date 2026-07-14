package com.stefdp.pterodactylpanel.network.client.models

import com.google.gson.annotations.SerializedName

enum class ServerState(val value: String) {
    @SerializedName("offline")
    OFFLINE("offline"),

    @SerializedName("running")
    RUNNING("running"),

    @SerializedName("starting")
    STARTING("starting"),

    @SerializedName("stopping")
    STOPPING("stopping"),

    @SerializedName("installing")
    INSTALLING("installing"),

    @SerializedName("suspended")
    SUSPENDED("suspended");

    override fun toString(): String = value
}
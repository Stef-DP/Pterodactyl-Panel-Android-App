package com.stefdp.pterodactylpanel.network.websocket.models

import com.google.gson.annotations.SerializedName

enum class WSEvents(val value: String) {
    // outgoing
    @SerializedName("auth")
    AUTH("auth"),

    @SerializedName("send stats")
    SEND_STATS("send stats"),

    @SerializedName("send logs")
    SEND_LOGS("send logs"),

    @SerializedName("set state")
    SET_STATE("set state"),

    @SerializedName("send command")
    SEND_COMMAND("send command"),

    // incoming
    @SerializedName("auth success")
    AUTH_SUCCESS("auth success"),

    @SerializedName("status")
    STATUS("status"),

    @SerializedName("console output")
    CONSOLE_OUTPUT("console output"),

    @SerializedName("stats")
    STATS("stats"),

    @SerializedName("token expiring")
    TOKEN_EXPIRING("token expiring"),

    @SerializedName("token expired")
    TOKEN_EXPIRED("token expired");

    override fun toString(): String = value
}

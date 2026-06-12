package com.stefdp.pterodactylpanel.network.client.models

import com.google.gson.annotations.SerializedName

enum class ServerPowerSignal(val value: String) {
    @SerializedName("start")
    START("start"),

    @SerializedName("stop")
    STOP("stop"),

    @SerializedName("restart")
    RESTART("restart"),

    @SerializedName("kill")
    KILL("kill");

    override fun toString(): String = value
}
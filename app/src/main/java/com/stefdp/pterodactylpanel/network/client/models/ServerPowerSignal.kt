package com.stefdp.pterodactylpanel.network.client.models

import com.google.gson.annotations.SerializedName

enum class ServerPowerSignal(
    val value: String,
    val uiName: String
) {
    @SerializedName("start")
    START(
        value = "start",
        uiName = "Start the server"
    ),

    @SerializedName("stop")
    STOP(
        value = "stop",
        uiName = "Stop the server"
    ),

    @SerializedName("restart")
    RESTART(
        value = "restart",
        uiName = "Restart the server"
    ),

    @SerializedName("kill")
    KILL(
        value = "kill",
        uiName = "Terminate the server"
    );

    override fun toString(): String = value
}
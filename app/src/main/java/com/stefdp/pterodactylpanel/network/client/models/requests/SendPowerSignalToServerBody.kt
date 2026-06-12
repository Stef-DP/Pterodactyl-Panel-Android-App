package com.stefdp.pterodactylpanel.network.client.models.requests

import com.stefdp.pterodactylpanel.network.client.models.ServerPowerSignal

data class SendPowerSignalToServerBody(
    val signal: ServerPowerSignal
)

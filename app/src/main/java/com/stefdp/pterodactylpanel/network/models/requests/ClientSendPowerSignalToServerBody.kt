package com.stefdp.pterodactylpanel.network.models.requests

import com.stefdp.pterodactylpanel.network.models.ServerPowerSignal

data class ClientSendPowerSignalToServerBody(
    val signal: ServerPowerSignal
)

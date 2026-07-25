package com.stefdp.pterodactylpanel.network.client.models.requests

import com.stefdp.pterodactylpanel.network.client.models.ServerSubuser

data class CreateServerSubuserBody(
    val email: String,
    val permissions: List<ServerSubuser.Permissions>
)

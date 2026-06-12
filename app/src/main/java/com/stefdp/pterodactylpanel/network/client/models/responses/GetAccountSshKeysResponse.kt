package com.stefdp.pterodactylpanel.network.client.models.responses

import com.stefdp.pterodactylpanel.network.client.models.SshKey

data class GetAccountSshKeysResponse(
    val `object`: String = "list",
    val data: List<SshKey>
)

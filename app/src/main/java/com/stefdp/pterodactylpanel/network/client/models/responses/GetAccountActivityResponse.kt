package com.stefdp.pterodactylpanel.network.client.models.responses

import com.stefdp.pterodactylpanel.network.client.models.AccountActivity

data class GetAccountActivityResponse(
    val `object`: String = "list",
    val data: List<AccountActivity>
)

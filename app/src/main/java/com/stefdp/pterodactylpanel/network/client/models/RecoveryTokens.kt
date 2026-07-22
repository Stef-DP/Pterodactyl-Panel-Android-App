package com.stefdp.pterodactylpanel.network.client.models

data class RecoveryCodes(
    val `object`: String = "recovery_tokens",
    val attributes: Attributes
) {
    data class Attributes(
        val tokens: List<String>
    )
}
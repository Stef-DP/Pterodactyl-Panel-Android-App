package com.stefdp.pterodactylpanel.network.client.models

data class RecoveryCodes(
    val `object`: String = "recovery_tokens",
    val attributes: RecoveryCodesAttributes
)

data class RecoveryCodesAttributes(
    val tokens: List<String>
)
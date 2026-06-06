package com.stefdp.pterodactylpanel.network.models

import com.google.gson.annotations.SerializedName

data class RecoveryCodes(
    @SerializedName("object") val objectType: String = "recovery_tokens",
    val attributes: RecoveryCodesAttributes
)

data class RecoveryCodesAttributes(
    val tokens: List<String>
)
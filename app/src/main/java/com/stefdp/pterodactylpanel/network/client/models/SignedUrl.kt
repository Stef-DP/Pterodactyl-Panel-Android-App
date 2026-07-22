package com.stefdp.pterodactylpanel.network.client.models

data class SignedUrl(
    val `object`: String = "signed_url",
    val attributes: Attributes
) {
    data class Attributes(
        val url: String,
    )
}
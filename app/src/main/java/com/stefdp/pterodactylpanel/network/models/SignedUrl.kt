package com.stefdp.pterodactylpanel.network.models

import com.google.gson.annotations.SerializedName

data class SignedUrl(
    val `object`: String = "signed_url",
    val attributes: SignedUrlAttributes
)

data class SignedUrlAttributes(
    val url: String,
)
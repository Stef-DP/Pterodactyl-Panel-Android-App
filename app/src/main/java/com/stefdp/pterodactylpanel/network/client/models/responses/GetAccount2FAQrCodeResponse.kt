package com.stefdp.pterodactylpanel.network.client.models.responses

import com.google.gson.annotations.SerializedName

data class GetAccount2FAQrCodeResponse(
    @SerializedName("image_url_data") val imageUrlData: String,
)
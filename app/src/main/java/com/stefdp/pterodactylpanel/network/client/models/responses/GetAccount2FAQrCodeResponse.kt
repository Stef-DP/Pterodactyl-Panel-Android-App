package com.stefdp.pterodactylpanel.network.client.models.responses

import com.google.gson.annotations.SerializedName

data class GetAccount2FAQrCodeResponse(
    val data: Data
) {
    data class Data(
        @SerializedName("image_url_data") val imageUrlData: String,
        val secret: String
    )
}
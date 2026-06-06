package com.stefdp.pterodactylpanel.network.models.responses

import com.google.gson.annotations.SerializedName

data class GetClient2FAQrCodeResponse(
    @SerializedName("image_url_data") val imageUrlData: String,
)
package com.stefdp.pterodactylpanel.network.client.models.requests

import com.google.gson.annotations.SerializedName

data class CreateServerScheduleBody(
    @SerializedName("day_of_month") val dayOfMonth: String,
    @SerializedName("day_of_week") val dayOfWeek: String,
    val hour: String,
    @SerializedName("is_active") val isActive: Boolean = true,
    val minute: String,
    val name: String,
    val month: String,
    @SerializedName("only_when_online") val onlyWhenOnline: Boolean = false,
)

package com.stefdp.pterodactylpanel.network.client.models.requests

import com.google.gson.annotations.SerializedName

enum class GetServersQueryInclude(val value: String) {
    @SerializedName("egg")
    EGG("egg"),

    @SerializedName("subusers")
    SUBUSERS("subusers");

    override fun toString(): String = value

    companion object {
        fun toQueryString(vararg includes: GetServersQueryInclude): String {
            return includes.joinToString(",") { it.value }
        }

        fun toQueryString(includes: Collection<GetServersQueryInclude>): String {
            return includes.joinToString(",") { it.value }
        }
    }
}
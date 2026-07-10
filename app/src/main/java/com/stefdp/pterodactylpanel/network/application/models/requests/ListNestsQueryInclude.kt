package com.stefdp.pterodactylpanel.network.application.models.requests

import com.google.gson.annotations.SerializedName

enum class ListNestsQueryInclude(val value: String) {
    @SerializedName("eggs")
    EGGS("eggs"),

    @SerializedName("servers")
    SERVERS("servers");

    override fun toString(): String = value

    companion object {
        fun toQueryString(vararg includes: ListNestsQueryInclude): String {
            return includes.joinToString(",") { it.value }
        }

        fun toQueryString(includes: Collection<ListNestsQueryInclude>): String {
            return includes.joinToString(",") { it.value }
        }
    }
}
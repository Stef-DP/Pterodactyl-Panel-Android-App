package com.stefdp.pterodactylpanel.network.application.models.requests

import com.google.gson.annotations.SerializedName

enum class ListNestEggsQueryInclude(val value: String) {
    @SerializedName("nest")
    NEST("nest"),

    @SerializedName("servers")
    SERVERS("servers"),

    @SerializedName("config")
    CONFIG("config"),

    @SerializedName("script")
    SCRIPT("script"),

    @SerializedName("variables")
    VARIABLES("variables");

    override fun toString(): String = value

    companion object {
        fun toQueryString(vararg includes: ListNestEggsQueryInclude): String {
            return includes.joinToString(",") { it.value }
        }

        fun toQueryString(includes: Collection<ListNestEggsQueryInclude>): String {
            return includes.joinToString(",") { it.value }
        }
    }
}
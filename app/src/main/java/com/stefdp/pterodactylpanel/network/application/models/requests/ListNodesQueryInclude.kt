package com.stefdp.pterodactylpanel.network.application.models.requests

import com.google.gson.annotations.SerializedName

enum class ListNodesQueryInclude(val value: String) {
    @SerializedName("servers")
    SERVERS("servers"),

    @SerializedName("allocations")
    ALLOCATIONS("allocations"),

    @SerializedName("location")
    LOCATION("location");

    override fun toString(): String = value

    companion object {
        fun toQueryString(vararg includes: ListNodesQueryInclude): String {
            return includes.joinToString(",") { it.value }
        }

        fun toQueryString(includes: Collection<ListNodesQueryInclude>): String {
            return includes.joinToString(",") { it.value }
        }
    }
}
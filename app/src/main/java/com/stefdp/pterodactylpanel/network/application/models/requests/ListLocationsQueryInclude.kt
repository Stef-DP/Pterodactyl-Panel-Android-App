package com.stefdp.pterodactylpanel.network.application.models.requests

import com.google.gson.annotations.SerializedName

enum class ListLocationsQueryInclude(val value: String) {
    @SerializedName("nodes")
    NODES("nodes"),

    @SerializedName("servers")
    SERVERS("servers");

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
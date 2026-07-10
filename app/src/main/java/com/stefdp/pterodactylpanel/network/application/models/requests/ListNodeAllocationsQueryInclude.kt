package com.stefdp.pterodactylpanel.network.application.models.requests

import com.google.gson.annotations.SerializedName

enum class ListNodeAllocationsQueryInclude(val value: String) {
    @SerializedName("node")
    NODE("node"),

    @SerializedName("server")
    SERVER("server");

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
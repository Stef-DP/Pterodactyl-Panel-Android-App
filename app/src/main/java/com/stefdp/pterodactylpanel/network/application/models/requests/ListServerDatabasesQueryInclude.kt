package com.stefdp.pterodactylpanel.network.application.models.requests

import com.google.gson.annotations.SerializedName

enum class ListServerDatabasesQueryInclude(val value: String) {
    @SerializedName("password")
    PASSWORD("password"),

    @SerializedName("host")
    HOST("host");

    override fun toString(): String = value

    companion object {
        fun toQueryString(vararg includes: ListServerDatabasesQueryInclude): String {
            return includes.joinToString(",") { it.value }
        }

        fun toQueryString(includes: Collection<ListServerDatabasesQueryInclude>): String {
            return includes.joinToString(",") { it.value }
        }
    }
}
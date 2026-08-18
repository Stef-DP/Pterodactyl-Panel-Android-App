package com.stefdp.pterodactylpanel.network.application.models.requests

import com.stefdp.pterodactylpanel.network.models.QueryInclude

enum class ListServerDatabasesQueryInclude(
    override val value: String
) : QueryInclude {
    PASSWORD("password"),

    HOST("host");

    override fun toString(): String = value
}
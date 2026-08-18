package com.stefdp.pterodactylpanel.network.client.models.requests

import com.stefdp.pterodactylpanel.network.models.QueryInclude

enum class ListServersQueryInclude(
    override val value: String
) : QueryInclude {
    EGG("egg"),
    SUBUSERS("subusers");

    override fun toString(): String = value
}
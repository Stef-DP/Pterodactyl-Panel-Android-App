package com.stefdp.pterodactylpanel.network.client.models.requests

import com.stefdp.pterodactylpanel.network.models.QueryInclude

enum class GetAccountActivityQueryInclude(
    override val value: String
) : QueryInclude {
    ACTOR("actor");

    override fun toString(): String = value
}
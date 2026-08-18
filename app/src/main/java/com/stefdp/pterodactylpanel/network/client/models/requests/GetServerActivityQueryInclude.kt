package com.stefdp.pterodactylpanel.network.client.models.requests

import com.stefdp.pterodactylpanel.network.models.QueryInclude

enum class GetServerActivityQueryInclude(
    override val value: String
) : QueryInclude {
    ACTOR("actor");

    override fun toString(): String = value
}
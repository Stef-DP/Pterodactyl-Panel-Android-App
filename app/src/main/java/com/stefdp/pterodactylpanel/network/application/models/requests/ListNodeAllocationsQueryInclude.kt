package com.stefdp.pterodactylpanel.network.application.models.requests

import com.stefdp.pterodactylpanel.network.models.QueryInclude

enum class ListNodeAllocationsQueryInclude(
    override val value: String
) : QueryInclude {
    NODE("node"),
    SERVER("server");

    override fun toString(): String = value
}
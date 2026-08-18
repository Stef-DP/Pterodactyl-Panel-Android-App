package com.stefdp.pterodactylpanel.network.application.models.requests

import com.stefdp.pterodactylpanel.network.models.QueryInclude

enum class ListServersQueryInclude(
    override val value: String
) : QueryInclude {
    ALLOCATIONS("allocations"),
    USER("user"),
    SUBUSERS("subusers"),
    NEST("nest"),
    EGG("egg"),
    VARIABLES("variables"),
    LOCATION("location"),
    NODE("node"),
    DATABASES("databases");
//    TRANSFER("transfer");

    override fun toString(): String = value
}
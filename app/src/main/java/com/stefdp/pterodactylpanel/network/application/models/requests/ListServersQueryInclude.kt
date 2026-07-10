package com.stefdp.pterodactylpanel.network.application.models.requests

import com.google.gson.annotations.SerializedName

enum class ListServersQueryInclude(val value: String) {
    @SerializedName("allocations")
    ALLOCATIONS("allocations"),

    @SerializedName("user")
    USER("user"),

    @SerializedName("subusers")
    SUBUSERS("subusers"),

    @SerializedName("nest")
    NEST("nest"),

    @SerializedName("egg")
    EGG("egg"),

    @SerializedName("variables")
    VARIABLES("variables"),

    @SerializedName("location")
    LOCATION("location"),

    @SerializedName("node")
    NODE("node"),

    @SerializedName("databases")
    DATABASES("databases");

//    @SerializedName("transfer")
//    TRANSFER("transfer");

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
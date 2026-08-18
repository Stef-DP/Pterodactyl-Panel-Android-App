package com.stefdp.pterodactylpanel.network.models

interface QueryInclude {
    val value: String
}

data class QueryIncludePath(
    override val value: String
) : QueryInclude {
    override fun toString(): String = value
}

operator fun QueryInclude.plus(child: QueryInclude): QueryIncludePath {
    return QueryIncludePath("$value.${child.value}")
}

fun Collection<QueryInclude>.toQueryString(): String {
    return joinToString(",") { it.value }
}
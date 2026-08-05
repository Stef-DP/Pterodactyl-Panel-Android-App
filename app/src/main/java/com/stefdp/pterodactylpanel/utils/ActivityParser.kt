package com.stefdp.pterodactylpanel.utils

fun parseActivityVariables(
    input: String,
    properties: Any?,
    ignoreMap: Boolean = false
): String {
    if (properties !is Map<*, *> && !ignoreMap) return input

    return VariableRegex.replace(input) { matchResult ->
        val path = matchResult.groupValues[1].trim()
        val resolvedValue = resolvePath(path, properties)

        resolvedValue?.formatToString() ?: matchResult.value
    }
}

private fun Any.formatToString(): String {
    return when (this) {
        is Double -> if (this % 1.0 == 0.0) this.toLong().toString() else this.toString()
        is Float -> if (this % 1.0f == 0.0f) this.toLong().toString() else this.toString()
        else -> this.toString()
    }
}

private fun resolvePath(
    path: String,
    root: Any?
): Any? {
    val keys = path.split(".")
    var current: Any? = root

    for (key in keys) {
        current = when (current) {
            is Map<*, *> -> current[key]

            is List<*> -> key.toIntOrNull()?.let { index ->
                current.getOrNull(index)
            }

            else -> return null
        }
    }

    return current
}
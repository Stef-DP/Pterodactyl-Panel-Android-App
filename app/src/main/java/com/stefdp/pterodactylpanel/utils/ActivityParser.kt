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

        resolvedValue?.toString() ?: matchResult.value
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
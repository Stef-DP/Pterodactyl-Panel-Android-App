package com.stefdp.pterodactylpanel.utils

import com.stefdp.pterodactylpanel.network.client.models.ServerSubuser

fun hasPermission(
    isServerOwner: Boolean,
    userPermissions: List<ServerSubuser.Permissions>,
    requiredPermissions: List<ServerSubuser.Permissions>?,
    all: Boolean = false
): Boolean {
    if (isServerOwner) return true
    if (requiredPermissions == null) return true
    if (ServerSubuser.Permissions.ADMIN in userPermissions) return true

    return if (all) {
        requiredPermissions.all { it in userPermissions }
    } else {
        requiredPermissions.any { it in userPermissions }
    }
}

fun hasPermission(
    isServerOwner: Boolean,
    userPermissions: List<ServerSubuser.Permissions>,
    requiredPermission: ServerSubuser.Permissions?,
): Boolean {
    if (isServerOwner) return true
    if (requiredPermission == null) return true
    if (ServerSubuser.Permissions.ADMIN in userPermissions) return true

    return requiredPermission in userPermissions
}
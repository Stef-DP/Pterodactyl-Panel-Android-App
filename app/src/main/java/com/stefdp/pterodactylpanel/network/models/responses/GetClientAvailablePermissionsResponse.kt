package com.stefdp.pterodactylpanel.network.models.responses

import com.google.gson.annotations.SerializedName

data class GetClientAvailablePermissionsResponse(
	val `object`: String = "system_permissions",
	val attributes: GetClientAvailablePermissionsAttributes,
)

data class GetClientAvailablePermissionsAttributes(
	val permissions: Map<String, GetClientAvailablePermissionsPermissionGroup>
)

// for the keys: https://old-api.redbanana.dev/docs/client-general/get-show-permissions
data class GetClientAvailablePermissionsPermissionGroup(
	val description: String,
	val keys: Map<String, String>
)


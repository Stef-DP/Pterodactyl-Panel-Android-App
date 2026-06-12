package com.stefdp.pterodactylpanel.network.client.models.responses

data class GetAvailablePermissionsResponse(
	val `object`: String = "system_permissions",
	val attributes: GetAvailablePermissionsAttributes,
)

data class GetAvailablePermissionsAttributes(
	val permissions: Map<String, GetAvailablePermissionsPermissionGroup>
)

// for the keys: https://old-api.redbanana.dev/docs/client-general/get-show-permissions
data class GetAvailablePermissionsPermissionGroup(
	val description: String,
	val keys: Map<String, String>
)


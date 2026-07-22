package com.stefdp.pterodactylpanel.network.client.models.responses

data class GetAvailablePermissionsResponse(
	val `object`: String = "system_permissions",
	val attributes: Attributes,
) {
	data class Attributes(
		val permissions: Map<String, PermissionGroup>
	) {
		// for the keys: https://pterodactyl-api.redbanana.dev/docs/client-general/get-show-permissions
		data class PermissionGroup(
			val description: String,
			val keys: Map<String, String>
		)
	}
}


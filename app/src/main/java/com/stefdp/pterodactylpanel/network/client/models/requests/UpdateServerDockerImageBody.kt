package com.stefdp.pterodactylpanel.network.client.models.requests

import com.google.gson.annotations.SerializedName

data class UpdateServerDockerImageBody(
    @SerializedName("docker_image") val dockerImage: String
)
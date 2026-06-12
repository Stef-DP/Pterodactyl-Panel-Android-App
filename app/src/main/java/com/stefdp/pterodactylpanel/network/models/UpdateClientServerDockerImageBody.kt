package com.stefdp.pterodactylpanel.network.models

import com.google.gson.annotations.SerializedName

data class UpdateClientServerDockerImageBody(
    @SerializedName("docker_image") val dockerImage: String
)

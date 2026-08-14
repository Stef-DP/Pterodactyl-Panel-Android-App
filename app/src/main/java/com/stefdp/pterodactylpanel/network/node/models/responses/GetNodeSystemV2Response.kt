package com.stefdp.pterodactylpanel.network.node.models.responses

import com.google.gson.annotations.SerializedName

data class GetNodeSystemV2Response(
    val version: String,
    val docker: Docker,
    val system: System
) {
    data class Docker(
        val version: String,
        val cgroup: Cgroup,
        val containers: Containers,
        val storage: Storage,
        val runc: Runc
    ) {
        data class Cgroup(
            val driver: String,
            val version: String
        )

        data class Containers(
            val total: Long,
            val running: Long,
            val paused: Long,
            val stopped: Long
        )

        data class Storage(
            val driver: String,
            val filesystem: String,
        )

        data class Runc(
            val version: String
        )
    }

    data class System(
        val architecture: String,
        @SerializedName("cpu_threads") val cpuThreads: Int,
        @SerializedName("memory_bytes") val memoryBytes: Long,
        @SerializedName("kernel_version") val kernelVersion: String,
        val os: String,
        @SerializedName("os_type") val osType: String,
    )
}
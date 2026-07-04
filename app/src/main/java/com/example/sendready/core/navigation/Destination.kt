package com.example.sendready.core.navigation

import kotlinx.serialization.Serializable

sealed interface Destination {
    
    @Serializable
    data object Home : Destination

    @Serializable
    data object Compress : Destination
    
    @Serializable
    data class Progress(val destinationId: String = "") : Destination

    @Serializable
    data class Finished(val videoId: String = "") : Destination

    @Serializable
    data object History : Destination

    @Serializable
    data object Settings : Destination
}

package com.sharfu.shaalevikasa

data class Contributor(
    val rank: Int,
    val name: String,
    val badge: String,
    val points: String,
    val avatarUrl: String? = null
)
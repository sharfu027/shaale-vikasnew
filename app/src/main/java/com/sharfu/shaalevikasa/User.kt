package com.sharfu.shaalevikasa

enum class Role {
    ADMIN, ALUMNI
}

data class User(
    val name: String,
    val email: String,
    val role: Role,
    val age: Int? = null,
    val gender: String? = null
)

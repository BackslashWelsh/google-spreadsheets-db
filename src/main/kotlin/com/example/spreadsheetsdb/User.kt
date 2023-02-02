package com.example.spreadsheetsdb

data class User(val username: String, val email: String, val dob: String, val password: String) {
    val sheet: List<String>
        get() = listOf(username, email, dob, password)
}

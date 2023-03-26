package com.devshadat.apprisefirebase.data

data class User(
    var userId: String? = null,
    var fullname: String? = null,
    var username: String? = null,
    var email: String? = null,
    var phone: String? = null,
    var password: String? = null,
    var timestamp: Map<String, String>? = null,
    var status: String? = null
)

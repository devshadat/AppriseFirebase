package com.devshadat.apprisefirebase.data

import java.text.SimpleDateFormat
import java.util.*

data class Users(
    var userId: String? = null,
    var fullname: String? = null,
    var username: String? = null,
    var email: String? = null,
    var phone: String? = null,
    var password: String? = null,
    var timestamp: Long? = null,
    var status: String? = null
)
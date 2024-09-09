package com.tuanhn.smartmovie.screen.loginscreen

import javax.crypto.SecretKey

data class User(
    val userName: String = "",
    var passWord: String = "",
    val email: String = "",
    var key: String = ""
)

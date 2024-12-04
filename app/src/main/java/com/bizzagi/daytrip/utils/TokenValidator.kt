package com.bizzagi.daytrip.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.exceptions.JWTVerificationException
import java.util.Date

object TokenValidator {
    fun isTokenValid(token: String): Boolean {
        return try {
            val jwt = JWT.decode(token)
            val expirationDate = jwt.expiresAt
            expirationDate != null && expirationDate.after(Date())
        } catch (e: JWTVerificationException) {
            false
        }
    }
}
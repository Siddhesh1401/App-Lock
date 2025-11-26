package com.applock.secure.util

import java.security.MessageDigest

object HashUtils {
    
    fun hashString(input: String): String {
        val bytes = input.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
    
    fun patternToString(pattern: List<Int>): String {
        return pattern.joinToString(",")
    }
    
    fun stringToPattern(patternString: String): List<Int> {
        return patternString.split(",").map { it.toInt() }
    }
}

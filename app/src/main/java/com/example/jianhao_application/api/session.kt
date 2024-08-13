package com.example.jianhao_application.api

import android.content.Context

fun getUserSession(context: Context): Pair<String?, String?> {
    val sharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
    val userId = sharedPreferences.getString("user_id", null)
    val token = sharedPreferences.getString("auth_token", null)
    return Pair(userId, token)
}

fun saveUserSession(context: Context, userId: String, token: String) {
    val sharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
    with(sharedPreferences.edit()) {
        putString("user_id", userId)
        putString("auth_token", token)
        apply()
    }
}


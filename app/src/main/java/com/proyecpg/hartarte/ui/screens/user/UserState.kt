package com.proyecpg.hartarte.ui.screens.user

data class UserState(
    val isUserEditSuccessful: Boolean = false,
    val userEditError: String? = null,
    val isLoading: Boolean = false
)
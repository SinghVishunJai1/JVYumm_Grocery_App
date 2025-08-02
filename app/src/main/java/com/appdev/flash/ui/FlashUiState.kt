package com.appdev.flash.ui

// This is a data class that stores the current state of the UI
// It contains all the data that we need to display or handle on the screen
data class FlashUiState(
    // When the user clicks on a photo or button, its message will be stored here
    val clickStatus: String = "Hello views model",

    // This indicates which category (or item) the user has selected
    val selectedCategory: Int = 0
)

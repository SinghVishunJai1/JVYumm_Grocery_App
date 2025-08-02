package com.appdev.flash.ui

import android.app.Activity
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

@Composable
fun NumberScreen(
    flashViewModel: FlashViewModel,
    callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
){

    // Observe phone number input from ViewModel state
    val phoneNumber by flashViewModel.phoneNumber.collectAsState()
    val context = LocalContext.current

    // Title text
    Text(
        text = "Login",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold
    )

    // Instruction text
    Text(
        text = "Enter your phone number to proceed",
        fontSize = 20.sp,
        modifier = Modifier.fillMaxWidth()
    )

    // Additional info about SMS verification
    Text(
        text = "This phone number will be used for the purpose of all communication. You shall receive an SMS with a code for verification",
        fontSize = 12.sp,
        color = Color(105,103,100)
    )

    // TextField for phone number input with number keyboard
    TextField(
        value = phoneNumber,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        ),
        onValueChange = {
            // Update phone number in ViewModel when input changes
            flashViewModel.setPhoneNumber(it)
        },
        label = {
            Text(text = "Your number")
        },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )

    // Button to send OTP to entered phone number
    Button(
        onClick = {
            // Build PhoneAuthOptions with phone number, timeout, activity and callbacks
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber("+91${phoneNumber}") // Add country code +91 (India)
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout for OTP validity
                .setActivity(context as Activity) // Activity context for callbacks
                .setCallbacks(callbacks) // Set verification callbacks
                .build()

            // Start phone number verification process
            PhoneAuthProvider.verifyPhoneNumber(options)

            // Show loading spinner
            flashViewModel.setLoading(true)
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = "Send OTP")
    }
}

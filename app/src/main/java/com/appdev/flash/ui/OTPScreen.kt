package com.appdev.flash.ui


import android.app.Activity
import android.content.Context
import android.text.format.DateUtils
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.font.FontWeight
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

// Composable function for OTP screen where user enters the OTP
@Composable
fun OtpScreen(
    otp: String, // The current OTP entered by user
    flashViewModel: FlashViewModel, // ViewModel holding state and logic
    callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks // Firebase OTP callbacks
) {
    val context = LocalContext.current  // Get current context for Toast messages
    val verificationId by flashViewModel.verificationId.collectAsState()  // Observe verification ID from ViewModel
    val ticks by flashViewModel.tick.collectAsState()  // Observe remaining seconds for resend OTP timer
    val phoneNumber by flashViewModel.phoneNumber.collectAsState()  // Observe phone number entered by user

    // Show OTP input boxes
    OtpTextBox(otp, flashViewModel = flashViewModel)

    // Button to verify the OTP
    Button(
        onClick = {
            if (otp.isEmpty()) {
                // Show message if OTP is empty
                Toast.makeText(context, "Please enter OTP", Toast.LENGTH_SHORT).show()
            } else {
                // Create Firebase credential with verification ID and OTP entered
                val credential = PhoneAuthProvider.getCredential(verificationId, otp)
                // Call sign in function to verify OTP and sign in user
                signInWithPhoneAuthCredential(
                    credential = credential,
                    context = context,
                    flashViewModel = flashViewModel
                )
            }
        },
        modifier = Modifier.fillMaxWidth()  // Button fills full width
    ) {
        Text(
            text = "Verify OTP"  // Button label
        )
    }

    // Text for Resend OTP with timer countdown
    Text(
        text = if (ticks == 0L) "Resend OTP" else "Resend OTP (${DateUtils.formatElapsedTime(ticks)})",
        color = Color(63, 81, 181, 255),
        fontWeight = if (ticks == 0L) FontWeight.Bold else FontWeight.Normal,
        modifier = Modifier.clickable {
            // If clicked and timer is zero, resend the OTP using Firebase
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber("+91${phoneNumber}") // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout duration
                .setActivity(context as Activity) // Current activity for callback binding
                .setCallbacks(callbacks) // Firebase callbacks
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)  // Send OTP again
        }
    )
}

// Composable for showing OTP input boxes (6 digits)
@Composable
fun OtpTextBox(otp: String, flashViewModel: FlashViewModel) {
    BasicTextField(
        value = otp,  // Current OTP value
        onValueChange = {
            flashViewModel.setOtp(it)  // Update OTP in ViewModel when user types
        },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true  // Single line input
    ) {
        Row(
            horizontalArrangement = Arrangement.Center
        ) {
            // Repeat 6 times to create 6 input boxes
            repeat(6) { index ->
                // Show character at current position or empty if none
                val number = when {
                    index >= otp.length -> ""
                    else -> otp[index].toString()
                }

                // Each digit with underline
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(4.dp)
                ) {
                    Text(text = number, fontSize = 32.sp) // Show digit
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(2.dp)
                            .background(Color.Gray) // Underline below digit
                    ) {
                    }
                }
            }
        }
    }
}

// Function to sign in user with Firebase using phone auth credential
private fun signInWithPhoneAuthCredential(
    credential: PhoneAuthCredential, // Credential created from OTP and verificationId
    context: Context, // Activity context for UI actions
    flashViewModel: FlashViewModel // ViewModel to update user info
) {
    auth.signInWithCredential(credential)
        .addOnCompleteListener(context as Activity) { task ->
            if (task.isSuccessful) {
                // If sign-in successful, show success message
                Toast.makeText(context, "Verification Successful", Toast.LENGTH_SHORT).show()
                val user = task.result?.user
                if (user != null) {
                    // Save signed-in user in ViewModel
                    flashViewModel.setUser(user)
                }
            } else {
                // If sign-in failed
                if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    // If OTP is wrong, show error message
                    Toast.makeText(
                        context,
                        "The OTP you have entered is Invalid. Please try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                // You can add more error handling here if needed
            }
        }
}

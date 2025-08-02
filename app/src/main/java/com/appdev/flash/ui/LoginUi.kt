package com.appdev.flash.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.appdev.flash.R
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

@Composable
fun LoginUi(flashViewModel: FlashViewModel){

    val context= LocalContext.current

    // Observe OTP value from ViewModel state
    val otp by flashViewModel.opt.collectAsState()

    // Observe verificationId from ViewModel state
    val verificationId by flashViewModel.verificationId.collectAsState()

    // Observe loading state for showing progress indicator
    val loading by flashViewModel.loading.collectAsState()

    // Callbacks for Firebase Phone Authentication
    val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        // Called when verification is completed automatically (instant verification)
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Toast.makeText(context, "Successfully verified", Toast.LENGTH_SHORT).show()
        }

        // Called when verification fails
        override fun onVerificationFailed(e: FirebaseException) {
            Log.e("LoginUi", "Verification failed", e)
            Toast.makeText(context, "Verification failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            flashViewModel.setLoading(false)
        }

        // Called when OTP is sent successfully
        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {
            // Save the verificationId in ViewModel to use later for OTP verification
            flashViewModel.setVerificationId(verificationId)
            Toast.makeText(context, "OTP Sent", Toast.LENGTH_SHORT).show()

            // Reset and start OTP timer
            flashViewModel.resetTimer()
            flashViewModel.runTimer()

            // Hide loading spinner
            flashViewModel.setLoading(false)
        }
    }

    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Login image at the top of screen
            Image(
                painter = painterResource(R.drawable.loginimg),
                contentDescription = "Login_image",
                modifier = Modifier
                    .padding(top = 50.dp, bottom = 10.dp)
                    .size(170.dp)
            )

            // Show phone number input screen if verificationId is empty,
            // otherwise show OTP input screen
            if (verificationId.isEmpty()) {
                NumberScreen(flashViewModel = flashViewModel, callbacks)
            } else {
                OtpScreen(otp = otp, flashViewModel = flashViewModel, callbacks = callbacks)
            }
        }

        // Show back button to allow user to go back to phone number input screen
        if(verificationId.isNotEmpty()){
            IconButton(onClick = {
                // Reset verificationId and OTP in ViewModel when back pressed
                flashViewModel.setVerificationId("")
                flashViewModel.setOtp("")
            }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        }

        // Show loading spinner overlay after OTP is sent but screen is still loading slowly
        if(loading){
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color(255, 255, 255, 190)) // translucent white overlay
            ) {
                CircularProgressIndicator()
                Text(text = "Loading")
            }
        }
    }
}

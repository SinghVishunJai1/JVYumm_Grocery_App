package com.appdev.flash.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.appdev.flash.R

// Composable to show a fullscreen offer/sale image screen with a blue background
@Composable
fun OfferScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Show sale offer image with padding and fixed size
        Image(
            painter = painterResource(R.drawable.banner),
            contentDescription = "Sale offer",
            modifier = Modifier.fillMaxSize()
        )
    }
}

/*
  Networking and Retrofit concepts explained:

  HTTP and REST
  -------------------
  - HTTP (Hyper Text Transfer Protocol) is a protocol which allows clients to send requests and receive responses from a server.
  - It doesn't store any memory but has methods like:
    * GET() to retrieve data from server
    * POST() to send data to a specific resource
    * PUT() to update an existing resource
    * DELETE() to delete a resource from the web server
  - Retrofit is a library that uses HTTP protocol to make network calls easier.

  REST (Representational State Transfer)
  -------------------
  - REST is an architectural style that defines how resources are defined and addressed.
  - RESTful APIs allow clients to fetch, update, delete, and create resources on server.
  - HTTP and REST together help smooth communication between client and server.

  Retrofit
  -------------------
  - Retrofit is an open-source library that simplifies HTTP requests to web services.
  - Acts as a bridge between the app and server, transferring data back and forth.
  - Supports GET, PUT, POST, DELETE requests.
  - Uses type-safe syntax to prevent errors.
  - Converts JSON data from the server into Kotlin objects and vice versa using converter factories.
  - Typical Retrofit setup includes creating a Retrofit object specifying base URL and converter factory.

  JSON (JavaScript Object Notation)
  -------------------
  - JSON is a lightweight data interchange format used to exchange data between API and Android app.
  - It is based on key-value pairs.
  - JSON Objects: Unordered set of key-value pairs.
  - JSON Arrays: Ordered collection of JSON objects.

  Example JSON Object:
  {
    "name": "JV",
    "age": 22,
    "isStudent": true
  }

  Example JSON Array:
  [
    {
      "name": "JV",
      "age": 22,
      "isStudent": true
    },
    {
      "name": "RJ",
      "age": 22,
      "isStudent": true
    }
  ]

  Serialization & Deserialization
  -------------------
  - Serialization: Converting Kotlin objects to JSON format (for sending data).
  - Deserialization: Converting JSON data received from server into Kotlin objects (for using data).

  Retrofit Important Concepts
  -------------------
  1) Retrofit Object:
     - Defines base URL and how data is serialized/deserialized.
     - Example syntax:
       private val retrofit = Retrofit.Builder()
           .addConverterFactory(ScalarsConverterFactory.create())
           .baseUrl(BASE_URL)
           .build()

  2) Converter Factory:
     - Responsible for converting JSON into Kotlin objects and vice versa.
     - Acts as a bridge between Kotlin data and JSON.
     - ScalarsConverterFactory handles simple types like String, Boolean, Integer, etc.

  Network Request
  -------------------
  - App requests data from server using Retrofit API calls.
  - E.g., an e-commerce app sends request to server to fetch product data.

*/


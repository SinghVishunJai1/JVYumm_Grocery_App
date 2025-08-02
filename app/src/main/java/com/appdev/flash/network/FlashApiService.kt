package com.appdev.flash.network

import com.appdev.flash.data.InternetItem
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET

private const val BASE_URL = "https://training-uploads.internshala.com"
private val retrofit = Retrofit.Builder()
    .addConverterFactory(
        Json.asConverterFactory(
            "application/json".toMediaType()
        )
    )
    .baseUrl(BASE_URL)
    .build()

// akk interface banayngee jo retrofit ke function ko implement karega
interface FlashApiService{

    //base url chor ke baki sab likhnge
    @GET("android/grocery_delivery_app/items.json")
    suspend fun getItem(): List<InternetItem>  // suspend isiliye kiya kyu ki .... jab ui me data fetching ho to other part function rahee...koi disturb na ho

}

// retrofit service ko initialize karne ke liye akk object banayngee

object FlashApi{
    // ye enable karta h interface ko jab app ko data chiye hota h
    val retrofitService: FlashApiService by lazy{
        retrofit.create(
            FlashApiService::class.java
        )
    }
}



//Why Coil needed after retrofit ?
// hum text sab to normal show akr skate h but
// mujhe online images show karna h wo kaise karee
// uske liye hum third party url use kartee h ....jo ki "Coil" hota h

// isme hum "ASYNCIMAGE()" composable ko use kar ke image show karenge


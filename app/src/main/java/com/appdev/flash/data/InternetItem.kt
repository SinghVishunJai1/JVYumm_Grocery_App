package com.appdev.flash.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// jo link he intershala ka usme jo array of object key value pair me show kiya h wahi likh rahe h
// https://training-uploads.intershala.com/android/grocery_delivery_app/items.json
@Serializable
data class InternetItem(

    @SerialName(value = "stringResourceId")
    val itemName:String="",

    @SerialName(value = "itemCategoryId")
    val itemCategory:String="",

    @SerialName(value = "itemQuantity")
    val itemQuantity:String="",

    @SerialName(value = "item_price")
    val itemPrice:Int=0,

    @SerialName(value = "imageResourceId")
    val imageUrl:String=""
)


// next json serialization karenge to hode the data
// why Kotlin Serialization
//Android cannot understand Json directly
//so Json file needs to be converted to kotlin objects for ensuring that the data in the Json can be displayed in our Apps ui


// Jo kisi object ko Json format me convert karta h --> Seraializaton
// Jo Json fromat se object me convert karta h --> deserialization khete h


package com.appdev.flash.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

// data class basically saree groceryitem aur usek name ko akk jagha store karega
data class Categories(
    // @StringRes --> ensure that the property stringResourceId only accept Integer representing the string Rsource from resource folder
    // @DrawableRes --> ensure that the property imageResourseId only accept integer reperesenting image resourse from resourse folder
    @StringRes val stringResourceId:Int,
    @DrawableRes val imageResourceId:Int
)
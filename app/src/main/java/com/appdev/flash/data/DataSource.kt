package com.appdev.flash.data

import androidx.annotation.StringRes
import com.appdev.flash.R


// ye akk singleton object jisko bina class ke direct object likh sakte h
object DataSource {
    fun loadCategories(): List<Categories>{
        return listOf<Categories>(
            Categories(stringResourceId = R.string.FreshFruits, imageResourceId = R.drawable.freshfruits),
            Categories(stringResourceId = R.string.Beverages, imageResourceId = R.drawable.beverages),
            Categories(stringResourceId = R.string.sweettooth, imageResourceId = R.drawable.sweettooth),
            Categories(stringResourceId = R.string.stationary, imageResourceId = R.drawable.stationary),
            Categories(stringResourceId = R.string.PetFood, imageResourceId = R.drawable.petfood),
            Categories(stringResourceId = R.string.PackagedFood, imageResourceId = R.drawable.packagedfood),
            Categories(stringResourceId = R.string.Munchies, imageResourceId = R.drawable.munchies),
            Categories(stringResourceId = R.string.KitchenEssentials, imageResourceId = R.drawable.kitchenessential),
            Categories(stringResourceId = R.string.FreshVegetables, imageResourceId = R.drawable.freshvegetables),
            Categories(stringResourceId = R.string.CleaningEssentials, imageResourceId = R.drawable.cleaningessential__2_),
            Categories(stringResourceId = R.string.BreadAndBiscuits, imageResourceId = R.drawable.breadandbiscuits),
            Categories(stringResourceId = R.string.BathAndBody, imageResourceId = R.drawable.bathandbody),


        )
    }


    fun loadItems(
        @StringRes categoryName:Int,
    ): List<Item> {
        return listOf<Item>(
            Item(R.string.banana_robusta, R.string.FreshFruits, "1 Kg", 100, R.drawable.babana),
            Item(R.string.shimala_apple, R.string.FreshFruits, "1 Kg", 250, R.drawable.shimlaapple),
            Item(R.string.papaya_semi_ripe, R.string.FreshFruits, "1 Kg", 150, R.drawable.papaya),
            Item(R.string.pomegranate, R.string.FreshFruits, "500 Kg", 150, R.drawable.pomegranate),
            Item(R.string.pepsi, R.string.Beverages, "1", 40, R.drawable.pepsican)
        ).filter {
            it.itemCategoryId==categoryName
        }
    }
}
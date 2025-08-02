package com.appdev.flash.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.appdev.flash.R
import com.appdev.flash.data.InternetItem
import com.appdev.flash.data.InternetItemWithQuantity

// CartScreen UI - Shows review of all items in user's cart and bill details
@Composable
fun CartScreen(
    flashViewModel: FlashViewModel,
    onHomeButtonClicked: () -> Unit
) {
    // Collect cart items from ViewModel
    val cartItems by flashViewModel.cartItems.collectAsState()

    // If user added same item multiple times, count its quantity
    val cartItemsWithQuantity = cartItems.groupBy { it }
        .map { (item, cartItems) ->
            InternetItemWithQuantity(
                item,
                cartItems.size  // quantity count
            )
        }

    if (cartItems.isNotEmpty()) {
        // If cart is not empty, show items and bill details
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Cart banner image
            item {
                Image(
                    painter = painterResource(R.drawable.item_banner),
                    contentDescription = "Offer",
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(200.dp)
                )
            }

            // Header: Review Items
            item {
                Text(
                    text = "Review Items",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                )
            }

            // Show card for each cart item
            items(cartItemsWithQuantity) {
                CartCard(it.item, flashViewModel, it.quantity)
            }

            // Header: Bill Details
            item {
                Text(
                    text = "Bill Details",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
            }

            // Bill calculations: item total, handling, delivery, grand total
            val totalPrice = cartItems.sumOf {
                it.itemPrice * 75 / 100  // Calculated price with 25% discount
            }
            val handlingCharge = totalPrice * 1 / 100
            val deliveryFee = 30
            val grandTotal = totalPrice + handlingCharge + deliveryFee

            // Bill details card
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(236, 236, 236, 255) // Light gray background
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        BillRow(
                            itemName = "Item Total",
                            itemPrice = totalPrice,
                            fontWeight = FontWeight.Normal
                        )
                        BillRow(
                            itemName = "Handling Charge",
                            itemPrice = handlingCharge,
                            fontWeight = FontWeight.Light
                        )
                        BillRow(
                            itemName = "Delivery Fee",
                            itemPrice = deliveryFee,
                            fontWeight = FontWeight.Light
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 5.dp),
                            thickness = 1.dp,
                            color = Color.LightGray
                        )
                        BillRow(
                            itemName = "To Pay",
                            itemPrice = grandTotal,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    } else {
        // If cart is empty, show this UI
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.emptycart),
                contentDescription = "Empty Cart",
                modifier = Modifier.size(170.dp)
            )

            Text(
                text = "Your Cart is Empty",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(20.dp)
            )

            FilledTonalButton(onClick = { onHomeButtonClicked() }) {
                Text(text = "Browse Products")
            }
        }
    }
}

// Card for each cart item showing image, name, price, quantity and remove button
@Composable
fun CartCard(
    cartItem: InternetItem,
    flashViewModel: FlashViewModel,
    cartItemWithQuantity: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Item image
        AsyncImage(
            model = cartItem.imageUrl,
            contentDescription = "Item Image",
            modifier = Modifier
                .fillMaxHeight()
                .padding(5.dp)
                .weight(4f)
        )

        // Item name and quantity text
        Column(
            modifier = Modifier
                .padding(horizontal = 5.dp)
                .fillMaxHeight()
                .weight(4f),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = cartItem.itemName,
                fontSize = 16.sp,
                maxLines = 1
            )

            Text(
                text = cartItem.itemQuantity,
                fontSize = 14.sp,
                maxLines = 1
            )
        }

        // Price section - Original price strikethrough + discounted price
        Column(
            modifier = Modifier
                .padding(horizontal = 5.dp)
                .fillMaxHeight()
                .weight(3f),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = "Rs. ${cartItem.itemPrice}",
                fontSize = 12.sp,
                maxLines = 1,
                color = Color.Gray,
                textDecoration = TextDecoration.LineThrough
            )

            Text(
                text = "Rs. ${cartItem.itemPrice * 75 / 100}",
                fontSize = 18.sp,
                maxLines = 1,
                color = Color(254, 116, 105, 255) // Highlighted color
            )
        }

        // Quantity and Remove button
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(3f),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = "Quantity: $cartItemWithQuantity",
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Card(
                modifier = Modifier
                    .clickable {
                        // On remove button click, remove item from cart
                        flashViewModel.removeFromCart(oldItem = cartItem)
                    }
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(254, 116, 105, 255)
                )
            ) {
                Text(
                    text = "Remove",
                    color = Color.White,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                )
            }
        }
    }
}

// BillRow composable - For each row in bill showing item name and price
@Composable
fun BillRow(
    itemName: String,
    itemPrice: Int,
    fontWeight: FontWeight
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = itemName, fontWeight = fontWeight)
        Text(text = "Rs. $itemPrice", fontWeight = fontWeight)
    }
}

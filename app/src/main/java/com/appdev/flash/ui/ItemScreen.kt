package com.appdev.flash.ui

import com.appdev.flash.R
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.appdev.flash.data.InternetItem

// ItemScreen - Shows items of the selected category in a grid
@Composable
fun ItemScreen(
    flashViewModel: FlashViewModel,
    items: List<InternetItem>
) {
    val flashUiState by flashViewModel.uiState.collectAsState()

    // Get the string resource of the selected category from ViewModel
    val selectedCategory = stringResource(id = flashUiState.selectedCategory)

    // Filter items to show only those that belong to the selected category
    val database = items.filter {
        it.itemCategory.lowercase() == selectedCategory.lowercase()
    }

    // Show items in a grid with adaptive columns
    LazyVerticalGrid(
        columns = GridCells.Adaptive(150.dp),
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {

        // Banner item that spans 2 columns in the grid
        item(
            span = {
                GridItemSpan(2)
            }
        ) {
            Column {
                Image(
                    painter = painterResource(R.drawable.item_banner),
                    contentDescription = "offer",
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(200.dp)
                )

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(58, 212, 63, 255)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(3.dp),
                ) {
                    Text(
                        text = "${stringResource(id = flashUiState.selectedCategory)} (${database.size})",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp),
                    )
                }
            }
        }

        // For each item, call the ItemCard composable
        items(database) {
            ItemCard(
                stringResourceId = it.itemName,
                imageResourceId = it.imageUrl,
                itemQuantity = it.itemQuantity,
                itemPrice = it.itemPrice,
                flashViewModel = flashViewModel
            )
        }
    }
}

// InternetItemScreen - Shows different screens based on UI state (Loading, Success, Error)
@Composable
fun InternetItemScreen(
    flashViewModel: FlashViewModel,
    itemUiState: FlashViewModel.ItemUiState
) {
    when (itemUiState) {
        is FlashViewModel.ItemUiState.Loading -> {
            LoadingScreen()
        }

        is FlashViewModel.ItemUiState.Success -> {
            ItemScreen(flashViewModel = flashViewModel, items = itemUiState.items)
        }

        else -> {
            ErrorScreen(flashViewModel = flashViewModel)
        }
    }
}

// ItemCard - Shows each product card with image, name, price, quantity, and Add to Cart button
@Composable
fun ItemCard(
    stringResourceId: String,
    imageResourceId: String,
    itemQuantity: String,
    itemPrice: Int,
    flashViewModel: FlashViewModel
) {
    val context = LocalContext.current

    Column(modifier = Modifier.width(150.dp)) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF8DDF8),
            )
        ) {
            Box {
                // Load product image from the internet
                AsyncImage(
                    model = imageResourceId,
                    contentDescription = stringResourceId,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                )

                // Discount label at the top-right corner
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.End
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFBA0F2F),
                        )
                    ) {
                        Text(
                            "25% Off",
                            color = Color.White,
                            fontSize = 8.sp,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        // Product name
        Text(
            text = stringResourceId,
            fontSize = 12.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            maxLines = 1,
            textAlign = TextAlign.Left
        )

        // Price and quantity row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // Original price with strikethrough
                Text(
                    text = "Rs. $itemPrice",
                    fontSize = 6.sp,
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                    color = Color(109, 109, 109, 225),
                    textDecoration = TextDecoration.LineThrough
                )

                // Discounted price
                Text(
                    text = "Rs. ${itemPrice * 75 / 100}",
                    fontSize = 10.sp,
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                    color = Color(255, 116, 105, 255)
                )
            }

            // Show quantity on the right
            Text(
                text = itemQuantity,
                fontSize = 14.sp,
                maxLines = 1,
                textAlign = TextAlign.Center,
                color = Color(114, 114, 114, 255)
            )
        }

        // Add to Cart button
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
                .clickable {
                    // On button click, add item to database and show a Toast
                    flashViewModel.addToDatabase(
                        InternetItem(
                            itemName = stringResourceId,
                            itemQuantity = itemQuantity,
                            itemPrice = itemPrice,
                            imageUrl = imageResourceId,
                            itemCategory = ""
                        )
                    )
                    Toast.makeText(context, "Added to Cart", Toast.LENGTH_SHORT).show()
                },
            colors = CardDefaults.cardColors(
                containerColor = Color(108, 194, 111, 255) // Green color
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
                    .padding(horizontal = 5.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Add to Cart",
                    fontSize = 11.sp,
                    color = Color.White
                )
            }
        }
    }
}

// LoadingScreen - Displayed when data is loading during network calls
@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.loadingimg),
            contentDescription = "Loading"
        )
    }
}

// ErrorScreen - Displayed when network fails or data is unavailable
@Composable
fun ErrorScreen(flashViewModel: FlashViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.errorimg),
            contentDescription = "Error"
        )

        Text(
            text = "Oops! Internet unavailable. Please check your connection or retry after turning your wifi or mobile data on.",
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            textAlign = TextAlign.Center
        )

        Button(
            onClick = {
                flashViewModel.getFlashItems()
            }
        ) {
            Text(text = "Retry")
        }
    }
}

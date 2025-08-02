package com.appdev.flash.ui

import android.content.Context
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.appdev.flash.R
import com.appdev.flash.data.DataSource

// Main screen showing all categories in a grid layout
@Composable
fun StartScreen(
    flashViewModel: FlashViewModel,   // ViewModel to access state and update UI
    onCategoryClicked: (Int) -> Unit  // Callback when category is clicked, passes category id
) {
    val context = LocalContext.current  // Get current context for Toast messages

    // Get current UI state from ViewModel (for example, selected category)
    //  val flashUiState by flashViewModel.uiState.collectAsState()

    // LazyVerticalGrid shows categories in a grid with adaptive columns based on size
    LazyVerticalGrid(
        columns = GridCells.Adaptive(138.dp),  // Automatically create columns based on minimum 138dp width per item
        contentPadding = PaddingValues(10.dp),  // Padding around the grid
        verticalArrangement = Arrangement.spacedBy(5.dp),  // Space between rows
        horizontalArrangement = Arrangement.spacedBy(5.dp)  // Space between columns
    ) {
        // Special banner item spanning 2 columns at top
        item (
            span = {
                GridItemSpan(2)  // Banner takes full width (2 columns)
            }
        ) {
            Column {
                // Banner image showing offer or category banner
                Image(
                    painter = painterResource(R.drawable.category_banner),
                    contentDescription = "offer",
                    modifier = Modifier
                        .fillMaxWidth().size(200.dp)

                )

                // Card below banner with some text
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(58, 212, 63, 255)  // Green background color
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(3.dp)
                ) {
                    Text(
                        text = "Shop by Category",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp)
                    )
                }
            }
        }

        // Load category list from DataSource and show each category in grid
        items(DataSource.loadCategories()) { category ->
            CategoryCard(
                context = context,
                stringResourceId = category.stringResourceId,
                imageResourceId = category.imageResourceId,
                flashViewModel = flashViewModel,
                onCategoryClicked = onCategoryClicked
            )
        }
    }
}

// Single category card composable showing name and image
@Composable
fun CategoryCard(
    context: Context,               // Context for Toast messages
    stringResourceId: Int,          // String resource ID for category name
    imageResourceId: Int,           // Image resource ID for category image
    flashViewModel: FlashViewModel, // ViewModel to update click state
    onCategoryClicked: (Int) -> Unit // Callback when card is clicked
) {
    // Get actual category name string from resource ID
    val categoryName = stringResource(stringResourceId)

    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp  // Shadow elevation for card
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF8DDF8) // Light purple background color
        ),
        modifier = Modifier
            .size(width = 160.dp, height = 160.dp)  // Fixed size for each category card
            .clickable {
                // When card is clicked:
                flashViewModel.updateClickText(categoryName)  // Update ViewModel click text state
                Toast.makeText(context, "This card was clicked", Toast.LENGTH_SHORT).show() // Show toast message
                onCategoryClicked(stringResourceId)  // Fire callback with category id
            }
    ) {
        Column(
            modifier = Modifier.padding(8.dp),  // Padding inside card
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Show category name text, centered
                Text(
                    text = categoryName,
                    fontSize = 17.sp,
                    color = Color.Black,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                // Show category image below the text
                Image(
                    painter = painterResource(id = imageResourceId),
                    contentDescription = categoryName,
                    modifier = Modifier.size(150.dp)
                )
            }
        }
    }
}

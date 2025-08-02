package com.appdev.flash.ui

// Necessary imports: Compose UI, Navigation, Firebase, ViewModel, etc.
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.appdev.flash.R
import com.appdev.flash.data.InternetItem
import com.google.firebase.auth.FirebaseAuth

// Enum class created to define different screens of the app
enum class FlashAppScreen(val title: String) {
    Start("JVYumm"),
    Items("Choose Items"),
    Cart("Your Cart")
}

// Global variables (for navigation and Firebase authentication)
var canNavigateBack = false
val auth = FirebaseAuth.getInstance()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashApp(
    flashViewModel: FlashViewModel = viewModel(), // ViewModel managing screen state
    navController: NavHostController = rememberNavController() // Navigation controller
) {
    // Observing Firebase user
    val user by flashViewModel.user.collectAsState()

    // Set current user on app launch (auto-login)
    flashViewModel.setUser(auth.currentUser)

    // Observing whether offer screen should be visible or not
    val isVisible by flashViewModel.isVisible.collectAsState()

    // Observing if logout button has been pressed
    val logoutClicked by flashViewModel.logoutClick.collectAsState()

    // Identify current screen
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = FlashAppScreen.valueOf(
        backStackEntry?.destination?.route ?: FlashAppScreen.Start.name // Default to Start screen
    )

    // Show back button if navigation back is possible
    canNavigateBack = navController.previousBackStackEntry != null

    // Observe how many items are in cart
    val cartItems by flashViewModel.cartItems.collectAsState()

    // UI logic: Which screen to show
    if (isVisible) {
        OfferScreen() // Show Offer screen if visible
    } else if (user == null) {
        LoginUi(flashViewModel = flashViewModel) // Show Login screen if user is null
    } else {
        // Scaffold: layout for Top bar, Bottom bar and screen content
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Left side: current screen title and cart item count (if Cart screen)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = currentScreen.title,
                                    fontSize = 26.sp,
                                    fontFamily = FontFamily.SansSerif,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.Black
                                )

                                // Show item count if current screen is Cart
                                if (currentScreen == FlashAppScreen.Cart) {
                                    Text(
                                        text = "(${cartItems.size})",
                                        fontSize = 26.sp,
                                        fontFamily = FontFamily.SansSerif,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.Black
                                    )
                                }
                            }

                            // Right side: Logout icon and text
                            Row(
                                modifier = Modifier.clickable {
                                    flashViewModel.setLogoutStatus(true) // Trigger logout alert
                                }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.logout),
                                    contentDescription = "Logout",
                                    modifier = Modifier.size(28.dp)
                                )
                                Text(
                                    text = "Logout",
                                    fontSize = 18.sp,
                                    modifier = Modifier.padding(start = 4.dp, end = 14.dp)
                                )
                            }
                        }
                    },
                    // Show back button if previous screen exists
                    navigationIcon = {
                        if (canNavigateBack) {
                            IconButton(onClick = {
                                navController.navigateUp()
                            }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back Button"
                                )
                            }
                        }
                    }
                )
            },
            // Bottom navigation bar
            bottomBar = {
                FlashAppBar(
                    navController = navController,
                    currentScreen = currentScreen,
                    cartItems = cartItems
                )
            }
        ) { innerPadding ->
            // Navigation: show content of which screen
            NavHost(
                navController = navController,
                startDestination = FlashAppScreen.Start.name,
                modifier = Modifier.padding(innerPadding) // Get padding from Scaffold
            ) {
                // Start screen (categories list)
                composable(route = FlashAppScreen.Start.name) {
                    StartScreen(
                        flashViewModel = flashViewModel,
                        onCategoryClicked = {
                            flashViewModel.updateSelectedCategory(it) // Set selected category
                            navController.navigate(FlashAppScreen.Items.name) // Go to Items screen
                        }
                    )
                }

                // Items screen (show items of selected category)
                composable(route = FlashAppScreen.Items.name) {
                    InternetItemScreen(
                        flashViewModel = flashViewModel,
                        itemUiState = flashViewModel.itemUiState
                    )
                }

                // Cart screen
                composable(route = FlashAppScreen.Cart.name) {
                    CartScreen(
                        flashViewModel = flashViewModel,
                        onHomeButtonClicked = {
                            navController.navigate(FlashAppScreen.Start.name) {
                                popUpTo(0) // Clear back stack (like logout effect)
                            }
                        }
                    )
                }
            }
        }

        // Logout confirmation dialog
        if (logoutClicked) {
            AlertCheck(
                onYesButtonPressed = {
                    flashViewModel.setLogoutStatus(false)
                    auth.signOut() // Firebase logout
                    flashViewModel.clearData() // Clear local data
                },
                onNoButtonPressed = {
                    flashViewModel.setLogoutStatus(false)
                }
            )
        }
    }
}

// Component for bottom bar: Home and Cart icons
@Composable
fun FlashAppBar(
    navController: NavHostController,
    currentScreen: FlashAppScreen,
    cartItems: List<InternetItem>
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 70.dp, vertical = 10.dp)
    ) {
        // Home button
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.clickable {
                navController.navigate(FlashAppScreen.Start.name) {
                    popUpTo(0) // Clear backstack
                }
            }
        ) {
            Icon(imageVector = Icons.Outlined.Home, contentDescription = "Home")
            Text(text = "Home", fontSize = 10.sp)
        }

        // Cart button
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.clickable {
                if (currentScreen != FlashAppScreen.Cart) {
                    navController.navigate(FlashAppScreen.Cart.name)
                }
            }
        ) {
            Box(
                modifier = Modifier.size(40.dp)  // Icon aur badge ke liye container size set karo
            ) {
                Icon(
                    imageVector = Icons.Outlined.ShoppingCart,
                    contentDescription = "Cart",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(32.dp)  // Icon size thoda chhota rakho
                )

                if (cartItems.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(20.dp),  // Badge chhota rakho
                        colors = CardDefaults.cardColors(containerColor = Color.Red),
                        shape = RoundedCornerShape(50)  // Circular badge
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = cartItems.size.toString(),
                                fontSize = 10.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }

            Text(text = "Cart", fontSize = 10.sp)
        }
    }
}

// Alert dialog for logout confirmation
@Composable
fun AlertCheck(
    onYesButtonPressed: () -> Unit,
    onNoButtonPressed: () -> Unit
) {
    AlertDialog(
        title = {
            Text(text = "Logout?", fontWeight = FontWeight.Bold)
        },
        containerColor = Color.White,
        text = {
            Text(text = "Are you sure want to logout?")
        },
        confirmButton = {
            TextButton(onClick = { onYesButtonPressed() }) {
                Text(text = "Yes")
            }
        },
        dismissButton = {
            TextButton(onClick = { onNoButtonPressed() }) {
                Text(text = "No")
            }
        },
        onDismissRequest = {
            onNoButtonPressed()
        }
    )
}

package com.appdev.flash.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appdev.flash.data.InternetItem

import com.appdev.flash.network.FlashApi
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


// FlashViewModel is a ViewModel class that handles UI related data
// This ViewModel provides data to UI and manages changes happening in the UI
class FlashViewModel: ViewModel(){

    // Private mutable state — can only be updated inside this ViewModel
    // Stores current UI state such as clickStatus and selectedCategory
    private val _uiState = MutableStateFlow(FlashUiState())

    // Public read-only state — observed by UI but cannot be modified
    val uiState: StateFlow<FlashUiState> = _uiState.asStateFlow()

    // Backing property for coroutine
    private val _isVisible = MutableStateFlow(true)
    val isVisible = _isVisible

    // Properties related to Firebase Authentication
    private val _user = MutableStateFlow<FirebaseUser?>(null)
    val user: MutableStateFlow<FirebaseUser?> get() = _user

    // State for phone number display
    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber: MutableStateFlow<String> get() = _phoneNumber

    // UI state for data fetched from Retrofit
    // Default state is Loading until data is fetched
    var itemUiState: ItemUiState by mutableStateOf(ItemUiState.Loading)
        private set  // can only be modified inside ViewModel

    // Cart functionality — list of InternetItem objects
    private val _cartItems = MutableStateFlow<List<InternetItem>>(emptyList())
    val cartItems: StateFlow<List<InternetItem>> get() = _cartItems.asStateFlow()

    // States related to OTP
    private val _otp = MutableStateFlow("")
    val opt: MutableStateFlow<String> get() = _otp

    private val _verificationId = MutableStateFlow("")
    val verificationId: MutableStateFlow<String> get() = _verificationId

    // State for OTP timer (seconds)
    private val _tick = MutableStateFlow(60L)
    val tick: MutableStateFlow<Long> get() = _tick

    // Loading indicator for progress bar
    private val _loading = MutableStateFlow(false)
    val loading: MutableStateFlow<Boolean> get() = _loading

    // Flag for logout alert dialog
    private val _logoutClick = MutableStateFlow(false)
    val logoutClick: MutableStateFlow<Boolean> get() = _logoutClick

    // Firebase Realtime Database reference for user's cart data
    private val database = Firebase.database
    private val myRef = database.getReference("users/${auth.currentUser?.uid}/cart")


    // Job object to cancel timer coroutine
    private lateinit var timerjob: Job

    // Coroutine Jobs for internet connectivity and screen
    private lateinit var internetJob: Job
    private var screenJob: Job

    // UI state sealed interface for data fetch
    sealed interface ItemUiState {
        data class Success(val items: List<InternetItem>) : ItemUiState
        data object Loading : ItemUiState
        data object Error : ItemUiState
    }

    // Function to set phone number
    fun setPhoneNumber(phoneNumber: String) {
        _phoneNumber.value = phoneNumber
    }

    // Function to set OTP
    fun setOtp(otp: String) {
        _otp.value = otp
    }

    // Function to set Verification ID
    fun setVerificationId(verificationId: String) {
        _verificationId.value = verificationId
    }

    // Function to set User
    fun setUser(user: FirebaseUser?) {
        _user.value = user
    }

    // Function to clear all data (for logout or reset)
    fun clearData() {
        _user.value = null
        _phoneNumber.value = ""
        _otp.value = ""
        verificationId.value = ""
        resetTimer()
    }

    // Function to set loading flag
    fun setLoading(isLoading: Boolean) {
        _loading.value = isLoading
    }

    // Function to set logout alert dialog flag
    fun setLogoutStatus(logoutStatus: Boolean) {
        _logoutClick.value = logoutStatus
    }

    // Function to start OTP timer
    fun runTimer() {
        timerjob = viewModelScope.launch {
            while (_tick.value > 0) {
                delay(1000)
                _tick.value = _tick.value - 1
            }
        }
    }

    // Function to reset timer (cancel and set to 60)
    fun resetTimer() {
        try {
            timerjob.cancel()
        } catch (_: Exception) {
            // Ignore exception if any occurs
        } finally {
            _tick.value = 60L
        }
    }

    // Function to add item to cart
    fun addToCart(item: InternetItem) {
        _cartItems.value = _cartItems.value + item
        // DataStore saving currently commented out
    }

    // Function to add item to Firebase realtime database
    fun addToDatabase(item: InternetItem) {
        myRef.push().setValue(item)
    }

    // Function to read cart items from Firebase database
    fun fillCartItem() {
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // First clear the cart
                _cartItems.value = emptyList()

                // Then add data from Firebase to cart
                for (childSnapshot in dataSnapshot.children) {
                    val item = childSnapshot.getValue(InternetItem::class.java)
                    item?.let {
                        addToCart(it)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle if data read fails
            }
        })
    }

    // Function to remove item from cart
    fun removeFromCart(oldItem: InternetItem) {
        // Remove from Firebase database
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (childSnapshot in dataSnapshot.children) {
                    var itemRemove = false
                    val item = childSnapshot.getValue(InternetItem::class.java)
                    item?.let {
                        if (oldItem.itemName == it.itemName && oldItem.itemPrice == it.itemPrice) {
                            childSnapshot.ref.removeValue()
                            itemRemove = true
                        }
                    }
                    if (itemRemove) break
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle if remove fails
            }
        })

        // Remove from local storage (if using DataStore, remove there as well)
        // _cartItems.value = _cartItems.value - item
    }

    // Update UI state when user clicks on photo/item
    fun updateClickText(updatedText: String) {
        _uiState.update { currentState ->
            currentState.copy(
                clickStatus = updatedText
            )
        }
    }

    // Update UI state when user selects a category
    fun updateSelectedCategory(updatedCategory: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                selectedCategory = updatedCategory
            )
        }
    }

    // Function to toggle visibility
    fun toggleVisiblity() {
        _isVisible.value = false
    }

    // Function to fetch data from Retrofit
    // This function helps UI interact with data fetching
    fun getFlashItems() {
        internetJob = viewModelScope.launch {
            try {
                val listresult = FlashApi.retrofitService.getItem()
                // Set success state as soon as data arrives
                itemUiState = ItemUiState.Success(listresult)
            } catch (_: Exception) {
                // Set error state if network or data fetch fails
                itemUiState = ItemUiState.Error
                toggleVisiblity()
                screenJob.cancel()
            }
        }
    }

    init {
        // Launch a coroutine for the screen that delays for 3 seconds
        // This delay is useful for animation or splash screen
        screenJob = viewModelScope.launch(Dispatchers.Default) {
            delay(3000)  // Coroutine pauses for 3 seconds without blocking thread
            toggleVisiblity()
            // Explanation about threads and coroutines below:

            /*
            Context: Environment where app is running, provides resources and services
            CoroutineContext: Context in which coroutine runs
            Dispatchers: Decides which thread coroutine runs on (Default, IO, Main)
            Threads: Small parallel programs running in background
            Coroutines: Lightweight threads that make concurrency easy
            Types of Dispatchers:
                - Default: For CPU-intensive work
                - IO: For file/network read-write
                - Main: For UI updates
            */
        }

        // When ViewModel is created, these functions are called to fetch data and fill cart
        getFlashItems()
        fillCartItem()
    }
}

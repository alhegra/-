package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.data.repository.MinyooRepository
import com.example.ui.admin.AdminPortalScreen
import com.example.ui.auth.AuthOnboardingScreen
import com.example.ui.auth.RestaurantUnderReviewScreen
import com.example.ui.components.*
import com.example.ui.courier.CourierPortalScreen
import com.example.ui.customer.*
import com.example.ui.restaurant.RestaurantOwnerOrdersScreen
import com.example.ui.restaurant.RestaurantPortalScreen
import com.example.ui.theme.*
import kotlinx.coroutines.launch

sealed class Screen {
    object Home : Screen()
    data class RestaurantDetail(val restaurant: Restaurant) : Screen()
    object Cart : Screen()
    object Checkout : Screen()
    data class OrderTracking(val order: Order) : Screen()
    object OrderHistory : Screen()
    object Favorites : Screen()
    object AiAssistant : Screen()
    object Profile : Screen()
    object Support : Screen()
    object Notifications : Screen()
    object RestaurantOwnerOrders : Screen()
}

@Composable
fun MinyooApp() {
    val context = LocalContext.current
    val repository = remember { MinyooRepository.getInstance(context) }
    val coroutineScope = rememberCoroutineScope()
    val isConnected by rememberNetworkConnectivityState()

    // State collections

    val hasActiveSession by repository.hasActiveSession.collectAsState()
    val currentUser by repository.currentUser.collectAsState()
    val currentAddress by repository.selectedAddress.collectAsState()
    val savedAddresses by repository.addresses.collectAsState(initial = emptyList())
    val restaurants by repository.restaurants.collectAsState()
    val products by repository.products.collectAsState()
    val cartItems by repository.cartItems.collectAsState()
    val appliedCoupon by repository.appliedCoupon.collectAsState()
    val orders by repository.allOrders.collectAsState(initial = emptyList())
    val favorites by repository.favorites.collectAsState(initial = emptyList())
    val notifications by repository.notifications.collectAsState(initial = emptyList())
    val supportTickets by repository.supportTickets.collectAsState(initial = emptyList())
    val reviews by repository.reviews.collectAsState(initial = emptyList())

    val (subtotal, deliveryFee, discount) = repository.getCartSummary()
    val cartCount = cartItems.sumOf { it.quantity }
    val unreadNotifsCount = notifications.count { !it.isRead }
    val favoriteIds = remember(favorites) { favorites.map { it.id }.toSet() }

    // Navigation state
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
    var isEnglish by remember { mutableStateOf(false) }

    // Modal dialogs
    var showAddressDialog by remember { mutableStateOf(false) }
    var showRoleDialog by remember { mutableStateOf(false) }
    var showOrderSuccessOverlay by remember { mutableStateOf(false) }
    var selectedProductForCustomization by remember { mutableStateOf<Pair<Product, Restaurant>?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    Column(modifier = Modifier.fillMaxSize()) {
        if (!isConnected) {
            OfflineBanner()
        }

        Box(modifier = Modifier.weight(1f)) {
            // If no active session, show Initial Login / Registration Onboarding Screen
            if (!hasActiveSession) {
                AuthOnboardingScreen(
                    isEnglish = isEnglish,
                    onToggleLanguage = { isEnglish = !isEnglish },
                    onLogin = { ident, pass ->
                        val res = repository.login(ident, pass)
                        if (res is LoginResult.Success && res.user.role == UserRole.CUSTOMER) {
                            currentScreen = Screen.Home
                        }
                        res
                    },
                    onCustomerRegistered = { name, phone, pass, city ->
                        val res = repository.registerCustomer(name, phone, pass, city)
                        currentScreen = Screen.Home
                        res
                    },
                    onRestaurantRegistered = { data, pass ->
                        repository.registerRestaurant(data, pass)
                    },
                    onCourierRegistered = { name, phone, pass, city ->
                        repository.registerCourier(name, phone, pass, city)
                    }
                )
            } else {
                when (currentUser.role) {
                    UserRole.RESTAURANT_OWNER -> {
            val regRest = currentUser.registeredRestaurant
            if (regRest != null && regRest.status == RestaurantStatus.PENDING) {
                RestaurantUnderReviewScreen(
                    registrationData = regRest,
                    onApproveClick = {
                        repository.approveRestaurantApplication()
                    },
                    onLogoutClick = {
                        repository.logoutSession()
                    }
                )
            } else {
                val userRest = restaurants.find { it.name == regRest?.restaurantName }
                    ?: restaurants.firstOrNull()
                    ?: Restaurant(
                        id = "rest_fallback",
                        name = "مطعم تجريبي",
                        nameEn = "Demo Restaurant",
                        logoUrl = "🍔",
                        coverUrl = "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=800&auto=format&fit=crop&q=80",
                        cuisines = listOf("وجبات سريعة"),
                        rating = 4.8,
                        reviewCount = 50,
                        deliveryTimeMinutes = 30,
                        deliveryFee = 15.0,
                        minOrder = 50.0,
                        area = "وسط البلد"
                    )
                RestaurantOwnerOrdersScreen(
                    restaurant = userRest,
                    allRestaurants = restaurants,
                    orders = orders,
                    onAdvanceOrderStatus = { orderId ->
                        coroutineScope.launch { repository.advanceOrderStatus(orderId) }
                    },
                    onRoleSwitcherClick = { showRoleDialog = true },
                    onAddSampleOrder = {
                        coroutineScope.launch { repository.createSampleIncomingOrder(userRest.id) }
                    }
                )
            }
        }
        UserRole.COURIER -> {
            CourierPortalScreen(
                orders = orders,
                onAdvanceOrderStatus = { orderId ->
                    coroutineScope.launch { repository.advanceOrderStatus(orderId) }
                },
                onRoleSwitcherClick = { showRoleDialog = true }
            )
        }
        UserRole.ADMIN -> {
            AdminPortalScreen(
                orders = orders,
                onAdvanceOrderStatus = { orderId ->
                    coroutineScope.launch { repository.advanceOrderStatus(orderId) }
                },
                onCancelOrder = { orderId ->
                    coroutineScope.launch { repository.cancelOrder(orderId) }
                },
                onRoleSwitcherClick = { showRoleDialog = true }
            )
        }
        UserRole.CUSTOMER -> {
            // Customer App Scaffolding
            Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    if (currentScreen is Screen.Home) {
                        MinyooTopBar(
                            currentAddress = currentAddress,
                            currentRole = currentUser.role,
                            unreadNotifsCount = unreadNotifsCount,
                            onAddressClick = { showAddressDialog = true },
                            onRoleClick = { showRoleDialog = true },
                            onNotificationsClick = { currentScreen = Screen.Notifications },
                            onAiAssistantClick = { currentScreen = Screen.AiAssistant }
                        )
                    }
                },
                bottomBar = {
                    // Show Bottom Navigation only on main tabs
                    val showBottomNav = currentScreen is Screen.Home ||
                            currentScreen is Screen.AiAssistant ||
                            currentScreen is Screen.Cart ||
                            currentScreen is Screen.OrderHistory ||
                            currentScreen is Screen.Profile

                    if (showBottomNav) {
                        Column {
                            HorizontalDivider(thickness = 1.dp, color = MinyooBorder)
                            NavigationBar(
                                containerColor = MinyooSurfaceLight,
                                tonalElevation = 0.dp
                            ) {
                                NavigationBarItem(
                                    selected = currentScreen is Screen.Home,
                                    onClick = { currentScreen = Screen.Home },
                                    icon = { Icon(Icons.Default.Home, contentDescription = "الرئيسية") },
                                    label = { Text("الرئيسية", fontSize = 10.sp, fontWeight = if (currentScreen is Screen.Home) FontWeight.Bold else FontWeight.Medium) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MinyooOrangePrimary,
                                        selectedTextColor = MinyooOrangePrimary,
                                        indicatorColor = MinyooOrangeContainer,
                                        unselectedIconColor = MinyooSlateLight,
                                        unselectedTextColor = MinyooSlateLight
                                    ),
                                    modifier = Modifier.testTag("nav_home")
                                )
                                NavigationBarItem(
                                    selected = currentScreen is Screen.AiAssistant,
                                    onClick = { currentScreen = Screen.AiAssistant },
                                    icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "المساعد الذكي") },
                                    label = { Text("المساعد ✨", fontSize = 10.sp, fontWeight = if (currentScreen is Screen.AiAssistant) FontWeight.Bold else FontWeight.Medium) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MinyooOrangePrimary,
                                        selectedTextColor = MinyooOrangePrimary,
                                        indicatorColor = MinyooOrangeContainer,
                                        unselectedIconColor = MinyooSlateLight,
                                        unselectedTextColor = MinyooSlateLight
                                    ),
                                    modifier = Modifier.testTag("nav_ai")
                                )
                                NavigationBarItem(
                                    selected = currentScreen is Screen.Cart,
                                    onClick = { currentScreen = Screen.Cart },
                                    icon = {
                                        BadgedBox(
                                            badge = {
                                                if (cartCount > 0) {
                                                    Badge(
                                                        containerColor = MinyooOrangePrimary,
                                                        modifier = Modifier.size(14.dp)
                                                    ) {
                                                        Text("$cartCount", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                        ) {
                                            Icon(Icons.Default.ShoppingCart, contentDescription = "السلة")
                                        }
                                    },
                                    label = { Text("السلة", fontSize = 10.sp, fontWeight = if (currentScreen is Screen.Cart) FontWeight.Bold else FontWeight.Medium) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MinyooOrangePrimary,
                                        selectedTextColor = MinyooOrangePrimary,
                                        indicatorColor = MinyooOrangeContainer,
                                        unselectedIconColor = MinyooSlateLight,
                                        unselectedTextColor = MinyooSlateLight
                                    ),
                                    modifier = Modifier.testTag("nav_cart")
                                )
                                NavigationBarItem(
                                    selected = currentScreen is Screen.OrderHistory,
                                    onClick = { currentScreen = Screen.OrderHistory },
                                    icon = { Icon(Icons.Default.ReceiptLong, contentDescription = "طلباتي") },
                                    label = { Text("طلباتي", fontSize = 10.sp, fontWeight = if (currentScreen is Screen.OrderHistory) FontWeight.Bold else FontWeight.Medium) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MinyooOrangePrimary,
                                        selectedTextColor = MinyooOrangePrimary,
                                        indicatorColor = MinyooOrangeContainer,
                                        unselectedIconColor = MinyooSlateLight,
                                        unselectedTextColor = MinyooSlateLight
                                    ),
                                    modifier = Modifier.testTag("nav_orders")
                                )
                                NavigationBarItem(
                                    selected = currentScreen is Screen.Profile,
                                    onClick = { currentScreen = Screen.Profile },
                                    icon = { Icon(Icons.Default.Person, contentDescription = "حسابي") },
                                    label = { Text("حسابي", fontSize = 10.sp, fontWeight = if (currentScreen is Screen.Profile) FontWeight.Bold else FontWeight.Medium) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MinyooOrangePrimary,
                                        selectedTextColor = MinyooOrangePrimary,
                                        indicatorColor = MinyooOrangeContainer,
                                        unselectedIconColor = MinyooSlateLight,
                                        unselectedTextColor = MinyooSlateLight
                                    ),
                                    modifier = Modifier.testTag("nav_profile")
                                )
                            }
                        }
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (val screen = currentScreen) {
                        is Screen.Home -> {
                            HomeScreen(
                                restaurants = restaurants,
                                products = products,
                                favoriteIds = favoriteIds,
                                lastOrder = orders.firstOrNull { it.status == OrderStatus.DELIVERED },
                                onRestaurantClick = { currentScreen = Screen.RestaurantDetail(it) },
                                onProductClick = { prod, rest ->
                                    selectedProductForCustomization = Pair(prod, rest)
                                },
                                onQuickAddProduct = { prod, rest ->
                                    selectedProductForCustomization = Pair(prod, rest)
                                },
                                onFavoriteToggle = { id, isRest ->
                                    coroutineScope.launch { repository.toggleFavorite(id, isRest) }
                                },
                                onAiAssistantClick = { currentScreen = Screen.AiAssistant },
                                onReorderClick = { pastOrder ->
                                    pastOrder.items.forEach { item ->
                                        val r = restaurants.find { it.id == item.restaurantId } ?: restaurants.first()
                                        repository.addToCart(item.product, r, item.quantity, item.selectedModifiers, item.notes)
                                    }
                                    currentScreen = Screen.Cart
                                }
                            )
                        }
                        is Screen.RestaurantDetail -> {
                            val restReviews = remember(reviews, screen.restaurant.id) {
                                reviews.filter { it.restaurantId == screen.restaurant.id }
                            }
                            RestaurantScreen(
                                restaurant = screen.restaurant,
                                products = products,
                                cartItems = cartItems,
                                isFavorite = favoriteIds.contains(screen.restaurant.id),
                                restaurantReviews = restReviews,
                                onBackClick = { currentScreen = Screen.Home },
                                onFavoriteToggle = {
                                    coroutineScope.launch { repository.toggleFavorite(screen.restaurant.id, true) }
                                },
                                onProductClick = { prod ->
                                    selectedProductForCustomization = Pair(prod, screen.restaurant)
                                },
                                onQuickAddProduct = { prod ->
                                    selectedProductForCustomization = Pair(prod, screen.restaurant)
                                },
                                onViewCartClick = { currentScreen = Screen.Cart }
                            )
                        }
                        is Screen.Cart -> {
                            CartScreen(
                                cartItems = cartItems,
                                appliedCoupon = appliedCoupon,
                                subtotal = subtotal,
                                deliveryFee = deliveryFee,
                                discount = discount,
                                onUpdateQuantity = { id, delta -> repository.updateCartItemQuantity(id, delta) },
                                onRemoveItem = { id -> repository.removeCartItem(id) },
                                onApplyCoupon = { code -> repository.applyCoupon(code) },
                                onRemoveCoupon = { repository.removeCoupon() },
                                onClearCart = { repository.clearCart() },
                                onProceedToCheckout = { currentScreen = Screen.Checkout },
                                onExploreRestaurantsClick = { currentScreen = Screen.Home }
                            )
                        }
                        is Screen.Checkout -> {
                            CheckoutScreen(
                                currentAddress = currentAddress,
                                cartItems = cartItems,
                                subtotal = subtotal,
                                deliveryFee = deliveryFee,
                                discount = discount,
                                customerName = currentUser.name,
                                customerPhone = currentUser.phone,
                                onBackClick = { currentScreen = Screen.Cart },
                                onChangeAddressClick = { showAddressDialog = true },
                                onConfirmOrder = { paymentMethod, notes, paymobResult ->
                                    coroutineScope.launch {
                                        showOrderSuccessOverlay = true
                                        val newOrder = repository.placeOrder(paymentMethod, notes, paymobResult)
                                        kotlinx.coroutines.delay(1600)
                                        showOrderSuccessOverlay = false
                                        if (newOrder != null) {
                                            currentScreen = Screen.OrderTracking(newOrder)
                                        }
                                    }
                                }
                            )
                        }
                        is Screen.OrderTracking -> {
                            // Find latest state of this order from orders flow
                            val liveOrder = orders.find { it.id == screen.order.id } ?: screen.order
                            OrderTrackingScreen(
                                order = liveOrder,
                                onBackClick = { currentScreen = Screen.OrderHistory },
                                onSimulateNextStep = {
                                    coroutineScope.launch { repository.advanceOrderStatus(liveOrder.id) }
                                },
                                onCancelOrder = {
                                    coroutineScope.launch { repository.cancelOrder(liveOrder.id) }
                                },
                                onSupportClick = { currentScreen = Screen.Support },
                                onSubmitReview = { ratingVal, commentVal ->
                                    coroutineScope.launch {
                                        val firstItem = liveOrder.items.firstOrNull()
                                        val restId = firstItem?.restaurantId ?: restaurants.first().id
                                        repository.submitReview(
                                            restaurantId = restId,
                                            orderId = liveOrder.id,
                                            customerName = currentUser.name.ifBlank { "عميل لقمة" },
                                            rating = ratingVal.toDouble(),
                                            comment = commentVal
                                        )
                                    }
                                }
                            )
                        }
                        is Screen.OrderHistory -> {
                            OrderHistoryScreen(
                                orders = orders,
                                onOrderClick = { currentScreen = Screen.OrderTracking(it) },
                                onReorderClick = { pastOrder ->
                                    pastOrder.items.forEach { item ->
                                        val r = restaurants.find { it.id == item.restaurantId } ?: restaurants.first()
                                        repository.addToCart(item.product, r, item.quantity, item.selectedModifiers, item.notes)
                                    }
                                    currentScreen = Screen.Cart
                                },
                                onExploreClick = { currentScreen = Screen.Home }
                            )
                        }
                        is Screen.Favorites -> {
                            FavoritesScreen(
                                restaurants = restaurants,
                                products = products,
                                favoriteIds = favoriteIds,
                                onRestaurantClick = { currentScreen = Screen.RestaurantDetail(it) },
                                onProductClick = { prod, rest ->
                                    selectedProductForCustomization = Pair(prod, rest)
                                },
                                onQuickAddProduct = { prod, rest ->
                                    selectedProductForCustomization = Pair(prod, rest)
                                },
                                onFavoriteToggle = { id, isRest ->
                                    coroutineScope.launch { repository.toggleFavorite(id, isRest) }
                                },
                                onExploreClick = { currentScreen = Screen.Home }
                            )
                        }
                        is Screen.AiAssistant -> {
                            AiFoodAssistantScreen(
                                restaurants = restaurants,
                                products = products,
                                onAddMealBundleToCart = { suggestion ->
                                    val rest = restaurants.find { it.id == suggestion.restaurantId } ?: restaurants.first()
                                    suggestion.suggestedProducts.forEach { p ->
                                        repository.addToCart(p, rest, 1, emptyList(), "اقتراح ذكي من مساعد لقمة")
                                    }
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("تمت إضافة وجبة ${suggestion.title} إلى السلة! 🛍️")
                                    }
                                    currentScreen = Screen.Cart
                                },
                                onRestaurantClick = { currentScreen = Screen.RestaurantDetail(it) }
                            )
                        }
                        is Screen.Profile -> {
                            ProfileScreen(
                                user = currentUser,
                                savedAddresses = savedAddresses,
                                isEnglish = isEnglish,
                                onToggleLanguage = { isEnglish = !isEnglish },
                                onSavedAddressesClick = { showAddressDialog = true },
                                onRoleSwitcherClick = { showRoleDialog = true },
                                onRestaurantPortalClick = { currentScreen = Screen.RestaurantOwnerOrders },
                                onSupportClick = { currentScreen = Screen.Support },
                                onNotificationsClick = { currentScreen = Screen.Notifications },
                                onLogoutClick = { repository.logoutSession() }
                            )
                        }
                        is Screen.RestaurantOwnerOrders -> {
                            val userRest = restaurants.first()
                            RestaurantOwnerOrdersScreen(
                                restaurant = userRest,
                                allRestaurants = restaurants,
                                orders = orders,
                                onAdvanceOrderStatus = { orderId ->
                                    coroutineScope.launch { repository.advanceOrderStatus(orderId) }
                                },
                                onRoleSwitcherClick = { showRoleDialog = true },
                                onBackClick = { currentScreen = Screen.Profile },
                                onAddSampleOrder = {
                                    coroutineScope.launch { repository.createSampleIncomingOrder(userRest.id) }
                                }
                            )
                        }
                        is Screen.Support -> {
                            SupportScreen(
                                tickets = supportTickets,
                                onBackClick = { currentScreen = Screen.Profile },
                                onSubmitTicket = { issueType, message ->
                                    coroutineScope.launch {
                                        repository.submitSupportTicket(null, issueType, message)
                                        snackbarHostState.showSnackbar("تم إرسال تذكرتك بنجاح! سيتم التواصل معك خلال دقائق.")
                                    }
                                }
                            )
                        }
                        is Screen.Notifications -> {
                            NotificationsScreen(
                                notifications = notifications,
                                onBackClick = { currentScreen = Screen.Profile }
                            )
                        }
                    }

                    if (showOrderSuccessOverlay) {
                        OrderSuccessAnimationOverlay()
                    }
                }
            }
        }
      }
    }
  }

    // Modal Sheet: Product Customization
    selectedProductForCustomization?.let { (product, restaurant) ->
        ProductCustomizationDialog(
            product = product,
            restaurant = restaurant,
            onAddToCart = { quantity, modifiers, notes ->
                repository.addToCart(product, restaurant, quantity, modifiers, notes)
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("تمت إضافة ${product.name} للسلة بنجاح 🍔")
                }
            },
            onDismiss = { selectedProductForCustomization = null }
        )
    }

    // Modal Dialog: Location / Address Selector
    if (showAddressDialog) {
        LocationSelectionDialog(
            currentAddress = currentAddress,
            savedAddresses = savedAddresses,
            onSelectAddress = { repository.setSelectedAddress(it) },
            onAddNewAddress = { coroutineScope.launch { repository.addNewAddress(it) } },
            onDismiss = { showAddressDialog = false }
        )
    }

    // Modal Dialog: Role Switcher
    if (showRoleDialog) {
        RoleSelectionDialog(
            currentRole = currentUser.role,
            onRoleSelected = { repository.setUserRole(it) },
            onDismiss = { showRoleDialog = false },
            onLogout = { repository.logoutSession() }
        )
    }
  }
}


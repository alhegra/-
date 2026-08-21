package com.example.data.repository

import android.content.Context
import com.example.data.local.AddressEntity
import com.example.data.local.AppDatabase
import com.example.data.local.FavoriteEntity
import com.example.data.local.NotificationEntity
import com.example.data.local.OrderEntity
import com.example.data.local.SupportTicketEntity
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

class MinyooRepository(
    private val database: AppDatabase,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    // In-memory restaurant and product data
    private val _restaurants = MutableStateFlow<List<Restaurant>>(SeedData.restaurants)
    val restaurants: StateFlow<List<Restaurant>> = _restaurants.asStateFlow()

    private val _products = MutableStateFlow<List<Product>>(SeedData.products)
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    // Current active user & Role
    private val _currentUser = MutableStateFlow(User())
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    // Selected location address
    private val _selectedAddress = MutableStateFlow(SeedData.sampleAddresses.first())
    val selectedAddress: StateFlow<Address> = _selectedAddress.asStateFlow()

    // Cart State
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    // Applied Coupon
    private val _appliedCoupon = MutableStateFlow<Coupon?>(null)
    val appliedCoupon: StateFlow<Coupon?> = _appliedCoupon.asStateFlow()

    // Active delivery address list from Room
    val addresses: Flow<List<Address>> = database.addressDao().getAllAddresses().map { entities ->
        if (entities.isEmpty()) {
            SeedData.sampleAddresses
        } else {
            entities.map { it.toDomain() }
        }
    }

    // Orders from Room
    val allOrders: Flow<List<Order>> = combine(
        database.orderDao().getAllOrders(),
        addresses
    ) { orderEntities, addressList ->
        orderEntities.map { entity ->
            val addr = addressList.find { it.id == entity.deliveryAddressId }
                ?: _selectedAddress.value
            entity.toDomain(addr)
        }
    }

    // Favorites
    val favorites: Flow<List<FavoriteEntity>> = database.favoriteDao().getAllFavorites()

    // Notifications
    val notifications: Flow<List<NotificationEntity>> = database.notificationDao().getAllNotifications()

    // Support Tickets
    val supportTickets: Flow<List<SupportTicketEntity>> = database.supportDao().getAllTickets()

    init {
        scope.launch {
            // Prepopulate addresses if empty
            val existing = database.addressDao().getAllAddresses().first()
            if (existing.isEmpty()) {
                database.addressDao().insertAll(SeedData.sampleAddresses.map { AddressEntity.fromDomain(it) })
            }
            // Prepopulate notifications
            val notifs = database.notificationDao().getAllNotifications().first()
            if (notifs.isEmpty()) {
                database.notificationDao().insertAll(
                    SeedData.sampleNotifications.map {
                        NotificationEntity(
                            id = it.id,
                            title = it.title,
                            body = it.body,
                            timeAgo = it.timeAgo,
                            isRead = it.isRead,
                            orderId = it.orderId
                        )
                    }
                )
            }
            // Prepopulate a realistic initial completed order
            val existingOrders = database.orderDao().getAllOrders().first()
            if (existingOrders.isEmpty()) {
                val initialOrder = OrderEntity(
                    id = "ord_init_1",
                    orderNumber = "#MNY-9820",
                    customerId = "cust_1",
                    customerName = "أحمد مصطفى",
                    customerPhone = "01098765432",
                    restaurantId = "rest_1",
                    restaurantName = "كشري أبو طارق",
                    restaurantArea = "وسط البلد / مدينة نصر",
                    deliveryAddressId = "addr_1",
                    deliveryAddressText = SeedData.sampleAddresses.first().fullAddressText,
                    items = listOf(
                        CartItem(
                            cartItemId = "cart_init_1",
                            product = SeedData.products[0],
                            restaurantId = "rest_1",
                            restaurantName = "كشري أبو طارق",
                            quantity = 2,
                            selectedModifiers = listOf(
                                SelectedModifier("mg_size_101", "اختر الحجم", "opt_2", "حجم كبير لارج", 15.0),
                                SelectedModifier("mg_extra_101", "الإضافات اللذيذة", "opt_ex1", "تقلية بصل مقرمش زيادة", 10.0)
                            )
                        )
                    ),
                    subtotal = 140.0,
                    deliveryFee = 15.0,
                    serviceFee = 5.0,
                    discount = 20.0,
                    total = 140.0,
                    paymentMethod = PaymentMethod.CASH_ON_DELIVERY,
                    paymentStatus = "PAID",
                    status = OrderStatus.DELIVERED,
                    courierName = "كابتن محمود علي",
                    courierPhone = "01122334455",
                    courierVehicle = "موتوسيكل هوندا أحمر",
                    createdAt = System.currentTimeMillis() - 7200000, // 2 hours ago
                    estimatedMinutes = 25,
                    deliveryNotes = "رن الجرس واترك الطلب أمام الباب"
                )
                database.orderDao().insertOrder(initialOrder)
            }
        }
    }

    // Role switching
    fun setUserRole(role: UserRole) {
        _currentUser.value = _currentUser.value.copy(role = role)
    }

    fun setSelectedAddress(address: Address) {
        _selectedAddress.value = address
    }

    suspend fun addNewAddress(address: Address) {
        database.addressDao().insertAddress(AddressEntity.fromDomain(address))
        _selectedAddress.value = address
    }

    // Search logic with Egyptian Arabic normalization
    fun searchProductsAndRestaurants(query: String): Pair<List<Restaurant>, List<Product>> {
        if (query.isBlank()) return Pair(_restaurants.value, emptyList())
        val normQuery = normalizeArabic(query.trim().lowercase())

        val matchedRestaurants = _restaurants.value.filter { rest ->
            val nameNorm = normalizeArabic(rest.name.lowercase())
            val nameEnNorm = rest.nameEn.lowercase()
            val cuisinesNorm = rest.cuisines.map { normalizeArabic(it.lowercase()) }

            nameNorm.contains(normQuery) ||
                    nameEnNorm.contains(normQuery) ||
                    cuisinesNorm.any { it.contains(normQuery) } ||
                    isSynonymMatch(normQuery, nameNorm)
        }

        val matchedProducts = _products.value.filter { prod ->
            val nameNorm = normalizeArabic(prod.name.lowercase())
            val descNorm = normalizeArabic(prod.description.lowercase())
            val catNorm = normalizeArabic(prod.categoryId.lowercase())

            nameNorm.contains(normQuery) ||
                    descNorm.contains(normQuery) ||
                    catNorm.contains(normQuery) ||
                    isSynonymMatch(normQuery, nameNorm)
        }

        return Pair(matchedRestaurants, matchedProducts)
    }

    private fun normalizeArabic(text: String): String {
        return text
            .replace("[أإآ]".toRegex(), "ا")
            .replace("ة", "ه")
            .replace("ى", "ي")
            .replace("ؤ", "و")
            .replace("ئ", "ي")
            .replace("ـ", "") // Tatweel
    }

    private fun isSynonymMatch(query: String, target: String): Boolean {
        val synonyms = mapOf(
            "برجر" to listOf("burger", "برغر", "همبرجر", "ساندوتش", "تشيز"),
            "بيتزا" to listOf("pizza", "بتزا", "فطير"),
            "كشري" to listOf("koshari", "koshary", "ابو طارق", "صلصه", "دقه"),
            "شاورما" to listOf("shawarma", "شاورمه", "سوري", "توميه"),
            "فراخ" to listOf("chicken", "دجاج", "كرسبي", "بروستد", "زنجر"),
            "كباب" to listOf("مشويات", "كفته", "ريش", "لحمه", "طرب"),
            "حلو" to listOf("حلويات", "قشطوطه", "نوتيلا", "بلبن", "ايس كريم", "كريب")
        )
        for ((key, list) in synonyms) {
            if ((query.contains(key) || list.any { query.contains(it) }) &&
                (target.contains(key) || list.any { target.contains(it) })
            ) {
                return true
            }
        }
        return false
    }

    // Cart Management
    fun addToCart(
        product: Product,
        restaurant: Restaurant,
        quantity: Int,
        selectedModifiers: List<SelectedModifier>,
        notes: String
    ) {
        val currentList = _cartItems.value.toMutableList()
        // If from another restaurant, clear old cart to prevent cross-restaurant ordering conflict
        if (currentList.isNotEmpty() && currentList.first().restaurantId != restaurant.id) {
            currentList.clear()
        }

        val cartItemId = UUID.randomUUID().toString()
        val newItem = CartItem(
            cartItemId = cartItemId,
            product = product,
            restaurantId = restaurant.id,
            restaurantName = restaurant.name,
            quantity = quantity,
            selectedModifiers = selectedModifiers,
            notes = notes
        )
        currentList.add(newItem)
        _cartItems.value = currentList
    }

    fun updateCartItemQuantity(cartItemId: String, delta: Int) {
        val currentList = _cartItems.value.mapNotNull { item ->
            if (item.cartItemId == cartItemId) {
                val newQty = item.quantity + delta
                if (newQty > 0) item.copy(quantity = newQty) else null
            } else {
                item
            }
        }
        _cartItems.value = currentList
    }

    fun removeCartItem(cartItemId: String) {
        _cartItems.value = _cartItems.value.filter { it.cartItemId != cartItemId }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
        _appliedCoupon.value = null
    }

    fun applyCoupon(code: String): Pair<Boolean, String> {
        val coupon = SeedData.sampleCoupons.find { it.code.equals(code.trim(), ignoreCase = true) }
        return if (coupon != null) {
            val subtotal = _cartItems.value.sumOf { it.totalPrice }
            if (subtotal < coupon.minOrder) {
                Pair(false, "الحد الأدنى لتطبيق الكوبون هو ${coupon.minOrder.toInt()} جنيه")
            } else {
                _appliedCoupon.value = coupon
                Pair(true, "تم تطبيق كود الخصم بنجاح! 🎉")
            }
        } else {
            Pair(false, "كود الخصم غير صالح أو منتهي الصلاحية")
        }
    }

    fun removeCoupon() {
        _appliedCoupon.value = null
    }

    // Calculations
    fun getCartSummary(): Triple<Double, Double, Double> { // subtotal, deliveryFee, discount
        val subtotal = _cartItems.value.sumOf { it.totalPrice }
        val restId = _cartItems.value.firstOrNull()?.restaurantId
        val rest = _restaurants.value.find { it.id == restId }
        var deliveryFee = rest?.deliveryFee ?: 15.0

        var discount = 0.0
        val coupon = _appliedCoupon.value
        if (coupon != null && subtotal >= coupon.minOrder) {
            if (coupon.discountPercentage > 0) {
                val pctDiscount = (subtotal * coupon.discountPercentage) / 100.0
                discount = pctDiscount.coerceAtMost(coupon.maxDiscount)
            } else if (coupon.fixedDiscount > 0) {
                discount = coupon.fixedDiscount.coerceAtMost(coupon.maxDiscount)
                if (coupon.code == "FREEDEL") {
                    deliveryFee = 0.0
                }
            }
        }

        return Triple(subtotal, deliveryFee, discount)
    }

    // Place Order
    suspend fun placeOrder(
        paymentMethod: PaymentMethod,
        notes: String
    ): Order? {
        val items = _cartItems.value
        if (items.isEmpty()) return null

        val restId = items.first().restaurantId
        val rest = _restaurants.value.find { it.id == restId } ?: return null
        val (subtotal, deliveryFee, discount) = getCartSummary()
        val serviceFee = 5.0
        val total = (subtotal + deliveryFee + serviceFee - discount).coerceAtLeast(0.0)

        val orderNumber = "#MNY-${(1000..9999).random()}"
        val orderId = "ord_${System.currentTimeMillis()}"
        val currentAddr = _selectedAddress.value

        val orderEntity = OrderEntity(
            id = orderId,
            orderNumber = orderNumber,
            customerId = _currentUser.value.id,
            customerName = _currentUser.value.name,
            customerPhone = _currentUser.value.phone,
            restaurantId = rest.id,
            restaurantName = rest.name,
            restaurantArea = rest.area,
            deliveryAddressId = currentAddr.id,
            deliveryAddressText = currentAddr.fullAddressText,
            items = items,
            subtotal = subtotal,
            deliveryFee = deliveryFee,
            serviceFee = serviceFee,
            discount = discount,
            total = total,
            paymentMethod = paymentMethod,
            paymentStatus = if (paymentMethod == PaymentMethod.CASH_ON_DELIVERY) "COD_PENDING" else "PAID_ONLINE",
            status = OrderStatus.PLACED,
            courierName = "كابتن إبراهيم حسن",
            courierPhone = "01012345678",
            courierVehicle = "سكوتر بينيلي رمادي",
            createdAt = System.currentTimeMillis(),
            estimatedMinutes = rest.deliveryTimeMinutes,
            deliveryNotes = notes
        )

        database.orderDao().insertOrder(orderEntity)

        // Add notification
        val notif = NotificationEntity(
            id = "notif_${System.currentTimeMillis()}",
            title = "تم تأكيد طلبك بنجاح! $orderNumber",
            body = "طلبك من ${rest.name} جاري إرساله للمطعم للتجهيز.",
            timeAgo = "الآن",
            isRead = false,
            orderId = orderId
        )
        database.notificationDao().insertNotification(notif)

        // Clear cart
        clearCart()

        return orderEntity.toDomain(currentAddr)
    }

    // Advance Order Status (for simulation, restaurant, courier, or admin)
    suspend fun advanceOrderStatus(orderId: String) {
        val order = database.orderDao().getOrderById(orderId).first() ?: return
        val nextStatus = when (order.status) {
            OrderStatus.PLACED -> OrderStatus.CONFIRMED
            OrderStatus.CONFIRMED -> OrderStatus.PREPARING
            OrderStatus.PREPARING -> OrderStatus.COURIER_ASSIGNED
            OrderStatus.COURIER_ASSIGNED -> OrderStatus.PICKED_UP
            OrderStatus.PICKED_UP -> OrderStatus.OUT_FOR_DELIVERY
            OrderStatus.OUT_FOR_DELIVERY -> OrderStatus.DELIVERED
            OrderStatus.DELIVERED -> OrderStatus.DELIVERED
            OrderStatus.CANCELLED -> OrderStatus.CANCELLED
        }
        database.orderDao().updateOrderStatus(orderId, nextStatus)

        // Notify user about status change
        val notif = NotificationEntity(
            id = "notif_${System.currentTimeMillis()}",
            title = "تحديث لطلبك ${order.orderNumber} 🚀",
            body = "${nextStatus.titleAr}: ${nextStatus.descriptionAr}",
            timeAgo = "الآن",
            isRead = false,
            orderId = orderId
        )
        database.notificationDao().insertNotification(notif)
    }

    suspend fun cancelOrder(orderId: String) {
        database.orderDao().updateOrderStatus(orderId, OrderStatus.CANCELLED)
    }

    // Favorites
    suspend fun toggleFavorite(id: String, isRestaurant: Boolean) {
        val isFav = database.favoriteDao().isFavorite(id).first()
        if (isFav) {
            database.favoriteDao().removeFavorite(id)
        } else {
            database.favoriteDao().insertFavorite(FavoriteEntity(id = id, isRestaurant = isRestaurant))
        }
    }

    // Support Tickets
    suspend fun submitSupportTicket(orderId: String?, issueType: String, message: String) {
        val ticket = SupportTicketEntity(
            id = "tkt_${System.currentTimeMillis()}",
            orderId = orderId,
            issueType = issueType,
            message = message,
            status = "OPEN",
            timestamp = System.currentTimeMillis()
        )
        database.supportDao().insertTicket(ticket)
    }

    // Restaurant Owner Menu management
    fun addOrUpdateProduct(product: Product) {
        val list = _products.value.toMutableList()
        val index = list.indexOfFirst { it.id == product.id }
        if (index >= 0) {
            list[index] = product
        } else {
            list.add(product)
        }
        _products.value = list
    }

    fun toggleProductAvailability(productId: String) {
        _products.value = _products.value.map {
            if (it.id == productId) it.copy(isAvailable = !it.isAvailable) else it
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: MinyooRepository? = null

        fun getInstance(context: Context): MinyooRepository {
            return INSTANCE ?: synchronized(this) {
                val db = AppDatabase.getDatabase(context)
                val instance = MinyooRepository(db)
                INSTANCE = instance
                instance
            }
        }
    }
}

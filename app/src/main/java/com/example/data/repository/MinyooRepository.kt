package com.example.data.repository

import android.content.Context
import com.example.data.local.AddressEntity
import com.example.data.local.AppDatabase
import com.example.data.local.FavoriteEntity
import com.example.data.local.NotificationEntity
import com.example.data.local.OrderEntity
import com.example.data.local.ProductEntity
import com.example.data.local.RestaurantEntity
import com.example.data.local.ReviewEntity
import com.example.data.local.SupportTicketEntity
import com.example.data.local.UserAccountEntity
import com.example.data.model.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

class MinyooRepository(
    private val database: AppDatabase,
    private val context: Context? = null,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private val prefs = context?.getSharedPreferences("lo2ma_session_prefs", Context.MODE_PRIVATE)

    // Room-persisted restaurants with reactive Flow
    val restaurants: StateFlow<List<Restaurant>> = database.restaurantDao().getAllRestaurants()
        .map { entities ->
            if (entities.isEmpty()) {
                SeedData.restaurants
            } else {
                entities.map { it.toDomain() }
            }
        }
        .stateIn(scope, SharingStarted.Eagerly, SeedData.restaurants)

    // Room-persisted products (menu items) with reactive Flow
    val products: StateFlow<List<Product>> = database.productDao().getAllProducts()
        .map { entities ->
            if (entities.isEmpty()) {
                SeedData.products
            } else {
                entities.map { it.toDomain() }
            }
        }
        .stateIn(scope, SharingStarted.Eagerly, SeedData.products)

    // Room-persisted reviews with reactive Flow
    val reviews: StateFlow<List<Review>> = database.reviewDao().getAllReviews()
        .map { entities ->
            if (entities.isEmpty()) {
                listOf(
                    Review(
                        id = "rev_1",
                        restaurantId = "rest_1",
                        orderId = "ord_101",
                        customerName = "محمود حسن",
                        rating = 5.0,
                        comment = "الأكل كان سخن وطعمه تحفة، والتوصيل سريع جداً! تسلم ايديكم",
                        date = "منذ يومين"
                    ),
                    Review(
                        id = "rev_2",
                        restaurantId = "rest_1",
                        orderId = "ord_102",
                        customerName = "سارة أحمد",
                        rating = 4.5,
                        comment = "الكشري ممتاز والصلصة مظبوطة، شكراً لقمة",
                        date = "منذ 3 أيام"
                    ),
                    Review(
                        id = "rev_3",
                        restaurantId = "rest_2",
                        orderId = "ord_103",
                        customerName = "كريم إبراهيم",
                        rating = 5.0,
                        comment = "أحلى برجر في مصر، الجبنة سايحة والعيش طري جداً",
                        date = "منذ أسبوع"
                    )
                )
            } else {
                entities.map { it.toDomain() }
            }
        }
        .stateIn(scope, SharingStarted.Eagerly, emptyList())

    // Session state
    private val _hasActiveSession = MutableStateFlow(false)
    val hasActiveSession: StateFlow<Boolean> = _hasActiveSession.asStateFlow()

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
        syncWithFirestore()
        val isLoggedIn = prefs?.getBoolean("is_logged_in", false) ?: false
        if (isLoggedIn) {
            val savedRoleStr = prefs?.getString("user_role", UserRole.CUSTOMER.name) ?: UserRole.CUSTOMER.name
            val savedRole = try { UserRole.valueOf(savedRoleStr) } catch (_: Exception) { UserRole.CUSTOMER }
            val savedName = prefs?.getString("user_name", "أحمد مصطفى") ?: "أحمد مصطفى"
            val savedPhone = prefs?.getString("user_phone", "01098765432") ?: "01098765432"

            val savedRestName = prefs?.getString("rest_name", "") ?: ""
            val savedRestPhone = prefs?.getString("rest_phone", "") ?: ""
            val savedRestArea = prefs?.getString("rest_area", "") ?: ""
            val savedRestCuisine = prefs?.getString("rest_cuisine", "مأكولات متنوعة") ?: "مأكولات متنوعة"
            val savedRestLogo = prefs?.getString("rest_logo", "🍔") ?: "🍔"
            val savedRestStatusStr = prefs?.getString("rest_status", RestaurantStatus.PENDING.name) ?: RestaurantStatus.PENDING.name
            val savedRestStatus = try { RestaurantStatus.valueOf(savedRestStatusStr) } catch (_: Exception) { RestaurantStatus.PENDING }

            val restData = if (savedRestName.isNotBlank()) {
                RestaurantRegistrationData(
                    restaurantName = savedRestName,
                    phone = savedRestPhone,
                    cityArea = savedRestArea,
                    cuisine = savedRestCuisine,
                    logoIcon = savedRestLogo,
                    status = savedRestStatus
                )
            } else null

            _currentUser.value = User(
                name = savedName,
                phone = savedPhone,
                role = savedRole,
                registeredRestaurant = restData
            )

            if (restData != null) {
                scope.launch {
                    val existing = database.restaurantDao().getRestaurantById("rest_custom_${restData.restaurantName}").first()
                    if (existing == null) {
                        val newRest = Restaurant(
                            id = "rest_custom_${restData.restaurantName.hashCode()}",
                            name = restData.restaurantName,
                            nameEn = restData.restaurantName,
                            logoUrl = restData.logoIcon,
                            coverUrl = "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=800&auto=format&fit=crop&q=80",
                            cuisines = listOf(restData.cuisine),
                            rating = 4.9,
                            reviewCount = 12,
                            deliveryTimeMinutes = restData.deliveryTimeMinutes,
                            deliveryFee = 15.0,
                            minOrder = restData.minOrder,
                            area = restData.cityArea,
                            isOpen = true,
                            isFeatured = true
                        )
                        database.restaurantDao().insertRestaurant(RestaurantEntity.fromDomain(newRest))
                    }
                }
            }

            _hasActiveSession.value = true
        } else {
            _hasActiveSession.value = false
        }

        scope.launch {
            // Prepopulate restaurants if empty in Room
            if (database.restaurantDao().countRestaurants() == 0) {
                database.restaurantDao().insertAll(SeedData.restaurants.map { RestaurantEntity.fromDomain(it) })
            }

            // Prepopulate products (restaurant menu items) if empty in Room
            if (database.productDao().countProducts() == 0) {
                database.productDao().insertAll(SeedData.products.map { ProductEntity.fromDomain(it) })
            }

            // Prepopulate seed reviews if empty in Room
            if (database.reviewDao().countReviews() == 0) {
                val seedReviews = listOf(
                    ReviewEntity(
                        id = "rev_1",
                        restaurantId = "rest_1",
                        orderId = "ord_101",
                        customerName = "محمود حسن",
                        rating = 5.0,
                        comment = "الأكل كان سخن وطعمه تحفة، والتوصيل سريع جداً! تسلم ايديكم",
                        date = "منذ يومين",
                        timestamp = System.currentTimeMillis() - 172800000
                    ),
                    ReviewEntity(
                        id = "rev_2",
                        restaurantId = "rest_1",
                        orderId = "ord_102",
                        customerName = "سارة أحمد",
                        rating = 4.5,
                        comment = "الكشري ممتاز والصلصة مظبوطة، شكراً لقمة",
                        date = "منذ 3 أيام",
                        timestamp = System.currentTimeMillis() - 259200000
                    ),
                    ReviewEntity(
                        id = "rev_3",
                        restaurantId = "rest_2",
                        orderId = "ord_103",
                        customerName = "كريم إبراهيم",
                        rating = 5.0,
                        comment = "أحلى برجر في مصر، الجبنة سايحة والعيش طري جداً",
                        date = "منذ أسبوع",
                        timestamp = System.currentTimeMillis() - 604800000
                    )
                )
                database.reviewDao().insertAll(seedReviews)
            }

            // No pre-seeded demo accounts. Users must register from scratch.

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
                    orderNumber = "#LQM-9820",
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

    // Unified Login System (Customer, Restaurant, or Admin)
    suspend fun login(identifier: String, password: String): LoginResult {
        val cleanIdent = identifier.trim().lowercase()
        val cleanPass = password.trim()

        if (cleanIdent.isBlank() || cleanPass.isBlank()) {
            return LoginResult.Error("يرجى إدخال رقم الموبايل أو البريد الإلكتروني وكلمة المرور")
        }

        // Database lookup
        var userAcc = database.userAccountDao().findByIdentifier(cleanIdent)



        if (userAcc == null) {
            return LoginResult.Error("لم يتم العثور على حساب مسجل بهذا الرقم أو البريد الإلكتروني.")
        }

        if (userAcc.passwordHash != cleanPass) {
            return LoginResult.Error("كلمة المرور غير صحيحة. يرجى المحاولة مرة أخرى.")
        }

        val regData = if (userAcc.role == UserRole.RESTAURANT_OWNER && userAcc.restaurantName != null) {
            RestaurantRegistrationData(
                restaurantName = userAcc.restaurantName,
                phone = userAcc.phone,
                cityArea = userAcc.cityArea,
                cuisine = userAcc.cuisine ?: "مأكولات متنوعة",
                logoIcon = userAcc.logoIcon ?: "🍔",
                minOrder = userAcc.minOrder,
                deliveryTimeMinutes = userAcc.deliveryTimeMinutes,
                status = userAcc.restaurantStatus ?: RestaurantStatus.PENDING,
                password = userAcc.passwordHash
            )
        } else null

        val userObj = User(
            id = userAcc.id,
            name = userAcc.name,
            phone = userAcc.phone,
            email = userAcc.email,
            role = userAcc.role,
            registeredRestaurant = regData
        )

        _currentUser.value = userObj
        saveFcmTokenForUser(userObj.id)

        if (regData != null) {
            scope.launch {
                val existing = database.restaurantDao().getAllRestaurants().first().find { it.name == regData.restaurantName }
                if (existing == null) {
                    val newRest = Restaurant(
                        id = "rest_custom_${regData.restaurantName.hashCode()}",
                        name = regData.restaurantName,
                        nameEn = regData.restaurantName,
                        logoUrl = regData.logoIcon,
                        coverUrl = "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=800&auto=format&fit=crop&q=80",
                        cuisines = listOf(regData.cuisine),
                        rating = 5.0,
                        reviewCount = 1,
                        deliveryTimeMinutes = regData.deliveryTimeMinutes,
                        deliveryFee = 15.0,
                        minOrder = regData.minOrder,
                        area = regData.cityArea,
                        isOpen = true,
                        isFeatured = true
                    )
                    database.restaurantDao().insertRestaurant(RestaurantEntity.fromDomain(newRest))
                }
            }
        }

        prefs?.edit()
            ?.putBoolean("is_logged_in", true)
            ?.putString("user_id", userAcc.id)
            ?.putString("user_role", userAcc.role.name)
            ?.putString("user_name", userAcc.name)
            ?.putString("user_phone", userAcc.phone)
            ?.putString("user_email", userAcc.email)
            ?.putString("user_city", userAcc.cityArea)
            ?.apply()

        if (regData != null) {
            prefs?.edit()
                ?.putString("rest_name", regData.restaurantName)
                ?.putString("rest_phone", regData.phone)
                ?.putString("rest_area", regData.cityArea)
                ?.putString("rest_cuisine", regData.cuisine)
                ?.putString("rest_logo", regData.logoIcon)
                ?.putString("rest_status", regData.status.name)
                ?.apply()
        }

        _hasActiveSession.value = true
        return LoginResult.Success(userObj)
    }

    // Customer Registration - STRICTLY CUSTOMER ONLY (No Admin escalation)
    fun registerCustomer(name: String, phone: String, city: String, password: String = "123456"): LoginResult {
        val cleanName = name.trim()
        val cleanPhone = phone.trim()
        val cleanPass = if (password.trim().isNotBlank()) password.trim() else "123456"

        val newAddress = Address(
            id = "addr_user_${System.currentTimeMillis()}",
            label = "المنزل",
            governorate = city,
            city = city,
            area = city,
            street = "الشارع الرئيسي",
            buildingNumber = "1",
            floor = "1",
            apartment = "1",
            landmark = "المدينة"
        )
        _selectedAddress.value = newAddress

        val userId = "cust_${System.currentTimeMillis()}"
        val userAcc = UserAccountEntity(
            id = userId,
            identifier = cleanPhone,
            name = cleanName,
            phone = cleanPhone,
            email = "$cleanPhone@lo2ma.click",
            passwordHash = cleanPass,
            role = UserRole.CUSTOMER, // STRICTLY CUSTOMER
            cityArea = city
        )

        val userObj = User(
            id = userId,
            name = cleanName,
            phone = cleanPhone,
            email = "$cleanPhone@lo2ma.click",
            role = UserRole.CUSTOMER,
            selectedAddressId = newAddress.id
        )
        _currentUser.value = userObj
        saveFcmTokenForUser(userObj.id)

        scope.launch {
            database.userAccountDao().insertUser(userAcc)
            database.addressDao().insertAddress(AddressEntity.fromDomain(newAddress))
        }

        prefs?.edit()
            ?.putBoolean("is_logged_in", true)
            ?.putString("user_id", userId)
            ?.putString("user_role", UserRole.CUSTOMER.name)
            ?.putString("user_name", cleanName)
            ?.putString("user_phone", cleanPhone)
            ?.putString("user_email", "$cleanPhone@lo2ma.click")
            ?.putString("user_city", city)
            ?.apply()

        _hasActiveSession.value = true
        return LoginResult.Success(userObj)
    }

    // Restaurant Partner Registration - STRICTLY RESTAURANT_OWNER ONLY
    fun registerRestaurant(data: RestaurantRegistrationData, password: String = "123456"): LoginResult {
        val restId = "rest_reg_${System.currentTimeMillis()}"
        val cleanPass = if (password.trim().isNotBlank()) password.trim() else "123456"
        val userId = "rest_owner_${System.currentTimeMillis()}"

        val userAcc = UserAccountEntity(
            id = userId,
            identifier = data.phone.trim(),
            name = "إدارة ${data.restaurantName}",
            phone = data.phone.trim(),
            email = "${data.phone.trim()}@lo2ma.click",
            passwordHash = cleanPass,
            role = UserRole.RESTAURANT_OWNER, // STRICTLY RESTAURANT_OWNER
            restaurantName = data.restaurantName,
            restaurantStatus = data.status,
            cityArea = data.cityArea,
            cuisine = data.cuisine,
            logoIcon = data.logoIcon,
            minOrder = data.minOrder,
            deliveryTimeMinutes = data.deliveryTimeMinutes
        )

        val newRest = Restaurant(
            id = restId,
            name = data.restaurantName,
            nameEn = data.restaurantName,
            logoUrl = data.logoIcon,
            coverUrl = "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=800&auto=format&fit=crop&q=80",
            cuisines = listOf(data.cuisine),
            rating = 5.0,
            reviewCount = 1,
            deliveryTimeMinutes = data.deliveryTimeMinutes,
            deliveryFee = 15.0,
            minOrder = data.minOrder,
            area = data.cityArea,
            isOpen = true,
            isFeatured = true
        )

        val sampleMenu = listOf(
            Product(
                id = "prod_${restId}_1",
                restaurantId = restId,
                categoryId = "الأطباق الرئيسية",
                name = "وجبة ${data.restaurantName} المميزة",
                description = "وجبة شهية ومحضرة بعناية فائقة وتوابل طازجة",
                price = 110.0,
                originalPrice = 135.0,
                imageUrl = "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=800&auto=format&fit=crop&q=80",
                isPopular = true,
                isAvailable = true
            ),
            Product(
                id = "prod_${restId}_2",
                restaurantId = restId,
                categoryId = "المقبلات والإضافات",
                name = "مقبلات وصوصات مشكلة",
                description = "أطباق مقبلات وصوصات طازجة لتكتمل وجبتك",
                price = 35.0,
                originalPrice = null,
                imageUrl = "https://images.unsplash.com/photo-1576107232684-1279f3908594?w=800&auto=format&fit=crop&q=80",
                isPopular = false,
                isAvailable = true
            )
        )

        val userObj = User(
            id = userId,
            name = "إدارة ${data.restaurantName}",
            phone = data.phone,
            role = UserRole.RESTAURANT_OWNER,
            registeredRestaurant = data
        )
        _currentUser.value = userObj
        saveFcmTokenForUser(userObj.id)

        scope.launch {
            database.userAccountDao().insertUser(userAcc)
            database.restaurantDao().insertRestaurant(RestaurantEntity.fromDomain(newRest))
            database.productDao().insertAll(sampleMenu.map { ProductEntity.fromDomain(it) })
        }

        prefs?.edit()
            ?.putBoolean("is_logged_in", true)
            ?.putString("user_id", userId)
            ?.putString("user_role", UserRole.RESTAURANT_OWNER.name)
            ?.putString("user_name", "إدارة ${data.restaurantName}")
            ?.putString("user_phone", data.phone)
            ?.putString("user_city", data.cityArea)
            ?.putString("rest_name", data.restaurantName)
            ?.putString("rest_phone", data.phone)
            ?.putString("rest_area", data.cityArea)
            ?.putString("rest_cuisine", data.cuisine)
            ?.putString("rest_logo", data.logoIcon)
            ?.putString("rest_status", data.status.name)
            ?.apply()

        _hasActiveSession.value = true
        return LoginResult.Success(userObj)
    }

    fun registerCourier(name: String, phone: String, city: String, password: String = "123456"): LoginResult {
        val cleanName = name.trim()
        val cleanPhone = phone.trim()
        val cleanPass = if (password.trim().isNotBlank()) password.trim() else "123456"

        val userId = "courier_${System.currentTimeMillis()}"
        val userAcc = UserAccountEntity(
            id = userId,
            identifier = cleanPhone,
            name = cleanName,
            phone = cleanPhone,
            email = "$cleanPhone@lo2ma.click",
            passwordHash = cleanPass,
            role = UserRole.COURIER,
            cityArea = city
        )

        val userObj = User(
            id = userId,
            name = cleanName,
            phone = cleanPhone,
            email = "$cleanPhone@lo2ma.click",
            role = UserRole.COURIER
        )
        _currentUser.value = userObj
        saveFcmTokenForUser(userObj.id)

        scope.launch {
            database.userAccountDao().insertUser(userAcc)
        }

        prefs?.edit()
            ?.putBoolean("is_logged_in", true)
            ?.putString("user_id", userId)
            ?.putString("user_role", UserRole.COURIER.name)
            ?.putString("user_name", cleanName)
            ?.putString("user_phone", cleanPhone)
            ?.putString("user_email", "$cleanPhone@lo2ma.click")
            ?.putString("user_city", city)
            ?.apply()

        _hasActiveSession.value = true
        return LoginResult.Success(userObj)
    }

    fun approveRestaurantApplication() {
        val currentRest = _currentUser.value.registeredRestaurant ?: return
        val updatedRest = currentRest.copy(status = RestaurantStatus.APPROVED)
        _currentUser.value = _currentUser.value.copy(registeredRestaurant = updatedRest)
        prefs?.edit()
            ?.putString("rest_status", RestaurantStatus.APPROVED.name)
            ?.apply()

        scope.launch {
            database.userAccountDao().updateRestaurantStatusByName(updatedRest.restaurantName, RestaurantStatus.APPROVED)
            val notif = NotificationEntity(
                id = "notif_appr_${System.currentTimeMillis()}",
                title = "تهانينا! تم اعتماد مطعم ${updatedRest.restaurantName} بنجاح 🎉",
                body = "تمت مراجعة بيانات المطعم والموافقة عليها. لوحة التحكم الآن متصلة ومباشرة للبدء في استقبال طلبات الزبائن.",
                timeAgo = "الآن",
                isRead = false,
                orderId = null
            )
            database.notificationDao().insertNotification(notif)
        }
    }

    fun logoutSession() {
        prefs?.edit()?.clear()?.apply()
        _hasActiveSession.value = false
        _currentUser.value = User(
            id = "cust_default",
            name = "أحمد مصطفى",
            phone = "01098765432",
            role = UserRole.CUSTOMER
        )
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
        if (query.isBlank()) return Pair(restaurants.value, emptyList())
        val normQuery = normalizeArabic(query.trim().lowercase())

        val matchedRestaurants = restaurants.value.filter { rest ->
            val nameNorm = normalizeArabic(rest.name.lowercase())
            val nameEnNorm = rest.nameEn.lowercase()
            val cuisinesNorm = rest.cuisines.map { normalizeArabic(it.lowercase()) }

            nameNorm.contains(normQuery) ||
                    nameEnNorm.contains(normQuery) ||
                    cuisinesNorm.any { it.contains(normQuery) } ||
                    isSynonymMatch(normQuery, nameNorm)
        }

        val matchedProducts = products.value.filter { prod ->
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
        val rest = restaurants.value.find { it.id == restId }
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
        notes: String,
        paymobResult: PaymobPaymentResult? = null
    ): Order? {
        val items = _cartItems.value
        if (items.isEmpty()) return null

        val restId = items.first().restaurantId
        val rest = restaurants.value.find { it.id == restId } ?: return null
        val (subtotal, deliveryFee, discount) = getCartSummary()
        val serviceFee = 5.0
        val total = (subtotal + deliveryFee + serviceFee - discount).coerceAtLeast(0.0)

        val orderNumber = "#MNY-${(1000..9999).random()}"
        val orderId = "ord_${System.currentTimeMillis()}"
        val currentAddr = _selectedAddress.value

        val paymentStatusText = when {
            paymobResult != null && paymobResult.isSuccess -> "PAID_ONLINE_PAYMOB"
            paymentMethod == PaymentMethod.ONLINE_CARD_PAYMOB -> "PAID_ONLINE_PAYMOB"
            paymentMethod == PaymentMethod.CASH_ON_DELIVERY -> "COD_PENDING"
            else -> "PAID_ONLINE"
        }

        val txnId = paymobResult?.transactionId ?: if (paymentMethod == PaymentMethod.ONLINE_CARD_PAYMOB) "PMOB-TXN-${(100000..999999).random()}" else null
        val maskedCard = paymobResult?.maskedPan ?: if (paymentMethod == PaymentMethod.ONLINE_CARD_PAYMOB) "**** **** **** 4242" else null

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
            paymentStatus = paymentStatusText,
            paymobTransactionId = txnId,
            maskedCardNumber = maskedCard,
            status = OrderStatus.PLACED,
            courierName = "كابتن إبراهيم حسن",
            courierPhone = "01012345678",
            courierVehicle = "سكوتر بينيلي رمادي",
            createdAt = System.currentTimeMillis(),
            estimatedMinutes = rest.deliveryTimeMinutes,
            deliveryNotes = notes
        )

        database.orderDao().insertOrder(orderEntity)

        try {
            val firestore = FirebaseFirestore.getInstance()
            val orderMap = hashMapOf(
                "id" to orderId,
                "orderNumber" to orderNumber,
                "customerId" to _currentUser.value.id,
                "restaurantId" to rest.id,
                "status" to OrderStatus.PLACED.name,
                "total" to total,
                "timestamp" to System.currentTimeMillis()
            )
            firestore.collection("orders").document(orderId).set(orderMap)
        } catch (_: Exception) {}

        // Add notification with payment details
        val notifBody = if (txnId != null) {
            "تم دفع ${total.toInt()} ج.م بنجاح عبر بوابة Paymob (رقم العملية: $txnId). طلبك من ${rest.name} قيد التحضير."
        } else {
            "طلبك من ${rest.name} جاري إرساله للمطعم للتجهيز (الدفع عند الاستلام)."
        }

        val notif = NotificationEntity(
            id = "notif_${System.currentTimeMillis()}",
            title = "تم تأكيد طلبك بنجاح! $orderNumber",
            body = notifBody,
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

        try {
            val firestore = FirebaseFirestore.getInstance()
            firestore.collection("orders").document(orderId).update("status", nextStatus.name)
        } catch (_: Exception) {}

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

    suspend fun createSampleIncomingOrder(restaurantId: String? = null) {
        val targetRest = restaurants.value.find { it.id == restaurantId } ?: restaurants.value.first()
        val restProducts = products.value.filter { it.restaurantId == targetRest.id }.ifEmpty { products.value }
        val randomProduct = restProducts.random()
        val orderNum = "#MNY-${(1000..9999).random()}"
        val orderId = "ord_sample_${System.currentTimeMillis()}"

        val sampleCustomerNames = listOf("أحمد مصطفى", "محمد عبد الرحمن", "سارة إبراهيم", "طارق فهمي", "ياسمين كمال")
        val samplePhones = listOf("01019283746", "01128374650", "01239485712", "01556677889")
        val custName = sampleCustomerNames.random()
        val custPhone = samplePhones.random()

        val sampleItem = CartItem(
            cartItemId = "item_${System.currentTimeMillis()}",
            product = randomProduct,
            restaurantId = targetRest.id,
            restaurantName = targetRest.name,
            quantity = (1..3).random(),
            selectedModifiers = emptyList(),
            notes = if (listOf(true, false).random()) "الرجاء زيادة الصوص والمناديل" else ""
        )

        val subtotal = sampleItem.totalPrice
        val deliveryFee = targetRest.deliveryFee
        val total = subtotal + deliveryFee

        val newOrder = OrderEntity(
            id = orderId,
            orderNumber = orderNum,
            customerId = "cust_${System.currentTimeMillis()}",
            customerName = custName,
            customerPhone = custPhone,
            restaurantId = targetRest.id,
            restaurantName = targetRest.name,
            restaurantArea = targetRest.area,
            deliveryAddressId = "addr_1",
            deliveryAddressText = "القاهرة - مصر الجديدة - شارع الميرغني عمارة 12",
            items = listOf(sampleItem),
            subtotal = subtotal,
            deliveryFee = deliveryFee,
            serviceFee = 5.0,
            discount = 0.0,
            total = total,
            paymentMethod = if (listOf(true, false).random()) PaymentMethod.ONLINE_CARD_PAYMOB else PaymentMethod.CASH_ON_DELIVERY,
            paymentStatus = "PAID",
            status = OrderStatus.PLACED,
            courierName = "كابتن إبراهيم حسن",
            courierPhone = "01012345678",
            courierVehicle = "سكوتر بينيلي رمادي",
            createdAt = System.currentTimeMillis(),
            estimatedMinutes = targetRest.deliveryTimeMinutes,
            deliveryNotes = "الاتصال عند الوصول أمام العمارة"
        )

        database.orderDao().insertOrder(newOrder)

        val notif = NotificationEntity(
            id = "notif_${System.currentTimeMillis()}",
            title = "طلب جديد وصل للمطعم! $orderNum 🛎️",
            body = "العميل $custName طلب ${sampleItem.quantity}x ${sampleItem.product.name} بإجمالي ${total.toInt()} ج.م",
            timeAgo = "الآن",
            isRead = false,
            orderId = orderId
        )
        database.notificationDao().insertNotification(notif)
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

    // Restaurant Owner Menu management with Room DB persistence
    fun addOrUpdateProduct(product: Product) {
        scope.launch {
            database.productDao().insertProduct(ProductEntity.fromDomain(product))
        }
    }

    fun toggleProductAvailability(productId: String) {
        scope.launch {
            val current = products.value.find { it.id == productId } ?: return@launch
            database.productDao().updateProductAvailability(productId, !current.isAvailable)
        }
    }

    fun submitReview(
        restaurantId: String,
        orderId: String,
        customerName: String,
        rating: Double,
        comment: String,
        onComplete: () -> Unit = {}
    ) {
        scope.launch {
            val revId = "rev_${System.currentTimeMillis()}"
            val reviewEntity = ReviewEntity(
                id = revId,
                restaurantId = restaurantId,
                orderId = orderId,
                customerName = customerName,
                rating = rating,
                comment = comment,
                date = "الآن",
                timestamp = System.currentTimeMillis()
            )
            database.reviewDao().insertReview(reviewEntity)

            // Update restaurant rating & review count
            val restEntity = database.restaurantDao().getRestaurantById(restaurantId).first()
            if (restEntity != null) {
                val currentCount = restEntity.reviewCount
                val currentRating = restEntity.rating
                val newCount = currentCount + 1
                val newRating = ((currentRating * currentCount) + rating) / newCount
                val updatedRest = restEntity.copy(
                    rating = String.format(java.util.Locale.US, "%.1f", newRating).toDouble(),
                    reviewCount = newCount
                )
                database.restaurantDao().insertRestaurant(updatedRest)
            }
            onComplete()
        }
    }

    private fun syncWithFirestore() {
        try {
            val firestore = FirebaseFirestore.getInstance()
            
            // Fetch and listen to restaurants collection
            firestore.collection("restaurants").addSnapshotListener { snapshot, _ ->
                if (snapshot != null && !snapshot.isEmpty) {
                    scope.launch(Dispatchers.IO) {
                        val restEntities = snapshot.documents.mapNotNull { doc ->
                            val id = doc.getString("id") ?: doc.id
                            val name = doc.getString("name") ?: return@mapNotNull null
                            val rating = doc.getDouble("rating") ?: 4.8
                            val reviewCount = doc.getLong("reviewCount")?.toInt() ?: 100
                            val deliveryTimeMinutes = doc.getLong("deliveryTimeMinutes")?.toInt() ?: 30
                            val deliveryFee = doc.getDouble("deliveryFee") ?: 15.0
                            val imageRes = doc.getString("imageRes") ?: "https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=600"
                            val cuisines = doc.get("cuisines") as? List<String> ?: listOf("مأكولات مصرية")
                            val isOpen = doc.getBoolean("isOpen") ?: true
                            RestaurantEntity(
                                id = id,
                                name = name,
                                nameEn = doc.getString("nameEn") ?: name,
                                logoUrl = doc.getString("logoUrl") ?: "🍔",
                                coverUrl = doc.getString("coverUrl") ?: "https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=600",
                                cuisines = doc.get("cuisines") as? List<String> ?: listOf("مأكولات مصرية"),
                                rating = rating,
                                reviewCount = reviewCount,
                                deliveryTimeMinutes = deliveryTimeMinutes,
                                deliveryFee = deliveryFee,
                                minOrder = doc.getDouble("minOrder") ?: 30.0,
                                area = doc.getString("area") ?: "القاهرة",
                                discountBadge = doc.getString("discountBadge"),
                                isOpen = isOpen,
                                isFeatured = doc.getBoolean("isFeatured") ?: false
                            )
                        }
                        if (restEntities.isNotEmpty()) {
                            database.restaurantDao().insertAll(restEntities)
                        }
                    }
                }
            }

            // Fetch and listen to products (menus) collection
            firestore.collection("products").addSnapshotListener { snapshot, _ ->
                if (snapshot != null && !snapshot.isEmpty) {
                    scope.launch(Dispatchers.IO) {
                        val prodEntities = snapshot.documents.mapNotNull { doc ->
                            val id = doc.getString("id") ?: doc.id
                            val restaurantId = doc.getString("restaurantId") ?: "rest_1"
                            val name = doc.getString("name") ?: return@mapNotNull null
                            val description = doc.getString("description") ?: ""
                            val price = doc.getDouble("price") ?: 50.0
                            val imageRes = doc.getString("imageRes") ?: "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=600"
                            val category = doc.getString("category") ?: "الرئيسية"
                            val isPopular = doc.getBoolean("isPopular") ?: false
                            ProductEntity(
                                id = id,
                                restaurantId = restaurantId,
                                categoryId = doc.getString("categoryId") ?: "cat_1",
                                name = name,
                                description = description,
                                price = price,
                                originalPrice = doc.getDouble("originalPrice"),
                                imageUrl = doc.getString("imageUrl") ?: "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=600",
                                isPopular = isPopular,
                                isAvailable = doc.getBoolean("isAvailable") ?: true,
                                modifierGroups = emptyList()
                            )
                        }
                        if (prodEntities.isNotEmpty()) {
                            database.productDao().insertAll(prodEntities)
                        }
                    }
                }
            }

            // Listen to orders for real-time delivery status updates
            firestore.collection("orders").addSnapshotListener { snapshot, _ ->
                if (snapshot != null && !snapshot.isEmpty) {
                    scope.launch(Dispatchers.IO) {
                        for (doc in snapshot.documents) {
                            val orderId = doc.getString("id") ?: doc.id
                            val statusStr = doc.getString("status")
                            if (statusStr != null) {
                                try {
                                    val status = OrderStatus.valueOf(statusStr)
                                    database.orderDao().updateOrderStatus(orderId, status)
                                } catch (_: Exception) {}
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            // Graceful fallback to local Room and seed data if Firestore is unconfigured or offline
        }
    }

    private fun saveFcmTokenForUser(userId: String) {
        try {
            com.google.firebase.messaging.FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                if (!token.isNullOrBlank()) {
                    val firestore = FirebaseFirestore.getInstance()
                    val userMap = hashMapOf(
                        "id" to userId,
                        "name" to _currentUser.value.name,
                        "phone" to _currentUser.value.phone,
                        "email" to _currentUser.value.email,
                        "role" to _currentUser.value.role.name,
                        "fcmToken" to token,
                        "updatedAt" to System.currentTimeMillis()
                    )
                    firestore.collection("users").document(userId)
                        .set(userMap, com.google.firebase.firestore.SetOptions.merge())
                }
            }
        } catch (_: Exception) {}
    }

    companion object {
        @Volatile
        private var INSTANCE: MinyooRepository? = null

        fun getInstance(context: Context): MinyooRepository {
            return INSTANCE ?: synchronized(this) {
                val db = AppDatabase.getDatabase(context)
                val instance = MinyooRepository(db, context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}

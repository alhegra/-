package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.Address
import com.example.data.model.CartItem
import com.example.data.model.Coupon
import com.example.data.model.NotificationItem
import com.example.data.model.Order
import com.example.data.model.OrderStatus
import com.example.data.model.PaymentMethod
import com.example.data.model.Product
import com.example.data.model.Restaurant
import com.example.data.model.RestaurantStatus
import com.example.data.model.Review
import com.example.data.model.SupportTicket
import com.example.data.model.UserRole

@Entity(tableName = "user_accounts")
data class UserAccountEntity(
    @PrimaryKey val id: String,
    val identifier: String, // phone or email (lowercased)
    val name: String,
    val phone: String,
    val email: String,
    val passwordHash: String,
    val role: UserRole,
    val restaurantName: String? = null,
    val restaurantStatus: RestaurantStatus? = null,
    val cityArea: String = "القاهرة",
    val cuisine: String? = null,
    val logoIcon: String? = null,
    val minOrder: Double = 50.0,
    val deliveryTimeMinutes: Int = 30,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "addresses")
data class AddressEntity(
    @PrimaryKey val id: String,
    val label: String,
    val governorate: String,
    val city: String,
    val area: String,
    val street: String,
    val buildingNumber: String,
    val floor: String,
    val apartment: String,
    val landmark: String,
    val deliveryInstructions: String,
    val latitude: Double,
    val longitude: Double
) {
    fun toDomain(): Address = Address(
        id = id,
        label = label,
        governorate = governorate,
        city = city,
        area = area,
        street = street,
        buildingNumber = buildingNumber,
        floor = floor,
        apartment = apartment,
        landmark = landmark,
        deliveryInstructions = deliveryInstructions,
        latitude = latitude,
        longitude = longitude
    )

    companion object {
        fun fromDomain(addr: Address): AddressEntity = AddressEntity(
            id = addr.id,
            label = addr.label,
            governorate = addr.governorate,
            city = addr.city,
            area = addr.area,
            street = addr.street,
            buildingNumber = addr.buildingNumber,
            floor = addr.floor,
            apartment = addr.apartment,
            landmark = addr.landmark,
            deliveryInstructions = addr.deliveryInstructions,
            latitude = addr.latitude,
            longitude = addr.longitude
        )
    }
}

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String,
    val orderNumber: String,
    val customerId: String,
    val customerName: String,
    val customerPhone: String,
    val restaurantId: String,
    val restaurantName: String,
    val restaurantArea: String,
    val deliveryAddressId: String,
    val deliveryAddressText: String,
    val items: List<CartItem>,
    val subtotal: Double,
    val deliveryFee: Double,
    val serviceFee: Double,
    val discount: Double,
    val total: Double,
    val paymentMethod: PaymentMethod,
    val paymentStatus: String,
    val paymobTransactionId: String? = null,
    val maskedCardNumber: String? = null,
    val status: OrderStatus,
    val courierName: String,
    val courierPhone: String,
    val courierVehicle: String,
    val createdAt: Long,
    val estimatedMinutes: Int,
    val deliveryNotes: String
) {
    fun toDomain(address: Address): Order = Order(
        id = id,
        orderNumber = orderNumber,
        customerId = customerId,
        customerName = customerName,
        customerPhone = customerPhone,
        restaurantId = restaurantId,
        restaurantName = restaurantName,
        restaurantArea = restaurantArea,
        deliveryAddress = address,
        items = items,
        subtotal = subtotal,
        deliveryFee = deliveryFee,
        serviceFee = serviceFee,
        discount = discount,
        total = total,
        paymentMethod = paymentMethod,
        paymentStatus = paymentStatus,
        paymobTransactionId = paymobTransactionId,
        maskedCardNumber = maskedCardNumber,
        status = status,
        courierName = courierName,
        courierPhone = courierPhone,
        courierVehicle = courierVehicle,
        createdAt = createdAt,
        estimatedMinutes = estimatedMinutes,
        deliveryNotes = deliveryNotes
    )
}

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val id: String, // format: "restaurant_id" or "product_id"
    val isRestaurant: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "support_tickets")
data class SupportTicketEntity(
    @PrimaryKey val id: String,
    val orderId: String?,
    val issueType: String,
    val message: String,
    val status: String,
    val timestamp: Long
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val body: String,
    val timeAgo: String,
    val isRead: Boolean,
    val orderId: String?
)

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey val id: String,
    val restaurantId: String,
    val orderId: String,
    val customerName: String,
    val rating: Double,
    val comment: String,
    val date: String,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun toDomain(): Review = Review(
        id = id,
        restaurantId = restaurantId,
        orderId = orderId,
        customerName = customerName,
        rating = rating,
        comment = comment,
        date = date
    )

    companion object {
        fun fromDomain(rev: Review): ReviewEntity = ReviewEntity(
            id = rev.id,
            restaurantId = rev.restaurantId,
            orderId = rev.orderId,
            customerName = rev.customerName,
            rating = rev.rating,
            comment = rev.comment,
            date = rev.date,
            timestamp = System.currentTimeMillis()
        )
    }
}

@Entity(tableName = "restaurants")
data class RestaurantEntity(
    @PrimaryKey val id: String,
    val name: String,
    val nameEn: String,
    val logoUrl: String,
    val coverUrl: String,
    val cuisines: List<String>,
    val rating: Double,
    val reviewCount: Int,
    val deliveryTimeMinutes: Int,
    val deliveryFee: Double,
    val minOrder: Double,
    val area: String,
    val discountBadge: String? = null,
    val isOpen: Boolean = true,
    val isFeatured: Boolean = false
) {
    fun toDomain(): Restaurant = Restaurant(
        id = id,
        name = name,
        nameEn = nameEn,
        logoUrl = logoUrl,
        coverUrl = coverUrl,
        cuisines = cuisines,
        rating = rating,
        reviewCount = reviewCount,
        deliveryTimeMinutes = deliveryTimeMinutes,
        deliveryFee = deliveryFee,
        minOrder = minOrder,
        area = area,
        discountBadge = discountBadge,
        isOpen = isOpen,
        isFeatured = isFeatured
    )

    companion object {
        fun fromDomain(rest: Restaurant): RestaurantEntity = RestaurantEntity(
            id = rest.id,
            name = rest.name,
            nameEn = rest.nameEn,
            logoUrl = rest.logoUrl,
            coverUrl = rest.coverUrl,
            cuisines = rest.cuisines,
            rating = rest.rating,
            reviewCount = rest.reviewCount,
            deliveryTimeMinutes = rest.deliveryTimeMinutes,
            deliveryFee = rest.deliveryFee,
            minOrder = rest.minOrder,
            area = rest.area,
            discountBadge = rest.discountBadge,
            isOpen = rest.isOpen,
            isFeatured = rest.isFeatured
        )
    }
}

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val restaurantId: String,
    val categoryId: String,
    val name: String,
    val description: String,
    val price: Double,
    val originalPrice: Double? = null,
    val imageUrl: String,
    val isPopular: Boolean = false,
    val isAvailable: Boolean = true,
    val modifierGroups: List<com.example.data.model.ModifierGroup> = emptyList()
) {
    fun toDomain(): Product = Product(
        id = id,
        restaurantId = restaurantId,
        categoryId = categoryId,
        name = name,
        description = description,
        price = price,
        originalPrice = originalPrice,
        imageUrl = imageUrl,
        isPopular = isPopular,
        isAvailable = isAvailable,
        modifierGroups = modifierGroups
    )

    companion object {
        fun fromDomain(prod: Product): ProductEntity = ProductEntity(
            id = prod.id,
            restaurantId = prod.restaurantId,
            categoryId = prod.categoryId,
            name = prod.name,
            description = prod.description,
            price = prod.price,
            originalPrice = prod.originalPrice,
            imageUrl = prod.imageUrl,
            isPopular = prod.isPopular,
            isAvailable = prod.isAvailable,
            modifierGroups = prod.modifierGroups
        )
    }
}


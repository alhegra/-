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

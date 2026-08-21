package com.example.data.model

enum class UserRole(val titleAr: String, val titleEn: String) {
    CUSTOMER("عميل", "Customer"),
    RESTAURANT_OWNER("صاحب مطعم", "Restaurant Owner"),
    COURIER("مندوب توصيل", "Courier / Driver"),
    ADMIN("لوحة الإدارة", "Admin Dashboard")
}

data class User(
    val id: String = "cust_1",
    val name: String = "أحمد مصطفى",
    val phone: String = "01098765432",
    val email: String = "ahmed@example.com",
    val role: UserRole = UserRole.CUSTOMER,
    val selectedAddressId: String = "addr_1"
)

data class Address(
    val id: String,
    val label: String, // "البيت", "الشغل", "بيت العيلة"
    val governorate: String = "القاهرة",
    val city: String = "مدينة نصر",
    val area: String = "منطقة عباس العقاد",
    val street: String = "شارع سمير عبد الرؤوف",
    val buildingNumber: String = "عمارة 24",
    val floor: String = "الدور الرابع",
    val apartment: String = "شقة 12",
    val landmark: String = "بجوار صيدلية العزبي وسوبرماركت سعودي",
    val deliveryInstructions: String = "يرجى رن الجرس وتركه أمام الباب",
    val latitude: Double = 30.0561,
    val longitude: Double = 31.3412
) {
    val fullAddressText: String
        get() = "$area، $street، $buildingNumber، $floor، $apartment ($landmark)"
}

data class ModifierOption(
    val id: String,
    val name: String,
    val priceModifier: Double = 0.0
)

data class ModifierGroup(
    val id: String,
    val title: String,
    val isRequired: Boolean = false,
    val minSelection: Int = 0,
    val maxSelection: Int = 1,
    val options: List<ModifierOption> = emptyList()
)

data class Product(
    val id: String,
    val restaurantId: String,
    val categoryId: String,
    val name: String,
    val description: String,
    val price: Double,
    val originalPrice: Double? = null,
    val imageUrl: String,
    val isPopular: Boolean = false,
    val isAvailable: Boolean = true,
    val modifierGroups: List<ModifierGroup> = emptyList()
)

data class MenuCategory(
    val id: String,
    val restaurantId: String,
    val name: String,
    val sortOrder: Int = 0
)

data class Restaurant(
    val id: String,
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
)

data class SelectedModifier(
    val groupId: String,
    val groupTitle: String,
    val optionId: String,
    val optionName: String,
    val price: Double
)

data class CartItem(
    val cartItemId: String,
    val product: Product,
    val restaurantId: String,
    val restaurantName: String,
    val quantity: Int = 1,
    val selectedModifiers: List<SelectedModifier> = emptyList(),
    val notes: String = ""
) {
    val unitPriceWithModifiers: Double
        get() = product.price + selectedModifiers.sumOf { it.price }

    val totalPrice: Double
        get() = unitPriceWithModifiers * quantity
}

enum class PaymentMethod(val titleAr: String, val titleEn: String, val iconName: String) {
    CASH_ON_DELIVERY("كاش عند الاستلام", "Cash on Delivery", "cash"),
    VODAFONE_CASH("محفظة إلكترونية (فودافون كاش / أورنج)", "Smart Wallet / Vodafone Cash", "wallet"),
    INSTAPAY("إنستاباي InstaPay", "InstaPay", "bank"),
    CREDIT_CARD("بطاقة بنكية (فيزا / ماستركارد)", "Credit / Debit Card", "card")
}

enum class OrderStatus(val titleAr: String, val descriptionAr: String, val stepIndex: Int) {
    PLACED("تم استلام الطلب", "طلبك وصل وجاري إرساله للمطعم", 0),
    CONFIRMED("المطعم أكد الطلب", "المطعم وافق على الطلب وسيبدأ التجهيز", 1),
    PREPARING("جاري التحضير", "الشيف بيجهز طلبك بأعلى جودة", 2),
    COURIER_ASSIGNED("المندوب في الطريق للمطعم", "الكابتن استلم الطلب ويتجه للمطعم", 3),
    PICKED_UP("المندوب استلم الأكل", "الكابتن استلم طلبك السخن من المطعم", 4),
    OUT_FOR_DELIVERY("في الطريق إليك", "الكابتن قرب يوصل لعنوانك", 5),
    DELIVERED("تم التسليم بنجاح", "ألف هنا وشفا! نتمنى الوجبة تعجبك", 6),
    CANCELLED("تم إلغاء الطلب", "تم إلغاء هذا الطلب", -1)
}

data class OrderTimelineEvent(
    val status: OrderStatus,
    val timeFormatted: String,
    val isCompleted: Boolean = false,
    val isCurrent: Boolean = false
)

data class Order(
    val id: String,
    val orderNumber: String, // e.g. #MNY-1082
    val customerId: String,
    val customerName: String,
    val customerPhone: String,
    val restaurantId: String,
    val restaurantName: String,
    val restaurantArea: String,
    val deliveryAddress: Address,
    val items: List<CartItem>,
    val subtotal: Double,
    val deliveryFee: Double,
    val serviceFee: Double = 5.0,
    val discount: Double = 0.0,
    val total: Double,
    val paymentMethod: PaymentMethod,
    val paymentStatus: String = "PAID_OR_COD",
    val status: OrderStatus = OrderStatus.PLACED,
    val courierName: String = "كابتن محمود علي",
    val courierPhone: String = "01122334455",
    val courierVehicle: String = "موتوسيكل هوندا أحمر",
    val createdAt: Long = System.currentTimeMillis(),
    val estimatedMinutes: Int = 35,
    val deliveryNotes: String = ""
)

data class Coupon(
    val code: String,
    val discountPercentage: Double = 0.0,
    val fixedDiscount: Double = 0.0,
    val minOrder: Double = 100.0,
    val maxDiscount: Double = 50.0,
    val description: String,
    val isFirstOrderOnly: Boolean = false
)

data class Review(
    val id: String,
    val restaurantId: String,
    val orderId: String,
    val customerName: String,
    val rating: Double,
    val comment: String,
    val date: String
)

data class SupportTicket(
    val id: String,
    val orderId: String?,
    val issueType: String,
    val message: String,
    val status: String = "OPEN", // "OPEN", "RESOLVED"
    val timestamp: Long = System.currentTimeMillis()
)

data class NotificationItem(
    val id: String,
    val title: String,
    val body: String,
    val timeAgo: String,
    val isRead: Boolean = false,
    val orderId: String? = null
)

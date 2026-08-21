package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.OrderStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface AddressDao {
    @Query("SELECT * FROM addresses")
    fun getAllAddresses(): Flow<List<AddressEntity>>

    @Query("SELECT * FROM addresses WHERE id = :id LIMIT 1")
    suspend fun getAddressById(id: String): AddressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddress(address: AddressEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(addresses: List<AddressEntity>)

    @Query("DELETE FROM addresses WHERE id = :id")
    suspend fun deleteAddress(id: String)
}

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE id = :id LIMIT 1")
    fun getOrderById(id: String): Flow<OrderEntity?>

    @Query("SELECT * FROM orders WHERE restaurantId = :restaurantId ORDER BY createdAt DESC")
    fun getOrdersByRestaurant(restaurantId: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE status NOT IN ('DELIVERED', 'CANCELLED') ORDER BY createdAt DESC")
    fun getActiveOrders(): Flow<List<OrderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(orders: List<OrderEntity>)

    @Update
    suspend fun updateOrder(order: OrderEntity)

    @Query("UPDATE orders SET status = :newStatus WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: String, newStatus: OrderStatus)
}

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE id = :id)")
    fun isFavorite(id: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(fav: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE id = :id")
    suspend fun removeFavorite(id: String)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY id DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(notifications: List<NotificationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: String)
}

@Dao
interface SupportDao {
    @Query("SELECT * FROM support_tickets ORDER BY timestamp DESC")
    fun getAllTickets(): Flow<List<SupportTicketEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicket(ticket: SupportTicketEntity)

    @Query("UPDATE support_tickets SET status = :status WHERE id = :id")
    suspend fun updateTicketStatus(id: String, status: String)
}

@Dao
interface RestaurantDao {
    @Query("SELECT * FROM restaurants")
    fun getAllRestaurants(): Flow<List<RestaurantEntity>>

    @Query("SELECT * FROM restaurants WHERE id = :id LIMIT 1")
    fun getRestaurantById(id: String): Flow<RestaurantEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRestaurant(restaurant: RestaurantEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(restaurants: List<RestaurantEntity>)

    @Query("DELETE FROM restaurants WHERE id = :id")
    suspend fun deleteRestaurant(id: String)

    @Query("SELECT COUNT(*) FROM restaurants")
    suspend fun countRestaurants(): Int
}

@Dao
interface ProductDao {
    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE restaurantId = :restaurantId")
    fun getProductsByRestaurant(restaurantId: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    fun getProductById(id: String): Flow<ProductEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<ProductEntity>)

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Query("UPDATE products SET isAvailable = :isAvailable WHERE id = :productId")
    suspend fun updateProductAvailability(productId: String, isAvailable: Boolean)

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteProduct(id: String)

    @Query("SELECT COUNT(*) FROM products")
    suspend fun countProducts(): Int
}


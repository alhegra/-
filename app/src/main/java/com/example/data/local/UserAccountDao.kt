package com.example.data.local

import androidx.room.*
import com.example.data.model.RestaurantStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface UserAccountDao {
    @Query("SELECT * FROM user_accounts WHERE LOWER(identifier) = LOWER(:ident) OR LOWER(phone) = LOWER(:ident) OR LOWER(email) = LOWER(:ident) LIMIT 1")
    suspend fun findByIdentifier(ident: String): UserAccountEntity?

    @Query("SELECT * FROM user_accounts WHERE id = :id LIMIT 1")
    suspend fun findById(id: String): UserAccountEntity?

    @Query("SELECT * FROM user_accounts")
    fun getAllUsersFlow(): Flow<List<UserAccountEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserAccountEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<UserAccountEntity>)

    @Query("SELECT COUNT(*) FROM user_accounts")
    suspend fun countUsers(): Int

    @Query("UPDATE user_accounts SET restaurantStatus = :status WHERE id = :userId")
    suspend fun updateRestaurantStatus(userId: String, status: RestaurantStatus)

    @Query("UPDATE user_accounts SET restaurantStatus = :status WHERE restaurantName = :restName")
    suspend fun updateRestaurantStatusByName(restName: String, status: RestaurantStatus)
}

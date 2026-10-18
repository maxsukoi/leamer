package com.cinebook.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User): Long

    @Query("SELECT * FROM users WHERE username = :username AND passwordHash = :hash LIMIT 1")
    suspend fun login(username: String, hash: String): User?

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun findByUsername(username: String): User?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): User?
}

@Dao
interface MovieDao {
    @Query("SELECT * FROM movies ORDER BY id")
    fun getAll(): Flow<List<Movie>>

    @Query("SELECT * FROM movies WHERE id = :id LIMIT 1")
    fun getById(id: Int): Flow<Movie?>

    @Query("SELECT COUNT(*) FROM movies")
    suspend fun count(): Int

    @Insert
    suspend fun insertAll(movies: List<Movie>)
}

@Dao
interface BookingDao {
    @Insert
    suspend fun insert(booking: Booking): Long

    @Delete
    suspend fun delete(booking: Booking)

    @Query("SELECT * FROM bookings WHERE userId = :userId ORDER BY createdAt DESC")
    fun getByUser(userId: Int): Flow<List<Booking>>

    @Query("SELECT * FROM bookings WHERE movieId = :movieId AND showtime = :showtime")
    fun getByShow(movieId: Int, showtime: String): Flow<List<Booking>>

    @Query("SELECT * FROM bookings WHERE movieId = :movieId AND showtime = :showtime")
    suspend fun getByShowOnce(movieId: Int, showtime: String): List<Booking>
}

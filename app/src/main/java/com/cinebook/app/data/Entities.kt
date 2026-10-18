package com.cinebook.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// data class #1 : ผู้ใช้งาน
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val passwordHash: String,
    val displayName: String
)

// data class #2 : ภาพยนตร์
@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val genre: String,
    val durationMin: Int,
    val rating: Double,
    val synopsis: String,
    val colorStart: Long,
    val colorEnd: Long,
    val emoji: String,
    val isNowShowing: Boolean = true,
    val releaseDate: String = "กำลังฉาย"
)

// data class #3 : การจองตั๋ว
@Entity(tableName = "bookings")
data class Booking(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val movieId: Int,
    val movieTitle: String,
    val showtime: String,
    val seats: String, // เก็บเป็นข้อความคั่นด้วย comma เช่น "A1,A2"
    val totalPrice: Int,
    val paymentMethod: String = "PromptPay",
    val createdAt: Long
)

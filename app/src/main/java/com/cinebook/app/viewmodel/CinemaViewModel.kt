package com.cinebook.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.cinebook.app.data.AppDatabase
import com.cinebook.app.data.Booking
import com.cinebook.app.data.Movie
import com.cinebook.app.data.SeedData
import com.cinebook.app.data.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CinemaViewModel(app: Application) : AndroidViewModel(app) {

    private val db = AppDatabase.get(app)
    private val movieDao = db.movieDao()
    private val bookingDao = db.bookingDao()

    val movies: StateFlow<List<Movie>> = movieDao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // ใส่ข้อมูลหนังตัวอย่างครั้งแรก
        viewModelScope.launch {
            if (movieDao.count() == 0) movieDao.insertAll(SeedData.movies)
        }
    }

    fun movie(id: Int): Flow<Movie?> = movieDao.getById(id)

    fun takenSeats(movieId: Int, showtime: String): Flow<Set<String>> =
        bookingDao.getByShow(movieId, showtime).map { list ->
            list.flatMap { it.seats.split(",") }.toSet()
        }

    fun myBookings(userId: Int): Flow<List<Booking>> = bookingDao.getByUser(userId)

    /** จองที่นั่ง — คืนค่า true ถ้าสำเร็จ, false ถ้ามีคนจองที่นั่งนั้นไปก่อน */
    fun book(
        user: User,
        movie: Movie,
        showtime: String,
        seats: List<String>,
        paymentMethod: String,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val ok = db.withTransaction {
                val taken = bookingDao.getByShowOnce(movie.id, showtime)
                    .flatMap { it.seats.split(",") }.toSet()
                if (seats.any { it in taken }) {
                    false
                } else {
                    val total = seats.sumOf { SeedData.seatPrice(it[0]) }
                    bookingDao.insert(
                        Booking(
                            userId = user.id,
                            movieId = movie.id,
                            movieTitle = movie.title,
                            showtime = showtime,
                            seats = seats.joinToString(","),
                            totalPrice = total,
                            paymentMethod = paymentMethod,
                            createdAt = System.currentTimeMillis()
                        )
                    )
                    true
                }
            }
            onResult(ok)
        }
    }

    fun cancel(booking: Booking) {
        viewModelScope.launch { bookingDao.delete(booking) }
    }
}

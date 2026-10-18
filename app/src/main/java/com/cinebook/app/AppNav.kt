package com.cinebook.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cinebook.app.ui.HomeScreen
import com.cinebook.app.ui.LoginScreen
import com.cinebook.app.ui.MovieDetailScreen
import com.cinebook.app.ui.PaymentScreen
import com.cinebook.app.ui.SeatScreen
import com.cinebook.app.ui.TicketsScreen
import com.cinebook.app.viewmodel.AuthViewModel
import com.cinebook.app.viewmodel.CinemaViewModel

@Composable
fun AppNav(auth: AuthViewModel, cinema: CinemaViewModel) {
    val nav = rememberNavController()
    val user by auth.user.collectAsState()
    val ready by auth.ready.collectAsState()

    if (!ready) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    NavHost(
        navController = nav,
        startDestination = if (user != null) "home" else "login"
    ) {
        composable("login") {
            LoginScreen(auth) {
                nav.navigate("home") { popUpTo("login") { inclusive = true } }
            }
        }
        composable("home") {
            val u = user ?: return@composable
            HomeScreen(
                user = u,
                cinema = cinema,
                onMovie = { nav.navigate("detail/$it") },
                onTickets = { nav.navigate("tickets") },
                onLogout = {
                    auth.logout()
                    nav.navigate("login") { popUpTo("home") { inclusive = true } }
                }
            )
        }
        composable(
            "detail/{movieId}",
            arguments = listOf(navArgument("movieId") { type = NavType.IntType })
        ) { entry ->
            val id = entry.arguments?.getInt("movieId") ?: return@composable
            MovieDetailScreen(
                movieId = id,
                cinema = cinema,
                onBack = { nav.popBackStack() },
                onBook = { nav.navigate("seats/$id") }
            )
        }
        composable(
            "seats/{movieId}",
            arguments = listOf(navArgument("movieId") { type = NavType.IntType })
        ) { entry ->
            val id = entry.arguments?.getInt("movieId") ?: return@composable
            val u = user ?: return@composable
            SeatScreen(
                movieId = id,
                user = u,
                cinema = cinema,
                onBack = { nav.popBackStack() },
                onProceedToPayment = { showtime, seatsCsv ->
                    val encoded = java.net.URLEncoder.encode(seatsCsv, "UTF-8")
                    nav.navigate("payment/$id/$showtime/$encoded")
                }
            )
        }
        composable(
            "payment/{movieId}/{showtime}/{seats}",
            arguments = listOf(
                navArgument("movieId") { type = NavType.IntType },
                navArgument("showtime") { type = NavType.StringType },
                navArgument("seats") { type = NavType.StringType }
            )
        ) { entry ->
            val id = entry.arguments?.getInt("movieId") ?: return@composable
            val showtime = entry.arguments?.getString("showtime") ?: ""
            val encodedSeats = entry.arguments?.getString("seats") ?: ""
            val seatsCsv = java.net.URLDecoder.decode(encodedSeats, "UTF-8")
            val u = user ?: return@composable
            PaymentScreen(
                movieId = id,
                showtime = showtime,
                seatsCsv = seatsCsv,
                user = u,
                cinema = cinema,
                onBack = { nav.popBackStack() },
                onPaymentSuccess = {
                    nav.navigate("tickets") {
                        popUpTo("home")
                    }
                }
            )
        }
        composable("tickets") {
            val u = user ?: return@composable
            TicketsScreen(user = u, cinema = cinema, onBack = { nav.popBackStack() })
        }
    }
}

package com.cinebook.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cinebook.app.data.User
import com.cinebook.app.viewmodel.CinemaViewModel

private const val ALL = "ทั้งหมด"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    user: User,
    cinema: CinemaViewModel,
    onMovie: (Int) -> Unit,
    onTickets: () -> Unit,
    onLogout: () -> Unit
) {
    val movies by cinema.movies.collectAsState()
    var selectedTab by remember { mutableStateOf(0) } // 0 = กำลังฉาย, 1 = เข้าเร็วๆ นี้
    var genre by remember { mutableStateOf(ALL) }

    val nowShowingMovies = remember(movies) { movies.filter { it.isNowShowing } }
    val comingSoonMovies = remember(movies) { movies.filter { !it.isNowShowing } }

    val genres = remember(nowShowingMovies) { listOf(ALL) + nowShowingMovies.map { it.genre }.distinct() }
    val shown = if (selectedTab == 0) {
        if (genre == ALL) nowShowingMovies else nowShowingMovies.filter { it.genre == genre }
    } else {
        comingSoonMovies
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Leamer Cinema", fontWeight = FontWeight.ExtraBold)
                        Text(
                            "สวัสดี, ${user.displayName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onTickets) {
                        Icon(Icons.Filled.ConfirmationNumber, contentDescription = "ตั๋วของฉัน")
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "ออกจากระบบ")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { pad ->
        Column(Modifier.padding(pad)) {
            // Tab เลือก กำลังฉาย / เข้าเร็วๆ นี้
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0; genre = ALL },
                    label = { Text("🎬 กำลังฉาย (${nowShowingMovies.size})") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    label = { Text("🚀 เข้าเร็วๆ นี้ (${comingSoonMovies.size})") },
                    modifier = Modifier.weight(1f)
                )
            }

            if (selectedTab == 0) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(genres) { g ->
                        FilterChip(
                            selected = g == genre,
                            onClick = { genre = g },
                            label = { Text(g) }
                        )
                    }
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(shown, key = { it.id }) { movie ->
                    MovieCard(movie) { onMovie(movie.id) }
                }
            }
        }
    }
}

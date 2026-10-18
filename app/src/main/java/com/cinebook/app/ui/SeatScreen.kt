package com.cinebook.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cinebook.app.data.SeedData
import com.cinebook.app.data.User
import com.cinebook.app.viewmodel.CinemaViewModel
import kotlinx.coroutines.launch

private val ROWS = listOf('A', 'B', 'C', 'D', 'E', 'F')
private const val COLS = 8

fun sortSeats(seats: Collection<String>): List<String> =
    seats.sortedWith(compareBy({ it[0] }, { it.substring(1).toInt() }))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeatScreen(
    movieId: Int,
    user: User,
    cinema: CinemaViewModel,
    onBack: () -> Unit,
    onProceedToPayment: (String, String) -> Unit
) {
    val movie by remember(movieId) { cinema.movie(movieId) }.collectAsState(initial = null)
    var showtime by remember { mutableStateOf(SeedData.showtimes.first()) }
    var selected by remember { mutableStateOf(setOf<String>()) }
    val taken by remember(movieId, showtime) { cinema.takenSeats(movieId, showtime) }
        .collectAsState(initial = emptySet<String>())

    val total = selected.sumOf { SeedData.seatPrice(it[0]) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(movie?.title ?: "") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "กลับ")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        bottomBar = {
            Surface(color = MaterialTheme.colorScheme.surface, tonalElevation = 6.dp) {
                Column(
                    Modifier
                        .navigationBarsPadding()
                        .padding(16.dp)
                ) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                if (selected.isEmpty()) "ยังไม่ได้เลือกที่นั่ง"
                                else "ที่นั่ง: ${sortSeats(selected).joinToString(", ")}",
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                "รอบ $showtime น.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text("฿$total", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Gold)
                    }
                    Spacer(Modifier.height(12.dp))
                    Button(
                        enabled = selected.isNotEmpty() && movie != null,
                        onClick = {
                            if (movie != null && selected.isNotEmpty()) {
                                onProceedToPayment(showtime, sortSeats(selected).joinToString(","))
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Text("ดำเนินการชำระเงิน", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .verticalScroll(rememberScrollState())
                .padding(vertical = 8.dp)
        ) {
            Text(
                "เลือกรอบฉาย",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            LazyRow(
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(SeedData.showtimes) { t ->
                    FilterChip(
                        selected = t == showtime,
                        onClick = { showtime = t; selected = emptySet() },
                        label = { Text(t) }
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // จอภาพ
            Box(
                modifier = Modifier
                    .padding(horizontal = 40.dp)
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Brush.horizontalGradient(listOf(Color.Transparent, Gold, Color.Transparent)))
            )
            Text(
                "จอภาพ",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(16.dp))

            // ผังที่นั่ง
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ROWS.forEach { row ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            row.toString(),
                            modifier = Modifier.width(16.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                        for (col in 1..COLS) {
                            val id = "$row$col"
                            SeatCell(
                                label = col.toString(),
                                isTaken = id in taken,
                                isSelected = id in selected,
                                modifier = Modifier.weight(1f)
                            ) {
                                selected = if (id in selected) selected - id else selected + id
                            }
                            if (col == COLS / 2) Spacer(Modifier.width(10.dp))
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // คำอธิบายสัญลักษณ์
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Legend(MaterialTheme.colorScheme.surfaceVariant, "ว่าง")
                Legend(Accent, "ที่เลือก")
                Legend(Color(0xFF3A3A44), "ไม่ว่าง")
            }
            Spacer(Modifier.height(10.dp))
            Text(
                "แถว A-B ฿200  •  C-D ฿240  •  E-F ฿280",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SeatCell(
    label: String,
    isTaken: Boolean,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val bg = when {
        isTaken -> Color(0xFF3A3A44)
        isSelected -> Accent
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp, bottomStart = 4.dp, bottomEnd = 4.dp))
            .background(bg)
            .then(if (isTaken) Modifier else Modifier.clickable(onClick = onClick)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            if (isTaken) "✕" else label,
            fontSize = 11.sp,
            color = if (isTaken) Color(0xFF777784) else Color.White
        )
    }
}

@Composable
private fun Legend(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(16.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(color)
        )
        Spacer(Modifier.width(6.dp))
        Text(text, style = MaterialTheme.typography.bodySmall)
    }
}

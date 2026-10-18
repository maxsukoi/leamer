package com.cinebook.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cinebook.app.data.Movie

/** โปสเตอร์หนังแบบไล่สี + อีโมจิ (ไม่ต้องใช้อินเทอร์เน็ต) */
@Composable
fun PosterBox(movie: Movie, modifier: Modifier = Modifier, emojiSize: Int = 56) {
    Box(
        modifier = modifier.background(
            Brush.verticalGradient(listOf(Color(movie.colorStart), Color(movie.colorEnd)))
        ),
        contentAlignment = Alignment.Center
    ) {
        Text(movie.emoji, fontSize = emojiSize.sp)
    }
}

@Composable
fun RatingBadge(rating: Double) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Filled.Star, contentDescription = null, tint = Gold, modifier = Modifier.height(16.dp))
        Text(
            text = " $rating",
            color = Gold,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun MovieCard(movie: Movie, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            PosterBox(
                movie = movie,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.78f)
            )
            Column(Modifier.padding(12.dp)) {
                Text(
                    movie.title,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                if (movie.isNowShowing) {
                    Text(
                        "${movie.genre} • ${movie.durationMin} นาที",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(4.dp))
                    RatingBadge(movie.rating)
                } else {
                    Text(
                        "กำหนดฉาย: ${movie.releaseDate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Gold,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

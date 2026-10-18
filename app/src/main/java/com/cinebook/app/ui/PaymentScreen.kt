package com.cinebook.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cinebook.app.data.SeedData
import com.cinebook.app.data.User
import com.cinebook.app.viewmodel.CinemaViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class PaymentMethodOption(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector
)

val paymentMethods = listOf(
    PaymentMethodOption("promptpay", "พร้อมเพย์ (PromptPay)", "สแกน QR Code ชำระเงินผ่านแอปธนาคาร", Icons.Default.QrCode),
    PaymentMethodOption("credit", "บัตรเครดิต / เดบิต", "Visa, Mastercard, JCB", Icons.Default.CreditCard)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    movieId: Int,
    showtime: String,
    seatsCsv: String,
    user: User,
    cinema: CinemaViewModel,
    onBack: () -> Unit,
    onPaymentSuccess: () -> Unit
) {
    val movie by remember(movieId) { cinema.movie(movieId) }.collectAsState(initial = null)
    val seats = remember(seatsCsv) { seatsCsv.split(",").filter { it.isNotBlank() } }
    val total = remember(seats) { seats.sumOf { SeedData.seatPrice(it[0]) } }

    var selectedMethod by remember { mutableStateOf("promptpay") }
    var isProcessing by remember { mutableStateOf(false) }

    // ข้อมูลบัตรเครดิต
    var cardNumber by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var cardName by remember { mutableStateOf("") }

    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = { Text("ชำระเงินค่าตั๋ว") },
                navigationIcon = {
                    IconButton(onClick = onBack, enabled = !isProcessing) {
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
                        Text("ยอดชำระสุทธิ", fontWeight = FontWeight.Medium)
                        Text("฿$total", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Gold)
                    }
                    Spacer(Modifier.height(12.dp))
                    Button(
                        enabled = !isProcessing && movie != null,
                        onClick = {
                            if (selectedMethod == "credit" && (cardNumber.isBlank() || expiryDate.isBlank() || cvv.isBlank() || cardName.isBlank())) {
                                scope.launch {
                                    snackbar.showSnackbar("กรุณากรอกข้อมูลบัตรเครดิตให้ครบถ้วน")
                                }
                                return@Button
                            }
                            val m = movie ?: return@Button
                            isProcessing = true
                            scope.launch {
                                delay(1200)
                                val methodName = paymentMethods.find { it.id == selectedMethod }?.title ?: "พร้อมเพย์"
                                cinema.book(user, m, showtime, seats, methodName) { ok ->
                                    isProcessing = false
                                    if (ok) {
                                        onPaymentSuccess()
                                    } else {
                                        scope.launch {
                                            snackbar.showSnackbar("ที่นั่งนี้ถูกจองไปแล้ว กรุณาเลือกใหม่")
                                            delay(1500)
                                            onBack()
                                        }
                                    }
                                }
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("กำลังดำเนินการชำระเงิน...", fontWeight = FontWeight.Bold)
                        } else {
                            Text("ชำระเงิน ฿$total", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("สรุปรายการจอง", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    PaymentInfoRow("ภาพยนตร์", movie?.title ?: "-")
                    PaymentInfoRow("รอบฉาย", "$showtime น.")
                    PaymentInfoRow("ที่นั่ง", seats.joinToString(", "))
                    PaymentInfoRow("ราคารวม", "฿$total", highlight = true)
                }
            }

            Text("เลือกช่องทางการชำระเงิน", fontWeight = FontWeight.Bold, fontSize = 16.sp)

            paymentMethods.forEach { method ->
                val selected = method.id == selectedMethod
                Card(
                    onClick = { selectedMethod = method.id },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selected) MaterialTheme.colorScheme.surfaceVariant
                        else MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                method.icon,
                                contentDescription = null,
                                tint = if (selected) Gold else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(Modifier.width(16.dp))
                            Column {
                                Text(method.title, fontWeight = FontWeight.Bold)
                                Text(
                                    method.subtitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        RadioButton(
                            selected = selected,
                            onClick = { selectedMethod = method.id }
                        )
                    }
                }
            }

            if (selectedMethod == "promptpay") {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("สแกน QR Code เพื่อชำระเงิน", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .size(160.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("📱 [ QR CODE ]\n฿$total", textAlign = TextAlign.Center, color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "พร้อมเพย์ CineBook Official",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else if (selectedMethod == "credit") {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("กรอกข้อมูลบัตรเครดิต / เดบิต", fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = cardNumber,
                            onValueChange = { cardNumber = it },
                            label = { Text("หมายเลขบัตร (16 หลัก)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = expiryDate,
                                onValueChange = { expiryDate = it },
                                label = { Text("วันหมดอายุ (MM/YY)") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = cvv,
                                onValueChange = { cvv = it },
                                label = { Text("CVV") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        OutlinedTextField(
                            value = cardName,
                            onValueChange = { cardName = it },
                            label = { Text("ชื่อผู้ถือบัตร") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PaymentInfoRow(label: String, value: String, highlight: Boolean = false) {
    Row(Modifier.fillMaxWidth()) {
        Text(
            label,
            modifier = Modifier.width(80.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            value,
            fontWeight = FontWeight.SemiBold,
            color = if (highlight) Gold else MaterialTheme.colorScheme.onSurface
        )
    }
}

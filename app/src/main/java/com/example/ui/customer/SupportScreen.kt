package com.example.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.SupportTicketEntity
import com.example.ui.theme.*

@Composable
fun SupportScreen(
    tickets: List<SupportTicketEntity>,
    onBackClick: () -> Unit,
    onSubmitTicket: (issueType: String, message: String) -> Unit
) {
    var showNewTicketDialog by remember { mutableStateOf(false) }
    var selectedIssueType by remember { mutableStateOf("تأخير في استلام الطلب") }
    var ticketMessage by remember { mutableStateOf("") }
    var showSuccessMessage by remember { mutableStateOf(false) }

    val issueTypes = listOf(
        "تأخير في استلام الطلب ⏳",
        "عنصر ناقص أو تالف في الأوردر 📦",
        "مشكلة في الدفع أو الخصم 💳",
        "تعديل عنوان التوصيل 📍",
        "شكوى بخصوص كابتن التوصيل 🛵",
        "استفسار عام أو اقتراح ✨"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "رجوع")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "مركز المساعدة والدعم الفني 🎧",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // Live Chat Quick Banner
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MinyooOrangeContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "💬", fontSize = 32.sp)
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "فريق خدمة عملاء MINYOO متاح 24/7",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MinyooOrangeDark
                            )
                            Text(
                                text = "نحن هنا لضمان تجربة طلب وتوصيل سريعة وخالية من المتاعب",
                                style = MaterialTheme.typography.bodySmall,
                                color = MinyooCharcoal
                            )
                        }
                    }
                }
            }

            // Quick Actions: New Ticket
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { showNewTicketDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MinyooOrangePrimary),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("open_new_ticket_btn")
                ) {
                    Icon(imageVector = Icons.Default.AddComment, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("فتح تذكرة مساعدة جديدة", fontWeight = FontWeight.Bold)
                }
            }

            // My Tickets Section
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "تذاكر الدعم والشكاوى السابقة",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            if (tickets.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "✅", fontSize = 36.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "لا توجد أي شكاوى أو تذاكر مفتوحة حالياً",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MinyooSlateLight
                            )
                        }
                    }
                }
            } else {
                items(tickets) { ticket ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .border(1.dp, MinyooCardBorder, RoundedCornerShape(14.dp))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = ticket.issueType,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (ticket.status == "OPEN") MinyooOrangeContainer else MinyooGreenLight
                                ) {
                                    Text(
                                        text = if (ticket.status == "OPEN") "قيد المتابعة" else "تم الحل ✅",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (ticket.status == "OPEN") MinyooOrangeDark else MinyooGreenDark,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = ticket.message,
                                style = MaterialTheme.typography.bodySmall,
                                color = MinyooSlateLight
                            )
                        }
                    }
                }
            }
        }

        // New Ticket Dialog
        if (showNewTicketDialog) {
            AlertDialog(
                onDismissRequest = { showNewTicketDialog = false },
                title = { Text("فتح تذكرة مساعدة جديدة 📝", fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text(
                            text = "اختر نوع المشكلة:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        issueTypes.forEach { type ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { selectedIssueType = type }
                                    .padding(vertical = 2.dp)
                            ) {
                                RadioButton(
                                    selected = selectedIssueType == type,
                                    onClick = { selectedIssueType = type },
                                    colors = RadioButtonDefaults.colors(selectedColor = MinyooOrangePrimary)
                                )
                                Text(text = type, style = MaterialTheme.typography.bodySmall)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = ticketMessage,
                            onValueChange = { ticketMessage = it },
                            placeholder = { Text("اشرح المشكلة بالتفصيل لمساعدتك فوراً...") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (ticketMessage.isNotBlank()) {
                                onSubmitTicket(selectedIssueType, ticketMessage)
                                showNewTicketDialog = false
                                ticketMessage = ""
                                showSuccessMessage = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MinyooOrangePrimary),
                        modifier = Modifier.testTag("submit_ticket_confirm_btn")
                    ) {
                        Text("إرسال التذكرة", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showNewTicketDialog = false }) {
                        Text("إلغاء")
                    }
                }
            )
        }
    }
}

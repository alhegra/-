package com.example.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RestaurantRegistrationData
import com.example.ui.theme.*

@Composable
fun RestaurantUnderReviewScreen(
    registrationData: RestaurantRegistrationData?,
    onApproveClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val restName = registrationData?.restaurantName ?: "المطعم الشريك"
    val restArea = registrationData?.cityArea ?: "القاهرة"
    val restPhone = registrationData?.phone ?: "010xxxx"
    val restLogo = registrationData?.logoIcon ?: "🍔"
    val restCuisine = registrationData?.cuisine ?: "مأكولات متنوعة"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFEF3C7),
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Pulsing hourglass icon badge
            Surface(
                shape = CircleShape,
                color = Color(0xFFFEF3C7),
                shadowElevation = 4.dp,
                modifier = Modifier
                    .size(90.dp)
                    .border(2.dp, Color(0xFFF59E0B), CircleShape)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = "⏳", fontSize = 44.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFFEF3C7)
            ) {
                Text(
                    text = "طلبك قيد المراجعة والتدقيق",
                    color = Color(0xFFB45309),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "مرحباً بإدارة $restName",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "تم استلام طلب انضمام مطعمك إلى منصة لقمة بنجاح، ويقوم فريق العمليات حالياً بمراجعة البيانات وتدقيق القائمة لتفعيل لوحة التحكم الخاصة بك.",
                style = MaterialTheme.typography.bodyMedium,
                color = MinyooSlateMuted,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Restaurant Details Card
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MinyooCardBorder, RoundedCornerShape(18.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MinyooOrangeContainer,
                            modifier = Modifier.size(50.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Storefront,
                                    contentDescription = null,
                                    tint = MinyooOrangePrimary,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = restName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = restCuisine,
                                style = MaterialTheme.typography.bodySmall,
                                color = MinyooSlateLight
                            )
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 12.dp), color = MinyooBorder)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "النطاق الجغرافي:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MinyooSlateLight
                        )
                        Text(
                            text = restArea,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "📞 رقم التواصل:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MinyooSlateLight
                        )
                        Text(
                            text = restPhone,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Verification Timeline Card
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MinyooCardBorder, RoundedCornerShape(18.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "مراحل مراجعة واعتماد المطعم:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Step 1
                    TimelineRow(
                        icon = "✅",
                        title = "1. استلام طلب الانضمام",
                        subtitle = "تم تسجيل بيانات المطعم والمسؤول بنجاح",
                        isCompleted = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Step 2
                    TimelineRow(
                        icon = "⏳",
                        title = "2. فحص وتدقيق البيانات وقائمة الطعام",
                        subtitle = "جاري المراجعة بواسطة إدارة لقمة",
                        isActive = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Step 3
                    TimelineRow(
                        icon = "🔒",
                        title = "3. تفعيل شاشة استقبال الطلبات الفورية",
                        subtitle = "سيتم فتح الشاشة تلقائياً بمجرد الاعتماد",
                        isLocked = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Instant Admin Approval Action for testing/demo
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, Color(0xFF22C55E).copy(alpha = 0.5f), RoundedCornerShape(18.dp))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = Color(0xFF15803D),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "موافقة الأدمن الفورية للتجربة (Admin Instant Approval)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF15803D)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "اضغط هنا لمحاكاة موافقة الإدارة والدخول فوراً إلى لوحة تحكم المطعم واستقبال الطلبات",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF166534),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onApproveClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("admin_instant_approve_btn")
                    ) {
                        Text(
                            text = "اعتماد المطعم والدخول للوحة التحكم",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Logout or Switch Account
            OutlinedButton(
                onClick = onLogoutClick,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("review_logout_btn")
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null, tint = MinyooSlateLight)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "تسجيل الخروج أو تبديل الحساب",
                    color = MinyooSlateLight,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "منصة لقمة • lo2ma.click",
                style = MaterialTheme.typography.labelSmall,
                color = MinyooSlateLight
            )
        }
    }
}

@Composable
private fun TimelineRow(
    icon: String,
    title: String,
    subtitle: String,
    isCompleted: Boolean = false,
    isActive: Boolean = false,
    isLocked: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = when {
                isCompleted -> Color(0xFFDCFCE7)
                isActive -> Color(0xFFFEF3C7)
                else -> Color(0xFFF1F5F9)
            },
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = icon, fontSize = 18.sp)
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isActive || isCompleted) FontWeight.Bold else FontWeight.Normal,
                color = if (isLocked) MinyooSlateLight else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MinyooSlateLight
            )
        }
    }
}

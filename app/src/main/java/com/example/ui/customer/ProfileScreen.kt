package com.example.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Address
import com.example.data.model.User
import com.example.data.model.UserRole
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    user: User,
    savedAddresses: List<Address>,
    onSavedAddressesClick: () -> Unit,
    onRoleSwitcherClick: () -> Unit,
    onRestaurantPortalClick: () -> Unit = {},
    onSupportClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onLogoutClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(MinyooOrangePrimary.copy(alpha = 0.15f), MaterialTheme.colorScheme.surface)
                        )
                    )
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MinyooOrangePrimary,
                        modifier = Modifier.size(60.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = user.name.take(1),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = user.name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MinyooOrangeContainer
                            ) {
                                Text(
                                    text = "لقمة PLUS ⭐",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MinyooOrangeDark,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${user.phone} • ${user.email}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MinyooSlateLight
                        )
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp)
        ) {
            // Role Switcher Tile
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MinyooWarmYellowLight),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onRoleSwitcherClick() }
                        .testTag("profile_role_switcher")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🔄", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "تبديل الواجهة (عميل / مطعم / مندوب / أدمن)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E)
                            )
                            Text(
                                text = "الواجهة الحالية: ${user.role.titleAr}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF92400E).copy(alpha = 0.8f)
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ArrowBack, // RTL forward
                            contentDescription = null,
                            tint = Color(0xFF92400E)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))

                // Direct Restaurant Owner Screen Entry
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onRestaurantPortalClick() }
                        .testTag("profile_restaurant_portal_entry")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MinyooOrangePrimary,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = "👨‍🍳", fontSize = 20.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "شاشة صاحب المطعم 🍽️",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFFEF4444)
                                ) {
                                    Text(
                                        text = "الطلبات الجديدة",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "متابعة الطلبات الجديدة وتحديث المراحل بضغطة واحدة",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF94A3B8)
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            // Menu Section: Account & Addresses
            item {
                Text(
                    text = "الحساب والعناوين",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MinyooSlateLight,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
                )

                ProfileMenuItem(
                    icon = Icons.Outlined.LocationOn,
                    title = "العناوين المحفوظة",
                    subtitle = "${savedAddresses.size} عناوين في القاهرة والجيزة",
                    onClick = onSavedAddressesClick
                )

                ProfileMenuItem(
                    icon = Icons.Outlined.Notifications,
                    title = "مركز الإشعارات والتنبيهات",
                    subtitle = "تحديثات الطلبات والعروض الحصرية",
                    onClick = onNotificationsClick
                )

                ProfileMenuItem(
                    icon = Icons.Outlined.Payment,
                    title = "طرق الدفع والمحافظ الإلكترونية",
                    subtitle = "فودافون كاش، InstaPay، والبطاقات",
                    onClick = {}
                )
            }

            // Menu Section: Support & Legal
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "المساعدة والأمان",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MinyooSlateLight,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
                )

                ProfileMenuItem(
                    icon = Icons.Outlined.HeadsetMic,
                    title = "مركز المساعدة وخدمة العملاء 24/7",
                    subtitle = "فتح تذكرة دعم لمتابعة أي طلب",
                    onClick = onSupportClick
                )

                ProfileMenuItem(
                    icon = Icons.Outlined.Language,
                    title = "اللغة (Language)",
                    subtitle = "العربية (مصر) 🇪🇬",
                    onClick = {}
                )

                ProfileMenuItem(
                    icon = Icons.Outlined.Security,
                    title = "سياسة الخصوصية والشروط والأحكام",
                    subtitle = "أعلى معايير حماية البيانات في مصر",
                    onClick = {}
                )

                ProfileMenuItem(
                    icon = Icons.Default.ExitToApp,
                    title = "تسجيل الخروج وتبديل الحساب",
                    subtitle = "العودة لشاشة الاختيار والتسجيل الأولى",
                    onClick = onLogoutClick
                )
            }

            // Version Footer
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "لقمة 🇪🇬 • lo2ma.click",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MinyooOrangePrimary
                    )
                    Text(
                        text = "منصة طلب وتوصيل الطعام الذكية في جمهورية مصر العربية",
                        style = MaterialTheme.typography.labelSmall,
                        color = MinyooSlateMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .border(1.dp, MinyooCardBorder, RoundedCornerShape(14.dp))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MinyooOrangePrimary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MinyooSlateLight
                )
            }

            Icon(
                imageVector = Icons.Default.ArrowBack, // RTL arrow
                contentDescription = null,
                tint = MinyooSlateLight,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

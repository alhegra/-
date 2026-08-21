package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MinyooTopBar(
    currentAddress: Address,
    currentRole: UserRole,
    unreadNotifsCount: Int,
    onAddressClick: () -> Unit,
    onRoleClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onAiAssistantClick: () -> Unit
) {
    Surface(
        color = MinyooSurfaceLight,
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = MinyooBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Brand Logo & Text (MINYOO)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MinyooOrangePrimary)
                ) {
                    Text(
                        text = "M",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "MINYOO",
                    color = MinyooOrangePrimary,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    letterSpacing = (-0.5).sp
                )
            }

            // Location Selector Pill
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onAddressClick() }
                    .padding(vertical = 2.dp, horizontal = 6.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "هتطلب فين؟",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MinyooSlateLight
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${currentAddress.area}، ${currentAddress.label}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MinyooCharcoal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "تغيير",
                        tint = MinyooOrangePrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // AI Smart Assistant Quick Button
            IconButton(
                onClick = onAiAssistantClick,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MinyooOrangeContainer)
                    .border(1.dp, MinyooOrangeLight, CircleShape)
                    .testTag("ai_assistant_top_btn")
            ) {
                Text(text = "✨", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Role Switcher Pill
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = when (currentRole) {
                    UserRole.CUSTOMER -> MinyooOrangeContainer
                    UserRole.RESTAURANT_OWNER -> Color(0xFFE0F2FE)
                    UserRole.COURIER -> Color(0xFFF3E8FF)
                    UserRole.ADMIN -> Color(0xFFDCFCE7)
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onRoleClick() }
                    .border(
                        1.dp,
                        when (currentRole) {
                            UserRole.CUSTOMER -> MinyooOrangeLight
                            UserRole.RESTAURANT_OWNER -> Color(0xFFBAE6FD)
                            UserRole.COURIER -> Color(0xFFE9D5FF)
                            UserRole.ADMIN -> Color(0xFFBBF7D0)
                        },
                        RoundedCornerShape(20.dp)
                    )
                    .testTag("role_switcher_btn")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = when (currentRole) {
                            UserRole.CUSTOMER -> "👤 عميل"
                            UserRole.RESTAURANT_OWNER -> "👨‍🍳 مطعم"
                            UserRole.COURIER -> "🛵 مندوب"
                            UserRole.ADMIN -> "⚙️ إدارة"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (currentRole) {
                            UserRole.CUSTOMER -> MinyooOrangeDark
                            UserRole.RESTAURANT_OWNER -> Color(0xFF0369A1)
                            UserRole.COURIER -> Color(0xFF6B21A8)
                            UserRole.ADMIN -> Color(0xFF15803D)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Notifications
            IconButton(
                onClick = onNotificationsClick,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MinyooBorder.copy(alpha = 0.5f))
                    .testTag("notifications_btn")
            ) {
                BadgedBox(
                    badge = {
                        if (unreadNotifsCount > 0) {
                            Badge(
                                containerColor = MinyooOrangePrimary,
                                modifier = Modifier.size(14.dp)
                            ) {
                                Text("$unreadNotifsCount", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "الإشعارات",
                        tint = MinyooSlateMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryGridItem(
    title: String,
    emoji: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(2.dp)
            .testTag("category_grid_$title")
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(if (isSelected) MinyooOrangePrimary else MinyooOrangeContainer)
                .border(
                    width = 1.dp,
                    color = if (isSelected) MinyooOrangePrimary else MinyooOrangeLight,
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            Text(text = emoji, fontSize = 24.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) MinyooOrangePrimary else MinyooSlateMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun CategoryChipItem(
    title: String,
    emoji: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        if (isSelected) MinyooOrangePrimary else MinyooSurfaceLight
    )
    val contentColor by animateColorAsState(
        if (isSelected) Color.White else MinyooCharcoal
    )

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = bgColor,
        shadowElevation = if (isSelected) 2.dp else 0.dp,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .border(
                width = 1.dp,
                color = if (isSelected) MinyooOrangePrimary else MinyooBorder,
                shape = RoundedCornerShape(20.dp)
            )
            .testTag("category_chip_$title")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(text = emoji, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = contentColor
            )
        }
    }
}

@Composable
fun RestaurantCard(
    restaurant: Restaurant,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MinyooSurfaceLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable { onClick() }
            .border(1.dp, MinyooBorder, RoundedCornerShape(24.dp))
            .testTag("restaurant_card_${restaurant.id}")
    ) {
        Column {
            // Cover Image with Badges
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp)
            ) {
                AsyncImage(
                    model = restaurant.coverUrl,
                    contentDescription = restaurant.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Rating Pill (Top Right in RTL)
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.White.copy(alpha = 0.92f),
                    shadowElevation = 1.dp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = MinyooWarmYellow,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${restaurant.rating}",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = MinyooCharcoal
                        )
                    }
                }

                // Delivery / Offer Pill (Bottom Left in RTL)
                val deliveryText = if (restaurant.deliveryFee == 0.0) {
                    "توصيل مجاني"
                } else if (!restaurant.discountBadge.isNullOrEmpty()) {
                    restaurant.discountBadge
                } else {
                    "وصل بـ ${restaurant.deliveryFee.toInt()} ج.م بس"
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = MinyooOrangePrimary,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                ) {
                    Text(
                        text = deliveryText,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                // Favorite Toggle (Top Left in RTL)
                IconButton(
                    onClick = onFavoriteToggle,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.85f))
                        .testTag("fav_btn_${restaurant.id}")
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "المفضلة",
                        tint = if (isFavorite) Color(0xFFEF4444) else MinyooCharcoal,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Restaurant Details Row (High Density)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Left text block: Name + Cuisines
                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Text(
                        text = restaurant.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = MinyooCharcoal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = restaurant.cuisines.joinToString(" • "),
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 10.sp,
                        color = MinyooSlateLight,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Right metadata block: Time + Tag
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Schedule,
                            contentDescription = null,
                            tint = MinyooSlateLight,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "${restaurant.deliveryTimeMinutes} دقيقة",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MinyooSlateLight
                        )
                    }

                    // Green "Most Popular" or "Best Value" tag
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MinyooGreenLight
                    ) {
                        Text(
                            text = if (restaurant.rating >= 4.7) "الأكثر طلباً" else "مميز",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MinyooGreen,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCardItem(
    product: Product,
    onClick: () -> Unit,
    onQuickAdd: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .border(1.dp, MinyooCardBorder, RoundedCornerShape(16.dp))
            .testTag("product_card_${product.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                if (product.isPopular) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MinyooWarmYellowLight,
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        Text(
                            text = "🔥 الأكثر طلباً",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB45309),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MinyooSlateLight,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${product.price.toInt()} جنيه",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MinyooOrangePrimary
                    )

                    if (product.originalPrice != null && product.originalPrice > product.price) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${product.originalPrice.toInt()} ج",
                            style = MaterialTheme.typography.bodySmall.copy(textDecoration = TextDecoration.LineThrough),
                            color = MinyooSlateMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Product Image with Add button
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(14.dp))
            ) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                IconButton(
                    onClick = onQuickAdd,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MinyooOrangePrimary)
                        .testTag("quick_add_${product.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "أضف",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun OrderStatusTimelineView(
    currentStatus: OrderStatus,
    modifier: Modifier = Modifier
) {
    val steps = listOf(
        OrderStatus.PLACED,
        OrderStatus.CONFIRMED,
        OrderStatus.PREPARING,
        OrderStatus.COURIER_ASSIGNED,
        OrderStatus.PICKED_UP,
        OrderStatus.OUT_FOR_DELIVERY,
        OrderStatus.DELIVERED
    )

    val currentIndex = steps.indexOf(currentStatus).coerceAtLeast(0)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
            .border(1.dp, MinyooBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "مراحل طلبك في الوقت الحقيقي 🛵",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        steps.forEachIndexed { index, step ->
            val isDone = index <= currentIndex
            val isCurrent = index == currentIndex

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circle Indicator
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isCurrent -> MinyooOrangePrimary
                                isDone -> MinyooGreen
                                else -> MinyooBorder
                            }
                        )
                ) {
                    if (isDone && !isCurrent) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    } else {
                        Text(
                            text = "${index + 1}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isDone || isCurrent) Color.White else MinyooSlateLight
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = step.titleAr,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                        color = if (isCurrent) MinyooOrangePrimary else if (isDone) MaterialTheme.colorScheme.onSurface else MinyooSlateMuted
                    )
                    if (isCurrent) {
                        Text(
                            text = step.descriptionAr,
                            style = MaterialTheme.typography.labelSmall,
                            color = MinyooSlateLight
                        )
                    }
                }

                if (isCurrent) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MinyooOrangeContainer
                    ) {
                        Text(
                            text = "جاري الآن",
                            style = MaterialTheme.typography.labelSmall,
                            color = MinyooOrangeDark,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BasicTextFieldWithPlaceholder(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    textStyle: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodyMedium.copy(
        color = MinyooCharcoal,
        fontSize = 13.sp
    )
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = textStyle,
        singleLine = true,
        modifier = modifier,
        decorationBox = { innerTextField ->
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = textStyle.copy(color = MinyooSlateMuted),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                innerTextField()
            }
        }
    )
}

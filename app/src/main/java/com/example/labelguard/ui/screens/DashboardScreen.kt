package com.example.labelguard.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.labelguard.data.model.ComplianceStatus
import com.example.labelguard.data.model.ProductCategory
import com.example.labelguard.data.model.ProductScanEntity
import com.example.labelguard.ui.components.ComplianceBadge
import com.example.labelguard.ui.components.ScoreMeter
import com.example.labelguard.ui.viewmodel.AppScreen
import com.example.labelguard.ui.viewmodel.DashboardStats
import com.example.labelguard.ui.viewmodel.LabelGuardViewModel
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkOnPrimaryContainer
import com.example.ui.theme.DarkOnPrimaryPurple
import com.example.ui.theme.DarkPrimaryContainerPurple
import com.example.ui.theme.DarkPrimaryLilac
import com.example.ui.theme.DarkSurfaceActive
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceCardHover
import com.example.ui.theme.StatusCleanContainer
import com.example.ui.theme.StatusCleanGreen
import com.example.ui.theme.StatusViolationContainer
import com.example.ui.theme.StatusViolationRed
import com.example.ui.theme.StatusWarningAmber
import com.example.ui.theme.StatusWarningContainer
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: LabelGuardViewModel,
    onNavigateToScan: () -> Unit,
    onNavigateToRules: () -> Unit,
    onSelectScan: (ProductScanEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val stats by viewModel.dashboardStats.collectAsState()
    val scans by viewModel.filteredScans.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedStatus by viewModel.selectedStatusFilter.collectAsState()
    val selectedCategory by viewModel.selectedCategoryFilter.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Header with Team Lead info
            TeamLeadHeader(onViewRules = onNavigateToRules)
        }

        item {
            // Hero Banner
            HeroScanBanner(
                stats = stats,
                onStartScan = onNavigateToScan,
                onNavigateToRules = onNavigateToRules
            )
        }

        item {
            // Analytics / Metrics Summary
            MetricsSection(stats = stats)
        }

        item {
            // Search & Filter controls
            SearchAndFilterSection(
                searchQuery = searchQuery,
                onSearchChange = viewModel::setSearchQuery,
                selectedStatus = selectedStatus,
                onSelectStatus = viewModel::setStatusFilter,
                selectedCategory = selectedCategory,
                onSelectCategory = viewModel::setCategoryFilter
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LATEST ANALYSIS (${scans.size})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    color = TextSecondary
                )
                Text(
                    text = "Audit History",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = DarkPrimaryLilac
                )
            }
        }

        if (scans.isEmpty()) {
            item {
                EmptyScansPlaceholder(
                    hasQuery = searchQuery.isNotBlank() || selectedStatus != "ALL",
                    onClearFilters = {
                        viewModel.setSearchQuery("")
                        viewModel.setStatusFilter("ALL")
                        viewModel.setCategoryFilter(null)
                    },
                    onStartScan = onNavigateToScan
                )
            }
        } else {
            items(scans, key = { it.id }) { scan ->
                ProductScanCard(
                    scan = scan,
                    onClick = { onSelectScan(scan) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TeamLeadHeader(onViewRules: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "COMPLIANCE ENGINE v2.4",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                color = DarkPrimaryLilac
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Label",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Light,
                    letterSpacing = (-0.5).sp,
                    color = Color.White
                )
                Text(
                    text = "Guard",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp,
                    color = DarkPrimaryLilac
                )
            }
            Text(
                text = "Balendu Bhushan • Team Lead",
                fontSize = 11.sp,
                color = TextSecondary
            )
        }

        Box(
            modifier = Modifier
                .clickable(onClick = onViewRules)
                .testTag("team_lead_avatar")
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(DarkPrimaryContainerPurple, DarkPrimaryLilac)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "BB",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            // Active status indicator dot
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(DarkBg)
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(StatusCleanGreen)
            )
        }
    }
}

@Composable
private fun HeroScanBanner(
    stats: DashboardStats,
    onStartScan: () -> Unit,
    onNavigateToRules: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Main Purple Card with Global Compliance Rate
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("hero_scan_banner"),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = DarkPrimaryContainerPurple),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Background subtle decorative circle
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .align(Alignment.TopEnd)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.05f))
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Text(
                                text = "GLOBAL COMPLIANCE RATE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = DarkOnPrimaryContainer.copy(alpha = 0.85f)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            val rateText = if (stats.totalScans > 0) "${stats.complianceRatePercent}%" else "84.2%"
                            Text(
                                text = rateText,
                                fontSize = 38.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-1).sp,
                                color = Color.White
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = DarkPrimaryLilac.copy(alpha = 0.2f)
                        ) {
                            val badgeText = if (stats.totalScans > 0) "${stats.compliantCount} Clean" else "+2.4%"
                            Text(
                                text = badgeText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkPrimaryLilac,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Progress bar
                    val progressFraction = if (stats.totalScans > 0) {
                        (stats.complianceRatePercent / 100f).coerceIn(0.05f, 1f)
                    } else 0.84f

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(CircleShape)
                            .background(DarkOnPrimaryPurple)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progressFraction)
                                .height(10.dp)
                                .clip(CircleShape)
                                .background(DarkPrimaryLilac)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Rule Engine: FSSAI-2024 Guidelines Active",
                            fontSize = 11.sp,
                            color = DarkOnPrimaryContainer.copy(alpha = 0.75f),
                            fontWeight = FontWeight.Medium
                        )
                        Image(
                            painter = painterResource(id = R.drawable.img_hero_scan),
                            contentDescription = "Scanner illustration",
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(6.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }

        // Prominent Scan Product Label Button
        Button(
            onClick = onStartScan,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .testTag("scan_packaging_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = DarkPrimaryLilac,
                contentColor = DarkOnPrimaryPurple
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = "Scan New Label",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "SCAN PRODUCT LABEL",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                letterSpacing = 0.5.sp
            )
        }

        // 2-card Quick Navigation Row (Ruleset & Reports)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onNavigateToRules)
                    .testTag("quick_nav_rules"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.06f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkPrimaryContainerPurple),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ListAlt,
                            contentDescription = "Ruleset",
                            tint = DarkPrimaryLilac,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Ruleset",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "FSSAI Catalog",
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            Card(
                modifier = Modifier
                    .weight(1f)
                    .testTag("quick_nav_reports"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.06f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkPrimaryContainerPurple),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Analytics,
                            contentDescription = "Audits",
                            tint = DarkPrimaryLilac,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Audits",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${stats.totalScans} Products",
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricsSection(stats: DashboardStats) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricCard(
                title = "Total Audited",
                value = stats.totalScans.toString(),
                icon = Icons.Default.Assignment,
                iconTint = DarkPrimaryLilac,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Pass Rate",
                value = "${stats.complianceRatePercent}%",
                icon = Icons.Default.CheckCircle,
                iconTint = StatusCleanGreen,
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricCard(
                title = "Critical Violations",
                value = stats.totalCriticalViolations.toString(),
                icon = Icons.Default.Warning,
                iconTint = StatusViolationRed,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Non-Compliant",
                value = stats.nonCompliantCount.toString(),
                icon = Icons.Default.Clear,
                iconTint = StatusWarningAmber,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = value,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = TextPrimary
                )
                Text(
                    text = title,
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun SearchAndFilterSection(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedStatus: String?,
    onSelectStatus: (String?) -> Unit,
    selectedCategory: ProductCategory?,
    onSelectCategory: (ProductCategory?) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_products_input"),
            placeholder = { Text("Search product name or brand...", fontSize = 13.sp, color = TextMuted) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = TextSecondary
                )
            },
            trailingIcon = {
                if (searchQuery.isNotBlank()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondary)
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = DarkSurfaceCard,
                unfocusedContainerColor = DarkSurfaceCard,
                focusedBorderColor = DarkPrimaryLilac,
                unfocusedBorderColor = Color.White.copy(alpha = 0.08f),
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )

        // Status Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val statusOptions = listOf(
                "ALL" to "All Status",
                ComplianceStatus.COMPLIANT.name to "Compliant",
                ComplianceStatus.NON_COMPLIANT.name to "Non-Compliant",
                ComplianceStatus.PARTIALLY_COMPLIANT.name to "Partially Compliant"
            )

            statusOptions.forEach { (key, label) ->
                val isSelected = selectedStatus == key
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelectStatus(if (isSelected && key != "ALL") "ALL" else key) },
                    label = { Text(label, fontSize = 12.sp) },
                    shape = RoundedCornerShape(10.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = DarkSurfaceCard,
                        selectedContainerColor = DarkPrimaryLilac.copy(alpha = 0.2f),
                        labelColor = TextSecondary,
                        selectedLabelColor = DarkPrimaryLilac
                    ),
                    border = BorderStroke(
                        0.5.dp,
                        if (isSelected) DarkPrimaryLilac.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.06f)
                    )
                )
            }
        }
    }
}

@Composable
private fun ProductScanCard(
    scan: ProductScanEntity,
    onClick: () -> Unit
) {
    val status = try {
        ComplianceStatus.valueOf(scan.complianceStatus)
    } catch (e: Exception) {
        ComplianceStatus.NON_COMPLIANT
    }

    val dateFormatter = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    val dateStr = dateFormatter.format(Date(scan.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("scan_card_${scan.id}")
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Packaging Thumbnail
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkBg),
                contentAlignment = Alignment.Center
            ) {
                when {
                    scan.sampleDrawableName == "img_sample_food" -> {
                        Image(
                            painter = painterResource(id = R.drawable.img_sample_food),
                            contentDescription = scan.productName,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    scan.sampleDrawableName == "img_sample_snack" -> {
                        Image(
                            painter = painterResource(id = R.drawable.img_sample_snack),
                            contentDescription = scan.productName,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    scan.imageUri != null -> {
                        AsyncImage(
                            model = scan.imageUri,
                            contentDescription = scan.productName,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    else -> {
                        Image(
                            painter = painterResource(id = R.drawable.img_sample_food),
                            contentDescription = scan.productName,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = scan.productName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = TextPrimary
                )
                Text(
                    text = scan.brand,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ComplianceBadge(status = status)

                    if (scan.criticalViolationsCount > 0) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = StatusViolationContainer,
                            border = BorderStroke(0.5.dp, StatusViolationRed.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = "${scan.criticalViolationsCount} Critical",
                                color = StatusViolationRed,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = dateStr,
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Score Circle and arrow
            Column(horizontalAlignment = Alignment.End) {
                ScoreMeter(score = scan.complianceScore)
                Spacer(modifier = Modifier.height(6.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "View Report",
                    tint = TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun EmptyScansPlaceholder(
    hasQuery: Boolean,
    onClearFilters: () -> Unit,
    onStartScan: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(DarkPrimaryLilac.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "No results",
                    tint = DarkPrimaryLilac,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = if (hasQuery) "No matching packaging scans" else "No packaging scanned yet",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (hasQuery) "Try adjusting your search keywords or filter status."
                else "Photograph or upload a food or packaged commodity label to verify compliance.",
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 16.sp
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (hasQuery) {
                Button(
                    onClick = onClearFilters,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkPrimaryContainerPurple,
                        contentColor = DarkPrimaryLilac
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Clear Filters")
                }
            } else {
                Button(
                    onClick = onStartScan,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkPrimaryLilac,
                        contentColor = DarkOnPrimaryPurple
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Start First Scan", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}


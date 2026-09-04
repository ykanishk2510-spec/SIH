package com.example.labelguard.ui.screens

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.labelguard.data.model.ComplianceReport
import com.example.labelguard.data.model.ComplianceStatus
import com.example.labelguard.data.model.ComplianceViolation
import com.example.labelguard.data.model.ExtractedLabelData
import com.example.labelguard.data.model.ViolationSeverity
import com.example.labelguard.ui.components.ComplianceBadge
import com.example.labelguard.ui.components.ScoreMeter
import com.example.labelguard.ui.components.SeverityBadge
import com.example.labelguard.ui.viewmodel.LabelGuardViewModel
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkOnPrimaryPurple
import com.example.ui.theme.DarkPrimaryContainerPurple
import com.example.ui.theme.DarkPrimaryLilac
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.StatusCleanGreen
import com.example.ui.theme.StatusCleanGreenContainer
import com.example.ui.theme.StatusViolationContainer
import com.example.ui.theme.StatusViolationRed
import com.example.ui.theme.StatusWarningAmber
import com.example.ui.theme.StatusWarningContainer
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportDetailsScreen(
    viewModel: LabelGuardViewModel,
    onBack: () -> Unit,
    onScanAnother: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activeExtracted by viewModel.activeExtractedData.collectAsState()
    val activeReport by viewModel.activeReport.collectAsState()
    val scanEntity by viewModel.selectedScanEntity.collectAsState()
    val selectedSample by viewModel.selectedSampleDrawableName.collectAsState()
    val selectedUri by viewModel.selectedImageUri.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val report = activeReport ?: return
    val extracted = activeExtracted ?: return

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Audit Record?", color = TextPrimary) },
            text = { Text("This will permanently remove this packaging compliance report from your dashboard history.", color = TextSecondary) },
            containerColor = DarkSurfaceCard,
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        scanEntity?.id?.let { viewModel.deleteScan(it) }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusViolationRed)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Compliance Audit Report",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("report_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    // Share Report
                    IconButton(
                        onClick = {
                            val shareText = buildShareReportSummary(extracted, report)
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Compliance Audit"))
                        },
                        modifier = Modifier.testTag("share_report_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = TextPrimary
                        )
                    }

                    // Delete
                    if (scanEntity != null) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = StatusViolationRed)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBg
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                // Top Executive Summary Card
                ExecutiveSummaryCard(
                    productName = scanEntity?.productName ?: extracted.productName,
                    brand = scanEntity?.brand ?: extracted.brandOrManufacturer,
                    report = report
                )
            }

            item {
                // Tab Selection: 0 = Violations, 1 = Declarations, 2 = Packaging & OCR
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = DarkSurfaceCard,
                    contentColor = DarkPrimaryLilac,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = DarkPrimaryLilac
                        )
                    },
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(14.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                text = "Violations (${report.violations.size})",
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTab == 0) DarkPrimaryLilac else TextSecondary
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                text = "Declarations",
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTab == 1) DarkPrimaryLilac else TextSecondary
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = {
                            Text(
                                text = "Photo & OCR",
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTab == 2) DarkPrimaryLilac else TextSecondary
                            )
                        }
                    )
                }
            }

            when (selectedTab) {
                0 -> {
                    // Violations & Remediation
                    if (report.violations.isEmpty()) {
                        item {
                            AllPassCard()
                        }
                    } else {
                        items(report.violations, key = { it.id + it.fieldName }) { violation ->
                            ViolationDetailCard(violation = violation)
                        }
                    }
                }
                1 -> {
                    // Extracted Declarations Table
                    item {
                        ExtractedDeclarationsSection(extracted = extracted)
                    }
                }
                2 -> {
                    // Photo Preview & Raw OCR Text
                    item {
                        PackagingPhotoAndOcrSection(
                            sampleDrawable = selectedSample ?: scanEntity?.sampleDrawableName,
                            imageUri = selectedUri?.toString() ?: scanEntity?.imageUri,
                            rawOcr = extracted.rawExtractedText
                        )
                    }
                }
            }

            item {
                // Bottom Actions
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
                    ) {
                        Text("Dashboard", fontSize = 13.sp)
                    }
                    Button(
                        onClick = onScanAnother,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("scan_another_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkPrimaryLilac,
                            contentColor = DarkOnPrimaryPurple
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Scan Another", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ExecutiveSummaryCard(
    productName: String,
    brand: String,
    report: ComplianceReport
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = productName.ifBlank { "Scanned Packaging" },
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = brand.ifBlank { "Manufacturer Unspecified" },
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    ComplianceBadge(status = report.status)
                }

                Spacer(modifier = Modifier.width(12.dp))

                ScoreMeter(score = report.complianceScore, modifier = Modifier.size(64.dp))
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.08f))
            Spacer(modifier = Modifier.height(12.dp))

            // Checks Summary Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StatPill(
                    title = "Checks Passed",
                    value = "${report.passedChecksCount} / ${report.totalChecksCount}",
                    color = StatusCleanGreen
                )
                val critical = report.violations.count { it.severity == ViolationSeverity.CRITICAL }
                StatPill(
                    title = "Critical Violations",
                    value = critical.toString(),
                    color = if (critical > 0) StatusViolationRed else StatusCleanGreen
                )
                val major = report.violations.count { it.severity == ViolationSeverity.MAJOR }
                StatPill(
                    title = "Major Defects",
                    value = major.toString(),
                    color = if (major > 0) StatusWarningAmber else StatusCleanGreen
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Legal Metrology / Statutory verdict text
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DarkBg,
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
            ) {
                Text(
                    text = report.summaryVerdict,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}

@Composable
private fun StatPill(title: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 15.sp,
            color = color
        )
        Text(
            text = title,
            fontSize = 10.sp,
            color = TextSecondary
        )
    }
}

@Composable
private fun ViolationDetailCard(violation: ComplianceViolation) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SeverityBadge(severity = violation.severity)
                Text(
                    text = violation.regulationCode,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkPrimaryLilac
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = violation.ruleTitle,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = violation.issueDescription,
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Remediation Advice
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = DarkPrimaryContainerPurple.copy(alpha = 0.35f),
                border = BorderStroke(1.dp, DarkPrimaryLilac.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = "Fix",
                        tint = DarkPrimaryLilac,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Corrective Action Required:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = DarkPrimaryLilac
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = violation.remediationAdvice,
                            fontSize = 11.sp,
                            color = TextPrimary,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AllPassCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
        border = BorderStroke(1.dp, StatusCleanGreen.copy(alpha = 0.25f))
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
                    .background(StatusCleanGreen.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "All Clear",
                    tint = StatusCleanGreen,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "No Violations Flagged",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = StatusCleanGreen
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "All statutory declarations (Legal Metrology & FSSAI standards) have been verified on this packaging label.",
                fontSize = 12.sp,
                color = TextSecondary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
private fun ExtractedDeclarationsSection(extracted: ExtractedLabelData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "STATUTORY FIELD DECLARATIONS",
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 1.sp,
                color = DarkPrimaryLilac
            )

            DeclarationRow(
                label = "Product Name",
                value = extracted.productName,
                isValid = extracted.productName.isNotBlank()
            )

            DeclarationRow(
                label = "Net Quantity",
                value = extracted.netQuantity,
                isValid = extracted.netQuantity.contains(Regex("""\d+\s*(g|kg|ml|l|gm|units|pieces|N)""", RegexOption.IGNORE_CASE))
            )

            DeclarationRow(
                label = "Manufacturer / Packer",
                value = extracted.brandOrManufacturer,
                isValid = extracted.brandOrManufacturer.isNotBlank()
            )

            DeclarationRow(
                label = "Manufacturer Address",
                value = extracted.manufacturerAddress,
                isValid = extracted.manufacturerAddress.isNotBlank()
            )

            DeclarationRow(
                label = "Country of Origin",
                value = extracted.countryOfOrigin,
                isValid = extracted.countryOfOrigin.isNotBlank()
            )

            DeclarationRow(
                label = "MRP & Tax Clause",
                value = "${extracted.mrp} ${if (extracted.isTaxesMentioned) "(Incl. all taxes)" else "(Taxes missing!)"}",
                isValid = extracted.mrp.isNotBlank() && extracted.isTaxesMentioned
            )

            DeclarationRow(
                label = "Mfg & Expiry Dates",
                value = "Mfg: ${extracted.manufacturingDate} | Exp: ${extracted.expiryOrBestBeforeDate}${if (extracted.isExpired) " [EXPIRED]" else ""}",
                isValid = extracted.manufacturingDate.isNotBlank() && extracted.expiryOrBestBeforeDate.isNotBlank() && !extracted.isExpired
            )

            DeclarationRow(
                label = "Batch / Lot No.",
                value = extracted.batchOrLotNumber,
                isValid = extracted.batchOrLotNumber.isNotBlank()
            )

            if (extracted.fssaiLicenseNumber.isNotBlank() || extracted.vegNonVegStatus != "NOT_APPLICABLE") {
                DeclarationRow(
                    label = "FSSAI License (14 Digits)",
                    value = extracted.fssaiLicenseNumber.ifBlank { "Missing" },
                    isValid = extracted.fssaiLicenseNumber.filter { it.isDigit() }.length == 14
                )

                DeclarationRow(
                    label = "Veg / Non-Veg Indicator",
                    value = extracted.vegNonVegStatus,
                    isValid = extracted.vegNonVegStatus == "VEG" || extracted.vegNonVegStatus == "NON_VEG"
                )
            }

            DeclarationRow(
                label = "Consumer Helpline",
                value = extracted.customerCareDetails,
                isValid = extracted.customerCareDetails.isNotBlank()
            )

            DeclarationRow(
                label = "Allergen Advisory",
                value = extracted.allergenDeclaration.ifBlank { "None declared" },
                isValid = extracted.allergenDeclaration.isNotBlank()
            )

            DeclarationRow(
                label = "Text Legibility",
                value = "${extracted.legibilityScore}/100" + if (extracted.unreadableFields.isNotEmpty()) " (${extracted.unreadableFields.joinToString()})" else "",
                isValid = extracted.legibilityScore >= 70 && extracted.unreadableFields.isEmpty()
            )

            if (extracted.nutritionalInfo.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Nutritional Breakdown:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = TextPrimary
                )
                extracted.nutritionalInfo.forEach { (nut, qty) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = nut, fontSize = 11.sp, color = TextSecondary)
                        Text(text = qty, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }
            }
        }
    }
}

@Composable
private fun DeclarationRow(
    label: String,
    value: String,
    isValid: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(if (isValid) StatusCleanGreen.copy(alpha = 0.2f) else StatusViolationRed.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isValid) Icons.Default.Check else Icons.Default.Close,
                    contentDescription = null,
                    tint = if (isValid) StatusCleanGreen else StatusViolationRed,
                    modifier = Modifier.size(12.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = value.ifBlank { "Not detected / Missing" },
                    fontSize = 11.sp,
                    color = if (isValid) TextSecondary else StatusViolationRed
                )
            }
        }
    }
}

@Composable
private fun PackagingPhotoAndOcrSection(
    sampleDrawable: String?,
    imageUri: String?,
    rawOcr: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Image View
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    sampleDrawable == "img_sample_food" -> {
                        Image(
                            painter = painterResource(id = R.drawable.img_sample_food),
                            contentDescription = "Inspected Packaging",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    sampleDrawable == "img_sample_snack" -> {
                        Image(
                            painter = painterResource(id = R.drawable.img_sample_snack),
                            contentDescription = "Inspected Packaging",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    imageUri != null -> {
                        AsyncImage(
                            model = imageUri,
                            contentDescription = "Inspected Packaging",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    else -> {
                        Image(
                            painter = painterResource(id = R.drawable.img_sample_food),
                            contentDescription = "Packaging",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }

        // Raw OCR Output
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = DarkPrimaryLilac
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Raw Optical Character Recognition (OCR) Log",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = DarkBg,
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                ) {
                    Text(
                        text = rawOcr.ifBlank { "OCR characters processed and mapped directly to statutory fields." },
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(10.dp),
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

private fun buildShareReportSummary(extracted: ExtractedLabelData, report: ComplianceReport): String {
    return """
        PACKAGING COMPLIANCE AUDIT REPORT
        Product: ${extracted.productName}
        Manufacturer: ${extracted.brandOrManufacturer}
        Status: ${report.status.label}
        Compliance Score: ${report.complianceScore}%
        Passed Checks: ${report.passedChecksCount}/${report.totalChecksCount}
        
        Violations Flagged (${report.violations.size}):
        ${report.violations.joinToString("\n") { "- [${it.severity.name}] ${it.ruleTitle} (${it.regulationCode}): ${it.issueDescription}" }}
        
        Audited via LabelGuard AI Compliance Checker
    """.trimIndent()
}

package com.example.labelguard.ui.screens

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.labelguard.data.model.ProductCategory
import com.example.labelguard.ui.viewmodel.LabelGuardViewModel
import com.example.labelguard.ui.viewmodel.ScanStep
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkOnPrimaryPurple
import com.example.ui.theme.DarkPrimaryContainerPurple
import com.example.ui.theme.DarkPrimaryLilac
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.StatusCleanGreen
import com.example.ui.theme.StatusViolationRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerScreen(
    viewModel: LabelGuardViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scanStep by viewModel.scanStep.collectAsState()
    val selectedBitmap by viewModel.selectedBitmap.collectAsState()
    val selectedUri by viewModel.selectedImageUri.collectAsState()
    val selectedSample by viewModel.selectedSampleDrawableName.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val errorMessage by viewModel.scanErrorMessage.collectAsState()

    // Camera picker
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            viewModel.selectBitmapFromCamera(bitmap)
        }
    }

    // Photo picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.selectImageUri(uri)
        }
    }

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Packaging Compliance Scanner",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("scanner_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBg
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Category Selector
            CategorySelectorSection(
                selectedCategory = selectedCategory,
                onCategorySelect = viewModel::setProductCategory,
                enabled = scanStep == ScanStep.IDLE || scanStep == ScanStep.IMAGE_CAPTURED
            )

            // Image Source Picker (Camera / Gallery / Demo samples)
            ImageSourceOptionsCard(
                onTakePhoto = { cameraLauncher.launch(null) },
                onUploadPhoto = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                onSelectSample1 = {
                    viewModel.selectSampleImage("img_sample_food", ProductCategory.FOOD_BEVERAGE)
                },
                onSelectSample2 = {
                    viewModel.selectSampleImage("img_sample_snack", ProductCategory.FOOD_BEVERAGE)
                },
                selectedSample = selectedSample,
                enabled = scanStep == ScanStep.IDLE || scanStep == ScanStep.IMAGE_CAPTURED
            )

            // Preview Container & Scanning Overlay
            PackagingPreviewContainer(
                bitmap = selectedBitmap,
                imageUri = selectedUri?.toString(),
                sampleDrawableName = selectedSample,
                scanStep = scanStep
            )

            // Step Progress Checklist when analyzing
            AnimatedVisibility(
                visible = scanStep == ScanStep.PERFORMING_OCR || scanStep == ScanStep.APPLYING_RULES
            ) {
                ScanProgressChecklist(scanStep = scanStep)
            }

            // Error notice if any
            if (errorMessage != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = StatusViolationRed.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, StatusViolationRed.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Info, contentDescription = "Error", tint = StatusViolationRed)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = errorMessage ?: "", color = StatusViolationRed, fontSize = 13.sp)
                    }
                }
            }

            // Inspection Trigger Action
            val hasImage = selectedBitmap != null || selectedUri != null || selectedSample != null
            val isProcessing = scanStep == ScanStep.PERFORMING_OCR || scanStep == ScanStep.APPLYING_RULES

            Button(
                onClick = { viewModel.startAnalysis() },
                enabled = hasImage && !isProcessing,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("run_inspection_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DarkPrimaryLilac,
                    contentColor = DarkOnPrimaryPurple,
                    disabledContainerColor = DarkSurfaceCard,
                    disabledContentColor = TextMuted
                )
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = DarkOnPrimaryPurple,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (scanStep == ScanStep.PERFORMING_OCR) "Running Multimodal OCR..." else "Evaluating Statutory Rules...",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                } else {
                    Icon(imageVector = Icons.Default.Visibility, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (hasImage) "RUN COMPLIANCE INSPECTION" else "SELECT PACKAGING PHOTO FIRST",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // Statutory Guidelines Disclaimer
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info",
                        tint = DarkPrimaryLilac,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Automated inspection audits declarations against Legal Metrology (Packaged Commodities) Rules 2011 & FSSAI Labelling Regulations 2020 including FSSAI logo, veg dot, MRP with taxes, net quantity, batch and expiry validity.",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun CategorySelectorSection(
    selectedCategory: ProductCategory,
    onCategorySelect: (ProductCategory) -> Unit,
    enabled: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "1. PRODUCT REGULATORY CATEGORY",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = TextSecondary
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CategoryChip(
                title = "Food (FSSAI)",
                icon = Icons.Default.Fastfood,
                isSelected = selectedCategory == ProductCategory.FOOD_BEVERAGE,
                onClick = { if (enabled) onCategorySelect(ProductCategory.FOOD_BEVERAGE) },
                modifier = Modifier.weight(1f)
            )
            CategoryChip(
                title = "Cosmetics",
                icon = Icons.Default.LocalPharmacy,
                isSelected = selectedCategory == ProductCategory.COSMETICS,
                onClick = { if (enabled) onCategorySelect(ProductCategory.COSMETICS) },
                modifier = Modifier.weight(1f)
            )
            CategoryChip(
                title = "Commodities",
                icon = Icons.Default.ShoppingBag,
                isSelected = selectedCategory == ProductCategory.PACKAGED_GOODS,
                onClick = { if (enabled) onCategorySelect(ProductCategory.PACKAGED_GOODS) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun CategoryChip(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) DarkPrimaryLilac.copy(alpha = 0.2f) else DarkSurfaceCard
        ),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(
            1.dp,
            if (isSelected) DarkPrimaryLilac else Color.White.copy(alpha = 0.05f)
        )
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) DarkPrimaryLilac else TextSecondary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) DarkPrimaryLilac else TextPrimary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ImageSourceOptionsCard(
    onTakePhoto: () -> Unit,
    onUploadPhoto: () -> Unit,
    onSelectSample1: () -> Unit,
    onSelectSample2: () -> Unit,
    selectedSample: String?,
    enabled: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "2. CAPTURE OR SELECT PACKAGING",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onTakePhoto,
                    enabled = enabled,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("camera_capture_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TextPrimary
                    ),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Camera",
                        modifier = Modifier.size(16.dp),
                        tint = DarkPrimaryLilac
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Take Photo", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                OutlinedButton(
                    onClick = onUploadPhoto,
                    enabled = enabled,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("upload_gallery_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TextPrimary
                    ),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = "Upload",
                        modifier = Modifier.size(16.dp),
                        tint = DarkPrimaryLilac
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Upload Label", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Instant 1-Tap Benchmark Labels:",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Sample 1: Cookies (Compliant)
                SamplePackagingPill(
                    title = "Compliant Cookie Box",
                    subtitle = "FSSAI + Veg dot + MRP",
                    badge = "Pass Demo",
                    badgeColor = StatusCleanGreen,
                    isSelected = selectedSample == "img_sample_food",
                    onClick = onSelectSample1,
                    modifier = Modifier.weight(1f)
                )

                // Sample 2: Namkeen (Violations)
                SamplePackagingPill(
                    title = "Non-Compliant Snack",
                    subtitle = "Expired + Missing FSSAI",
                    badge = "Fail Demo",
                    badgeColor = StatusViolationRed,
                    isSelected = selectedSample == "img_sample_snack",
                    onClick = onSelectSample2,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun SamplePackagingPill(
    title: String,
    subtitle: String,
    badge: String,
    badgeColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) DarkPrimaryLilac.copy(alpha = 0.15f) else DarkBg,
        border = if (isSelected) BorderStroke(1.5.dp, DarkPrimaryLilac) else BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = TextPrimary
                )
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = badgeColor.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = badge,
                        color = badgeColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun PackagingPreviewContainer(
    bitmap: Bitmap?,
    imageUri: String?,
    sampleDrawableName: String?,
    scanStep: ScanStep
) {
    val isScanning = scanStep == ScanStep.PERFORMING_OCR || scanStep == ScanStep.APPLYING_RULES

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .testTag("packaging_preview_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when {
                bitmap != null -> {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Selected packaging preview",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                sampleDrawableName == "img_sample_food" -> {
                    Image(
                        painter = painterResource(id = R.drawable.img_sample_food),
                        contentDescription = "Sample food packaging",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                sampleDrawableName == "img_sample_snack" -> {
                    Image(
                        painter = painterResource(id = R.drawable.img_sample_snack),
                        contentDescription = "Sample snack packaging",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                imageUri != null -> {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Selected packaging image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                else -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(DarkBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Camera placeholder",
                                tint = DarkPrimaryLilac
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No Packaging Label Loaded",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Take a photo, pick from gallery, or tap a sample above.",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Laser Beam Animation during OCR scanning
            if (isScanning) {
                val transition = rememberInfiniteTransition(label = "scanner_laser")
                val laserProgress by transition.animateFloat(
                    initialValue = 0f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1400, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "laser_pos"
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.45f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .align(Alignment.TopCenter)
                            .offset(y = (220 * laserProgress).dp)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        DarkPrimaryLilac,
                                        Color(0xFFEADDFF),
                                        DarkPrimaryLilac,
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun ScanProgressChecklist(scanStep: ScanStep) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "PROCESSING INSPECTION PIPELINE",
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 1.sp,
                color = DarkPrimaryLilac
            )

            val step1Done = scanStep == ScanStep.PERFORMING_OCR || scanStep == ScanStep.APPLYING_RULES || scanStep == ScanStep.COMPLETED
            val step2Done = scanStep == ScanStep.APPLYING_RULES || scanStep == ScanStep.COMPLETED

            ProgressCheckItem(
                title = "Optical Character Recognition (OCR)",
                subtitle = "Extracting raw text, dates, batch code and numbers",
                isDone = step1Done,
                isActive = scanStep == ScanStep.PERFORMING_OCR
            )

            ProgressCheckItem(
                title = "Statutory Rules Verification",
                subtitle = "Verifying Legal Metrology & FSSAI declaration mandates",
                isDone = step2Done,
                isActive = scanStep == ScanStep.APPLYING_RULES
            )
        }
    }
}

@Composable
private fun ProgressCheckItem(
    title: String,
    subtitle: String,
    isDone: Boolean,
    isActive: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(
                    if (isDone) StatusCleanGreen.copy(alpha = 0.2f) else DarkBg
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isActive) {
                CircularProgressIndicator(
                    modifier = Modifier.size(14.dp),
                    strokeWidth = 2.dp,
                    color = DarkPrimaryLilac
                )
            } else if (isDone) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Done",
                    tint = StatusCleanGreen,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                color = TextPrimary
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = TextSecondary
            )
        }
    }
}

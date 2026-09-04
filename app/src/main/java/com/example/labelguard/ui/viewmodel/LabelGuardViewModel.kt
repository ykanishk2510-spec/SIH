package com.example.labelguard.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.labelguard.data.model.ComplianceReport
import com.example.labelguard.data.model.ComplianceStatus
import com.example.labelguard.data.model.ExtractedLabelData
import com.example.labelguard.data.model.ProductCategory
import com.example.labelguard.data.model.ProductScanEntity
import com.example.labelguard.data.repository.JsonHelper
import com.example.labelguard.data.repository.ScanRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen {
    DASHBOARD,
    SCANNER,
    REPORT_DETAILS,
    RULES_CATALOG
}

enum class ScanStep {
    IDLE,
    IMAGE_CAPTURED,
    PERFORMING_OCR,
    APPLYING_RULES,
    COMPLETED,
    ERROR
}

data class DashboardStats(
    val totalScans: Int = 0,
    val compliantCount: Int = 0,
    val nonCompliantCount: Int = 0,
    val partiallyCompliantCount: Int = 0,
    val complianceRatePercent: Int = 0,
    val totalCriticalViolations: Int = 0
)

class LabelGuardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ScanRepository(application)

    private val _currentScreen = MutableStateFlow(AppScreen.DASHBOARD)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedStatusFilter = MutableStateFlow<String?>("ALL")
    val selectedStatusFilter: StateFlow<String?> = _selectedStatusFilter.asStateFlow()

    private val _selectedCategoryFilter = MutableStateFlow<ProductCategory?>(null)
    val selectedCategoryFilter: StateFlow<ProductCategory?> = _selectedCategoryFilter.asStateFlow()

    // Active scan process state
    private val _scanStep = MutableStateFlow(ScanStep.IDLE)
    val scanStep: StateFlow<ScanStep> = _scanStep.asStateFlow()

    private val _selectedBitmap = MutableStateFlow<Bitmap?>(null)
    val selectedBitmap: StateFlow<Bitmap?> = _selectedBitmap.asStateFlow()

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri.asStateFlow()

    private val _selectedSampleDrawableName = MutableStateFlow<String?>(null)
    val selectedSampleDrawableName: StateFlow<String?> = _selectedSampleDrawableName.asStateFlow()

    private val _selectedCategory = MutableStateFlow(ProductCategory.FOOD_BEVERAGE)
    val selectedCategory: StateFlow<ProductCategory> = _selectedCategory.asStateFlow()

    private val _activeExtractedData = MutableStateFlow<ExtractedLabelData?>(null)
    val activeExtractedData: StateFlow<ExtractedLabelData?> = _activeExtractedData.asStateFlow()

    private val _activeReport = MutableStateFlow<ComplianceReport?>(null)
    val activeReport: StateFlow<ComplianceReport?> = _activeReport.asStateFlow()

    private val _selectedScanEntity = MutableStateFlow<ProductScanEntity?>(null)
    val selectedScanEntity: StateFlow<ProductScanEntity?> = _selectedScanEntity.asStateFlow()

    private val _scanErrorMessage = MutableStateFlow<String?>(null)
    val scanErrorMessage: StateFlow<String?> = _scanErrorMessage.asStateFlow()

    // Flow of all scans from DB
    val allScansFlow = repository.allScans.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Filtered scans
    val filteredScans = combine(
        allScansFlow,
        _searchQuery,
        _selectedStatusFilter,
        _selectedCategoryFilter
    ) { scans, query, status, category ->
        scans.filter { scan ->
            val matchesQuery = query.isBlank() ||
                    scan.productName.contains(query, ignoreCase = true) ||
                    scan.brand.contains(query, ignoreCase = true)

            val matchesStatus = status == null || status == "ALL" || scan.complianceStatus == status

            val matchesCategory = category == null || scan.category == category.name

            matchesQuery && matchesStatus && matchesCategory
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Dashboard computed stats
    val dashboardStats = allScansFlow.combine(_searchQuery) { scans, _ ->
        val total = scans.size
        val compliant = scans.count { it.complianceStatus == ComplianceStatus.COMPLIANT.name }
        val nonCompliant = scans.count { it.complianceStatus == ComplianceStatus.NON_COMPLIANT.name }
        val partial = scans.count { it.complianceStatus == ComplianceStatus.PARTIALLY_COMPLIANT.name }
        val rate = if (total > 0) ((compliant.toDouble() / total) * 100).toInt() else 0
        val critical = scans.sumOf { it.criticalViolationsCount }

        DashboardStats(
            totalScans = total,
            compliantCount = compliant,
            nonCompliantCount = nonCompliant,
            partiallyCompliantCount = partial,
            complianceRatePercent = rate,
            totalCriticalViolations = critical
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardStats()
    )

    init {
        viewModelScope.launch {
            repository.checkAndSeedInitialScans()
        }
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setStatusFilter(status: String?) {
        _selectedStatusFilter.value = status
    }

    fun setCategoryFilter(category: ProductCategory?) {
        _selectedCategoryFilter.value = category
    }

    fun setProductCategory(category: ProductCategory) {
        _selectedCategory.value = category
    }

    fun selectSampleImage(drawableName: String, category: ProductCategory) {
        _selectedSampleDrawableName.value = drawableName
        _selectedBitmap.value = null
        _selectedImageUri.value = null
        _selectedCategory.value = category
        _scanStep.value = ScanStep.IMAGE_CAPTURED
    }

    fun selectBitmapFromCamera(bitmap: Bitmap) {
        _selectedBitmap.value = bitmap
        _selectedImageUri.value = null
        _selectedSampleDrawableName.value = null
        _scanStep.value = ScanStep.IMAGE_CAPTURED
    }

    fun selectImageUri(uri: Uri) {
        _selectedImageUri.value = uri
        _selectedSampleDrawableName.value = null
        try {
            val bitmap = MediaStore.Images.Media.getBitmap(
                getApplication<Application>().contentResolver,
                uri
            )
            _selectedBitmap.value = bitmap
        } catch (e: Exception) {
            _selectedBitmap.value = null
        }
        _scanStep.value = ScanStep.IMAGE_CAPTURED
    }

    fun resetScanner() {
        _scanStep.value = ScanStep.IDLE
        _selectedBitmap.value = null
        _selectedImageUri.value = null
        _selectedSampleDrawableName.value = null
        _activeExtractedData.value = null
        _activeReport.value = null
        _scanErrorMessage.value = null
    }

    fun startAnalysis() {
        val sampleName = _selectedSampleDrawableName.value
        val bitmap = _selectedBitmap.value
        val category = _selectedCategory.value

        if (sampleName == null && bitmap == null) {
            _scanErrorMessage.value = "Please select or capture a packaging photo first."
            return
        }

        viewModelScope.launch {
            try {
                _scanStep.value = ScanStep.PERFORMING_OCR
                delay(800) // Visual progress feedback

                _scanStep.value = ScanStep.APPLYING_RULES
                delay(700)

                val (extracted, report) = repository.analyzeAndEvaluate(
                    bitmap = bitmap,
                    category = category,
                    sampleIdentifier = sampleName
                )

                _activeExtractedData.value = extracted
                _activeReport.value = report

                // Save to Room DB
                val scanId = repository.saveScan(
                    productName = extracted.productName,
                    brand = extracted.brandOrManufacturer,
                    category = category,
                    imageUri = _selectedImageUri.value?.toString(),
                    sampleDrawableName = sampleName,
                    extractedData = extracted,
                    report = report
                )

                // Select newly saved scan for report viewing
                _selectedScanEntity.value = ProductScanEntity(
                    id = scanId,
                    productName = extracted.productName,
                    brand = extracted.brandOrManufacturer,
                    category = category.name,
                    imageUri = _selectedImageUri.value?.toString(),
                    sampleDrawableName = sampleName,
                    complianceStatus = report.status.name,
                    complianceScore = report.complianceScore,
                    criticalViolationsCount = report.violations.count { it.severity == com.example.labelguard.data.model.ViolationSeverity.CRITICAL },
                    majorViolationsCount = report.violations.count { it.severity == com.example.labelguard.data.model.ViolationSeverity.MAJOR },
                    minorViolationsCount = report.violations.count { it.severity == com.example.labelguard.data.model.ViolationSeverity.MINOR },
                    extractedJson = JsonHelper.serializeExtractedData(extracted),
                    reportJson = JsonHelper.serializeReport(report)
                )

                _scanStep.value = ScanStep.COMPLETED
                _currentScreen.value = AppScreen.REPORT_DETAILS
            } catch (e: Exception) {
                _scanStep.value = ScanStep.ERROR
                _scanErrorMessage.value = "Analysis failed: ${e.localizedMessage ?: "Unknown error"}"
            }
        }
    }

    fun openReportDetails(scan: ProductScanEntity) {
        _selectedScanEntity.value = scan
        _activeExtractedData.value = JsonHelper.deserializeExtractedData(scan.extractedJson)
        _activeReport.value = JsonHelper.deserializeReport(scan.reportJson)
        _selectedSampleDrawableName.value = scan.sampleDrawableName
        _selectedImageUri.value = scan.imageUri?.let { Uri.parse(it) }
        _currentScreen.value = AppScreen.REPORT_DETAILS
    }

    fun deleteScan(id: Long) {
        viewModelScope.launch {
            repository.deleteScan(id)
            if (_selectedScanEntity.value?.id == id) {
                _selectedScanEntity.value = null
                _currentScreen.value = AppScreen.DASHBOARD
            }
        }
    }
}

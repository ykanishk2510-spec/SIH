package com.example.labelguard.data.repository

import android.content.Context
import android.graphics.Bitmap
import com.example.labelguard.data.local.LabelGuardDatabase
import com.example.labelguard.data.local.ProductScanDao
import com.example.labelguard.data.model.ComplianceReport
import com.example.labelguard.data.model.ComplianceStatus
import com.example.labelguard.data.model.ExtractedLabelData
import com.example.labelguard.data.model.ProductCategory
import com.example.labelguard.data.model.ProductScanEntity
import com.example.labelguard.data.model.ViolationSeverity
import com.example.labelguard.remote.GeminiLabelAnalyzer
import com.example.labelguard.rules.LabelRulesEngine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class ScanRepository(context: Context) {

    private val db = LabelGuardDatabase.getDatabase(context)
    private val scanDao: ProductScanDao = db.productScanDao()

    val allScans: Flow<List<ProductScanEntity>> = scanDao.getAllScans()

    fun getScanById(id: Long): Flow<ProductScanEntity?> = scanDao.getScanById(id)

    suspend fun saveScan(
        productName: String,
        brand: String,
        category: ProductCategory,
        imageUri: String?,
        sampleDrawableName: String?,
        extractedData: ExtractedLabelData,
        report: ComplianceReport
    ): Long {
        val criticalCount = report.violations.count { it.severity == ViolationSeverity.CRITICAL }
        val majorCount = report.violations.count { it.severity == ViolationSeverity.MAJOR }
        val minorCount = report.violations.count { it.severity == ViolationSeverity.MINOR }

        val entity = ProductScanEntity(
            productName = if (productName.isNotBlank()) productName else extractedData.productName.ifBlank { "Untitled Product" },
            brand = if (brand.isNotBlank()) brand else extractedData.brandOrManufacturer.ifBlank { "Unbranded" },
            category = category.name,
            imageUri = imageUri,
            sampleDrawableName = sampleDrawableName,
            complianceStatus = report.status.name,
            complianceScore = report.complianceScore,
            criticalViolationsCount = criticalCount,
            majorViolationsCount = majorCount,
            minorViolationsCount = minorCount,
            timestamp = System.currentTimeMillis(),
            extractedJson = JsonHelper.serializeExtractedData(extractedData),
            reportJson = JsonHelper.serializeReport(report)
        )
        return scanDao.insertScan(entity)
    }

    suspend fun deleteScan(id: Long) {
        scanDao.deleteScanById(id)
    }

    suspend fun analyzeAndEvaluate(
        bitmap: Bitmap?,
        category: ProductCategory,
        sampleIdentifier: String?
    ): Pair<ExtractedLabelData, ComplianceReport> {
        val extractedData = GeminiLabelAnalyzer.analyzePackagingImage(
            bitmap = bitmap,
            category = category,
            sampleIdentifier = sampleIdentifier
        )
        val report = LabelRulesEngine.evaluate(extractedData, category)
        return Pair(extractedData, report)
    }

    suspend fun checkAndSeedInitialScans() {
        val existing = scanDao.getAllScans().firstOrNull()
        if (existing.isNullOrEmpty()) {
            // Seed sample 1: Compliant Delights Cookies
            val sample1Extracted = GeminiLabelAnalyzer.getSampleAnalysis("img_sample_food", ProductCategory.FOOD_BEVERAGE)
            val sample1Report = LabelRulesEngine.evaluate(sample1Extracted, ProductCategory.FOOD_BEVERAGE)
            scanDao.insertScan(
                ProductScanEntity(
                    productName = sample1Extracted.productName,
                    brand = sample1Extracted.brandOrManufacturer,
                    category = ProductCategory.FOOD_BEVERAGE.name,
                    imageUri = null,
                    sampleDrawableName = "img_sample_food",
                    complianceStatus = sample1Report.status.name,
                    complianceScore = sample1Report.complianceScore,
                    criticalViolationsCount = sample1Report.violations.count { it.severity == ViolationSeverity.CRITICAL },
                    majorViolationsCount = sample1Report.violations.count { it.severity == ViolationSeverity.MAJOR },
                    minorViolationsCount = sample1Report.violations.count { it.severity == ViolationSeverity.MINOR },
                    timestamp = System.currentTimeMillis() - 3600000 * 4,
                    extractedJson = JsonHelper.serializeExtractedData(sample1Extracted),
                    reportJson = JsonHelper.serializeReport(sample1Report)
                )
            )

            // Seed sample 2: Non-Compliant Spicy Namkeen
            val sample2Extracted = GeminiLabelAnalyzer.getSampleAnalysis("img_sample_snack", ProductCategory.FOOD_BEVERAGE)
            val sample2Report = LabelRulesEngine.evaluate(sample2Extracted, ProductCategory.FOOD_BEVERAGE)
            scanDao.insertScan(
                ProductScanEntity(
                    productName = sample2Extracted.productName,
                    brand = sample2Extracted.brandOrManufacturer,
                    category = ProductCategory.FOOD_BEVERAGE.name,
                    imageUri = null,
                    sampleDrawableName = "img_sample_snack",
                    complianceStatus = sample2Report.status.name,
                    complianceScore = sample2Report.complianceScore,
                    criticalViolationsCount = sample2Report.violations.count { it.severity == ViolationSeverity.CRITICAL },
                    majorViolationsCount = sample2Report.violations.count { it.severity == ViolationSeverity.MAJOR },
                    minorViolationsCount = sample2Report.violations.count { it.severity == ViolationSeverity.MINOR },
                    timestamp = System.currentTimeMillis() - 3600000 * 2,
                    extractedJson = JsonHelper.serializeExtractedData(sample2Extracted),
                    reportJson = JsonHelper.serializeReport(sample2Report)
                )
            )
        }
    }
}

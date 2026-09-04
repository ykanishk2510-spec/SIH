package com.example.labelguard.data.model

data class ExtractedLabelData(
    val productName: String = "",
    val brandOrManufacturer: String = "",
    val manufacturerAddress: String = "",
    val countryOfOrigin: String = "",
    val netQuantity: String = "",
    val mrp: String = "",
    val isTaxesMentioned: Boolean = false,
    val manufacturingDate: String = "",
    val expiryOrBestBeforeDate: String = "",
    val isExpired: Boolean = false,
    val batchOrLotNumber: String = "",
    val fssaiLicenseNumber: String = "",
    val isFssaiValid: Boolean = false,
    val vegNonVegStatus: String = "NOT_APPLICABLE", // "VEG", "NON_VEG", "NOT_APPLICABLE", "MISSING"
    val ingredients: List<String> = emptyList(),
    val nutritionalInfo: Map<String, String> = emptyMap(),
    val hasNutritionalTable: Boolean = false,
    val allergenDeclaration: String = "",
    val customerCareDetails: String = "",
    val barcodeOrQr: String = "",
    val legibilityScore: Int = 100, // 0-100
    val unreadableFields: List<String> = emptyList(),
    val rawExtractedText: String = ""
)

data class ComplianceViolation(
    val id: String,
    val ruleTitle: String,
    val regulationCode: String, // e.g. "FSSAI Reg. 2.2.2", "Legal Metrology Sec 6(1)"
    val severity: ViolationSeverity,
    val fieldName: String,
    val issueDescription: String,
    val remediationAdvice: String
)

data class ComplianceReport(
    val status: ComplianceStatus,
    val complianceScore: Int, // 0 to 100
    val violations: List<ComplianceViolation> = emptyList(),
    val passedChecksCount: Int = 0,
    val totalChecksCount: Int = 0,
    val summaryVerdict: String = "",
    val analyzedTimestamp: Long = System.currentTimeMillis()
)

data class ComplianceRuleDefinition(
    val id: String,
    val title: String,
    val regulationReference: String,
    val category: ProductCategory?,
    val defaultSeverity: ViolationSeverity,
    val description: String,
    val mandatoryFor: String
)

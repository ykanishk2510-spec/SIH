package com.example.labelguard.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product_scans")
data class ProductScanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productName: String,
    val brand: String,
    val category: String, // FOOD_BEVERAGE, COSMETICS, PACKAGED_GOODS
    val imageUri: String? = null,
    val sampleDrawableName: String? = null,
    val complianceStatus: String, // COMPLIANT, PARTIALLY_COMPLIANT, NON_COMPLIANT
    val complianceScore: Int,
    val criticalViolationsCount: Int = 0,
    val majorViolationsCount: Int = 0,
    val minorViolationsCount: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val extractedJson: String = "",
    val reportJson: String = ""
)

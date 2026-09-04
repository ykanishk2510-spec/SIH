package com.example

import com.example.labelguard.data.model.ComplianceStatus
import com.example.labelguard.data.model.ExtractedLabelData
import com.example.labelguard.data.model.ProductCategory
import com.example.labelguard.rules.LabelRulesEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ComplianceRulesEngineTest {

    @Test
    fun testCompliantLabelEvaluation() {
        val sampleData = ExtractedLabelData(
            productName = "Organic Green Tea",
            brandOrManufacturer = "Himalayan Herbs Ltd",
            manufacturerAddress = "Plot 42, Industrial Area, Solan, HP 173212",
            netQuantity = "100 g",
            mrp = "₹250.00",
            isTaxesMentioned = true,
            manufacturingDate = "15/01/2026",
            expiryOrBestBeforeDate = "14/01/2027",
            batchOrLotNumber = "BCH-88219",
            fssaiLicenseNumber = "10012011000142",
            isFssaiValid = true,
            vegNonVegStatus = "VEG",
            countryOfOrigin = "India",
            customerCareDetails = "care@himalayanherbs.in, 1800-112-233",
            hasNutritionalTable = true,
            allergenDeclaration = "Contains green tea leaves",
            legibilityScore = 95
        )

        val report = LabelRulesEngine.evaluate(sampleData, ProductCategory.FOOD_BEVERAGE)
        assertNotNull(report)
        assertEquals(ComplianceStatus.COMPLIANT, report.status)
        assertTrue(report.complianceScore >= 90)
    }

    @Test
    fun testNonCompliantMissingFieldsEvaluation() {
        val incompleteData = ExtractedLabelData(
            productName = "",
            brandOrManufacturer = "",
            netQuantity = "",
            mrp = "250",
            isTaxesMentioned = false
        )

        val report = LabelRulesEngine.evaluate(incompleteData, ProductCategory.PACKAGED_GOODS)
        assertNotNull(report)
        assertTrue(report.violations.isNotEmpty())
    }
}

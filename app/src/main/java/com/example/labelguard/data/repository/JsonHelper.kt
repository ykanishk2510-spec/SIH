package com.example.labelguard.data.repository

import com.example.labelguard.data.model.ComplianceReport
import com.example.labelguard.data.model.ComplianceStatus
import com.example.labelguard.data.model.ComplianceViolation
import com.example.labelguard.data.model.ExtractedLabelData
import com.example.labelguard.data.model.ViolationSeverity
import org.json.JSONArray
import org.json.JSONObject

object JsonHelper {

    fun serializeExtractedData(data: ExtractedLabelData): String {
        val json = JSONObject()
        json.put("productName", data.productName)
        json.put("brandOrManufacturer", data.brandOrManufacturer)
        json.put("manufacturerAddress", data.manufacturerAddress)
        json.put("countryOfOrigin", data.countryOfOrigin)
        json.put("netQuantity", data.netQuantity)
        json.put("mrp", data.mrp)
        json.put("isTaxesMentioned", data.isTaxesMentioned)
        json.put("manufacturingDate", data.manufacturingDate)
        json.put("expiryOrBestBeforeDate", data.expiryOrBestBeforeDate)
        json.put("isExpired", data.isExpired)
        json.put("batchOrLotNumber", data.batchOrLotNumber)
        json.put("fssaiLicenseNumber", data.fssaiLicenseNumber)
        json.put("isFssaiValid", data.isFssaiValid)
        json.put("vegNonVegStatus", data.vegNonVegStatus)

        val ingArray = JSONArray()
        data.ingredients.forEach { ingArray.put(it) }
        json.put("ingredients", ingArray)

        val nutJson = JSONObject()
        data.nutritionalInfo.forEach { (k, v) -> nutJson.put(k, v) }
        json.put("nutritionalInfo", nutJson)

        json.put("hasNutritionalTable", data.hasNutritionalTable)
        json.put("allergenDeclaration", data.allergenDeclaration)
        json.put("customerCareDetails", data.customerCareDetails)
        json.put("barcodeOrQr", data.barcodeOrQr)
        json.put("legibilityScore", data.legibilityScore)

        val unreadable = JSONArray()
        data.unreadableFields.forEach { unreadable.put(it) }
        json.put("unreadableFields", unreadable)

        json.put("rawExtractedText", data.rawExtractedText)
        return json.toString()
    }

    fun deserializeExtractedData(raw: String): ExtractedLabelData {
        if (raw.isBlank()) return ExtractedLabelData()
        val json = JSONObject(raw)
        val ingredients = mutableListOf<String>()
        val ingArray = json.optJSONArray("ingredients")
        if (ingArray != null) {
            for (i in 0 until ingArray.length()) {
                ingredients.add(ingArray.getString(i))
            }
        }

        val nutMap = mutableMapOf<String, String>()
        val nutJson = json.optJSONObject("nutritionalInfo")
        if (nutJson != null) {
            val keys = nutJson.keys()
            while (keys.hasNext()) {
                val k = keys.next()
                nutMap[k] = nutJson.optString(k)
            }
        }

        val unreadable = mutableListOf<String>()
        val unreadableArray = json.optJSONArray("unreadableFields")
        if (unreadableArray != null) {
            for (i in 0 until unreadableArray.length()) {
                unreadable.add(unreadableArray.getString(i))
            }
        }

        return ExtractedLabelData(
            productName = json.optString("productName"),
            brandOrManufacturer = json.optString("brandOrManufacturer"),
            manufacturerAddress = json.optString("manufacturerAddress"),
            countryOfOrigin = json.optString("countryOfOrigin"),
            netQuantity = json.optString("netQuantity"),
            mrp = json.optString("mrp"),
            isTaxesMentioned = json.optBoolean("isTaxesMentioned", true),
            manufacturingDate = json.optString("manufacturingDate"),
            expiryOrBestBeforeDate = json.optString("expiryOrBestBeforeDate"),
            isExpired = json.optBoolean("isExpired", false),
            batchOrLotNumber = json.optString("batchOrLotNumber"),
            fssaiLicenseNumber = json.optString("fssaiLicenseNumber"),
            isFssaiValid = json.optBoolean("isFssaiValid", true),
            vegNonVegStatus = json.optString("vegNonVegStatus", "VEG"),
            ingredients = ingredients,
            nutritionalInfo = nutMap,
            hasNutritionalTable = json.optBoolean("hasNutritionalTable", false),
            allergenDeclaration = json.optString("allergenDeclaration"),
            customerCareDetails = json.optString("customerCareDetails"),
            barcodeOrQr = json.optString("barcodeOrQr"),
            legibilityScore = json.optInt("legibilityScore", 85),
            unreadableFields = unreadable,
            rawExtractedText = json.optString("rawExtractedText")
        )
    }

    fun serializeReport(report: ComplianceReport): String {
        val json = JSONObject()
        json.put("status", report.status.name)
        json.put("complianceScore", report.complianceScore)
        json.put("passedChecksCount", report.passedChecksCount)
        json.put("totalChecksCount", report.totalChecksCount)
        json.put("summaryVerdict", report.summaryVerdict)
        json.put("analyzedTimestamp", report.analyzedTimestamp)

        val vArray = JSONArray()
        report.violations.forEach { v ->
            val vObj = JSONObject()
            vObj.put("id", v.id)
            vObj.put("ruleTitle", v.ruleTitle)
            vObj.put("regulationCode", v.regulationCode)
            vObj.put("severity", v.severity.name)
            vObj.put("fieldName", v.fieldName)
            vObj.put("issueDescription", v.issueDescription)
            vObj.put("remediationAdvice", v.remediationAdvice)
            vArray.put(vObj)
        }
        json.put("violations", vArray)

        return json.toString()
    }

    fun deserializeReport(raw: String): ComplianceReport {
        if (raw.isBlank()) return ComplianceReport(ComplianceStatus.NON_COMPLIANT, 0)
        val json = JSONObject(raw)
        val status = try {
            ComplianceStatus.valueOf(json.optString("status", ComplianceStatus.NON_COMPLIANT.name))
        } catch (e: Exception) {
            ComplianceStatus.NON_COMPLIANT
        }

        val violations = mutableListOf<ComplianceViolation>()
        val vArray = json.optJSONArray("violations")
        if (vArray != null) {
            for (i in 0 until vArray.length()) {
                val vObj = vArray.getJSONObject(i)
                val sev = try {
                    ViolationSeverity.valueOf(vObj.optString("severity", ViolationSeverity.MAJOR.name))
                } catch (e: Exception) {
                    ViolationSeverity.MAJOR
                }
                violations.add(
                    ComplianceViolation(
                        id = vObj.optString("id"),
                        ruleTitle = vObj.optString("ruleTitle"),
                        regulationCode = vObj.optString("regulationCode"),
                        severity = sev,
                        fieldName = vObj.optString("fieldName"),
                        issueDescription = vObj.optString("issueDescription"),
                        remediationAdvice = vObj.optString("remediationAdvice")
                    )
                )
            }
        }

        return ComplianceReport(
            status = status,
            complianceScore = json.optInt("complianceScore", 0),
            violations = violations,
            passedChecksCount = json.optInt("passedChecksCount", 0),
            totalChecksCount = json.optInt("totalChecksCount", 0),
            summaryVerdict = json.optString("summaryVerdict"),
            analyzedTimestamp = json.optLong("analyzedTimestamp", System.currentTimeMillis())
        )
    }
}

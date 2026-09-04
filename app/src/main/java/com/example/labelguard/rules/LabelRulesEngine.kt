package com.example.labelguard.rules

import com.example.labelguard.data.model.ComplianceReport
import com.example.labelguard.data.model.ComplianceRuleDefinition
import com.example.labelguard.data.model.ComplianceStatus
import com.example.labelguard.data.model.ComplianceViolation
import com.example.labelguard.data.model.ExtractedLabelData
import com.example.labelguard.data.model.ProductCategory
import com.example.labelguard.data.model.ViolationSeverity

object LabelRulesEngine {

    val predefinedRules: List<ComplianceRuleDefinition> = listOf(
        ComplianceRuleDefinition(
            id = "LM-01",
            title = "Principal Display Panel - Product Identification",
            regulationReference = "Legal Metrology (Packaged Commodities) Rules, 2011 - Rule 6(1)(a)",
            category = null,
            defaultSeverity = ViolationSeverity.CRITICAL,
            description = "The common or generic name of the commodity contained in the package must be prominently displayed.",
            mandatoryFor = "All Packaged Commodities"
        ),
        ComplianceRuleDefinition(
            id = "LM-02",
            title = "Standard Net Quantity & Units Declaration",
            regulationReference = "Legal Metrology Act, 2009 & PCR 2011 - Rule 6(1)(b)",
            category = null,
            defaultSeverity = ViolationSeverity.CRITICAL,
            description = "Net quantity must be declared in standard metric units (g, kg, ml, l, or units) with correct symbol abbreviations.",
            mandatoryFor = "All Packaged Commodities"
        ),
        ComplianceRuleDefinition(
            id = "LM-03",
            title = "Manufacturer / Packer Identity & Country of Origin",
            regulationReference = "Legal Metrology (Packaged Commodities) Rules, 2011 - Rule 6(1)(d)",
            category = null,
            defaultSeverity = ViolationSeverity.CRITICAL,
            description = "Name and complete address of the manufacturer, packer or importer, along with 'Country of Origin'.",
            mandatoryFor = "All Packaged Commodities"
        ),
        ComplianceRuleDefinition(
            id = "LM-04",
            title = "Maximum Retail Price (MRP) & Tax Notice",
            regulationReference = "Legal Metrology (Packaged Commodities) Rules, 2011 - Rule 6(1)(e)",
            category = null,
            defaultSeverity = ViolationSeverity.MAJOR,
            description = "MRP must be clearly stated in Indian Rupees and MUST include the phrase 'Inclusive of all taxes' or 'incl. of all taxes'.",
            mandatoryFor = "All Packaged Commodities"
        ),
        ComplianceRuleDefinition(
            id = "LM-05",
            title = "Manufacturing Date & Expiry / Best Before Validity",
            regulationReference = "Legal Metrology Rule 6(1)(c) & FSSAI Regulations 2.2.2(9)",
            category = null,
            defaultSeverity = ViolationSeverity.CRITICAL,
            description = "Month and year of manufacture/packaging and expiry or best-before period must be clearly marked.",
            mandatoryFor = "Food, Cosmetics & Perishable Commodities"
        ),
        ComplianceRuleDefinition(
            id = "LM-06",
            title = "Batch / Lot / Identification Number",
            regulationReference = "Legal Metrology (Packaged Commodities) Rules, 2011 - Rule 6(1)(g)",
            category = null,
            defaultSeverity = ViolationSeverity.MAJOR,
            description = "A distinguishing batch, lot or code number to trace production identity.",
            mandatoryFor = "All Packaged Commodities"
        ),
        ComplianceRuleDefinition(
            id = "LM-07",
            title = "Consumer Grievance / Customer Care Helpline",
            regulationReference = "Legal Metrology (Packaged Commodities) Rules, 2011 - Rule 6(1)(h)",
            category = null,
            defaultSeverity = ViolationSeverity.MAJOR,
            description = "Contact details of consumer care officer including telephone helpline number and email address.",
            mandatoryFor = "All Packaged Commodities"
        ),
        ComplianceRuleDefinition(
            id = "FSSAI-01",
            title = "FSSAI 14-Digit License Number & Logo",
            regulationReference = "Food Safety & Standards (Labelling and Display) Regs 2020 - Reg 2.2.2",
            category = ProductCategory.FOOD_BEVERAGE,
            defaultSeverity = ViolationSeverity.CRITICAL,
            description = "Food Business Operator must display the FSSAI logo alongside the valid 14-digit state/central license number.",
            mandatoryFor = "Food & Beverage Products"
        ),
        ComplianceRuleDefinition(
            id = "FSSAI-02",
            title = "Veg / Non-Veg Symbolic Indicator",
            regulationReference = "FSSAI (Labelling & Display) Regs 2020 - Reg 2.2.2(4)",
            category = ProductCategory.FOOD_BEVERAGE,
            defaultSeverity = ViolationSeverity.CRITICAL,
            description = "Every package of food must carry green circle inside green square for vegetarian or brown triangle inside brown square for non-vegetarian.",
            mandatoryFor = "Food & Beverage Products"
        ),
        ComplianceRuleDefinition(
            id = "FSSAI-03",
            title = "Mandatory Nutritional Information Panel",
            regulationReference = "FSSAI (Labelling & Display) Regs 2020 - Reg 2.2.2(3)",
            category = ProductCategory.FOOD_BEVERAGE,
            defaultSeverity = ViolationSeverity.MAJOR,
            description = "Nutritional values per 100g/100ml: Energy, Protein, Carbohydrate, Added Sugars, Saturated Fat, Trans Fat & Sodium.",
            mandatoryFor = "Food & Beverage Products"
        ),
        ComplianceRuleDefinition(
            id = "FSSAI-04",
            title = "Allergen Warning & Declaration",
            regulationReference = "FSSAI (Labelling & Display) Regs 2020 - Reg 2.2.2(5)",
            category = ProductCategory.FOOD_BEVERAGE,
            defaultSeverity = ViolationSeverity.MAJOR,
            description = "Mandatory declaration for major food allergens (cereals containing gluten, milk, nuts, soy, etc.).",
            mandatoryFor = "Food & Beverage Products"
        ),
        ComplianceRuleDefinition(
            id = "QUAL-01",
            title = "Text Legibility, Contrast & Minimum Font Height",
            regulationReference = "Legal Metrology Rules 2011 - Rule 9 & FSSAI Standards",
            category = null,
            defaultSeverity = ViolationSeverity.MAJOR,
            description = "Mandatory declarations must be clearly legible, prominent, indelible and have adequate color contrast.",
            mandatoryFor = "All Packaged Commodities"
        )
    )

    fun evaluate(data: ExtractedLabelData, category: ProductCategory): ComplianceReport {
        val violations = mutableListOf<ComplianceViolation>()
        var passedCount = 0
        var totalCount = 0

        // 1. LM-01: Product Name & Generic Description
        totalCount++
        if (data.productName.isBlank()) {
            violations.add(
                ComplianceViolation(
                    id = "LM-01",
                    ruleTitle = "Missing Product Identification",
                    regulationCode = "LMPCR Rule 6(1)(a)",
                    severity = ViolationSeverity.CRITICAL,
                    fieldName = "Product Name",
                    issueDescription = "The package does not clearly declare the generic or common identity of the commodity on the principal display panel.",
                    remediationAdvice = "Display the generic name of the product prominently in bold typeface on the front display panel."
                )
            )
        } else {
            passedCount++
        }

        // 2. LM-02: Net Quantity
        totalCount++
        val netQtyValid = data.netQuantity.isNotBlank() &&
                (data.netQuantity.contains(Regex("""\d+\s*(g|kg|ml|l|gm|units|pieces|N)""", RegexOption.IGNORE_CASE)))
        if (!netQtyValid) {
            violations.add(
                ComplianceViolation(
                    id = "LM-02",
                    ruleTitle = "Invalid / Missing Net Quantity",
                    regulationCode = "LMPCR Rule 6(1)(b)",
                    severity = ViolationSeverity.CRITICAL,
                    fieldName = "Net Quantity",
                    issueDescription = if (data.netQuantity.isBlank()) "Net quantity is completely missing from the packaging."
                    else "Net quantity '${data.netQuantity}' does not follow standard metric SI units (e.g. g, kg, ml, l).",
                    remediationAdvice = "Declare Net Quantity in standard SI metric units with correct spacing (e.g., 'Net Quantity: 200 g' or '500 ml') in minimum required font size."
                )
            )
        } else {
            passedCount++
        }

        // 3. LM-03: Manufacturer / Packer & Country of Origin
        totalCount++
        val hasMfg = data.brandOrManufacturer.isNotBlank() && data.manufacturerAddress.isNotBlank()
        val hasCountry = data.countryOfOrigin.isNotBlank()
        if (!hasMfg || !hasCountry) {
            val missingParts = mutableListOf<String>()
            if (data.brandOrManufacturer.isBlank()) missingParts.add("Manufacturer name")
            if (data.manufacturerAddress.isBlank()) missingParts.add("Complete address")
            if (!hasCountry) missingParts.add("Country of Origin")

            violations.add(
                ComplianceViolation(
                    id = "LM-03",
                    ruleTitle = "Incomplete Manufacturer / Origin Declaration",
                    regulationCode = "LMPCR Rule 6(1)(d)",
                    severity = ViolationSeverity.CRITICAL,
                    fieldName = "Manufacturer & Country of Origin",
                    issueDescription = "Mandatory details missing: ${missingParts.joinToString(", ")}.",
                    remediationAdvice = "Print 'Manufactured / Packed by: [Full Company Name], [Complete Postal Address with PIN code]' and explicit 'Country of Origin: India' (or country of manufacture)."
                )
            )
        } else {
            passedCount++
        }

        // 4. LM-04: MRP with Tax Notice
        totalCount++
        val hasMrp = data.mrp.isNotBlank() && data.mrp.contains(Regex("""\d+"""))
        if (!hasMrp) {
            violations.add(
                ComplianceViolation(
                    id = "LM-04",
                    ruleTitle = "Missing Maximum Retail Price (MRP)",
                    regulationCode = "LMPCR Rule 6(1)(e)",
                    severity = ViolationSeverity.CRITICAL,
                    fieldName = "MRP",
                    issueDescription = "No retail price declaration was detected on the packaging label.",
                    remediationAdvice = "State Maximum Retail Price clearly: 'MRP ₹ xx.xx (inclusive of all taxes)' in accordance with Rule 6."
                )
            )
        } else if (!data.isTaxesMentioned) {
            violations.add(
                ComplianceViolation(
                    id = "LM-04",
                    ruleTitle = "MRP Missing Tax Declaration Clause",
                    regulationCode = "LMPCR Rule 6(1)(e)",
                    severity = ViolationSeverity.MAJOR,
                    fieldName = "MRP Tax Inclusion",
                    issueDescription = "MRP is present ('${data.mrp}'), but the mandatory phrase '(incl. of all taxes)' or 'inclusive of all taxes' is missing.",
                    remediationAdvice = "Append '(inclusive of all taxes)' right beside or directly beneath the MRP figure."
                )
            )
        } else {
            passedCount++
        }

        // 5. LM-05: Mfg Date & Expiry
        totalCount++
        if (data.manufacturingDate.isBlank() || data.expiryOrBestBeforeDate.isBlank()) {
            violations.add(
                ComplianceViolation(
                    id = "LM-05",
                    ruleTitle = "Missing Manufacturing / Expiry Date",
                    regulationCode = "LMPCR Rule 6(1)(c) & FSSAI Reg. 2.2.2(9)",
                    severity = ViolationSeverity.CRITICAL,
                    fieldName = "Mfg & Expiry Dates",
                    issueDescription = "Date of manufacture/packing or Best-Before/Expiry date is absent or illegible.",
                    remediationAdvice = "Mark 'Mfg. Date: MM/YYYY' and 'Use by / Best Before: MM/YYYY' or 'Best before X months from manufacture'."
                )
            )
        } else if (data.isExpired) {
            violations.add(
                ComplianceViolation(
                    id = "LM-05",
                    ruleTitle = "Product Past Expiry Date",
                    regulationCode = "FSSAI Act Section 31 / Consumer Protection",
                    severity = ViolationSeverity.CRITICAL,
                    fieldName = "Product Validity",
                    issueDescription = "Extracted expiry date indicates this batch is expired or past best-before date.",
                    remediationAdvice = "Immediate quarantine required. Do not distribute or sell past-dated stock."
                )
            )
        } else {
            passedCount++
        }

        // 6. LM-06: Batch / Lot Number
        totalCount++
        if (data.batchOrLotNumber.isBlank()) {
            violations.add(
                ComplianceViolation(
                    id = "LM-06",
                    ruleTitle = "Missing Batch / Lot Identification Code",
                    regulationCode = "LMPCR Rule 6(1)(g)",
                    severity = ViolationSeverity.MAJOR,
                    fieldName = "Batch Number",
                    issueDescription = "Batch, Lot or Code number is missing, preventing batch traceability.",
                    remediationAdvice = "Print 'Batch No.: XXXXX' on the packaging label or embossed on crimp/lid."
                )
            )
        } else {
            passedCount++
        }

        // 7. LM-07: Customer Care Helpline
        totalCount++
        if (data.customerCareDetails.isBlank()) {
            violations.add(
                ComplianceViolation(
                    id = "LM-07",
                    ruleTitle = "Missing Consumer Care / Grievance Contact",
                    regulationCode = "LMPCR Rule 6(1)(h)",
                    severity = ViolationSeverity.MAJOR,
                    fieldName = "Customer Care",
                    issueDescription = "No telephone helpline or email address for consumer grievance redressal was found.",
                    remediationAdvice = "Provide 'For feedback/complaints contact Consumer Care Cell at: Phone: 1800-XXX-XXXX, Email: care@domain.com, Address: [Headquarters]'."
                )
            )
        } else {
            passedCount++
        }

        // FOOD SPECIFIC RULES:
        if (category == ProductCategory.FOOD_BEVERAGE) {
            // 8. FSSAI-01: FSSAI License Number
            totalCount++
            val is14Digit = data.fssaiLicenseNumber.filter { it.isDigit() }.length == 14
            if (data.fssaiLicenseNumber.isBlank()) {
                violations.add(
                    ComplianceViolation(
                        id = "FSSAI-01",
                        ruleTitle = "Missing FSSAI License Number",
                        regulationCode = "FSSAI Labelling Regs 2020 - Reg 2.2.2",
                        severity = ViolationSeverity.CRITICAL,
                        fieldName = "FSSAI License",
                        issueDescription = "Mandatory 14-digit FSSAI License Number and logo are missing on this food packaging.",
                        remediationAdvice = "Incorporate the official FSSAI logo followed by 'Lic. No. XXXXXXXXXXXXXX' on the label display panel."
                    )
                )
            } else if (!is14Digit) {
                violations.add(
                    ComplianceViolation(
                        id = "FSSAI-01",
                        ruleTitle = "Invalid FSSAI License Format",
                        regulationCode = "FSSAI Labelling Regs 2020 - Reg 2.2.2",
                        severity = ViolationSeverity.MAJOR,
                        fieldName = "FSSAI License Format",
                        issueDescription = "FSSAI license number '${data.fssaiLicenseNumber}' does not contain exactly 14 digits.",
                        remediationAdvice = "Verify the 14-digit FSSAI registration/license sequence issued by state or central food authority."
                    )
                )
            } else {
                passedCount++
            }

            // 9. FSSAI-02: Veg / Non-Veg Symbol
            totalCount++
            if (data.vegNonVegStatus == "MISSING" || data.vegNonVegStatus == "NONE") {
                violations.add(
                    ComplianceViolation(
                        id = "FSSAI-02",
                        ruleTitle = "Missing Vegetarian / Non-Vegetarian Logo",
                        regulationCode = "FSSAI Reg. 2.2.2(4)",
                        severity = ViolationSeverity.CRITICAL,
                        fieldName = "Veg/Non-Veg Symbol",
                        issueDescription = "No green circle inside green square (Veg) or brown triangle inside square (Non-Veg) was detected.",
                        remediationAdvice = "Affix the standard FSSAI Vegetarian (green dot) or Non-Vegetarian (brown triangle) symbol on the front principal display panel."
                    )
                )
            } else {
                passedCount++
            }

            // 10. FSSAI-03: Nutritional Panel
            totalCount++
            if (!data.hasNutritionalTable && data.nutritionalInfo.isEmpty()) {
                violations.add(
                    ComplianceViolation(
                        id = "FSSAI-03",
                        ruleTitle = "Missing Nutritional Information Panel",
                        regulationCode = "FSSAI Labelling Regs 2020 - Reg 2.2.2(3)",
                        severity = ViolationSeverity.MAJOR,
                        fieldName = "Nutritional Facts",
                        issueDescription = "Mandatory nutritional breakdown table is absent.",
                        remediationAdvice = "Include table declaring Energy (kcal), Protein, Carbohydrates, Total & Added Sugars, Total Fat, Saturated & Trans Fat, and Sodium per 100g/per serving."
                    )
                )
            } else {
                val missingNutrients = mutableListOf<String>()
                val keys = data.nutritionalInfo.keys.map { it.lowercase() }
                if (!keys.any { it.contains("trans") }) missingNutrients.add("Trans Fat (g)")
                if (!keys.any { it.contains("sugar") || it.contains("added") }) missingNutrients.add("Added Sugar (g)")

                if (missingNutrients.isNotEmpty()) {
                    violations.add(
                        ComplianceViolation(
                            id = "FSSAI-03",
                            ruleTitle = "Incomplete Mandatory Nutrient Declarations",
                            regulationCode = "FSSAI Labelling Regs 2020 - Reg 2.2.2(3)",
                            severity = ViolationSeverity.MINOR,
                            fieldName = "Nutritional Breakdown",
                            issueDescription = "FSSAI 2020 requires explicit mention of: ${missingNutrients.joinToString(", ")}.",
                            remediationAdvice = "Ensure 'Trans Fat' and 'Added Sugars' are explicitly listed even if the quantity is 0 g."
                        )
                    )
                } else {
                    passedCount++
                }
            }

            // 11. FSSAI-04: Allergen Warning
            totalCount++
            if (data.allergenDeclaration.isBlank()) {
                violations.add(
                    ComplianceViolation(
                        id = "FSSAI-04",
                        ruleTitle = "Missing Allergen Warning / Advice",
                        regulationCode = "FSSAI Reg. 2.2.2(5)",
                        severity = ViolationSeverity.MINOR,
                        fieldName = "Allergen Declaration",
                        issueDescription = "No allergen advisory statement was found (e.g. 'Contains wheat, nuts' or 'Manufactured in facility handling soy').",
                        remediationAdvice = "Print allergen statement directly below the ingredient list in distinct bold or uppercase font."
                    )
                )
            } else {
                passedCount++
            }
        }

        // 12. QUAL-01: Legibility & Unreadable Text
        totalCount++
        if (data.legibilityScore < 70 || data.unreadableFields.isNotEmpty()) {
            violations.add(
                ComplianceViolation(
                    id = "QUAL-01",
                    ruleTitle = "Unreadable / Low Legibility Packaging Text",
                    regulationCode = "LMPCR Rule 9 & FSSAI Standards",
                    severity = if (data.legibilityScore < 50) ViolationSeverity.CRITICAL else ViolationSeverity.MAJOR,
                    fieldName = "Text Legibility",
                    issueDescription = if (data.unreadableFields.isNotEmpty())
                        "The following fields are smudged, blurred or illegible: ${data.unreadableFields.joinToString(", ")}."
                    else "Overall label contrast and legibility score is low (${data.legibilityScore}/100).",
                    remediationAdvice = "Ensure minimum font height (e.g. 1mm to 2mm depending on area of principal display panel) with high contrasting background ink."
                )
            )
        } else {
            passedCount++
        }

        // Compute overall score:
        val criticalCount = violations.count { it.severity == ViolationSeverity.CRITICAL }
        val majorCount = violations.count { it.severity == ViolationSeverity.MAJOR }
        val minorCount = violations.count { it.severity == ViolationSeverity.MINOR }

        // Deduct points based on severity
        var score = 100 - (criticalCount * 25) - (majorCount * 12) - (minorCount * 5)
        if (score < 0) score = 0
        if (criticalCount > 0 && score > 65) score = 65

        val status = when {
            criticalCount > 0 || score < 60 -> ComplianceStatus.NON_COMPLIANT
            majorCount > 0 || score < 85 -> ComplianceStatus.PARTIALLY_COMPLIANT
            else -> ComplianceStatus.COMPLIANT
        }

        val summaryVerdict = when (status) {
            ComplianceStatus.COMPLIANT ->
                "Package meets statutory declarations under Legal Metrology Act & FSSAI Labelling Regulations. All critical declarations verified."
            ComplianceStatus.PARTIALLY_COMPLIANT ->
                "Minor regulatory deficiencies identified ($majorCount major, $minorCount minor issues). Remediation needed prior to mass distribution."
            ComplianceStatus.NON_COMPLIANT ->
                "Critical non-compliance detected ($criticalCount critical violations). Package is subject to seizure or penalty under applicable statutory laws."
        }

        return ComplianceReport(
            status = status,
            complianceScore = score,
            violations = violations,
            passedChecksCount = passedCount,
            totalChecksCount = totalCount,
            summaryVerdict = summaryVerdict
        )
    }
}

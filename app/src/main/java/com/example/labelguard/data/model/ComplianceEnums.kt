package com.example.labelguard.data.model

enum class ComplianceStatus(val label: String) {
    COMPLIANT("Fully Compliant"),
    PARTIALLY_COMPLIANT("Partially Compliant"),
    NON_COMPLIANT("Non-Compliant")
}

enum class ViolationSeverity(val label: String) {
    CRITICAL("Critical Violation"),
    MAJOR("Major Violation"),
    MINOR("Minor Issue / Advisory")
}

enum class ProductCategory(val label: String) {
    FOOD_BEVERAGE("Food & Beverage (FSSAI)"),
    COSMETICS("Cosmetics & Personal Care"),
    PACKAGED_GOODS("General Packaged Commodity")
}

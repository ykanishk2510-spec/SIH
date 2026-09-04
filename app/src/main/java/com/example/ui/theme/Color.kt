package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Sophisticated Dark Design Theme Tokens (from Design HTML specification)
val DarkBg = Color(0xFF1C1B1F)                  // Pure dark charcoal background
val DarkSurfaceCard = Color(0xFF2B2930)         // Sophisticated card surface
val DarkSurfaceActive = Color(0xFF333138)       // Active / interactive surface
val DarkSurfaceCardHover = Color(0xFF333138)    // Hover state surface
val DarkPrimaryLilac = Color(0xFFD0BCFF)        // Signature lilac accent
val DarkOnPrimaryPurple = Color(0xFF381E72)     // Deep regal purple for text on lilac
val DarkPrimaryContainerPurple = Color(0xFF4F378B) // Rich purple hero/accent container
val DarkOnPrimaryContainer = Color(0xFFEADDFF)  // Light lilac on dark purple container

val TextPrimary = Color(0xFFE6E1E5)             // Crisp light grey text
val TextSecondary = Color(0xFFCAC4D0)           // Muted secondary text
val TextMuted = Color(0xFF938F99)               // Tertiary / timestamp text
val BorderSubtle = Color(0x14FFFFFF)            // Subtle white border (alpha 0.08)

// Status and Semantic Highlights
val StatusCleanGreen = Color(0xFFB1D18A)        // Soft sage green for CLEAN / COMPLIANT
val StatusCleanContainer = Color(0x26B1D18A)     // 15% opacity container
val StatusCleanGreenContainer = StatusCleanContainer
val StatusViolationRed = Color(0xFFF2B8B5)      // Soft coral red for VIOLATION / NON_COMPLIANT
val StatusViolationContainer = Color(0x26F2B8B5) // 15% opacity container
val StatusWarningAmber = Color(0xFFF9D776)      // Warm amber for PARTIAL / WARNING
val StatusWarningContainer = Color(0x26F9D776)  // 15% opacity container
val StatusPendingPurple = Color(0xFFD0BCFF)     // Pending state lilac
val StatusPendingContainer = Color(0x26D0BCFF)  // 15% opacity container

// Aliases for backwards compatibility across existing components
val ComplianceTeal = DarkPrimaryLilac
val ComplianceTealDark = DarkPrimaryLilac
val NavyDeep = DarkBg
val SurfaceDark = DarkBg
val SurfaceCard = DarkSurfaceCard
val AccentSky = Color(0xFFD0BCFF)
val TextPrimaryDark = TextPrimary
val TextSecondaryDark = TextSecondary
val TextMutedDark = TextMuted

val SuccessGreen = StatusCleanGreen
val SuccessGreenContainer = StatusCleanContainer
val WarningAmber = StatusWarningAmber
val WarningAmberContainer = StatusWarningContainer
val DangerRed = StatusViolationRed
val DangerRedContainer = StatusViolationContainer


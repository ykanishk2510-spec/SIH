package com.example.labelguard.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.labelguard.data.model.ComplianceStatus
import com.example.labelguard.data.model.ViolationSeverity
import com.example.ui.theme.DarkSurfaceActive
import com.example.ui.theme.StatusCleanContainer
import com.example.ui.theme.StatusCleanGreen
import com.example.ui.theme.StatusViolationContainer
import com.example.ui.theme.StatusViolationRed
import com.example.ui.theme.StatusWarningContainer
import com.example.ui.theme.StatusWarningAmber
import com.example.ui.theme.TextSecondary

@Composable
fun ComplianceBadge(status: ComplianceStatus, modifier: Modifier = Modifier) {
    val (bg, fg, icon, text) = when (status) {
        ComplianceStatus.COMPLIANT -> Quadruple(
            StatusCleanContainer,
            StatusCleanGreen,
            Icons.Default.CheckCircle,
            "CLEAN"
        )
        ComplianceStatus.PARTIALLY_COMPLIANT -> Quadruple(
            StatusWarningContainer,
            StatusWarningAmber,
            Icons.Default.Warning,
            "PARTIAL"
        )
        ComplianceStatus.NON_COMPLIANT -> Quadruple(
            StatusViolationContainer,
            StatusViolationRed,
            Icons.Default.Error,
            "VIOLATION"
        )
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .border(BorderStroke(0.5.dp, fg.copy(alpha = 0.3f)), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = fg,
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            color = fg,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun SeverityBadge(severity: ViolationSeverity, modifier: Modifier = Modifier) {
    val (bg, fg, text) = when (severity) {
        ViolationSeverity.CRITICAL -> Triple(StatusViolationContainer, StatusViolationRed, "CRITICAL")
        ViolationSeverity.MAJOR -> Triple(StatusWarningContainer, StatusWarningAmber, "MAJOR")
        ViolationSeverity.MINOR -> Triple(DarkSurfaceActive, TextSecondary, "MINOR")
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .border(BorderStroke(0.5.dp, fg.copy(alpha = 0.25f)), RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            color = fg,
            fontSize = 9.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun ScoreMeter(score: Int, modifier: Modifier = Modifier) {
    val color = when {
        score >= 85 -> StatusCleanGreen
        score >= 60 -> StatusWarningAmber
        else -> StatusViolationRed
    }

    Box(
        modifier = modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.12f))
            .border(BorderStroke(1.5.dp, color.copy(alpha = 0.4f)), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$score%",
            color = color,
            fontWeight = FontWeight.Black,
            fontSize = 14.sp
        )
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

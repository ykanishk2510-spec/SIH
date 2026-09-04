package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.labelguard.ui.screens.DashboardScreen
import com.example.labelguard.ui.screens.ReportDetailsScreen
import com.example.labelguard.ui.screens.RulesCatalogScreen
import com.example.labelguard.ui.screens.ScannerScreen
import com.example.labelguard.ui.viewmodel.AppScreen
import com.example.labelguard.ui.viewmodel.LabelGuardViewModel
import com.example.ui.theme.DarkPrimaryContainerPurple
import com.example.ui.theme.DarkPrimaryLilac
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextSecondary

class MainActivity : ComponentActivity() {

    private val viewModel: LabelGuardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                LabelGuardMainContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun LabelGuardMainContent(viewModel: LabelGuardViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()

    val showBottomBar = currentScreen == AppScreen.DASHBOARD ||
            currentScreen == AppScreen.SCANNER ||
            currentScreen == AppScreen.RULES_CATALOG

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = DarkSurfaceCard,
                    contentColor = DarkPrimaryLilac,
                    modifier = Modifier
                        .testTag("bottom_nav_bar")
                        .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                        .border(
                            BorderStroke(1.dp, Color.White.copy(alpha = 0.06f)),
                            RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                        )
                ) {
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.DASHBOARD,
                        onClick = { viewModel.navigateTo(AppScreen.DASHBOARD) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Dashboard,
                                contentDescription = "Dashboard",
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Text(
                                "Dashboard",
                                fontSize = 11.sp,
                                fontWeight = if (currentScreen == AppScreen.DASHBOARD) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier.testTag("nav_dashboard"),
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = DarkPrimaryContainerPurple,
                            selectedIconColor = DarkPrimaryLilac,
                            selectedTextColor = DarkPrimaryLilac,
                            unselectedIconColor = TextSecondary.copy(alpha = 0.6f),
                            unselectedTextColor = TextSecondary.copy(alpha = 0.6f)
                        )
                    )

                    NavigationBarItem(
                        selected = currentScreen == AppScreen.SCANNER,
                        onClick = {
                            viewModel.resetScanner()
                            viewModel.navigateTo(AppScreen.SCANNER)
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Scan Label",
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Text(
                                "Scan Label",
                                fontSize = 11.sp,
                                fontWeight = if (currentScreen == AppScreen.SCANNER) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier.testTag("nav_scanner"),
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = DarkPrimaryContainerPurple,
                            selectedIconColor = DarkPrimaryLilac,
                            selectedTextColor = DarkPrimaryLilac,
                            unselectedIconColor = TextSecondary.copy(alpha = 0.6f),
                            unselectedTextColor = TextSecondary.copy(alpha = 0.6f)
                        )
                    )

                    NavigationBarItem(
                        selected = currentScreen == AppScreen.RULES_CATALOG,
                        onClick = { viewModel.navigateTo(AppScreen.RULES_CATALOG) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Gavel,
                                contentDescription = "Rules",
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Text(
                                "Rules",
                                fontSize = 11.sp,
                                fontWeight = if (currentScreen == AppScreen.RULES_CATALOG) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier.testTag("nav_rules"),
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = DarkPrimaryContainerPurple,
                            selectedIconColor = DarkPrimaryLilac,
                            selectedTextColor = DarkPrimaryLilac,
                            unselectedIconColor = TextSecondary.copy(alpha = 0.6f),
                            unselectedTextColor = TextSecondary.copy(alpha = 0.6f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            label = "screen_transition"
        ) { targetScreen ->
            when (targetScreen) {
                AppScreen.DASHBOARD -> {
                    DashboardScreen(
                        viewModel = viewModel,
                        onNavigateToScan = {
                            viewModel.resetScanner()
                            viewModel.navigateTo(AppScreen.SCANNER)
                        },
                        onNavigateToRules = { viewModel.navigateTo(AppScreen.RULES_CATALOG) },
                        onSelectScan = { scan -> viewModel.openReportDetails(scan) }
                    )
                }

                AppScreen.SCANNER -> {
                    ScannerScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateTo(AppScreen.DASHBOARD) }
                    )
                }

                AppScreen.REPORT_DETAILS -> {
                    ReportDetailsScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateTo(AppScreen.DASHBOARD) },
                        onScanAnother = {
                            viewModel.resetScanner()
                            viewModel.navigateTo(AppScreen.SCANNER)
                        }
                    )
                }

                AppScreen.RULES_CATALOG -> {
                    RulesCatalogScreen(
                        onBack = { viewModel.navigateTo(AppScreen.DASHBOARD) }
                    )
                }
            }
        }
    }
}

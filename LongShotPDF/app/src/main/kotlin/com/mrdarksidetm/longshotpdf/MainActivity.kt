package com.mrdarksidetm.longshotpdf

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.mrdarksidetm.longshotpdf.ui.screens.AboutScreen
import com.mrdarksidetm.longshotpdf.ui.screens.HomeScreen
import com.mrdarksidetm.longshotpdf.ui.theme.LongShotPdfTheme
import com.mrdarksidetm.longshotpdf.ui.viewmodel.PdfViewModel

enum class AppScreen { Home, About }

class MainActivity : ComponentActivity() {

    private val viewModel: PdfViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Enforce modern Edge-to-Edge system bars
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Handle incoming images shared from other apps or screenshot tools
        handleIncomingIntent(intent)

        setContent {
            LongShotPdfTheme {
                var currentScreen by remember { mutableStateOf(AppScreen.Home) }

                BackHandler(enabled = currentScreen == AppScreen.About) {
                    currentScreen = AppScreen.Home
                }

                when (currentScreen) {
                    AppScreen.Home -> HomeScreen(
                        viewModel = viewModel,
                        onNavigateToAbout = { currentScreen = AppScreen.About }
                    )
                    AppScreen.About -> AboutScreen(
                        onBack = { currentScreen = AppScreen.Home }
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent?) {
        if (intent == null) return

        when (intent.action) {
            Intent.ACTION_SEND -> {
                val imageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra(Intent.EXTRA_STREAM) as? Uri
                }
                if (imageUri != null) {
                    viewModel.selectImages(listOf(imageUri))
                }
            }
            Intent.ACTION_SEND_MULTIPLE -> {
                val uris = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM, Uri::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM)
                }
                if (!uris.isNullOrEmpty()) {
                    viewModel.selectImages(uris)
                }
            }
        }
    }
}

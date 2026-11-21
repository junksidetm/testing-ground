package com.mrdarksidetm.longshotpdf.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Gavel
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.SystemUpdate
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mrdarksidetm.longshotpdf.BuildConfig
import com.mrdarksidetm.longshotpdf.R
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * =========================================================================================
 * UNIVERSAL ABOUT PAGE: LongShotPDF
 *
 * Design Spec:
 * 1. Top bar with navigation back button.
 * 2. Hero Visualizer:
 *    - Main Container: Squircle (RoundedCornerShape(44.dp)) with subtle outline border.
 *    - App Icon: Centers inside squircle and fills exactly 75% of the space (150dp).
 *    - Ambient Organic Shapes:
 *      * Top-left: M3 Expressive "Bun" shape (floating/rotating ~14s).
 *      * Bottom-right: M3 Expressive "9-Side Cookie" shape (rotating/scaling ~32s).
 * 3. App Title & Dynamic Version Badge:
 *    - Reads BuildConfig.VERSION_NAME automatically on every release.
 *    - 7-tap Easter Egg counter activating Developer Mode.
 * 4. Developer Profile Card:
 *    - Avatar (developer.png) + "Abhijeet Yadav" + "Developer".
 *    - Action pills: GitHub (points to repository) & Email.
 * 5. Community FOSS "The Other's Way" Connected Settings List:
 *    - 28dp rounded container, 2dp separation, position-aware adaptive corners,
 *      spring press morphing, and vertically centered leading icons.
 *    - Licenses opens an in-app bottom sheet.
 * 6. Footer: "Made with ❤️".
 * =========================================================================================
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Dynamic app version from Gradle BuildConfig
    val appVersion = BuildConfig.VERSION_NAME

    // In-app License Sheet State
    var showLicenseSheet by remember { mutableStateOf(false) }
    val licenseSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Easter Egg Tap Counter
    var versionTapCount by remember { mutableIntStateOf(0) }
    var lastTapTime by remember { mutableLongStateOf(0L) }

    fun onVersionTap() {
        val now = System.currentTimeMillis()
        if (now - lastTapTime > 3000L) {
            versionTapCount = 0
        }
        lastTapTime = now
        versionTapCount++

        coroutineScope.launch {
            if (versionTapCount >= 7) {
                versionTapCount = 0
                snackbarHostState.showSnackbar("🎉 Developer Mode / Error Collector Activated!")
            } else if (versionTapCount >= 3) {
                val remaining = 7 - versionTapCount
                snackbarHostState.showSnackbar("You are $remaining step${if (remaining > 1) "s" else ""} away from Developer Mode")
            }
        }
    }

    fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }

    // Ambient Infinite Motion for M3 Expressive Shapes (Bun Shape & 9-Side Cookie)
    val infiniteTransition = rememberInfiniteTransition(label = "AboutShapesAmbient")

    val bunRotation by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 14000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "BunRotation"
    )
    val bunOffsetY by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "BunFloat"
    )

    val cookieRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 32000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "CookieRotation"
    )
    val cookieScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 7500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "CookieScale"
    )

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "About",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(top = 16.dp, bottom = 32.dp)
            ) {
                // -------------------------------------------------------------------------
                // HERO ILLUSTRATION: SQUIRCLE CONTAINER + 75% APP ICON + M3 EXPRESSIVE SHAPES
                // -------------------------------------------------------------------------
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Top-Left Animated M3 Expressive "Bun" Shape
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .offset(x = 18.dp, y = (-32).dp + bunOffsetY.dp)
                            .graphicsLayer {
                                rotationZ = bunRotation
                            }
                    ) {
                        Canvas(modifier = Modifier.size(135.dp)) {
                            val c = Offset(size.width / 2f, size.height / 2f)
                            val baseR = size.width * 0.40f
                            val path = Path()
                            val steps = 72
                            for (i in 0..steps) {
                                val angle = i * (2.0 * PI / steps)
                                val modulation = 1f + 0.22f * cos(angle * 2.0).toFloat() - 0.14f * sin(angle).toFloat()
                                val curR = baseR * modulation
                                val x = c.x + (curR * cos(angle)).toFloat()
                                val y = c.y + (curR * sin(angle)).toFloat()
                                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                            }
                            path.close()
                            drawPath(path, color = Color(0xFF0078D4).copy(alpha = 0.88f))
                        }
                    }

                    // Bottom-Right Animated M3 Expressive "9-Side Cookie" Shape
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .offset(x = (-12).dp, y = 48.dp)
                            .graphicsLayer {
                                rotationZ = cookieRotation
                                scaleX = cookieScale
                                scaleY = cookieScale
                            }
                    ) {
                        Canvas(modifier = Modifier.size(145.dp)) {
                            val c = Offset(size.width / 2f, size.height / 2f)
                            val baseR = size.width * 0.40f
                            val path = Path()
                            val steps = 90
                            for (i in 0..steps) {
                                val angle = i * (2.0 * PI / steps)
                                val mod = 1f + 0.16f * sin(angle * 9.0).toFloat()
                                val curR = baseR * mod
                                val x = c.x + (curR * cos(angle)).toFloat()
                                val y = c.y + (curR * sin(angle)).toFloat()
                                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                            }
                            path.close()
                            drawPath(path, color = Color(0xFF758AEF).copy(alpha = 0.88f))
                        }
                    }

                    // Main Icon Squircle Container (App Icon fills exactly 75% of space: 150dp / 200dp)
                    Surface(
                        modifier = Modifier
                            .size(200.dp)
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f),
                                shape = RoundedCornerShape(44.dp)
                            ),
                        shape = RoundedCornerShape(44.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerLowest
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.app_logo),
                                contentDescription = "App Icon",
                                modifier = Modifier
                                    .size(150.dp)
                                    .clip(RoundedCornerShape(32.dp))
                            )
                        }
                    }
                }

                // -------------------------------------------------------------------------
                // APP TITLE & DYNAMIC VERSION BADGE
                // -------------------------------------------------------------------------
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "LongShotPDF",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        onClick = { onVersionTap() },
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = "Version $appVersion",
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // -------------------------------------------------------------------------
                // DEVELOPER PROFILE CARD
                // -------------------------------------------------------------------------
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
                ) {
                    Column {
                        ListItem(
                            headlineContent = { Text("Abhijeet Yadav", fontWeight = FontWeight.Bold) },
                            supportingContent = { Text("Developer") },
                            leadingContent = {
                                Image(
                                    painter = painterResource(id = R.drawable.developer),
                                    contentDescription = "Developer Avatar",
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                )
                            },
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                            modifier = Modifier.clickable { openUrl("https://github.com/junksidetm/testing-ground") }
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // GitHub Pill Button
                            Surface(
                                onClick = { openUrl("https://github.com/junksidetm/testing-ground") },
                                shape = RoundedCornerShape(100),
                                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 14.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_github),
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "GitHub",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Email Pill Button
                            Surface(
                                onClick = { openUrl("mailto:contact.dsidetm@gmail.com") },
                                shape = RoundedCornerShape(100),
                                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 14.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_email_at),
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Email",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // -------------------------------------------------------------------------
                // JUNE CONNECTED SETTINGS ARCHITECTURE (Pulse Studio standard)
                // -------------------------------------------------------------------------
                JuneSettingsGroup(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    JuneSettingsTile(
                        title = "Check for Updates",
                        subtitle = "v$appVersion • Up to date",
                        leadingIcon = { Icon(Icons.Rounded.SystemUpdate, contentDescription = null) },
                        trailingContent = { Icon(Icons.AutoMirrored.Rounded.ArrowForwardIos, contentDescription = null, modifier = Modifier.size(14.dp)) },
                        index = 0,
                        count = 4,
                        onClick = { openUrl("https://github.com/junksidetm/testing-ground/releases/latest") }
                    )

                    JuneSettingsTile(
                        title = "Open Source Repository",
                        subtitle = "View and contribute on GitHub",
                        leadingIcon = { Icon(Icons.Rounded.Code, contentDescription = null) },
                        trailingContent = { Icon(Icons.AutoMirrored.Rounded.ArrowForwardIos, contentDescription = null, modifier = Modifier.size(14.dp)) },
                        index = 1,
                        count = 4,
                        onClick = { openUrl("https://github.com/junksidetm/testing-ground") }
                    )

                    JuneSettingsTile(
                        title = "Material 3 Guidelines",
                        subtitle = "Official design system authority (m3.material.io)",
                        leadingIcon = { Icon(Icons.Rounded.Palette, contentDescription = null) },
                        trailingContent = { Icon(Icons.AutoMirrored.Rounded.ArrowForwardIos, contentDescription = null, modifier = Modifier.size(14.dp)) },
                        index = 2,
                        count = 4,
                        onClick = { openUrl("https://m3.material.io/") }
                    )

                    JuneSettingsTile(
                        title = "License",
                        subtitle = "Apache License 2.0 (Open in-app)",
                        leadingIcon = { Icon(Icons.Rounded.Gavel, contentDescription = null) },
                        trailingContent = { Icon(Icons.AutoMirrored.Rounded.ArrowForwardIos, contentDescription = null, modifier = Modifier.size(14.dp)) },
                        index = 3,
                        count = 4,
                        onClick = { showLicenseSheet = true }
                    )
                }

                Spacer(modifier = Modifier.height(36.dp))

                // -------------------------------------------------------------------------
                // FOOTER: "Made with ❤️"
                // -------------------------------------------------------------------------
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Made with ❤️",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )

            // -----------------------------------------------------------------------------
            // IN-APP LICENSE MODAL BOTTOM SHEET
            // -----------------------------------------------------------------------------
            if (showLicenseSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showLicenseSheet = false },
                    sheetState = licenseSheetState,
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 8.dp)
                            .padding(bottom = 24.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "License",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Apache License, Version 2.0",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            TextButton(onClick = {
                                coroutineScope.launch {
                                    licenseSheetState.hide()
                                    showLicenseSheet = false
                                }
                            }) {
                                Text("Close")
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(320.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = """
                                      Apache License
                                Version 2.0, January 2004
                             http://www.apache.org/licenses/

        Copyright 2026 Abhijeet Yadav / junksidetm

        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

            http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.
                                    """.trimIndent(),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 12.sp,
                                        lineHeight = 18.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Connected settings group container with 28dp rounded perimeter.
 */
@Composable
fun JuneSettingsGroup(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp)),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        content = content
    )
}

/**
 * Connected setting tile with position-aware adaptive corners and spring press morphing.
 */
@Composable
fun JuneSettingsTile(
    title: String,
    subtitle: String? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    index: Int = 0,
    count: Int = 1,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isClickable = onClick != null

    val animatedCorner by animateDpAsState(
        targetValue = if (isClickable && isPressed) 28.dp else 8.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "TileCornerMorph"
    )

    val shape = when {
        count == 1 -> RoundedCornerShape(28.dp)
        index == 0 -> RoundedCornerShape(
            topStart = 28.dp,
            topEnd = 28.dp,
            bottomStart = animatedCorner,
            bottomEnd = animatedCorner
        )
        index == count - 1 -> RoundedCornerShape(
            topStart = animatedCorner,
            topEnd = animatedCorner,
            bottomStart = 28.dp,
            bottomEnd = 28.dp
        )
        else -> RoundedCornerShape(animatedCorner)
    }

    Surface(
        onClick = { onClick?.invoke() },
        enabled = isClickable,
        shape = shape,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        interactionSource = interactionSource,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 64.dp)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingIcon != null) {
                Box(
                    modifier = Modifier.size(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    leadingIcon()
                }
                Spacer(modifier = Modifier.width(16.dp))
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (trailingContent != null) {
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    trailingContent()
                }
            }
        }
    }
}

package com.tracker.syllabus.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// 1. Classic Gold (Indigo + Teal)
private val GoldDarkColorScheme = darkColorScheme(
    primary = Color(0xFF6366F1),
    secondary = Color(0xFF06B6D4),
    tertiary = Color(0xFFEC4899),
    background = Color(0xFF0F172A),
    surface = Color(0xFF1E293B),
    onPrimary = Color(0xFFF1F5F9),
    onSecondary = Color(0xFF0F172A),
    onBackground = Color(0xFFF1F5F9),
    onSurface = Color(0xFFF1F5F9)
)
private val GoldLightColorScheme = lightColorScheme(
    primary = Color(0xFF4F46E5),
    secondary = Color(0xFF06B6D4),
    tertiary = Color(0xFFEC4899),
    background = Color(0xFFF8FAFC),
    surface = Color(0xFFFFFFFF),
    onPrimary = Color(0xFFFFFFFF),
    onSecondary = Color(0xFFFFFFFF),
    onBackground = Color(0xFF0F172A),
    onSurface = Color(0xFF0F172A)
)

// 2. Forest Green
private val GreenDarkColorScheme = darkColorScheme(
    primary = Color(0xFF10B981),
    secondary = Color(0xFF34D399),
    tertiary = Color(0xFFF59E0B),
    background = Color(0xFF0B1B15),
    surface = Color(0xFF132D24),
    onPrimary = Color(0xFFF0FDF4),
    onSecondary = Color(0xFF0B1B15),
    onBackground = Color(0xFFF0FDF4),
    onSurface = Color(0xFFF0FDF4)
)
private val GreenLightColorScheme = lightColorScheme(
    primary = Color(0xFF059669),
    secondary = Color(0xFF10B981),
    tertiary = Color(0xFFF59E0B),
    background = Color(0xFFF0FDF4),
    surface = Color(0xFFFFFFFF),
    onPrimary = Color(0xFFFFFFFF),
    onSecondary = Color(0xFFFFFFFF),
    onBackground = Color(0xFF064E3B),
    onSurface = Color(0xFF064E3B)
)

// 3. Royal Blue
private val BlueDarkColorScheme = darkColorScheme(
    primary = Color(0xFF3B82F6),
    secondary = Color(0xFF60A5FA),
    tertiary = Color(0xFFF43F5E),
    background = Color(0xFF070F2B),
    surface = Color(0xFF1B1A55),
    onPrimary = Color(0xFFECF2FF),
    onSecondary = Color(0xFF070F2B),
    onBackground = Color(0xFFECF2FF),
    onSurface = Color(0xFFECF2FF)
)
private val BlueLightColorScheme = lightColorScheme(
    primary = Color(0xFF2563EB),
    secondary = Color(0xFF3B82F6),
    tertiary = Color(0xFFF43F5E),
    background = Color(0xFFEEF2FF),
    surface = Color(0xFFFFFFFF),
    onPrimary = Color(0xFFFFFFFF),
    onSecondary = Color(0xFFFFFFFF),
    onBackground = Color(0xFF1E1B4B),
    onSurface = Color(0xFF1E1B4B)
)

// 4. Rosewood Crimson
private val CrimsonDarkColorScheme = darkColorScheme(
    primary = Color(0xFFEF4444),
    secondary = Color(0xFFFB7185),
    tertiary = Color(0xFFF59E0B),
    background = Color(0xFF1C0A10),
    surface = Color(0xFF2D121B),
    onPrimary = Color(0xFFFFF1F2),
    onSecondary = Color(0xFF1C0A10),
    onBackground = Color(0xFFFFF1F2),
    onSurface = Color(0xFFFFF1F2)
)
private val CrimsonLightColorScheme = lightColorScheme(
    primary = Color(0xFFDC2626),
    secondary = Color(0xFFEF4444),
    tertiary = Color(0xFFF59E0B),
    background = Color(0xFFFFF1F2),
    surface = Color(0xFFFFFFFF),
    onPrimary = Color(0xFFFFFFFF),
    onSecondary = Color(0xFFFFFFFF),
    onBackground = Color(0xFF4C0519),
    onSurface = Color(0xFF4C0519)
)

// 5. Midnight Purple
private val PurpleDarkColorScheme = darkColorScheme(
    primary = Color(0xFF8B5CF6),
    secondary = Color(0xFFC084FC),
    tertiary = Color(0xFF06B6D4),
    background = Color(0xFF0D0B21),
    surface = Color(0xFF1A163B),
    onPrimary = Color(0xFFF5F3FF),
    onSecondary = Color(0xFF0D0B21),
    onBackground = Color(0xFFF5F3FF),
    onSurface = Color(0xFFF5F3FF)
)
private val PurpleLightColorScheme = lightColorScheme(
    primary = Color(0xFF7C3AED),
    secondary = Color(0xFF8B5CF6),
    tertiary = Color(0xFF06B6D4),
    background = Color(0xFFF5F3FF),
    surface = Color(0xFFFFFFFF),
    onPrimary = Color(0xFFFFFFFF),
    onSecondary = Color(0xFFFFFFFF),
    onBackground = Color(0xFF2E1065),
    onSurface = Color(0xFF2E1065)
)

// 6. Sunset Orange
private val OrangeDarkColorScheme = darkColorScheme(
    primary = Color(0xFFF97316),
    secondary = Color(0xFFFBBF24),
    tertiary = Color(0xFF10B981),
    background = Color(0xFF1A0F0A),
    surface = Color(0xFF2A1B14),
    onPrimary = Color(0xFFFFF7ED),
    onSecondary = Color(0xFF1A0F0A),
    onBackground = Color(0xFFFFF7ED),
    onSurface = Color(0xFFFFF7ED)
)
private val OrangeLightColorScheme = lightColorScheme(
    primary = Color(0xFFEA580C),
    secondary = Color(0xFFF97316),
    tertiary = Color(0xFF10B981),
    background = Color(0xFFFFF7ED),
    surface = Color(0xFFFFFFFF),
    onPrimary = Color(0xFFFFFFFF),
    onSecondary = Color(0xFFFFFFFF),
    onBackground = Color(0xFF431407),
    onSurface = Color(0xFF431407)
)

// 7. Teal Mint
private val TealDarkColorScheme = darkColorScheme(
    primary = Color(0xFF0D9488),
    secondary = Color(0xFF2DD4BF),
    tertiary = Color(0xFFFB923C),
    background = Color(0xFF041516),
    surface = Color(0xFF0A2527),
    onPrimary = Color(0xFFF0FDFA),
    onSecondary = Color(0xFF041516),
    onBackground = Color(0xFFF0FDFA),
    onSurface = Color(0xFFF0FDFA)
)
private val TealLightColorScheme = lightColorScheme(
    primary = Color(0xFF0F766E),
    secondary = Color(0xFF0D9488),
    tertiary = Color(0xFFFB923C),
    background = Color(0xFFF0FDFA),
    surface = Color(0xFFFFFFFF),
    onPrimary = Color(0xFFFFFFFF),
    onSecondary = Color(0xFFFFFFFF),
    onBackground = Color(0xFF042F2E),
    onSurface = Color(0xFF042F2E)
)

// 8. Sakura Pink
private val PinkDarkColorScheme = darkColorScheme(
    primary = Color(0xFFF43F5E),
    secondary = Color(0xFFEC4899),
    tertiary = Color(0xFFEAB308),
    background = Color(0xFF18060D),
    surface = Color(0xFF280E1A),
    onPrimary = Color(0xFFFFF1F2),
    onSecondary = Color(0xFF18060D),
    onBackground = Color(0xFFFFF1F2),
    onSurface = Color(0xFFFFF1F2)
)
private val PinkLightColorScheme = lightColorScheme(
    primary = Color(0xFFE11D48),
    secondary = Color(0xFFF43F5E),
    tertiary = Color(0xFFEAB308),
    background = Color(0xFFFFF1F2),
    surface = Color(0xFFFFFFFF),
    onPrimary = Color(0xFFFFFFFF),
    onSecondary = Color(0xFFFFFFFF),
    onBackground = Color(0xFF4C0519),
    onSurface = Color(0xFF4C0519)
)

@Composable
fun SyllabusTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    appTheme: String = "gold",
    content: @Composable () -> Unit
) {
    val colorScheme = when (appTheme) {
        "green" -> if (darkTheme) GreenDarkColorScheme else GreenLightColorScheme
        "blue" -> if (darkTheme) BlueDarkColorScheme else BlueLightColorScheme
        "crimson" -> if (darkTheme) CrimsonDarkColorScheme else CrimsonLightColorScheme
        "purple" -> if (darkTheme) PurpleDarkColorScheme else PurpleLightColorScheme
        "orange" -> if (darkTheme) OrangeDarkColorScheme else OrangeLightColorScheme
        "teal" -> if (darkTheme) TealDarkColorScheme else TealLightColorScheme
        "pink" -> if (darkTheme) PinkDarkColorScheme else PinkLightColorScheme
        else -> if (darkTheme) GoldDarkColorScheme else GoldLightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

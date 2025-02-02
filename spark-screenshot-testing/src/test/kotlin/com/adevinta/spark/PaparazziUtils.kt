/*
 * Copyright (c) 2023 Adevinta
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.adevinta.spark

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import app.cash.paparazzi.Paparazzi
import app.cash.paparazzi.detectEnvironment

internal fun Paparazzi.sparkSnapshot(
    name: String? = null,
    drawBackground: Boolean = true,
    composable: @Composable () -> Unit,
): Unit = snapshot(name) {
    // Behave like in Android Studio Preview renderer
    CompositionLocalProvider(LocalInspectionMode provides true) {
        SparkTheme(useLegacyStyle = false) {
            // The first box acts as a shield from ComposeView which forces the first layout node
            // to match it's size. This allows the content below to wrap as needed.
            Box {
                // The second box adds a border and background so we can easily see layout bounds in screenshots
                Box(
                    Modifier.background(if (drawBackground) SparkTheme.colors.surface else Color.Transparent),
                ) {
                    composable()
                }
            }
        }
    }
}

/**
 * Lower the current Paparazzi Environment from API level 34 to 33 to work around new resource conflicts:
 *
 * ```
 * SEVERE: resources.format: Hexadecimal color expected, found Color State List for @android:color/system_bar_background_semi_transparent
 * java.lang.NumberFormatException: Color value '/usr/local/lib/android/sdk/platforms/android-34/data/res/color/system_bar_background_semi_transparent.xml' has wrong size. Format is either#AARRGGBB, #RRGGBB, #RGB, or #ARGB
 * ```
 *
 * GitHub issue: https://github.com/cashapp/paparazzi/issues/1025
 */
internal fun patchedEnvironment() = with(detectEnvironment()) {
    copy(compileSdkVersion = 33, platformDir = platformDir.replace("34", "33"))
}

internal const val MaxPercentDifference: Double = 0.01
internal const val PaparazziTheme: String = "android:Theme.MaterialComponent.Light.NoActionBar"

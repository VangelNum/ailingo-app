package org.ailingo.app.core.presentation.custom

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.window.core.layout.WindowSizeClass

@Composable
fun CustomOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val isCompactWidth = !adaptiveInfo.windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .defaultMinSize(
                minHeight = OutlinedTextFieldDefaults.MinHeight,
                minWidth = OutlinedTextFieldDefaults.MinWidth
            )
            .then(if (isCompactWidth) Modifier.fillMaxWidth() else Modifier),
        enabled = enabled
    ) {
        content()
    }
}
package org.ailingo.app.core.presentation.custom

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.window.core.layout.WindowSizeClass

@Composable
fun CustomButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.shape,
    content: @Composable RowScope.() -> Unit
) {
    val adaptiveInfo = currentWindowAdaptiveInfo()

    val isCompactWidth = !adaptiveInfo.windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)

    Button(
        onClick = onClick,
        modifier = modifier
            .defaultMinSize(
                minHeight = OutlinedTextFieldDefaults.MinHeight,
                minWidth = OutlinedTextFieldDefaults.MinWidth
            )
            .then(if (isCompactWidth) Modifier.fillMaxWidth() else Modifier),
        enabled = enabled,
        shape = shape
    ) {
        content()
    }
}
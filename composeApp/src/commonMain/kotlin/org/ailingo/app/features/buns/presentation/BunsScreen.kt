package org.ailingo.app.features.buns.presentation

import ailingo.composeapp.generated.resources.Res
import ailingo.composeapp.generated.resources.buns_info
import ailingo.composeapp.generated.resources.maskotgivemoney
import ailingo.composeapp.generated.resources.thanks
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.ailingo.app.core.presentation.custom.CustomButton
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun BunsScreen(
    onNavigateToHomeScreen: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
            ) {
                Text(
                    stringResource(Res.string.buns_info),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
                Image(
                    painter = painterResource(Res.drawable.maskotgivemoney),
                    contentDescription = null, modifier = Modifier.size(150.dp))
                CustomButton(
                    onClick = onNavigateToHomeScreen,
                ) {
                    Text(stringResource(Res.string.thanks))
                }
            }
        }
    }
}
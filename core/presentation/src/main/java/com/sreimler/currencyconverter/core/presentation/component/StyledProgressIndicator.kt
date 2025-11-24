package com.sreimler.currencyconverter.core.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sreimler.currencyconverter.core.presentation.R
import com.sreimler.currencyconverter.core.presentation.theme.ProgressStyle

@Composable
fun StyledProgressIndicator(modifier: Modifier = Modifier) {
    // Box needs to be wrapped around the other box to center the progress indicator
    // while not causing the refresh shade to span the entire screen
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(stringResource(R.string.test_tag_styled_progress_indicator)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = modifier
                .padding(top = 16.dp)
                .size(ProgressStyle.size + ProgressStyle.padding * 2)
                .background(
                    color = ProgressStyle.backgroundColor,
                    shape = ProgressStyle.shape
                ),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(ProgressStyle.size),
                strokeWidth = ProgressStyle.stroke,
                color = ProgressStyle.color
            )
        }
    }
}

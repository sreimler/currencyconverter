package com.sreimler.currencyconverter.core.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun CurrencyFlagImage(flagRes: Int, contentDescription: String) {
    Image(
        painter = painterResource(id = flagRes),
        contentDescription = contentDescription,
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape),
        contentScale = ContentScale.Crop
    )
    Spacer(modifier = Modifier.width(16.dp))
}
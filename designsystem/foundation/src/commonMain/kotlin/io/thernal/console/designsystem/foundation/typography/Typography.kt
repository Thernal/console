package io.thernal.console.designsystem.foundation.typography

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object Typography {
    // Body — okuma metinleri
    val body01 = TextStyle(
        fontSize = 16.sp,
        lineHeight = 22.sp,
    )
    val body02 = TextStyle(
        fontSize = 14.sp,
        lineHeight = 20.sp,
    )
    val body03 = TextStyle(
        fontSize = 13.sp,
        lineHeight = 18.sp,
    )

    // Label — chip, badge, timestamp, metadata
    val label01 = TextStyle(
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Medium,
    )
    val label02 = TextStyle(
        fontSize = 12.sp,
        lineHeight = 17.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.1.sp,
    )

    // Title — section başlıkları, AppBar
    val title01 = TextStyle(
        fontSize = 18.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.SemiBold,
    )
    val title02 = TextStyle(
        fontSize = 16.sp,
        lineHeight = 22.sp,
        fontWeight = FontWeight.SemiBold,
    )

    // Heading — sayfa düzeyinde büyük başlıklar
    val heading01 = TextStyle(
        fontSize = 22.sp,
        lineHeight = 28.sp,
        fontWeight = FontWeight.Bold,
    )
    val heading02 = TextStyle(
        fontSize = 20.sp,
        lineHeight = 26.sp,
        fontWeight = FontWeight.SemiBold,
    )
}

package az.theternal.console.designsystem.foundation.typography

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object Typography {
    val body01 = TextStyle(
        fontSize = 15.sp,
        lineHeight = 20.sp,
    )
    val body02 = TextStyle(
        fontSize = 13.sp,
        lineHeight = 18.sp,
    )
    val body03 = TextStyle(
        fontSize = 12.sp,
        lineHeight = 16.sp,
    )
    val label01 = TextStyle(
        fontSize = 11.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.Medium,
    )
    val label02 = TextStyle(
        fontSize = 10.sp,
        lineHeight = 14.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.1.sp,
    )
    val title01 = TextStyle(
        fontSize = 15.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.SemiBold,
    )
    val title02 = TextStyle(
        fontSize = 13.sp,
        lineHeight = 18.sp,
        fontWeight = FontWeight.SemiBold,
    )
}

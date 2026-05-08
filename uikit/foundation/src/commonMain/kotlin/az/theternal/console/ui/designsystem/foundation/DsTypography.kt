package az.theternal.console.ui.designsystem.foundation

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object DsTypography {
    val bodySmall = TextStyle(fontSize = 12.sp, lineHeight = 16.sp)
    val body = TextStyle(fontSize = 13.sp, lineHeight = 18.sp)
    val bodyLarge = TextStyle(fontSize = 15.sp, lineHeight = 20.sp)
    val labelSmall = TextStyle(
        fontSize = 10.sp,
        lineHeight = 14.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.1.sp,
    )
    val labelMedium = TextStyle(fontSize = 11.sp, lineHeight = 16.sp, fontWeight = FontWeight.Medium)
    val titleSmall = TextStyle(fontSize = 13.sp, lineHeight = 18.sp, fontWeight = FontWeight.SemiBold)
    val titleMedium = TextStyle(fontSize = 15.sp, lineHeight = 20.sp, fontWeight = FontWeight.SemiBold)
}

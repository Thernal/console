package az.theternal.console.sample

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

@Suppress("FunctionNaming")
fun MainViewController(): UIViewController {
    return ComposeUIViewController {
        SampleApp()
    }
}

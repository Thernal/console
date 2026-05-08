package az.theternal.console.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import az.theternal.console.sample.jvm.SampleRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        SampleRepository().jvmLog()
        setContent {
            SampleApp()
        }
    }
}

package at.mcbabo.corex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import at.mcbabo.corex.navigation.CoreXNavGraph
import at.mcbabo.corex.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setBackgroundDrawableResource(android.R.color.transparent)

        enableEdgeToEdge()
        setContent {
            AppTheme(darkTheme = isSystemInDarkTheme()) {
                navController = rememberNavController()
                DismissKeyboard {
                    CoreXNavGraph(navController = navController)
                }
            }
        }
    }
}

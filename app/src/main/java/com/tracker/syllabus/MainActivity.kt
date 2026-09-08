package com.tracker.syllabus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.firestore.FirebaseFirestore
import com.tracker.syllabus.data.model.UserProfile
import com.tracker.syllabus.di.DependencyProvider
import com.tracker.syllabus.ui.auth.AuthViewModel
import com.tracker.syllabus.ui.auth.LoginScreen
import com.tracker.syllabus.ui.auth.SignupScreen
import com.tracker.syllabus.ui.dashboard.DashboardScreen
import com.tracker.syllabus.ui.dashboard.DashboardViewModel
import com.tracker.syllabus.ui.settings.SettingsScreen
import com.tracker.syllabus.ui.settings.SettingsViewModel
import com.tracker.syllabus.ui.subject.SubjectDetailScreen
import com.tracker.syllabus.ui.subject.SubjectViewModel
import com.tracker.syllabus.ui.theme.SyllabusTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val authRepository = DependencyProvider.authRepository
            val currentUser by authRepository.currentUser.collectAsState()
            var userProfile by remember { mutableStateOf<UserProfile?>(null) }

            if (currentUser != null) {
                val uid = currentUser!!.uid
                DisposableEffect(uid) {
                    val listener = FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(uid)
                        .addSnapshotListener { snapshot, _ ->
                            if (snapshot != null && snapshot.exists()) {
                                userProfile = snapshot.toObject(UserProfile::class.java)
                            }
                        }
                    onDispose {
                        listener.remove()
                    }
                }
            } else {
                userProfile = null
            }

            val systemInDark = isSystemInDarkTheme()
            val darkTheme = when (userProfile?.themeMode) {
                "light" -> false
                "dark" -> true
                else -> systemInDark
            }
            val appTheme = userProfile?.appTheme ?: "gold"

            SyllabusTrackerTheme(darkTheme = darkTheme, appTheme = appTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()

    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
    val isProfileSetupRequired by authViewModel.isProfileSetupRequired.collectAsState()

    // Determine the start destination based on current authentication state
    val startDestination = if (isAuthenticated) {
        "dashboard"
    } else if (isProfileSetupRequired == true) {
        "signup"
    } else {
        "login"
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToSignup = { navController.navigate("signup") }
            )
        }
        
        composable("signup") {
            SignupScreen(
                viewModel = authViewModel,
                onNavigateToLogin = {
                    authViewModel.logout()
                    navController.navigate("login") {
                        popUpTo("signup") { inclusive = true }
                    }
                }
            )
        }
        
        composable("dashboard") {
            val dashboardViewModel: DashboardViewModel = viewModel()
            DashboardScreen(
                viewModel = dashboardViewModel,
                onNavigateToSubject = { subjectId ->
                    navController.navigate("subject/$subjectId")
                },
                onNavigateToSettings = {
                    navController.navigate("settings")
                }
            )
        }
        
        composable(
            route = "subject/{subjectId}",
            arguments = listOf(navArgument("subjectId") { type = NavType.StringType })
        ) { backStackEntry ->
            val subjectId = backStackEntry.arguments?.getString("subjectId") ?: ""
            val subjectViewModel: SubjectViewModel = viewModel(
                key = subjectId,
                initializer = {
                    SubjectViewModel(subjectId = subjectId)
                }
            )
            SubjectDetailScreen(
                viewModel = subjectViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable("settings") {
            val settingsViewModel: SettingsViewModel = viewModel()
            SettingsScreen(
                viewModel = settingsViewModel,
                onNavigateBack = { navController.popBackStack() },
                onLogoutSuccess = {
                    authViewModel.logout()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }

    // Reactive redirection if authentication state changes globally
    LaunchedEffect(isAuthenticated, isProfileSetupRequired) {
        if (isAuthenticated) {
            navController.navigate("dashboard") {
                popUpTo(0) { inclusive = true }
            }
        } else if (isProfileSetupRequired == true) {
            navController.navigate("signup") {
                popUpTo("login") { inclusive = true }
            }
        } else {
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
            }
        }
    }
}

package com.example.gmailappclone

import android.os.Build
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gmailappclone.dataclasses.MailLayoutItem
import com.example.gmailappclone.viewmodels.ProfileScreenViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(profileScreenViewModel: ProfileScreenViewModel = viewModel(
)){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screens.PrimaryScreen.route){
        composable(route = Screens.PrimaryScreen.route){

            ProfileScreen(
                onFabClicked = { navController.navigate(Screens.ComposeScreen.route) },
                onMailClicked = {
                    mailId->

                    navController.navigate("${Screens.ShowMailScreen.route}/${mailId}")
                                },
                profileScreenViewModel = profileScreenViewModel
            )
        }
        composable(route = Screens.ComposeScreen.route) {
            ComposeScreen(
                onBackButtonClicked = {navController.navigateUp()},
                onSendButtonClicked = {navController.navigateUp()},
                profileScreenViewModel = profileScreenViewModel
            )

        }
        composable(
            route = "${Screens.ShowMailScreen.route}/{mailId}"
        ) {navBackStackEntry->
            val mailId = navBackStackEntry.arguments?.getString("mailId")?.toIntOrNull() ?: 0

            ShowMailScreen(
                onBackButtonClicked = {navController.navigateUp()},
                onUnreadMailClicked = {navController.navigateUp()},
                profileScreenViewModel = profileScreenViewModel,
                mailId = mailId

            )
        }
    }
}
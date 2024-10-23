package com.example.gmailappclone

import android.content.Context
import android.os.Build
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gmailappclone.dataclasses.MailLayoutItem
import com.example.gmailappclone.drawerscreen.DrawerHelperForUiScreens
import com.example.gmailappclone.viewmodels.ComposeViewModel
import com.example.gmailappclone.viewmodels.DrawerViewModel
import com.example.gmailappclone.viewmodels.LoginScreenViewModel
import com.example.gmailappclone.viewmodels.ProfileScreenViewModel
import kotlinx.coroutines.delay

//@RequiresApi(Build.VERSION_CODES.O)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(
    drawerViewModel: DrawerViewModel,
    profileScreenViewModel: ProfileScreenViewModel,
    context: Context,
    loginScreenViewModel: LoginScreenViewModel,
    composeViewModel: ComposeViewModel,
    onPickingFile : () -> Unit,
    onUploadServiceStart: (List<String>) -> Unit,
    checkInternetConnection : () -> Boolean,
    onUserDeleted : () -> Unit
){
    var isConnected by remember { mutableStateOf<Boolean>(checkInternetConnection()) }
    val navController = rememberNavController()
    val isUserPresent by composeViewModel.isUserPresent.observeAsState()
    val isRemoteUserPresent by composeViewModel.isRemoteUserPresent.observeAsState()

    // LaunchedEffect to monitor isUserPresent and handle navigation when it changes
    LaunchedEffect(isUserPresent) {
        if ((isUserPresent == false && isRemoteUserPresent == true)) {
            // Trigger the user deletion callback
            onUserDeleted()

            // Navigate to the login screen and clear the backstack
            navController.navigate(Screens.LoginOrRegisterScreen.route) {
                popUpTo(Screens.PrimaryScreen.route) { inclusive = true }
            }
        }
    }

    LaunchedEffect(Unit) {
        while (true){
            isConnected = checkInternetConnection()
            delay(1000)
        }

    }

    // Conditionally navigate to NotConnectedUI or PrimaryScreen
    val startDestination = when {
        !isConnected -> Screens.NotConnectedUi.route
        loginScreenViewModel.isAlreadySignedIn() -> Screens.PrimaryScreen.route
        isUserPresent ?: false -> {
            Screens.LoginOrRegisterScreen.route
        }
        else -> Screens.LoginOrRegisterScreen.route
    }

    NavHost(navController = navController,
        startDestination = startDestination
    ){

        composable(route = Screens.LoginOrRegisterScreen.route){
            LoginOrRegisterScreen(
                context = context,
                viewModel = loginScreenViewModel,
                onRegisterSuccess = {navController.navigate(Screens.PrimaryScreen.route)},
                onRegiterFailure = {navController.navigate(Screens.LoginOrRegisterScreen.route)},
                onLoginSuccess = {navController.navigate(Screens.PrimaryScreen.route)},
                onLoginFailure = {navController.navigate(Screens.LoginOrRegisterScreen.route)}
            )
        }
        composable(route = Screens.PrimaryScreen.route){

            ProfileScreen(
                onSignOutClicked = {
                    loginScreenViewModel.signOut()
                    navController.navigate(Screens.LoginOrRegisterScreen.route)
                                   },
                onFabClicked = { navController.navigate(Screens.ComposeScreen.route) },
                onMailClicked = {
                    mailId->

                    navController.navigate("${Screens.ShowMailScreen.route}/${mailId}")
                                },
                profileScreenViewModel = profileScreenViewModel,
                context = context,
                onLabelClicked = {labelName->
                    navController.navigate("${Screens.DrawerHelperForUi.route}/$labelName")
                },
                drawerViewModel = drawerViewModel
            )
        }

        composable("${Screens.DrawerHelperForUi.route}/{labelName}"){
            navBackStackEntry->
            val labelName = navBackStackEntry.arguments?.getString("labelName") ?: "Nothing Found"
            DrawerHelperForUiScreens(
                drawerViewModel = drawerViewModel,
                labelName = labelName,
                onSignOutClicked = {
                    loginScreenViewModel.signOut()
                    navController.navigate(Screens.LoginOrRegisterScreen.route)
                },
                onFabClicked = { navController.navigate(Screens.ComposeScreen.route) },
                onMailClicked = {
                        mailId->

                    navController.navigate("${Screens.ShowMailScreen.route}/${mailId}")
                },
                profileScreenViewModel = profileScreenViewModel,
                context = context,
                onLabelClicked = {label->
                    if (label == "All Inbox") navController.navigate(Screens.PrimaryScreen.route) else navController.navigate("${Screens.DrawerHelperForUi.route}/$label")
                }

            )

        }

        composable(route = Screens.ComposeScreen.route) {
            ComposeScreen(
                context = context,
                composeViewModel = composeViewModel,
                onBackButtonClicked = {navController.navigateUp()},
                onSendButtonClicked = {navController.navigateUp()},
                profileScreenViewModel = profileScreenViewModel,
                onPickingFile = {onPickingFile()},
                onUploadServiceStart = {
                    onUploadServiceStart(it)}
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
        composable(Screens.NotConnectedUi.route) {
            NotConnectedUI()
        }
    }
}
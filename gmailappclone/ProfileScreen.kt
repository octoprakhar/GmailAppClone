package com.example.gmailappclone

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gmailappclone.dataclasses.MailLayoutItem
import com.example.gmailappclone.profilescreenhelper.MailLayout
import com.example.gmailappclone.topbars.TopBarOne
import com.example.gmailappclone.viewmodels.ProfileScreenViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onFabClicked : () -> Unit,
    onMailClicked: (Int) -> Unit,
    profileScreenViewModel: ProfileScreenViewModel
) {

    val TAG = "ProfileScreen"
    val mailList by profileScreenViewModel.mailLayoutList.observeAsState(emptyList())

    Log.d(TAG,"Size is : ${mailList.size}")
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false

    )
    var showSheet by remember {
        mutableStateOf(false)
    }


    //Track if FAB is extended
    var isExtended by remember {
        mutableStateOf(false)
    }



    Box (
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    //Detect swipe up or down
                    if (dragAmount < 0) {
                        //Swiped Up
                        isExtended = true
                    } else if (dragAmount > 0) {
                        // Swiped down
                        isExtended = false
                    }
                }
            }
    ) {

        ModalNavigationDrawer(
            drawerState = drawerState, // Use ModalDrawer's state
            drawerContent = {
                DrawerItemUiLayout() // Drawer content
            },
            gesturesEnabled = true, // Allow gestures to open/close the drawer
            scrimColor = Color.Black.copy(alpha = 0.32f) // Scrim with dimming effect
        ) {

            Scaffold(
                modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars),
                topBar = {
                    TopBarOne(
                        openDrawer = {
                            scope.launch {
                                drawerState.open() // Open drawer with built-in animation
                            }
                        },
                        openProfile = {
                            showSheet = true

                        }
                    )
                },
                bottomBar = {
                    BottomBarLayout()
                },
                floatingActionButton = {
                    if (isExtended) {
                        Log.d("FAB","value is : $isExtended")
                        ExtendedFloatingActionButton(
                            onClick = { onFabClicked() },
                            modifier = Modifier.background(Color.Cyan)
                        ) {
                            Icon(imageVector = Icons.Default.Create, contentDescription = null)
                            Text(text = "Compose")
                        }
                    } else {
                        Log.d("FAB","value is : $isExtended")

                        FloatingActionButton(onClick = { onFabClicked() }) {
                            Icon(imageVector = Icons.Default.Create, contentDescription = null)
                        }
                    }
                }
            ) { innerPadding ->


                if (showSheet){
                    ModalBottomSheet(
                        modifier = Modifier.fillMaxHeight(),
                        sheetState = bottomSheetState,
                        onDismissRequest = { showSheet = false }
                    ) {
                        Text(
                            "Swipe up to open sheet. Swipe down to dismiss.",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {

                    // Main content here
                    items(mailList){
                        mails ->
                        MailLayout(
                            mailLayoutItem = mails,
                            onMailClicked = {mailId->
                                onMailClicked(mailId)
//                                Log.d(TAG, "Navigating to ShowMailScreen with id: ${it}")
                                mailList.find { it.id == mailId }?.isMailOpened = true
                                            },
                            profileScreenViewModel = profileScreenViewModel
                        )
                    }



                }
            }

        }
    }

}

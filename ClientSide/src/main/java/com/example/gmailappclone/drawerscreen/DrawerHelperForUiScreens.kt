package com.example.gmailappclone.drawerscreen

import android.content.Context
import android.util.Log
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.gmailappclone.BottomBarLayout
import com.example.gmailappclone.DrawerItemUiLayout
import com.example.gmailappclone.dataclasses.MailLayoutItem
import com.example.gmailappclone.profilescreenhelper.MailLayout
import com.example.gmailappclone.topbars.TopBarOne
import com.example.gmailappclone.viewmodels.DrawerViewModel
import com.example.gmailappclone.viewmodels.ProfileScreenViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerHelperForUiScreens(
    drawerViewModel : DrawerViewModel,
    labelName : String,
    onFabClicked : () -> Unit,
    onMailClicked: (Int) -> Unit,
    onSignOutClicked : () -> Unit,
    profileScreenViewModel: ProfileScreenViewModel,
    context : Context,
    onLabelClicked : (String) -> Unit = {},
){

//    val labelNames = listOf("Starred","Snoozed","Important","Sent","Scheduled","Outbox","Drafts","All Mail","Spam","Bin","[Imap]/Trash")

    val TAG = "DrawerHelperForUiScreens"

//    val mailList by profileScreenViewModel.mailLayoutList.observeAsState(emptyList())
    var mailList by remember { mutableStateOf(emptyList<MailLayoutItem>()) }

// Observe the LiveData based on the labelName condition
    when (labelName) {
        "Promotion" -> {
            val promotionMail by drawerViewModel.getPromotionMail.observeAsState(emptyList())
            mailList = promotionMail
        }
        "Social" -> {
            val socialMail by drawerViewModel.getSocialMail.observeAsState(emptyList())
            mailList = socialMail
        }
        "Primary" -> {
            val primaryMail by drawerViewModel.getPrimaryMail.observeAsState(emptyList())
            mailList = primaryMail
        }
        "Starred" -> {
            val starredMail by drawerViewModel.getStarredMail.observeAsState(emptyList())
            mailList = starredMail
        }
        "Sent" -> {
            val sentMail by drawerViewModel.getSentMail.observeAsState(emptyList())
            mailList = sentMail
        }
        "Draft" -> {
            val draftMail by drawerViewModel.getDraftMail.observeAsState(emptyList())
            mailList = draftMail
        }
        "Bin" -> {
            val binMail by drawerViewModel.getBinMail.observeAsState(emptyList())
            mailList = binMail
        }
        else -> {
            mailList = emptyList()
        }
    }
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
                DrawerItemUiLayout(
                    onLabelNameClicked = {labelName->
                        onLabelClicked(labelName)

                    }
                ) // Drawer content
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

                        },
                        onSignOutClicked = {onSignOutClicked()}
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
                    item { 
                        Text(text = "This is drawer screen with label $labelName and mailList : ${mailList.size}")
                    }

                    // Main content here
                    items(mailList){
                            mails ->
                        val dismissState = rememberSwipeToDismissBoxState()

                        SwipeToDismissBox(
                            state = dismissState,
                            backgroundContent = {
                                // Red background only for the currently swiped mail
                                if (dismissState.targetValue == SwipeToDismissBoxValue.StartToEnd) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Red)
                                            .padding(horizontal = 20.dp),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = Color.White
                                        )
                                    }
                                }
                            },
                            content = {
                                // Main content of the mail item
                                MailLayout(
                                    mailLayoutItem = mails,
                                    onMailClicked = { mailId ->
                                        onMailClicked(mailId)
                                        mailList.find { it.id == mailId }?.isMailOpened = true
                                    },
                                    profileScreenViewModel = profileScreenViewModel,
                                    context = context,
                                    drawerViewModel = drawerViewModel
                                )
                            }
                        )

                        // Check if the item has been swiped and trigger delete
                        if (dismissState.currentValue == SwipeToDismissBoxValue.StartToEnd) {
                            LaunchedEffect(Unit) {
                                profileScreenViewModel.deleteMail(mails)
                            }
                        }
                    }



                }
            }

        }
    }

}
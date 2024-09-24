package com.example.gmailappclone

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gmailappclone.dataclasses.MailLayoutItem
import com.example.gmailappclone.showmailscreenhelper.ReplyRowLayout
import com.example.gmailappclone.topbars.TopBarThree
import com.example.gmailappclone.viewmodels.ProfileScreenViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShowMailScreen(
    onBackButtonClicked: () -> Unit,
    onUnreadMailClicked: () -> Unit,
    profileScreenViewModel: ProfileScreenViewModel,
    mailId: Int
) {

    // Initiate fetching the specific mail
    LaunchedEffect(mailId) {
        profileScreenViewModel.findAParticularMail(mailId)
    }


    //Observing the specific mail
    val specificMail by profileScreenViewModel.specificMail.observeAsState()

    var isStarClicked by remember { mutableStateOf(false) }


    // Update isStarClicked when specificMail changes
    LaunchedEffect(specificMail) {
        specificMail?.let {
            isStarClicked = it.isFavoriteClicked
        }
    }

    val TAG = "SHOW_MAIL_SCREEN"
    Log.d(TAG, "Got mail with id: ${mailId}")



    val example = listOf("1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1")



    Scaffold(
        topBar = {
            TopBarThree(
                onBackButtonClicked = { onBackButtonClicked() },
                onMoreButtonClicked = { /*TODO*/ },
                onArchiveClicked = { /*TODO*/ },
                onDeleteClicked = { /*TODO*/ },
                onUnreadMailClicked = {
                    profileScreenViewModel.isMailSeen(mailId)
                    onUnreadMailClicked()
                }
            )
        },
        bottomBar = { BottomBarLayout()}
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // LazyColumn taking 3/4th of the screen
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(3f)
            ) {

                specificMail?.let {
                    mail->
                    item {
                        Row (
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ){
                            Text(text = "Your ebook bargains for Tuesday.", fontSize = 26.sp, modifier = Modifier.weight(3f))
                            IconButton(onClick = {
                                specificMail?.id?.let { profileScreenViewModel.ToggleFavorite(mailId = it) } ?: run {
                                    // Show a loading or error state
                                    Log.d(TAG,"Mail is loading")
                                }
                                isStarClicked = !isStarClicked
                            }) {
                                if (isStarClicked){
                                    Icon(
                                        imageVector = Icons.Filled.Star,
                                        contentDescription = null,
                                        tint = Color.Blue
                                    )
                                }else{
                                    Icon(painter = painterResource(id = R.drawable.baseline_star_border_24) , contentDescription = null )
                                }
                            }
                        }
                    }
                } ?: run {
                    item {
                        Text(text = "Loading mail...", modifier = Modifier.padding(16.dp))
                    }
                }
                items(example) { item: String ->
                    Text(text = item)
                }
                stickyHeader {
                    Text(text = "This is the second part", fontWeight = FontWeight.Bold)
                }
            }

            ReplyRowLayout()
        }
    }
}


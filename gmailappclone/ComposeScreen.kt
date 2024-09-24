package com.example.gmailappclone

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gmailappclone.dataclasses.MailLayoutItem
import com.example.gmailappclone.topbars.TopBarTwo
import com.example.gmailappclone.viewmodels.ProfileScreenViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ComposeScreen(
    onBackButtonClicked : () -> Unit,
    profileScreenViewModel: ProfileScreenViewModel,
    onSendButtonClicked : () -> Unit

){

    var fromText by remember {
        mutableStateOf("")
    }
    var toText by remember {
        mutableStateOf("")
    }
    var subjectText by remember {
        mutableStateOf("")
    }
    var composeText by remember {
        mutableStateOf("")
    }

    val newId = if (profileScreenViewModel.mailLayoutList.value?.isNotEmpty() == true) {
        profileScreenViewModel.mailLayoutList.value!!.maxOf { it.id } + 1
    } else {
        1
    }
    Scaffold (
        topBar = { TopBarTwo(
            onBackButtonClicked,
            onAttachFileClicked = {},
            onSendButtonClicked = {
                profileScreenViewModel.addNewMail(
                    mailLayoutItem = MailLayoutItem(
                        id = newId,
                        icon = R.drawable.dragonimage,
                        sender = fromText,
                        receiver = toText,
                        subject = subjectText,
                        content = composeText,
                        time = "",
                        isFavoriteClicked = false
                    )
                )
                fromText = ""
                toText = ""
                subjectText = ""
                composeText = ""
                onSendButtonClicked()
            },
            onMoreButtonClicked = {}
        ) }
    ){innerPadding->
        LazyColumn (
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ){
            item {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = fromText,
                    onValueChange = {
                        fromText = it
                    },
                    placeholder = { Text(text = "From")},
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )
            }
            item {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = toText,
                    onValueChange = {
                        toText = it
                    },
                    placeholder = { Text(text = "To")},
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )
            }
            item {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = subjectText,
                    onValueChange = {
                        subjectText = it
                    },
                    placeholder = { Text(text = "Subject")},
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )
            }
            item {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = composeText,
                    onValueChange = {
                        composeText = it
                    },
                    placeholder = { Text(text = "Compose email")},
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )
            }

        }

    }
}
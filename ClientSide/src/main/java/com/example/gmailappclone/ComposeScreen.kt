package com.example.gmailappclone

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.gmailappclone.dataclasses.MailLayoutItem
import com.example.gmailappclone.singletons.MailItemGraph
import com.example.gmailappclone.topbars.TopBarTwo
import com.example.gmailappclone.viewmodels.ComposeViewModel
import com.example.gmailappclone.viewmodels.ProfileScreenViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ComposeScreen(
    onBackButtonClicked : () -> Unit,
    profileScreenViewModel: ProfileScreenViewModel,
    composeViewModel: ComposeViewModel,
    onSendButtonClicked : () -> Unit,
    onPickingFile : () -> Unit,
    onUploadServiceStart: (List<String>) -> Unit,
    context: Context

){
    val TAG = "ComposeScreenResponse"


    val gotUserName by composeViewModel.savedUserName.observeAsState()
    val checkingUploadState by composeViewModel.checkingImageUploading.observeAsState()
    val getAllNames by composeViewModel.getAllNames.observeAsState()
    val labelNames = listOf("Primary","Promotion","Social")

    //all names without this device's name so that they all will be for sending purpose
    val allNamesWithoutThisDevice = getAllNames?.filter { it != gotUserName }

    var isFileNull by remember {
        mutableStateOf(true)
    }

    var isDropDownExpanded by remember {
        mutableStateOf(false)
    }

    val fileLink = mutableListOf<String?>()

    var toText by remember {
        mutableStateOf("Select")
    }
    var subjectText by remember {
        mutableStateOf("")
    }
    var composeText by remember {
        mutableStateOf("")
    }
    var labelText by remember {
        mutableStateOf("Primary")
    }
    var isLabelDropDownExpanded by remember {
        mutableStateOf(false)
    }

    val newId = if (profileScreenViewModel.mailLayoutList.value?.isNotEmpty() == true) {
        profileScreenViewModel.mailLayoutList.value!!.maxOf { it.id } + 1
    } else {
        1
    }
    val imageUri by composeViewModel.imageUri.observeAsState()


    LaunchedEffect(imageUri) {
        if (imageUri != null){
            fileLink.clear()
            fileLink.addAll(imageUri!!)
            onUploadServiceStart(fileLink.toList().map { it ?: "noting found" })
            isFileNull = false


            fileLink.forEach { link->
                Log.d(TAG, "Image uri is $link")
            }
        }else{
            fileLink.clear()
            Log.d(TAG, "Image uri is null")
        }
    }

    Scaffold (
        topBar = { TopBarTwo(
            onBackButtonClicked,
            onAttachFileClicked = {
                onPickingFile()


            },
            onSendButtonClicked = {

                if (toText == "" || toText == "Select"){
                    Toast.makeText(context,"Can't get to whom you want to send message",Toast.LENGTH_LONG).show()
                }else if (subjectText =="" && composeText == ""){
                    Toast.makeText(context,"Can't send empty message",Toast.LENGTH_LONG).show()
                }else{
                    //To send it
                    composeViewModel.getTokenByUserName(
                        userName = toText,
                        onSuccess = {
                                remoteToken->
                            composeViewModel.onRemoteTokenChange(remoteToken)
                            Log.d(TAG, "Got token as $remoteToken")
                        },
                        onFailure = {e->
                            Log.d(TAG,"Can't fetch token : $e")
                        }
                    )
                    composeViewModel.onToChange(toText)
                    composeViewModel.onLabelChange(labelText)
                    composeViewModel.onSubjectChange(subjectText)
                    composeViewModel.onFromChange(gotUserName ?: "No name")
                    composeViewModel.onTextChange(composeText)
                    composeViewModel.onFileLinkChange("")
                    val mailLayoutItem = MailLayoutItem(
                        id = newId,
                        icon = R.drawable.dragonimage,
                        sender = gotUserName ?: "No name",
                        receiver = toText,
                        subject = subjectText,
                        content = composeText,
                        time = "",
                        isFavoriteClicked = false,
                        fileLink = fileLink.joinToString("*||*||*"),
                        isMailOpened = true,
                        label = labelText,
                        isReceived = false,
                        isInBinOrTrash = false,
                        isSentSuccessfully = false
                    )

                    composeViewModel.sendMessage(
                        onSuccess = {
                            //To add in our own device
                            profileScreenViewModel.addNewMail(
                                mailLayoutItem
                            )
                            Toast.makeText(context, "Message sent successfully", Toast.LENGTH_SHORT).show()
                        },
                        onFailure = {message->
                            Toast.makeText(context, "Message failed to send : $message", Toast.LENGTH_SHORT).show()

                        }
                    )


                    toText = ""
                    subjectText = ""
                    composeText = ""
                    fileLink.clear()
                    onSendButtonClicked()

                }
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
                Text(text = "From : ${gotUserName ?: "No name"}")
            }
            item {
                Box(){
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text(text = "To : ")
                        OutlinedButton(onClick = { isDropDownExpanded = true }) {
                            Text(text = toText)
                            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    }
                    DropdownMenu(expanded = isDropDownExpanded, onDismissRequest = { isDropDownExpanded = false }) {
                        if (allNamesWithoutThisDevice != null ){
                            allNamesWithoutThisDevice.forEach {
                                DropdownMenuItem(
                                    text = { Text(text = it) },
                                    onClick = {
                                        toText = it
                                        isDropDownExpanded = false
                                    }
                                )
                            }
                        }else{
                            DropdownMenuItem(text = { Text(text = "No name found") }, onClick = { isDropDownExpanded = false })
                        }

                    }
                }
            }
            item {
                Box(){
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text(text = "Label : ")
                        OutlinedButton(onClick = { isLabelDropDownExpanded = true }) {
                            Text(text = labelText)
                            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    }
                    DropdownMenu(expanded = isLabelDropDownExpanded, onDismissRequest = { isLabelDropDownExpanded = false }) {
                                labelNames.forEach {
                                    DropdownMenuItem(
                                        text = { Text(text = it) },
                                        onClick = {
                                            labelText = it
                                            isLabelDropDownExpanded = false
                                        }
                                    )
                                }
                    }
                }
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

            item{
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Text(text = "Attachments")
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))

            }


                if (!isFileNull){
                    if (checkingUploadState == true){
                        fileLink.forEach { link->
                            item {
                                Box(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .fillMaxWidth()
                                        .height(400.dp)

                                        .border(2.dp, Color.Black)
                                ) {
                                    val painter = rememberAsyncImagePainter(link ?: "")
                                    androidx.compose.foundation.Image(
                                        painter = painter,
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                        }
                    }else{
                        item {
                            CircularProgressIndicator(modifier = Modifier.fillMaxSize())
                        }

                    }
                }else{
                    item {
                        Text(text = "No file selected")
                    }

                }


        }

    }
}
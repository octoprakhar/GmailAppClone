package com.example.gmailappclone.topbars

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.example.gmailappclone.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarTwo(
    onBackButtonClicked : () -> Unit,
    onAttachFileClicked: () -> Unit,
    onSendButtonClicked : () -> Unit,
    onMoreButtonClicked : () -> Unit,
){
    TopAppBar(
        title = { /*TODO*/ },
        navigationIcon = {
            IconButton(onClick = {onBackButtonClicked()}) {
                Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = null)
            }
        },
        actions = {
            IconButton(onClick = { onAttachFileClicked() }) {
                Icon(painter = painterResource(id = R.drawable.baseline_attachment_24), contentDescription = null)

            }
            IconButton(onClick = { onSendButtonClicked() }) {
                Icon(painter = painterResource(id = R.drawable.baseline_send_24), contentDescription = null)

            }
            IconButton(onClick = { onMoreButtonClicked() }) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)

            }
        }
    )

}
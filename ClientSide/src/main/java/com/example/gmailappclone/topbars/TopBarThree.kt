package com.example.gmailappclone.topbars

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.painterResource
import com.example.gmailappclone.R
import com.example.gmailappclone.dataclasses.MailLayoutItem
import com.example.gmailappclone.viewmodels.ProfileScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarThree(
    onBackButtonClicked : () -> Unit,
    onMoreButtonClicked : () -> Unit,
    onArchiveClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
    onUnreadMailClicked: () -> Unit,

    ){



    TopAppBar(
        title = { /*TODO*/ },
        navigationIcon = {
            IconButton(onClick = {onBackButtonClicked()}) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            }
        },
        actions = {
            IconButton(onClick = { onArchiveClicked() }) {
                Icon(painter = painterResource(id = R.drawable.baseline_archive_24), contentDescription =null )
            }
            IconButton(onClick = { onDeleteClicked() }) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = null)

            }
            IconButton(onClick = {
                onUnreadMailClicked()
            }) {
                Icon(imageVector = Icons.Default.MailOutline, contentDescription = null)

            }
            IconButton(onClick = { onMoreButtonClicked() }) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)

            }
        }
    )
}
package com.example.gmailappclone

import android.content.ClipData.Item
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gmailappclone.drawerItemUiHelper.DrawerLayoutOne


@Composable
fun DrawerItemUiLayout() {
    // Wrap the drawer content in a Box or Surface to control the background color
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val labelNames = listOf("Starred","Snoozed","Important","Sent","Scheduled","Outbox","Drafts","All Mail","Spam","Bin","[Imap]/Trash")


    Box(
        modifier = Modifier
            .fillMaxHeight() // Ensure the drawer fills the available space
            .width(screenWidth * 4 / 5)
            .background(Color.White) // Set your custom background color here
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()

        ) {
            item {
                Text(text = "Gmail", fontSize = 20.sp, modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 32.dp, bottom = 16.dp), color = Color(0xFFb82606))
            }
            item {
                HorizontalDivider(modifier = Modifier.fillMaxWidth())
            }
            item {
                DrawerLayoutOne(icon = R.drawable.baseline_stay_primary_portrait_24, text = "All Inbox", extra = {})

            }
            item {
                HorizontalDivider(modifier = Modifier.fillMaxWidth())
            }
            item {
                DrawerLayoutOne(icon = R.drawable.baseline_stay_primary_portrait_24, text = "primary", extra = {})

            }
            item {
                DrawerLayoutOne(icon = R.drawable.baseline_stay_primary_portrait_24, text = "Promotion", extra = {})

            }
            item {
                DrawerLayoutOne(icon = R.drawable.baseline_stay_primary_portrait_24, text = "Social", extra = {})

            }
            item {
                Text(text = "All Label", fontSize = 10.sp)
            }
            items(labelNames){
                DrawerLayoutOne(icon = R.drawable.baseline_stay_primary_portrait_24, text = it, extra = {})

            }
        }
    }
}


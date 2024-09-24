package com.example.gmailappclone.topbars

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import com.example.gmailappclone.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarOne(
    openDrawer : () -> Unit,
    openProfile: () -> Unit
){
    val imageBitmap = ImageBitmap.imageResource(id = R.drawable.pletter)
    TopAppBar(
        modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(Color.LightGray, RoundedCornerShape(32.dp)),
        title = {
            Text(text = "Search in emails", modifier = Modifier.fillMaxWidth().clickable(onClick = {}))
        },
        navigationIcon = {
            IconButton(onClick = { openDrawer() }) {
                Icon(imageVector = Icons.Default.Menu, contentDescription = null)
            }
        },
        actions = {
            Box(modifier = Modifier
                .size(60.dp)
                .padding(8.dp)
                .clip(CircleShape)
                .clickable(onClick = openProfile)
            ) {
                Image(bitmap = imageBitmap, contentDescription = null, contentScale = ContentScale.Fit)

            }

        },
        colors = TopAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent,
            navigationIconContentColor = Color.Black,
            titleContentColor = Color.Gray,
            actionIconContentColor = Color.Transparent
        )
    )
}
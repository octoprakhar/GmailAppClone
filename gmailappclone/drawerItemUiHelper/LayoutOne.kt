package com.example.gmailappclone.drawerItemUiHelper

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.gmailappclone.R

@Composable
fun DrawerLayoutOne(
    icon : Int,
    text : String,
    extra: @Composable (RowScope.() -> Unit)
){


    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(end = 8.dp)
        .clip(RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp))

    ){
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Row {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    modifier = Modifier.padding(end = 16.dp)
                )

                Text(text = text)

            }
            extra()
        }
    }
}
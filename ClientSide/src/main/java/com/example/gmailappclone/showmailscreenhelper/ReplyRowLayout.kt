package com.example.gmailappclone.showmailscreenhelper

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gmailappclone.R

@Composable
fun ReplyRowLayout(){
    var replyText by remember {
        mutableStateOf("")
    }
    Row (
        modifier = Modifier
            .fillMaxWidth()
        ,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        IconButton(
            modifier = Modifier.weight(1f),
            onClick = { /*TODO*/ }
        ) {
            Icon(painter = painterResource(id = R.drawable.baseline_attach_file_24), contentDescription = null)
        }
        Box(modifier = Modifier
            .weight(10f)
            .padding(vertical = 16.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(Color.LightGray)
        ){
            Row (
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ){
                Row (
                    modifier = Modifier.clickable {
                        /*TODO*/
                    }
                ){
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = Color.Blue
                    )
                    Text(text = "\u25BC", color = Color.Blue)
                }
                TextField(
                    modifier = Modifier.weight(5f),
                    value = replyText,
                    onValueChange = { replyText = it },
                    placeholder = { Text(text = "Reply") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.LightGray,
                        unfocusedContainerColor = Color.LightGray
                    )
                )
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.Blue
                    )
                }

            }

        }
        Text(text = "\uD83D\uDE0A", modifier = Modifier
            .weight(1f)
            .clickable { /*TODO*/ },
            fontSize = 40.sp
        )
    }
}
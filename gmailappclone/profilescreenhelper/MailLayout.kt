package com.example.gmailappclone.profilescreenhelper

import android.os.Parcelable
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gmailappclone.R
import com.example.gmailappclone.dataclasses.MailLayoutItem
import com.example.gmailappclone.viewmodels.ProfileScreenViewModel
import kotlinx.parcelize.Parcelize

@Composable
fun MailLayout(
    mailLayoutItem: MailLayoutItem,
    onMailClicked: (Int) -> Unit,
    profileScreenViewModel: ProfileScreenViewModel
){
    val TAG = "MAIL_LAYOUT_SCREEN"

    fun truncateString(input:String, maxLength:Int):String{
        if (input.length > maxLength){
            return input.take(maxLength) + "..."
        }else{
            return input
        }
    }
    
    val mailImageBitmap = ImageBitmap.imageResource(id = mailLayoutItem.icon)

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                onMailClicked(
                    mailLayoutItem.id
                )
                Log.d(TAG, "Navigating to ShowMailScreen with id: ${mailLayoutItem.id}")

            },
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ){
        Box(modifier = Modifier
            .size(60.dp)
            .clip(CircleShape)
            ){
            Image(bitmap = mailImageBitmap, contentDescription = null, contentScale = ContentScale.Fit)
        }

        Column(
            modifier = Modifier.fillMaxHeight().padding(horizontal = 8.dp).weight(5f),
        ) {
            Text(text = truncateString(mailLayoutItem.sender, 25), fontWeight = if (!mailLayoutItem.isMailOpened) FontWeight.Bold else FontWeight.Light)
            Text(text = truncateString(mailLayoutItem.subject, 50), fontSize = 12.sp, fontWeight = if (!mailLayoutItem.isMailOpened) FontWeight.Bold else FontWeight.Light)
            Text(text = truncateString(mailLayoutItem.content, 40), fontSize = 12.sp)
        }
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = mailLayoutItem.time, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
            IconButton(onClick = {
                profileScreenViewModel.ToggleFavorite(mailId = mailLayoutItem.id)
            }) {
                if (mailLayoutItem.isFavoriteClicked){
                    Icon(imageVector = Icons.Filled.Star, contentDescription = null, tint = Color.Blue)
                }else{
                    Icon(painter = painterResource(id = R.drawable.baseline_star_border_24), contentDescription = null)
                }
            }
        }
    }

}




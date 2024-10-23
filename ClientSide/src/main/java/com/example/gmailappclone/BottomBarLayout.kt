package com.example.gmailappclone

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BottomBarLayout(){

    var isMailClicked by remember {
        mutableStateOf(true)
    }
    var isVideoCallClicked by remember {
        mutableStateOf(false)
    }
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(80.dp)
        .background(Color.LightGray)){
        Row (
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ){
            Box(modifier = if (isMailClicked){
                Modifier
                    .wrapContentSize()
                    .background(Color(0xFF27abf2), RoundedCornerShape(16.dp))
                    .clickable(
                        onClick = {
                            isMailClicked = true
                            isVideoCallClicked = false
                        }
                    )
            }else {
                Modifier
                    .wrapContentSize()
                    .clickable(
                        onClick = {
                            isMailClicked = true
                            isVideoCallClicked = false
                        }
                    )
            }
            ){
                Icon(
                    modifier =  Modifier.padding(16.dp),
                    imageVector = Icons.Default.Email,
                    contentDescription = null
                )
            }
            Box(modifier = if (isVideoCallClicked){
                Modifier
                    .wrapContentSize()
                    .background(Color(0xFF72d4f2), RoundedCornerShape(16.dp))
                    .clickable(
                        onClick = {
                            isMailClicked = false
                            isVideoCallClicked = true
                        }
                    )
            }else {
                Modifier
                    .wrapContentSize()
                    .clickable(
                        onClick = {
                            isMailClicked = false
                            isVideoCallClicked = true
                        }
                    )
            }){
                Icon(
                    modifier = Modifier.padding(16.dp),

                    imageVector = Icons.Default.Call,
                    contentDescription = null
                )
            }
        }
    }

}
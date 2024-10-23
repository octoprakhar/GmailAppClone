package com.example.gmailappclone

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun PermissionDialog(
    permissionTextProvider: PermissionTextProvider,
    isPermanentlyDeclined : Boolean,
    onDismiss : () -> Unit,
    onOkClick : () -> Unit,
    onGoToAppSettingsClick : () -> Unit,
    modifier : Modifier = Modifier
){

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                HorizontalDivider()
                Text(
                    text = if (isPermanentlyDeclined){
                        "Grant Permission"
                    }else{
                        "OK"
                    },
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (isPermanentlyDeclined){
                                onGoToAppSettingsClick()
                            }else{
                                onOkClick()
                            }
                        }
                        .padding(16.dp)
                )
            }
        },
        title = {
            Text(text = "Permission Required")

        },
        text = {
            Text(text = permissionTextProvider.getDescription(isPermanentlyDeclined))

        },
        modifier = modifier
    )
}

interface PermissionTextProvider{
    fun getDescription (isPermanentlyDeclined: Boolean) : String
}

class NotificationPermissionTextProvider : PermissionTextProvider{
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        if (isPermanentlyDeclined){
            return "It seems you permanently declined the permission to receive notifications."  +
                    "You can go to the app settings to grant it. Without it you will not be able to receive notifications."

        }else{
            return "This app needs permission to receive notifications." +
                    "Please allow to receive notifications."

        }
    }

}

class ImageAndVideoPermissionTextProvider : PermissionTextProvider{
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        if (isPermanentlyDeclined){
            return "It seems you permanently declined the permission to access images and videos."  +
                    "You can go to the app settings to grant it. Without it you will not be able to access images and videos."
        }else{
            return "This app needs permission to access images and videos." +
                    "Please allow to access images and videos."

        }
    }

}
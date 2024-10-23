package com.example.gmailappclone

sealed class Screens(val route : String) {

    object PrimaryScreen : Screens("primaryscreen")
    object ComposeScreen : Screens("composescreen")
    object ShowMailScreen : Screens("showmailscreen")
    object LoginOrRegisterScreen : Screens("loginorregisterscreen")
    object NotConnectedUi : Screens("notconnectedui")
    object DrawerHelperForUi : Screens("drawerhelperforui")

}
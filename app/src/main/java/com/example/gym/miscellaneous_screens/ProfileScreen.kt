package com.example.gym

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.gym.navigation.NavViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreen(
    auth: FirebaseAuth,
    navModel: NavViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        auth.currentUser?.email?.let { Text(text = it) }
        Button(onClick = {
            auth.signOut()
            navModel.updateSignIn(false)
        }) {
            Text(text = "sign out")
        }
    }
}
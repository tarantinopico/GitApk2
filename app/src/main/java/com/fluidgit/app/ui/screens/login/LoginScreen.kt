package com.fluidgit.app.ui.screens.login

import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fluidgit.app.ui.theme.*

@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit
) {
    var patToken by remember { mutableStateOf("") }
    val context = LocalContext.current
    
    // We can use a public Client ID if we have one, but we'll instruct the user.
    val clientId = "Iv23ligl85a5Fv" // A dummy client ID or you need to supply your own.
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("GitHub Authorization", color = Cyan400, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "To unlock complete Fluid Git capabilities, log in with your GitHub account.",
            color = Slate400,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Button(
            onClick = {
                val authUri = Uri.parse("https://github.com/login/oauth/authorize?client_id=$clientId&scope=repo,user")
                val intent = CustomTabsIntent.Builder().build()
                intent.launchUrl(context, authUri)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Cyan500, contentColor = Slate100),
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Login via Browser (OAuth)", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        Text("OR", color = Slate500, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))
        
        OutlinedTextField(
            value = patToken,
            onValueChange = { patToken = it },
            label = { Text("Personal Access Token (PAT)", color = Slate400) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Cyan400,
                unfocusedBorderColor = Slate600,
                focusedTextColor = Slate100,
                unfocusedTextColor = Slate100
            ),
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = {
                if (patToken.isNotBlank()) {
                    onLoginSuccess(patToken)
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = SurfaceElevated, contentColor = Cyan400),
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Login with Token", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = {
             val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/settings/tokens/new?scopes=repo,read:org,workflow"))
             context.startActivity(intent)
        }) {
            Text("Get empty PAT instead", color = Cyan400)
        }
    }
}

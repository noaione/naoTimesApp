package me.naoti.panelapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.naoti.panelapp.network.ErrorCode
import me.naoti.panelapp.network.models.RegisterModel
import me.naoti.panelapp.state.AppState
import me.naoti.panelapp.ui.ScreenItem
import me.naoti.panelapp.ui.theme.*
import me.naoti.panelapp.utils.getLogger

@Composable
fun RegisterScreen(appState: AppState) {
    val navController = appState.navController
    val log = getLogger()
    var allowMutate by remember { mutableStateOf(true) }
    val (errorMessage, setErrorMessage) = remember {
        mutableStateOf<String?>(null)
    }

    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val username = remember {
            mutableStateOf(TextFieldValue())
        }
        val administrator = remember {
            mutableStateOf(TextFieldValue())
        }

        Text(text = "Register", style = TextStyle(fontSize = 24.sp))
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            label = { Text(text = "Server ID") },
            value = username.value,
            onValueChange = { username.value = it },
            enabled = allowMutate,
        )
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            label = { Text(text = "Administrator") },
            value = administrator.value,
            onValueChange = { administrator.value = it },
            enabled = allowMutate,
        )

        Spacer(modifier = Modifier.height(20.dp))
        Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
            Button(
                enabled = allowMutate,
                onClick = {
                    appState.coroutineScope.launch {
                        allowMutate = false
                        log.i("Registering with ${username.value.text} and user ${administrator.value.text}")
                        when (val registerState = appState.apiState.registerUser(
                            RegisterModel(username.value.text, administrator.value.text)
                        )) {
                            is NetworkResponse.Success -> {
                                val result = registerState.body
                                if (result.success) {
                                    log.i("Registration success, will redirect to login view later...")
                                    // show snack bar then navigate
                                    setErrorMessage("Success")
                                } else {
                                    val theText = when (result.code) {
                                        ErrorCode.ServerNotFound -> result.code.asText(username.value.text)
                                        ErrorCode.UserNotFound -> result.code.asText(username.value.text)
                                        ErrorCode.MissingPermission -> result.code.asText("Manage guild or administrator, to register!")
                                        ErrorCode.ServerRegistered -> result.code.asText(username.value.text)
                                        else -> {
                                            if (result.code != null) {
                                                result.code.asText()
                                            } else {
                                                ErrorCode.UnknownError.asText()
                                            }
                                        }
                                    }
                                    log.e("Registration failed, $theText")
                                    setErrorMessage(theText)
                                }
                            }
                            is NetworkResponse.Error -> {
                                val body = registerState.body
                                var theText = registerState.error.toString()
                                if (body != null) {
                                    theText = when (body.code) {
                                        ErrorCode.ServerNotFound -> body.code.asText(username.value.text)
                                        ErrorCode.UserNotFound -> body.code.asText(username.value.text)
                                        ErrorCode.MissingPermission -> body.code.asText("Manage guild or administrator, to register!")
                                        ErrorCode.ServerRegistered -> body.code.asText(username.value.text)
                                        else -> {
                                            if (body.code != null) {
                                                body.code.asText()
                                            } else {
                                                ErrorCode.UnknownError.asText()
                                            }
                                        }
                                    }
                                }
                                log.e("Failed to register: $theText")
                                setErrorMessage(theText)
                            }
                        }
                    }
                },
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = "Register")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        ClickableText(
            text = AnnotatedString("Have an account?"),
            onClick = {
                if (allowMutate) {
                    navController.navigate(ScreenItem.LoginScreen.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            },
            style = TextStyle(
                fontSize = 14.sp,
                color = if (appState.isDarkMode()) Blue300 else Blue500
            )
        )
    }

    if (errorMessage != null) {
        if (errorMessage == "Success") {
            LaunchedEffect(key1 = true) {
                Toast.makeText(
                    appState.contextState,
                    "Success, redirecting...",
                    Toast.LENGTH_SHORT
                ).show()
                delay(1500L)
                navController.navigate(ScreenItem.LoginScreen.route) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            }
        } else {
            Toast.makeText(
                appState.contextState,
                errorMessage,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}

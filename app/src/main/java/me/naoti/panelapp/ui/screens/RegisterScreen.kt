package me.naoti.panelapp.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.naoti.panelapp.R
import me.naoti.panelapp.network.ErrorCode
import me.naoti.panelapp.network.models.RegisterModel
import me.naoti.panelapp.state.AppState
import me.naoti.panelapp.ui.ScreenItem
import me.naoti.panelapp.utils.getLogger

internal fun validateRegisterInput(username: String, password: String): Pair<String?, Pair<Boolean, Boolean>> {
    if (username.isEmpty() && password.isEmpty()) {
        return Pair("You must fill all forms!", Pair(true, true))
    }
    if (username.isEmpty()) {
        return Pair("Please enter server ID!", Pair(true, false))
    }
    if (password.isEmpty()) {
        return Pair("Please enter administrator!", Pair(false, true))
    }
    if (!username.matches("[0-9]+".toRegex())) {
        return Pair("Server ID must be a number", Pair(true, false))
    }
    if (!password.matches("[0-9]+".toRegex())) {
        return Pair("Administrator must be a number", Pair(false, true))
    }
    return Pair(null, Pair(false, false))
}

@Composable
fun RegisterScreen(appState: AppState) {
    val navController = appState.navController
    val log = getLogger()
    var allowMutate by remember { mutableStateOf(true) }
    var usernameData by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }
    var administratorData by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }

    val (errorMessage, setErrorMessage) = remember {
        mutableStateOf<String?>(null)
    }
    val (lastKnownError, setLastKnownError) = rememberSaveable {
        mutableStateOf<String?>(null)
    }

    var usernameError by rememberSaveable {
        mutableStateOf(false)
    }
    var adminError by rememberSaveable {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Register", style = TextStyle(fontSize = 24.sp))
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            label = { Text(text = "Server ID") },
            value = usernameData,
            onValueChange = { usernameData = it },
            enabled = allowMutate,
            modifier = Modifier
                .testTag("RegisterServerID")
                .fillMaxWidth(),
            singleLine = true,
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_icons_server),
                    contentDescription = "Server ID"
                )
            },
            isError = usernameError,
        )
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            label = { Text(text = "Administrator") },
            value = administratorData,
            onValueChange = {
                administratorData = it
                if (errorMessage != null) {
                    setErrorMessage(null)
                }
                if (adminError) adminError = false
            },
            enabled = allowMutate,
            modifier = Modifier
                .testTag("RegisterAdmin")
                .fillMaxWidth(),
            singleLine = true,
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_icons_account),
                    contentDescription = "Administrator"
                )
            },
            isError = adminError
        )

        Spacer(modifier = Modifier.height(20.dp))
        Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
            Button(
                enabled = allowMutate,
                onClick = {
                    val (errorMsg, errorBool) = validateLoginInput(usernameData.text, administratorData.text)
                    val (userError, passError) = errorBool
                    if (errorMsg != null) {
                        setErrorMessage(errorMsg)
                        setLastKnownError(errorMessage)
                        adminError = passError
                        usernameError = userError
                        return@Button
                    }
                    allowMutate = false
                    usernameError = false
                    adminError = false
                    appState.coroutineScope.launch {
                        log.i("Registering with ${usernameData.text} and user ${administratorData.text}")
                        when (val registerState = appState.apiState.registerUser(
                            RegisterModel(usernameData.text, administratorData.text)
                        )) {
                            is NetworkResponse.Success -> {
                                val result = registerState.body
                                if (result.success) {
                                    log.i("Registration success, will redirect to login view later...")
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
                                } else {
                                    val theText = when (result.code) {
                                        ErrorCode.ServerNotFound -> {
                                            usernameError = true
                                            result.code.asText(usernameData.text)
                                        }
                                        ErrorCode.UserNotFound -> {
                                            adminError = true
                                            result.code.asText(usernameData.text)
                                        }
                                        ErrorCode.MissingPermission -> {
                                            adminError = true
                                            result.code.asText("Manage guild or administrator, to register!")
                                        }
                                        ErrorCode.ServerRegistered -> {
                                            usernameError = true
                                            result.code.asText(usernameData.text)
                                        }
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
                                    setLastKnownError(theText)
                                }
                            }
                            is NetworkResponse.Error -> {
                                val body = registerState.body
                                var theText = registerState.error.toString()
                                if (body != null) {
                                    theText = when (body.code) {
                                        ErrorCode.ServerNotFound -> {
                                            usernameError = true
                                            body.code.asText(usernameData.text)
                                        }
                                        ErrorCode.UserNotFound -> {
                                            adminError = true
                                            body.code.asText(usernameData.text)
                                        }
                                        ErrorCode.MissingPermission -> {
                                            adminError = true
                                            body.code.asText("Manage guild or administrator, to register!")
                                        }
                                        ErrorCode.ServerRegistered -> {
                                            usernameError = true
                                            body.code.asText(usernameData.text)
                                        }
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
                                setLastKnownError(theText)
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

        AnimatedVisibility(
            visible = errorMessage != null,
        ) {
            Text(
                text = errorMessage ?: (lastKnownError ?: "An Unknown Error Has Occurred!"),
                style = TextStyle(
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                ),
                modifier = Modifier
                    .padding(horizontal = 4.dp, vertical = 6.dp)
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )
        }

        TextButton(
            modifier = Modifier
                .padding(horizontal = 4.dp, vertical = 6.dp)
                .align(Alignment.CenterHorizontally),
            onClick = {
                if (allowMutate) {
                    navController.navigate(ScreenItem.LoginScreen.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            },
            enabled = allowMutate,
        ) {
            Text(
                text = "Have an account?",
                style = TextStyle(
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

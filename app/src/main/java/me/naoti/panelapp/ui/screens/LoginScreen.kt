package me.naoti.panelapp.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.launch
import me.naoti.panelapp.R
import me.naoti.panelapp.network.ErrorCode
import me.naoti.panelapp.network.models.LoginModel
import me.naoti.panelapp.state.AppState
import me.naoti.panelapp.ui.ScreenItem
import me.naoti.panelapp.utils.getLogger

@Composable
fun PasswordInput(
    text: String,
    modifier: Modifier = Modifier,
    onPasswordChange: ((String) -> Unit)? = null,
    enabled: Boolean = true,
    isError: Boolean = false,
) {
    var passwordData by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(text))
    }
    var showPassword by rememberSaveable { mutableStateOf(false) }
    OutlinedTextField(
        label = { Text(text = "Password") },
        modifier = modifier
            .testTag("LoginPassword")
            .fillMaxWidth(),
        value = passwordData,
        visualTransformation = if (showPassword) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        isError = isError,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            IconButton(
                onClick = { showPassword = !showPassword }
            ) {
                if (showPassword) {
                    Icon(painterResource(id = R.drawable.ic_icons_eye), contentDescription = "Hide Password")
                } else {
                    Icon(painterResource(id = R.drawable.ic_icons_eye_off), contentDescription = "Show Password")
                }
            }
        },
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_icons_lock_outline),
                contentDescription = "Server ID"
            )
        },
        singleLine = true,
        onValueChange = {
            passwordData = it
            if (onPasswordChange != null) {
                onPasswordChange(passwordData.text)
            }
        },
        enabled = enabled,
    )
}

internal fun validateLoginInput(username: String, password: String): Pair<String?, Pair<Boolean, Boolean>> {
    if (username.isEmpty() && password.isEmpty()) {
        return Pair("You must fill all forms!", Pair(true, true))
    }
    if (username.isEmpty()) {
        return Pair("Please enter server ID!", Pair(true, false))
    }
    if (password.isEmpty()) {
        return Pair("Please enter password!", Pair(false, true))
    }
    if (!username.matches("[0-9]+".toRegex())) {
        return Pair("Server ID must be a number", Pair(true, false))
    }
    return Pair(null, Pair(false, false))
}

@Composable
fun LoginScreen(context: AppState) {
    val log = getLogger("LoginScreenActivity")
    log.i("Resetting navAppController since we reach login compose")
    var allowMutate by rememberSaveable { mutableStateOf(true) }
    var usernameData by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }
    var passwordData by rememberSaveable { mutableStateOf("") }
    var usernameError by rememberSaveable {
        mutableStateOf(false)
    }
    var passwordError by rememberSaveable {
        mutableStateOf(false)
    }
    val (errorMessage, setErrorMessage) = rememberSaveable {
        mutableStateOf<String?>(null)
    }
    val (lastKnownError, setLastKnownError) = rememberSaveable {
        mutableStateOf<String?>(null)
    }

    Column(
        modifier = Modifier
            .padding(20.dp)
            .wrapContentSize(Alignment.Center),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Login", style = TextStyle(fontSize = 24.sp))
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            label = { Text(text = "Server ID") },
            value = usernameData,
            onValueChange = {
                usernameData = it
                if (errorMessage != null) {
                    setErrorMessage(null)
                }
                if (usernameError) usernameError = false
            },
            enabled = allowMutate,
            modifier = Modifier
                .testTag("LoginUsername")
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
        PasswordInput(
            text = passwordData,
            onPasswordChange = {
                passwordData = it
                if (errorMessage != null) {
                    setErrorMessage(null)
                }
                if (passwordError) passwordError = false
            },
            enabled = allowMutate,
            isError = passwordError,
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        Box(modifier = Modifier
            .padding(40.dp, 0.dp, 40.dp, 0.dp)
            .fillMaxWidth()) {
            Button(
                enabled = allowMutate,
                onClick = {
                    val (errorMsg, errorBool) = validateLoginInput(usernameData.text, passwordData)
                    val (userError, passError) = errorBool
                    if (errorMsg != null) {
                        setErrorMessage(errorMsg)
                        setLastKnownError(errorMessage)
                        passwordError = passError
                        usernameError = userError
                        return@Button
                    }
                    allowMutate = false
                    usernameError = false
                    passwordError = false
                    setErrorMessage(null)
                    context.coroutineScope.launch {
                        log.i("Logging in with ${usernameData.text}")
                        when (val loginState = context.apiState.loginUser(LoginModel(usernameData.text, passwordData))) {
                            is NetworkResponse.Success -> {
                                val result = loginState.body
                                if (result.loggedIn) {
                                    log.i("Fetching user information...")
                                    when (val userState = context.apiState.getUser()) {
                                        is NetworkResponse.Success -> {
                                            log.d("UserInfo=" + userState.body.toString())
                                            val userInfo = userState.body
                                            if (userInfo.loggedIn) {
                                                context.setCurrentUser(userInfo)
                                                context.navController.navigate(ScreenItem.AppScaffold.route) {
                                                    popUpTo(context.navController.graph.startDestinationId)
                                                    launchSingleTop = true
                                                }
                                            } else {
                                                context.setCurrentUser(null)
                                                setErrorMessage(ErrorCode.ServerError.asText())
                                                setLastKnownError(ErrorCode.ServerError.asText())
                                            }
                                        }
                                        is NetworkResponse.Error -> {
                                            context.setCurrentUser(null)
                                            setErrorMessage(ErrorCode.ServerError.asText())
                                            setLastKnownError(ErrorCode.ServerError.asText())
                                        }
                                    }
                                } else {
                                    log.e("Log in somehow succeeded, but user fetching failed")
                                    context.setCurrentUser(null)
                                    // show snack bar
                                    val theText = when (result.code) {
                                        ErrorCode.UnknownServerID -> {
                                            usernameError = true
                                            result.code.asText(usernameData.text)
                                        }
                                        ErrorCode.WrongPassword -> {
                                            passwordError = true
                                            result.code.asText()
                                        }
                                        else -> result.code.asText()
                                    }
                                    setErrorMessage(theText)
                                    setLastKnownError(theText)
                                }
                            }
                            is NetworkResponse.Error -> {
                                context.setCurrentUser(null)
                                val body = loginState.body
                                var theText = loginState.error.toString()
                                if (body != null) {
                                    theText = when (body.code) {
                                        ErrorCode.UnknownServerID -> {
                                            usernameError = true
                                            body.code.asText(usernameData.text)
                                        }
                                        ErrorCode.WrongPassword -> {
                                            passwordError = true
                                            body.code.asText()
                                        }
                                        else -> body.code.asText()
                                    }
                                }
                                log.e("Failed to log in: $theText")
                                setErrorMessage(theText)
                                setLastKnownError(theText)
                            }
                        }
                        allowMutate = true
                    }
                },
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("SubmitLogin")
            ) {
                Text(text = "Login")
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
                    context.navController.navigate(ScreenItem.RegisterScreen.route) {
                        popUpTo(context.navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            },
            enabled = allowMutate,
        ) {
            Text(
                text = "Signup",
                style = TextStyle(
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

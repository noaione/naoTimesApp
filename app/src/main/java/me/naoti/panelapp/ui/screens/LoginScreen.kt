package me.naoti.panelapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.launch
import me.naoti.panelapp.network.ErrorCode
import me.naoti.panelapp.network.models.LoginModel
import me.naoti.panelapp.state.rememberAppState
import me.naoti.panelapp.ui.ScreenItem
import me.naoti.panelapp.ui.theme.*
import me.naoti.panelapp.utils.getLogger

@Composable
fun LoginScreen(navController: NavController) {
    val context = rememberAppState(navController = navController as NavHostController)
    val log = getLogger("LoginScreenActivity")
    var allowMutate by remember { mutableStateOf(true) }
    val username = remember {
        mutableStateOf(TextFieldValue())
    }
    val (errorMessage, setErrorMessage) = remember {
        mutableStateOf<String?>(null)
    }

    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val password = remember {
            mutableStateOf(TextFieldValue())
        }

        Text(text = "Login", style = TextStyle(fontSize = 24.sp))
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            label = { Text(text = "Server ID") },
            value = username.value,
            onValueChange = { username.value = it },
            enabled = allowMutate,
        )
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            label = { Text(text = "Password") },
            value = password.value,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            onValueChange = { password.value = it },
            enabled = allowMutate,
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
            Button(
                enabled = allowMutate,
                onClick = {
                    context.coroutineScope.launch {
                        allowMutate = false
                        setErrorMessage(null)
                        log.i("Logging in with ${username.value.text}")
                        when (val loginState = context.apiState.loginUser(LoginModel(username.value.text, password.value.text))) {
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
                                                navController.navigate(ScreenItem.AppScaffold.route) {
                                                    popUpTo(navController.graph.startDestinationId)
                                                    launchSingleTop = true
                                                }
                                            } else {
                                                context.setCurrentUser(null)
                                                setErrorMessage(ErrorCode.ServerError.asText())
                                            }
                                        }
                                        is NetworkResponse.Error -> {
                                            context.setCurrentUser(null)
                                            setErrorMessage(ErrorCode.ServerError.asText())
                                        }
                                    }
                                } else {
                                    log.e("Log in somehow succeeded, but user fetching failed")
                                    context.setCurrentUser(null)
                                    // show snack bar
                                    val theText = when (result.code) {
                                        ErrorCode.UnknownServerID -> result.code.asText(username.value.text)
                                        else -> result.code.asText()
                                    }
                                    setErrorMessage(theText)
                                }
                            }
                            is NetworkResponse.Error -> {
                                context.setCurrentUser(null)
                                val body = loginState.body
                                var theText = loginState.error.toString()
                                if (body != null) {
                                    theText = when (body.code) {
                                        ErrorCode.UnknownServerID -> body.code.asText(username.value.text)
                                        else -> body.code.asText()
                                    }
                                }
                                log.e("Failed to log in: $theText")
                                setErrorMessage(theText)
                            }
                        }
                        allowMutate = true
                    }
                },
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = "Login")
            }
        }

        if (errorMessage != null) {
            log.i("Showing failure snack bar! Error: $errorMessage")
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = errorMessage,
                style = TextStyle(
                    color = if (context.isDarkMode()) Red300 else Red600,
                    fontWeight = FontWeight.Medium,
                )
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
        ClickableText(
            text = AnnotatedString("Signup"),
            onClick = {
                if (allowMutate) {
                    navController.navigate(ScreenItem.RegisterScreen.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            },
            style = TextStyle(
                fontSize = 14.sp,
                color = if (context.isDarkMode()) Blue300 else Blue500
            )
        )
    }


}

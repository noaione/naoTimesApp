package me.naoti.panelapp.ui.theme

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val DarkColorPalette = darkColors(
    primary = Orange400,
    primaryVariant = Orange600,
    secondary = Indigo300,
    background = Gray800,
    surface = Gray800,
    onPrimary = Gray100,
    onSecondary = Gray100,
)

private val LightColorPalette = lightColors(
    primary = Orange400,
    primaryVariant = Orange600,
    secondary = Indigo300,
    background = White,
    surface = Gray100,

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun NaoTimesTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}

@Composable
fun TestOverview() {
    val username = remember {
        mutableStateOf(TextFieldValue())
    }
    val password = remember {
        mutableStateOf(TextFieldValue())
    }
    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Login", style = TextStyle(fontSize = 40.sp))
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            label = { Text(text = "Server ID") },
            value = username.value,
            onValueChange = { username.value = it },
        )
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            label = { Text(text = "Password") },
            value = password.value,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            onValueChange = { password.value = it },
        )

        Spacer(modifier = Modifier.height(20.dp))
        Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
            Button(onClick = { /*TODO*/ }, shape = RoundedCornerShape(8.dp), modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)) {
                Text(text = "Login")
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        ClickableText(
            text = AnnotatedString("Signup"),
            onClick = {
                // ignore
            },
            style = TextStyle(
                fontSize = 14.sp,
                color = Color.Blue
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ThemeOverview() {
    NaoTimesTheme {
        Surface(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(),
            color = MaterialTheme.colors.background
        ) {
            TestOverview()
        }
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun ThemeOverviewDark() {
    NaoTimesTheme {
        Surface(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(),
            color = MaterialTheme.colors.background
        ) {
            TestOverview()
        }
    }
}
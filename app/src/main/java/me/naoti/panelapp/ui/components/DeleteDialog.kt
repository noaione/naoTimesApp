package me.naoti.panelapp.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.naoti.panelapp.ui.theme.NaoTimesTheme
import me.naoti.panelapp.ui.theme.darker


@Composable
fun DeleteDialog(
    title: String = "Are you sure?",
    extraText: String? = null,
    verificationWord: String? = null,
    onDismiss: (() -> Unit)? = null,
    onConfirm: (() -> Unit)? = null,
    enabled: Boolean = true,
) {
    var zaWaurdo by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }
    var allowClicking by remember { mutableStateOf(verificationWord == null) }
    AlertDialog(
        onDismissRequest = {
            if (onDismiss != null && enabled) {
                onDismiss()
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .testTag("DeleteModalDialog"),
        confirmButton = {
            TextButton(
                onClick = {
                    if (onConfirm != null && allowClicking) {
                        onConfirm()
                    }
                },
                enabled = allowClicking && enabled,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    disabledContentColor = MaterialTheme.colorScheme.onErrorContainer.darker(.2f)
                ),
            ) {
                Text(text = "Yes")
            }
        },
        icon = {
            Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete Icon")
        },
        dismissButton = {
            TextButton(
                onClick = {
                    if (onDismiss != null) {
                        onDismiss()
                    }
                },
                enabled = enabled,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    disabledContentColor = MaterialTheme.colorScheme.onErrorContainer.darker(.2f)
                ),
            ) {
                Text(text = "Cancel")
            }
        },
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.sp
            )
        },
//        containerColor = MaterialTheme.colorScheme.errorContainer,
//        titleContentColor = MaterialTheme.colorScheme.onErrorContainer,
//        textContentColor = MaterialTheme.colorScheme.onErrorContainer,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = "This action cannot be undone!")
                if (extraText != null) {
                    Text(
                        text = extraText,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(
                            bottom = 2.dp,
                        )
                    )
                }
                if (verificationWord != null) {
                    Text(text = "Please enter the paraphrase below to confirm!", textAlign = TextAlign.Center)
                    Text(
                        text = verificationWord,
                        style = TextStyle(
                            letterSpacing = 0.sp,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier
                            .padding(top = 6.dp, bottom = 6.dp)
                            .testTag("DeleteDialogParaphrase"),
                        textAlign = TextAlign.Center
                    )
                    OutlinedTextField(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .testTag("DeleteDialogParaphraseInput"),
                        value = zaWaurdo,
                        onValueChange = {
                            zaWaurdo = it
                            allowClicking = verificationWord == it.text.lowercase()
                        },
                        label = { Text("Enter paraphrase") },
                        enabled = enabled,
                        singleLine = true,
                        shape = RoundedCornerShape(6.dp),
                    )
                }

            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun DeleteDialogDefaultPreview() {
    NaoTimesTheme {
        DeleteDialog()
    }
}

@Preview(showBackground = true)
@Composable
private fun DeleteDialogWithExtraPreview() {
    NaoTimesTheme {
        DeleteDialog(extraText = "This will delete episode 1")
    }
}

@Preview(showBackground = true)
@Composable
private fun DeleteDialogVerificationPreview() {
    NaoTimesTheme {
        DeleteDialog(verificationWord = "this-is-a-test")
    }
}

@Preview(showBackground = true)
@Composable
private fun DeleteDialogVerificationExtraPreview() {
    NaoTimesTheme {
        DeleteDialog(verificationWord = "this-is-a-test", extraText = "This will remove episode 1")
    }
}
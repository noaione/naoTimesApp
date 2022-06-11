package me.naoti.panelapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AssignmentBox(type: StatusRole, userName: String?, paddingH: Dp = 4.dp, paddingV: Dp = 0.dp) {
    val colors = getStatusColor(type)
    val corner = RoundedCornerShape(4.dp)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(
                horizontal = paddingH,
                vertical = paddingV
            )
            .background(
                color = colors.bg,
                shape = corner
            )
            .border(
                width = 1.75.dp,
                color = colors.border,
                shape = corner
            )
            .clip(corner)
    ) {
        var txtCnt = type.getFull()
        txtCnt += ": " + (userName ?: "Unknown")
        Text(
            txtCnt,
            textAlign = TextAlign.Center,
            color = colors.text,
            modifier = Modifier.padding(
                horizontal = 4.dp,
                vertical = 2.dp,
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AssignmentBoxPreview() {
    Row(modifier = Modifier.padding(6.dp)) {
        AssignmentBox(type = StatusRole.TL, null)
        AssignmentBox(type = StatusRole.TLC, null)
        AssignmentBox(type = StatusRole.ENC, null)
        AssignmentBox(type = StatusRole.ED, null)
        AssignmentBox(type = StatusRole.TS, null)
        AssignmentBox(type = StatusRole.TM, null)
        AssignmentBox(type = StatusRole.QC, null)
    }
}
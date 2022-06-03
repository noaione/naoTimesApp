package me.naoti.panelapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import me.naoti.panelapp.ui.theme.*

enum class StatusRole {
    TL {
        override fun getShort() = "TL"
    },
    TLC {
        override fun getShort() = "TLC"
    },
    ED {
        override fun getShort() = "Edit"
    },
    ENC {
        override fun getShort() = "Encode"
    },
    TM {
        override fun getShort() = "Timing"
    },
    TS {
        override fun getShort() = "TS"
    },
    QC {
        override fun getShort() = "QC"
    };

    abstract fun getShort(): String;
}

data class ColorBox(val bg: Color, val text: Color, val border: Color)

internal fun getColor(type: StatusRole): ColorBox {
    return when (type) {
        StatusRole.TL -> ColorBox(Red100, Red800, Red200)
        StatusRole.TLC -> ColorBox(Yellow100, Yellow800, Yellow300)
        StatusRole.ED -> ColorBox(Blue100, Blue800, Blue200)
        StatusRole.ENC -> ColorBox(Green100, Green800, Green200)
        StatusRole.TM -> ColorBox(Indigo100, Indigo800, Indigo200)
        StatusRole.TS -> ColorBox(Purple100, Purple800, Purple200)
        StatusRole.QC -> ColorBox(Pink100, Pink800, Pink200)
    }
}

@Composable
fun StatusBox(type: StatusRole, paddingH: Dp = 4.dp, paddingV: Dp = 0.dp) {
    val colors = getColor(type)
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
        Text(
            type.getShort(),
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
fun StatusBoxPreview() {
    Row(modifier = Modifier.padding(6.dp)) {
        StatusBox(type = StatusRole.TL)
        StatusBox(type = StatusRole.TLC)
        StatusBox(type = StatusRole.ENC)
        StatusBox(type = StatusRole.ED)
        StatusBox(type = StatusRole.TS)
        StatusBox(type = StatusRole.TM)
        StatusBox(type = StatusRole.QC)
    }
}
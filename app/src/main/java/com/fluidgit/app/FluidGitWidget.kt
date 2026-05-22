package com.fluidgit.app

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.text.FontWeight
import androidx.glance.unit.ColorProvider

class FluidGitWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = FluidGitWidget()
}

class FluidGitWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            WidgetContent()
        }
    }
}

@Composable
fun WidgetContent() {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(Color(0xE60A0A0F))) // 90% opacity Slate Background
            .padding(16.dp)
    ) {
        Text(
            text = "Fluid Git",
            style = TextStyle(
                color = ColorProvider(Color(0xFF22D3EE)), // Cyan400
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(GlanceModifier.height(8.dp))
        Box(
            modifier = GlanceModifier.fillMaxWidth().background(ColorProvider(Color(0xFF14141A))).padding(8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = GlanceModifier.size(8.dp).background(ColorProvider(Color(0xFF00FF88)))) {}
                Spacer(GlanceModifier.size(8.dp))
                Text("fluid-engine-core", style = TextStyle(color = ColorProvider(Color.White)))
            }
        }
    }
}

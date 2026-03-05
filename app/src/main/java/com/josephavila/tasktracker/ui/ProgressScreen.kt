package com.josephavila.tasktracker.ui

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.josephavila.tasktracker.model.Task
import com.josephavila.tasktracker.model.TaskCategory
import kotlin.math.roundToInt

@Composable
fun ProgressScreen(
    tasks: List<Task>,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val bg = Color(0xFFF7F2EF)

    val categories = listOf(
        TaskCategory.WALKING to "Walking",
        TaskCategory.RUNNING to "Running",
        TaskCategory.MEDITATION to "Meditation",
        TaskCategory.DRINK to "Drink",
    )

    val percentByCategory: List<Pair<String, Int>> = categories.map { (cat, label) ->
        val total = tasks.count { it.category == cat }
        val done = tasks.count { it.category == cat && it.done }
        val pct = if (total == 0) 0 else ((done.toFloat() / total.toFloat()) * 100f).roundToInt()
        label to pct
    }

    Surface(color = bg) {
        Column(
            modifier = Modifier
                .padding(18.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "Your progress\nand insights",
                    fontSize = 22.sp,
                    color = Color(0xFF1E1E1E)
                )

                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF555555))
                }
            }

            Spacer(Modifier.height(18.dp))

            // Bars + points card (lo mismo que enseñas en pantalla)
            ShareCardContent(percentByCategory = percentByCategory, tasks = tasks)

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    // Captura la pantalla actual (ProgressScreen ya está attach a ventana)
                    val activity = context.findActivity() as? ComponentActivity ?: return@Button
                    val bitmap = activity.captureScreenBitmap()
                    shareBitmapImage(context, bitmap, filename = "progress.png")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7A2F))
            ) {
                Text("Share Progress", color = Color.White)
            }
        }
    }
}

@Composable
private fun ShareCardContent(
    percentByCategory: List<Pair<String, Int>>,
    tasks: List<Task>
) {
    // Bars
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        val colors = listOf(
            Color(0xFF4D2E25),
            Color(0xFFB86A1B),
            Color(0xFF8BC34A),
            Color(0xFFE85AAE)
        )

        percentByCategory.forEachIndexed { i, (label, pct) ->
            ProgressBarPill(
                label = label,
                percent = pct,
                fillColor = colors.getOrElse(i) { Color(0xFF4D2E25) }
            )
        }
    }

    Spacer(Modifier.height(16.dp))

    // Points card
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Points Earned", fontSize = 13.sp, color = Color(0xFF1E1E1E))
                    Text("This week", fontSize = 11.sp, color = Color(0xFF9B9B9B))
                }

                val points = tasks.count { it.done } * 100 + tasks.sumOf { it.minutes }
                Text("$points Points", fontSize = 16.sp, color = Color(0xFFB86A1B))
            }

            Spacer(Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MiniStat(label = "Done", value = "${tasks.count { it.done }}")
                MiniStat(label = "Habits", value = "${tasks.size}")
                MiniStat(label = "Time", value = "${tasks.sumOf { it.minutes }}m")
            }
        }
    }
}

@Composable
private fun ProgressBarPill(
    label: String,
    percent: Int,
    fillColor: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .width(58.dp)
                .height(190.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(Color(0xFFECE6E3)),
            contentAlignment = Alignment.BottomCenter
        ) {
            val fillHeight = (190f * (percent.coerceIn(0, 100) / 100f)).dp
            Box(
                modifier = Modifier
                    .width(58.dp)
                    .height(fillHeight)
                    .clip(RoundedCornerShape(30.dp))
                    .background(fillColor),
                contentAlignment = Alignment.Center
            ) {
                Text("$percent%", color = Color.White, fontSize = 12.sp)
            }
        }

        Spacer(Modifier.height(10.dp))
        Text(label, fontSize = 12.sp, color = Color(0xFF1E1E1E))
    }
}

@Composable
private fun MiniStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 10.sp, color = Color(0xFF9B9B9B))
        Spacer(Modifier.height(6.dp))
        Text(value, fontSize = 12.sp, color = Color(0xFF1E1E1E))
    }
}
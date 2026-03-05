package com.josephavila.tasktracker.ui

import android.content.Context
import android.content.Intent
import com.josephavila.tasktracker.model.Task
import com.josephavila.tasktracker.model.TaskCategory
import kotlin.math.roundToInt

fun shareProgressText(context: Context, tasks: List<Task>) {
    val total = tasks.size
    val done = tasks.count { it.done }
    val minutes = tasks.sumOf { it.minutes }

    fun pct(cat: TaskCategory): Int {
        val t = tasks.count { it.category == cat }
        val d = tasks.count { it.category == cat && it.done }
        return if (t == 0) 0 else ((d.toFloat() / t.toFloat()) * 100f).roundToInt()
    }

    val text = buildString {
        appendLine("My habit progress")
        appendLine()
        appendLine("Completed: $done/$total")
        appendLine("Total time: ${minutes} min")
        appendLine()
        appendLine("By category:")
        appendLine("- Walking: ${pct(TaskCategory.WALKING)}%")
        appendLine("- Running: ${pct(TaskCategory.RUNNING)}%")
        appendLine("- Meditation: ${pct(TaskCategory.MEDITATION)}%")
        appendLine("- Drink: ${pct(TaskCategory.DRINK)}%")
        appendLine()
        appendLine("Built with TaskTracker")
    }

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "Share progress"))
}
package com.josephavila.tasktracker.model

data class Task(
    val id: Int,
    val title: String,
    val minutes: Int,
    val done: Boolean = false,

    // UI look
    val streakDays: Int = 3,
    val icon: String = "📝",
    val iconBgHex: Long = 0xFFE7F0FF,

    // For progress screen
    val category: TaskCategory = TaskCategory.OTHER,

    // “Goal date” as millis (API 24 friendly)
    val goalEnabled: Boolean = false,
    val goalDateMillis: Long? = null, // timestamp (00:00) or chosen day
    val goalAmount: Int? = null,

    val repeatDays: Set<DayOfWeekLetter> = emptySet(),
    val remindersEnabled: Boolean = true
)

enum class TaskCategory { WALKING, RUNNING, MEDITATION, DRINK, OTHER }

enum class DayOfWeekLetter { M, T, W, TH, F, S, SU }
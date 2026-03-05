package com.josephavila.tasktracker.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.josephavila.tasktracker.model.Task
import kotlinx.coroutines.flow.first

private val Context.dataStore by preferencesDataStore(name = "tasktracker")

class TaskLocalDataStore(
    private val context: Context,
    private val gson: Gson = Gson()
) {
    private val tasksKey = stringPreferencesKey("tasks_json")

    suspend fun loadTasks(): List<Task> {
        val prefs = context.dataStore.data.first()
        val json = prefs[tasksKey].orEmpty()
        if (json.isBlank()) return emptyList()

        val type = object : TypeToken<List<Task>>() {}.type
        return runCatching { gson.fromJson<List<Task>>(json, type) }.getOrDefault(emptyList())
    }

    suspend fun saveTasks(tasks: List<Task>) {
        val json = gson.toJson(tasks)
        context.dataStore.edit { prefs ->
            prefs[tasksKey] = json
        }
    }

    suspend fun clearAll() {
        context.dataStore.edit { prefs ->
            prefs[tasksKey] = ""
        }
    }
}
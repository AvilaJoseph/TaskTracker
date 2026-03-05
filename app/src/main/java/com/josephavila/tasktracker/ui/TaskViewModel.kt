package com.josephavila.tasktracker.ui

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.josephavila.tasktracker.data.TaskLocalDataStore
import com.josephavila.tasktracker.model.Task
import kotlinx.coroutines.launch

class TaskViewModel(app: Application) : AndroidViewModel(app) {

    private val storage = TaskLocalDataStore(app.applicationContext)

    var tasks = mutableStateListOf<Task>()
        private set

    private var counter = 0

    init {
        viewModelScope.launch {
            val loaded = storage.loadTasks()
            tasks.clear()
            tasks.addAll(loaded)
            counter = (loaded.maxOfOrNull { it.id } ?: -1) + 1
        }
    }

    fun addTask(task: Task) {
        val newTask = task.copy(id = counter++)
        tasks.add(newTask)
        persist()
    }

    fun updateTask(task: Task) {
        val index = tasks.indexOfFirst { it.id == task.id }
        if (index != -1) {
            tasks[index] = task
            persist()
        }
    }

    fun deleteTask(task: Task) {
        tasks.removeAll { it.id == task.id }
        persist()
    }

    fun toggleTask(task: Task) {
        val index = tasks.indexOfFirst { it.id == task.id }
        if (index != -1) {
            tasks[index] = task.copy(done = !task.done)
            persist()
        }
    }

    private fun persist() {
        viewModelScope.launch {
            storage.saveTasks(tasks.toList())
        }
    }

    // Solo si algún día quieres un botón “Reset”
    fun clearAllTasks() {
        tasks.clear()
        counter = 0
        viewModelScope.launch { storage.clearAll() }
    }
}
package com.josephavila.tasktracker.ui

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.josephavila.tasktracker.model.DayOfWeekLetter
import com.josephavila.tasktracker.model.Task
import com.josephavila.tasktracker.model.TaskCategory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<TaskViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { AppRoot(viewModel) } }
    }
}

private enum class Screen { HOME, PROGRESS }

@Composable
private fun AppRoot(viewModel: TaskViewModel) {
    var screen by remember { mutableStateOf(Screen.HOME) }

    when (screen) {
        Screen.HOME -> RoutineScreen(
            viewModel = viewModel,
            onOpenProgress = { screen = Screen.PROGRESS }
        )

        Screen.PROGRESS -> ProgressScreen(
            tasks = viewModel.tasks,
            onClose = { screen = Screen.HOME }
        )
    }
}

@Composable
fun RoutineScreen(
    viewModel: TaskViewModel,
    onOpenProgress: () -> Unit
) {
    var showModal by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<Task?>(null) }

    val bg = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFF7F2EF),
            Color(0xFFF7F2EF),
            Color(0xFFF2D4BF)
        )
    )

    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingTask = null
                    showModal = true
                },
                containerColor = Color(0xFF7A4D2E),
                shape = CircleShape,
                modifier = Modifier.size(58.dp)
            ) {
                Text("+", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bg)
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(top = 18.dp)
            ) {
                HeaderLikeMock(onOpenProgress = onOpenProgress)

                Spacer(modifier = Modifier.height(16.dp))

                ReminderCardLikeMock()

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Daily routine",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF1E1E1E)
                    )

                    Text(
                        "Progress",
                        color = Color(0xFF9B9B9B),
                        fontSize = 12.sp,
                        modifier = Modifier.clickable { onOpenProgress() }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (viewModel.tasks.isEmpty()) {
                    EmptyState(
                        onAdd = {
                            editingTask = null
                            showModal = true
                        }
                    )
                } else {
                    LazyColumn {
                        items(viewModel.tasks) { task ->
                            RoutineItem(
                                task = task,
                                onToggle = { viewModel.toggleTask(task) },
                                onEdit = {
                                    editingTask = task
                                    showModal = true
                                }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(90.dp)) }
                    }
                }
            }
        }
    }

    if (showModal) {
        AddEditHabitModal(
            initial = editingTask,
            onDismiss = { showModal = false },
            onDelete = { t ->
                viewModel.deleteTask(t)
                showModal = false
            },
            onSave = { task ->
                if (editingTask == null) viewModel.addTask(task) else viewModel.updateTask(task)
                showModal = false
            }
        )
    }
}

@Composable
private fun EmptyState(onAdd: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "No habits yet",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1E1E1E)
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Tap the + button to create your first habit.",
                fontSize = 12.sp,
                color = Color(0xFF8A8A8A)
            )
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = onAdd,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7A2F))
            ) {
                Text("Add habit", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun HeaderLikeMock(onOpenProgress: () -> Unit) {
    val cal = Calendar.getInstance()
    val fmt = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.ENGLISH)
    val dateText = fmt.format(cal.time).replaceFirstChar { it.uppercase() }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Morning, Joseph",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E1E1E)
                )
                Text(
                    text = dateText,
                    color = Color(0xFF8A8A8A),
                    fontSize = 13.sp
                )
            }

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFE3D1))
                    .clickable { onOpenProgress() },
                contentAlignment = Alignment.Center
            ) {
                Text("📊", fontSize = 18.sp)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        DayStripLikeMock()
    }
}

@Composable
private fun DayStripLikeMock() {
    val labels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val numbers = listOf(7, 8, 9, 10, 11, 12, 13)
    val selectedIndex = 3

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            labels.forEach { day ->
                Text(day, fontSize = 11.sp, color = Color(0xFF9B9B9B))
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            numbers.forEachIndexed { index, n ->
                val selected = index == selectedIndex
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(if (selected) Color(0xFF1E1E1E) else Color(0xFFF0F0F0)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = n.toString(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (selected) Color.White else Color(0xFF1E1E1E)
                    )
                }
            }
        }
    }
}

@Composable
private fun ReminderCardLikeMock() {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFD8B8)),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Set the reminder",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF1E1E1E)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Never miss your morning routine!",
                    color = Color(0xFF6E6E6E),
                    fontSize = 12.sp
                )
                Spacer(Modifier.height(10.dp))
                Button(
                    onClick = {},
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7A4D2E)),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        horizontal = 16.dp,
                        vertical = 10.dp
                    )
                ) {
                    Text("Set Now", fontSize = 12.sp, color = Color.White)
                }
            }

            Spacer(Modifier.width(12.dp))

            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFFFC89D)),
                contentAlignment = Alignment.Center
            ) {
                Text("🔔", fontSize = 26.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddEditHabitModal(
    initial: Task?,
    onDismiss: () -> Unit,
    onDelete: (Task) -> Unit,
    onSave: (Task) -> Unit
) {
    val ctx = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val isEditing = initial != null

    var name by remember { mutableStateOf(initial?.title ?: "") }
    var minutes by remember { mutableStateOf(initial?.minutes?.toString() ?: "") }

    var category by remember { mutableStateOf(initial?.category ?: TaskCategory.OTHER) }

    var goalEnabled by remember { mutableStateOf(initial?.goalEnabled ?: false) }
    var goalDateMillis by remember { mutableStateOf<Long?>(initial?.goalDateMillis) }
    var goalAmount by remember { mutableStateOf(initial?.goalAmount?.toString() ?: "") }

    var remindersEnabled by remember { mutableStateOf(initial?.remindersEnabled ?: true) }

    val days: List<DayOfWeekLetter> = listOf(
        DayOfWeekLetter.M, DayOfWeekLetter.T, DayOfWeekLetter.W,
        DayOfWeekLetter.TH, DayOfWeekLetter.F, DayOfWeekLetter.S, DayOfWeekLetter.SU
    )
    var repeatDays by remember { mutableStateOf(initial?.repeatDays ?: emptySet()) }

    val goalDateText = goalDateMillis?.let {
        val fmt = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
        fmt.format(it)
    } ?: "Add date"

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFFF7F2EF),
        modifier = Modifier.imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isEditing) "Edit habit" else "New habit",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF555555))
                }
            }

            Spacer(Modifier.height(10.dp))

            Text("Name your habit", fontSize = 12.sp, color = Color(0xFF9B9B9B))
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Morning Meditations") },
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            Text("Minutes", fontSize = 12.sp, color = Color(0xFF9B9B9B))
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = minutes,
                onValueChange = { minutes = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("10") },
                singleLine = true,
                textStyle = TextStyle(fontSize = 14.sp)
            )

            Spacer(Modifier.height(12.dp))

            Text("Category", fontSize = 12.sp, color = Color(0xFF9B9B9B))
            Spacer(Modifier.height(8.dp))
            CategoryRow(category = category, onChange = { category = it })

            Spacer(Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Set a goal", fontSize = 12.sp, color = Color(0xFF9B9B9B))
                Switch(
                    checked = goalEnabled,
                    onCheckedChange = { goalEnabled = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF6B4CAF),
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color(0xFFD8D8D8)
                    )
                )
            }

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GoalChipClickable(
                    enabled = goalEnabled,
                    text = goalDateText,
                    modifier = Modifier.weight(1f)
                ) {
                    val cal = Calendar.getInstance()
                    if (goalDateMillis != null) cal.timeInMillis = goalDateMillis!!

                    DatePickerDialog(
                        ctx,
                        { _, y, m, d ->
                            val chosen = Calendar.getInstance()
                            chosen.set(Calendar.YEAR, y)
                            chosen.set(Calendar.MONTH, m)
                            chosen.set(Calendar.DAY_OF_MONTH, d)
                            chosen.set(Calendar.HOUR_OF_DAY, 0)
                            chosen.set(Calendar.MINUTE, 0)
                            chosen.set(Calendar.SECOND, 0)
                            chosen.set(Calendar.MILLISECOND, 0)
                            goalDateMillis = chosen.timeInMillis
                        },
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }

                GoalAmountChip(
                    enabled = goalEnabled,
                    value = goalAmount,
                    onValueChange = { goalAmount = it },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(16.dp))

            Text("Repeat days", fontSize = 12.sp, color = Color(0xFF9B9B9B))
            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                days.forEach { d ->
                    val selected = repeatDays.contains(d)
                    val label = when (d) {
                        DayOfWeekLetter.M -> "M"
                        DayOfWeekLetter.T -> "T"
                        DayOfWeekLetter.W -> "W"
                        DayOfWeekLetter.TH -> "T"
                        DayOfWeekLetter.F -> "F"
                        DayOfWeekLetter.S -> "S"
                        DayOfWeekLetter.SU -> "S"
                    }

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (selected) Color(0xFF1E1E1E) else Color.White)
                            .clickable {
                                val next = repeatDays.toMutableSet()
                                if (selected) next.remove(d) else next.add(d)
                                repeatDays = next.toSet()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            label,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = if (selected) Color.White else Color(0xFF1E1E1E)
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Get reminders", fontSize = 12.sp, color = Color(0xFF9B9B9B))
                Switch(
                    checked = remindersEnabled,
                    onCheckedChange = { remindersEnabled = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFFFF7A2F),
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color(0xFFD8D8D8)
                    )
                )
            }

            Spacer(Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (isEditing) {
                    Button(
                        onClick = { onDelete(initial!!) },
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E1E1E))
                    ) {
                        Text("Delete", color = Color.White)
                    }
                }

                Button(
                    onClick = {
                        val m = minutes.toIntOrNull() ?: 0
                        val amount = goalAmount.toIntOrNull()

                        val iconAndColor = categoryDefaults(category)

                        onSave(
                            Task(
                                id = initial?.id ?: 0,
                                title = name.trim(),
                                minutes = m.coerceAtLeast(0),
                                done = initial?.done ?: false,
                                streakDays = initial?.streakDays ?: 0,
                                icon = iconAndColor.first,
                                iconBgHex = iconAndColor.second,
                                category = category,
                                goalEnabled = goalEnabled,
                                goalDateMillis = if (goalEnabled) goalDateMillis else null,
                                goalAmount = if (goalEnabled) amount else null,
                                repeatDays = repeatDays,
                                remindersEnabled = remindersEnabled
                            )
                        )
                    },
                    enabled = name.isNotBlank(),
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7A2F))
                ) {
                    Text(if (isEditing) "Save changes" else "Save habit", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(18.dp))
        }
    }
}

private fun categoryDefaults(category: TaskCategory): Pair<String, Long> =
    when (category) {
        TaskCategory.WALKING -> "🚶" to 0xFFFFE3D1
        TaskCategory.RUNNING -> "🏃" to 0xFFFFE3D1
        TaskCategory.MEDITATION -> "🧘" to 0xFFDFF4E5
        TaskCategory.DRINK -> "🥤" to 0xFFFFE3D1
        TaskCategory.OTHER -> "📝" to 0xFFE7F0FF
    }

@Composable
private fun CategoryRow(category: TaskCategory, onChange: (TaskCategory) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
        CategoryChip("Walk", category == TaskCategory.WALKING) { onChange(TaskCategory.WALKING) }
        CategoryChip("Run", category == TaskCategory.RUNNING) { onChange(TaskCategory.RUNNING) }
        CategoryChip("Meditate", category == TaskCategory.MEDITATION) { onChange(TaskCategory.MEDITATION) }
        CategoryChip("Drink", category == TaskCategory.DRINK) { onChange(TaskCategory.DRINK) }
    }
}

@Composable
private fun CategoryChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) Color(0xFF1E1E1E) else Color.White)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, fontSize = 12.sp, color = if (selected) Color.White else Color(0xFF1E1E1E))
    }
}

@Composable
private fun GoalChipClickable(
    enabled: Boolean,
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.height(46.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp)
                .let { base -> if (enabled) base.then(Modifier.clickable { onClick() }) else base },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text,
                fontSize = 12.sp,
                color = if (enabled) Color(0xFF6B6B6B) else Color(0xFFB5B5B5)
            )
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF0F0F0))
            )
        }
    }
}

@Composable
private fun GoalAmountChip(
    enabled: Boolean,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        modifier = modifier.height(54.dp),
        placeholder = { Text("Add amount") },
        singleLine = true,
        textStyle = TextStyle(fontSize = 12.sp)
    )
}
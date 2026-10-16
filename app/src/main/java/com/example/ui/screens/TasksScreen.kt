package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Language
import com.example.model.OfficeBearerTask
import com.example.model.TaskPriority
import com.example.model.TaskStatus
import com.example.ui.theme.*
import com.example.util.AppStrings
import com.example.viewmodel.AppViewModel

@Composable
fun TasksScreen(
    viewModel: AppViewModel,
    language: Language,
    tasks: List<OfficeBearerTask>
) {
    var selectedFilter by remember { mutableStateOf<TaskStatus?>(null) }
    var taskToSubmitEvidence by remember { mutableStateOf<OfficeBearerTask?>(null) }
    var evidenceNote by remember { mutableStateOf("") }
    var showTaskSubmittedDialog by remember { mutableStateOf(false) }
    var lastSubmittedTaskTitle by remember { mutableStateOf("") }

    val filtered = if (selectedFilter == null) tasks else tasks.filter { it.status == selectedFilter }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(RathoreBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = AppStrings.get("tasks_title", language),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = RathoreGreen
                    )
                    Text(
                        text = "जिम्मेदारियाँ, समयसीमा एवं फील्ड रिपोर्ट",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = RathoreGreenLight
                ) {
                    Text(
                        text = "${tasks.count { it.status == TaskStatus.COMPLETED }}/${tasks.size} पूर्ण",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = RathoreGreen,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Filter chips (All, Pending, In Progress, Completed)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterTab(
                    title = "सभी (${tasks.size})",
                    isSelected = selectedFilter == null,
                    onClick = { selectedFilter = null }
                )
                FilterTab(
                    title = "प्रगति पर (${tasks.count { it.status == TaskStatus.IN_PROGRESS }})",
                    isSelected = selectedFilter == TaskStatus.IN_PROGRESS,
                    onClick = { selectedFilter = TaskStatus.IN_PROGRESS }
                )
                FilterTab(
                    title = "लंबित (${tasks.count { it.status == TaskStatus.PENDING }})",
                    isSelected = selectedFilter == TaskStatus.PENDING,
                    onClick = { selectedFilter = TaskStatus.PENDING }
                )
                FilterTab(
                    title = "पूर्ण (${tasks.count { it.status == TaskStatus.COMPLETED }})",
                    isSelected = selectedFilter == TaskStatus.COMPLETED,
                    onClick = { selectedFilter = TaskStatus.COMPLETED }
                )
            }
        }

        // Task Items or Empty State
        if (filtered.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderStroke)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.AssignmentLate,
                            contentDescription = null,
                            tint = TextTertiary,
                            modifier = Modifier.size(52.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (language == Language.HINDI) "कोई टास्क उपलब्ध नहीं है" else "No Tasks Available",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (language == Language.HINDI)
                                "वर्तमान में इस श्रेणी में कोई सक्रिय दायित्व या टास्क आवंटित नहीं है।"
                            else
                                "Currently there are no active tasks assigned in this category.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(filtered) { task ->
                TaskCard(
                    task = task,
                    language = language,
                    onAccept = { viewModel.updateTaskStatus(task.id, TaskStatus.IN_PROGRESS) },
                    onStart = { viewModel.updateTaskStatus(task.id, TaskStatus.IN_PROGRESS) },
                    onComplete = { taskToSubmitEvidence = task }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(70.dp))
        }
    }

    // Evidence Submission Dialog (PDF page 22 & 32)
    if (taskToSubmitEvidence != null) {
        val task = taskToSubmitEvidence!!
        AlertDialog(
            onDismissRequest = { taskToSubmitEvidence = null },
            title = {
                Text("कार्य पूर्णता रिपोर्ट एवं प्रमाण", fontWeight = FontWeight.Bold, color = RathoreGreen)
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = if (language == Language.HINDI) task.titleHi else task.titleEn,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = evidenceNote,
                        onValueChange = { evidenceNote = it },
                        label = { Text("कार्य विवरण / निरीक्षण नोट लिखें...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Photo attachment mock
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { /* attach photo */ },
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF1F5F9),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderStroke)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = RathoreOrange)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("मौके की फोटो जोड़ें (1 Photo संलग्न)", style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        lastSubmittedTaskTitle = if (language == Language.HINDI) task.titleHi else task.titleEn
                        viewModel.updateTaskStatus(task.id, TaskStatus.COMPLETED, evidenceNote)
                        taskToSubmitEvidence = null
                        evidenceNote = ""
                        showTaskSubmittedDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RathoreGreen)
                ) {
                    Text("जमा करें (Submit Complete)", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { taskToSubmitEvidence = null }) {
                    Text("रद्द करें", color = TextSecondary)
                }
            }
        )
    }

    // Task Update Submitted Feedback Dialog
    if (showTaskSubmittedDialog) {
        AlertDialog(
            onDismissRequest = { showTaskSubmittedDialog = false },
            icon = {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = RathoreGreen,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = "टास्क रिपोर्ट सफलतापूर्वक प्रेषित!",
                    fontWeight = FontWeight.Bold,
                    color = RathoreGreen,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "कार्य: $lastSubmittedTaskTitle",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "आपकी कार्य प्रगति रिपोर्ट व संलग्न प्रमाण जिला नियंत्रण कक्ष व प्रदेश नेतृत्व को भेज दी गई है।",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showTaskSubmittedDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = RathoreGreen)
                ) {
                    Text("ठीक है (Done)", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
private fun FilterTab(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) RathoreGreen else Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) RathoreGreen else BorderStroke),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 11.sp
            ),
            color = if (isSelected) Color.White else TextPrimary,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun TaskCard(
    task: OfficeBearerTask,
    language: Language,
    onAccept: () -> Unit,
    onStart: () -> Unit,
    onComplete: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderStroke)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Priority & Status Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Priority
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = when (task.priority) {
                        TaskPriority.HIGH -> Color(0xFFFFEBEE)
                        TaskPriority.MEDIUM -> Color(0xFFFFF3E0)
                        TaskPriority.LOW -> Color(0xFFE8F5E9)
                    }
                ) {
                    Text(
                        text = "प्राथमिकता: ${task.priority.titleHi}",
                        color = when (task.priority) {
                            TaskPriority.HIGH -> Color(0xFFD32F2F)
                            TaskPriority.MEDIUM -> RathoreOrange
                            TaskPriority.LOW -> RathoreGreen
                        },
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                // Status Badge
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when {
                        task.isOverdue -> Color(0xFFFFEBEE)
                        task.status == TaskStatus.PENDING -> Color(0xFFF1F5F9)
                        task.status == TaskStatus.ACCEPTED || task.status == TaskStatus.IN_PROGRESS -> Color(0xFFFFF3E0)
                        task.status == TaskStatus.COMPLETED -> Color(0xFFE8F5E9)
                        else -> Color(0xFFF1F5F9)
                    }
                ) {
                    Text(
                        text = if (task.isOverdue) "समयसीमा समाप्त" else task.status.titleHi,
                        color = when {
                            task.isOverdue -> Color(0xFFD32F2F)
                            task.status == TaskStatus.PENDING -> TextSecondary
                            task.status == TaskStatus.ACCEPTED || task.status == TaskStatus.IN_PROGRESS -> RathoreOrange
                            task.status == TaskStatus.COMPLETED -> RathoreGreen
                            else -> TextSecondary
                        },
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title
            Text(
                text = if (language == Language.HINDI) task.titleHi else task.titleEn,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )

            // Description
            Text(
                text = if (language == Language.HINDI) task.descriptionHi else task.descriptionEn,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            // Assigned by & Due Date
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("द्वारा: ${task.assignedBy}", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                Text("अंतिम तिथि: ${task.dueDateHi}", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = RathoreOrange)
            }

            // Evidence Note if completed
            if (task.evidenceSubmitted && task.evidenceNote.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFF9FAFB),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = RathoreGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "प्रमाण: ${task.evidenceNote}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons based on status
            when (task.status) {
                TaskStatus.PENDING -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onAccept,
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .testTag("btn_accept_task_${task.id}"),
                            colors = ButtonDefaults.buttonColors(containerColor = RathoreGreen),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(AppStrings.get("task_accept", language), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
                TaskStatus.ACCEPTED, TaskStatus.IN_PROGRESS -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onComplete,
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .testTag("btn_complete_task_${task.id}"),
                            colors = ButtonDefaults.buttonColors(containerColor = RathoreOrange),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(AppStrings.get("task_complete_action", language), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
                TaskStatus.COMPLETED -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.TaskAlt, contentDescription = null, tint = RathoreGreen, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("सत्यापित एवं स्वीकृत (Verified)", color = RathoreGreen, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
        }
    }
}

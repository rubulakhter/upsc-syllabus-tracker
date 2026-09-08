package com.tracker.syllabus.ui.subject

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tracker.syllabus.data.model.Topic
import com.tracker.syllabus.ui.theme.Gold
import com.tracker.syllabus.ui.theme.Success
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectDetailScreen(
    viewModel: SubjectViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val subject by viewModel.subject.collectAsState()
    val topics by viewModel.topics.collectAsState()
    val customInterval by viewModel.customInterval.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var showAddTopicDialog by remember { mutableStateOf(false) }
    var topicToEdit by remember { mutableStateOf<Topic?>(null) }
    var topicToDelete by remember { mutableStateOf<Topic?>(null) }

    val subjectProgress = remember(topics) {
        if (topics.isEmpty()) 0f
        else (topics.count { it.completed }.toFloat() / topics.size) * 100f
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = subject?.name ?: "Loading...",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        if (subject != null && subject?.description?.isNotEmpty() == true) {
                            Text(
                                text = subject?.description ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Add Topic action button
                    IconButton(onClick = { showAddTopicDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Topic")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddTopicDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Topic")
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Header subject progress bar
                LinearProgressIndicator(
                    progress = subjectProgress / 100f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                )

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(topics) { topic ->
                        TopicItemCard(
                            topic = topic,
                            customInterval = customInterval,
                            onMarkRead = {
                                viewModel.markTopicReadToday(topic.id)
                            },
                            onUndoLastRead = {
                                viewModel.undoLastRead(topic.id)
                            },
                            onEditTopic = {
                                topicToEdit = topic
                            },
                            onDeleteTopic = {
                                topicToDelete = topic
                            }
                        )
                    }
                }
            }
        }
    }

    // Add Topic Dialog
    if (showAddTopicDialog) {
        var title by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddTopicDialog = false },
            title = { Text("Add Topic", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Topic Title") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            viewModel.addTopic(title)
                            showAddTopicDialog = false
                        }
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddTopicDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Edit Topic Dialog
    topicToEdit?.let { topic ->
        var title by remember { mutableStateOf(topic.title) }

        AlertDialog(
            onDismissRequest = { topicToEdit = null },
            title = { Text("Edit Topic", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Topic Title") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            viewModel.editTopic(topic.id, title)
                            topicToEdit = null
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { topicToEdit = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Delete Topic Dialog
    topicToDelete?.let { topic ->
        AlertDialog(
            onDismissRequest = { topicToDelete = null },
            title = { Text("Delete Topic", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete '${topic.title}'? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteTopic(topic.id)
                        topicToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { topicToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun TopicItemCard(
    topic: Topic,
    customInterval: Int,
    onMarkRead: () -> Unit,
    onUndoLastRead: () -> Unit,
    onEditTopic: () -> Unit,
    onDeleteTopic: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    val nextDueText = remember(topic, customInterval) {
        val nextDue = topic.getNextDueDate(customInterval)
        if (nextDue != null) {
            val date = Instant.ofEpochMilli(nextDue)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            
            val uniqueDays = if (topic.revisionDates.isNullOrEmpty() && topic.revisionCount > 0) {
                topic.revisionCount
            } else {
                topic.revisionDates?.map { timestamp ->
                    Instant.ofEpochMilli(timestamp)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                }?.distinct()?.size ?: 0
            }

            val label = when (uniqueDays) {
                1 -> "Day 4"
                2 -> "Day 7"
                3 -> "Day 30"
                else -> "Day ${30 + (uniqueDays - 3) * customInterval}"
            }
            "Next: $label (${date.format(DateTimeFormatter.ofPattern("dd MMM"))})"
        } else {
            "Not Read Yet"
        }
    }

    val isDue = remember(topic, customInterval) {
        topic.isDueForRevision(customInterval)
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Completed Check indicator
                if (topic.completed) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Completed",
                        tint = Success,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                } else {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = topic.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Revision Count Badge
                        SuggestionChip(
                            onClick = {},
                            label = { Text("Revisions: ${topic.revisionCount}") },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                labelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            ),
                            border = null,
                            modifier = Modifier.height(28.dp)
                        )

                        // Next Due or Revision Status Badge
                        val badgeColor = when {
                            topic.revisionCount == 0 -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            isDue -> Gold.copy(alpha = 0.2f)
                            else -> Success.copy(alpha = 0.15f)
                        }
                        val badgeTextColor = when {
                            topic.revisionCount == 0 -> MaterialTheme.colorScheme.primary
                            isDue -> Gold
                            else -> Success
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(badgeColor)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (isDue) "DUE FOR REVISION" else nextDueText,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = badgeTextColor
                            )
                        }
                    }
                }
            }

            // Expanded view containing read details, edit, delete, and Read Today button
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, start = 8.dp, end = 8.dp)
                ) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                    Spacer(modifier = Modifier.height(12.dp))

                    if (topic.revisionDates != null && topic.revisionDates.isNotEmpty()) {
                        Text(
                            text = "Revision History:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        topic.revisionDates.forEachIndexed { index, timestamp ->
                            val dateStr = Instant.ofEpochMilli(timestamp)
                                .atZone(ZoneId.systemDefault())
                                .format(DateTimeFormatter.ofPattern("dd MMM yyyy 'at' hh:mm a"))
                            Text(
                                text = "Read ${index + 1}: $dateStr",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val buttonText = "Read Today"
                        val buttonColor = if (topic.revisionCount == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                        val buttonIcon = if (topic.revisionCount == 0) Icons.Default.Check else Icons.Default.Refresh

                        Button(
                            onClick = onMarkRead,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = buttonColor
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                        ) {
                            Icon(buttonIcon, contentDescription = "Action")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = buttonText,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        // Undo Button (Only visible if the topic has been read at least once)
                        if (topic.revisionCount > 0) {
                            OutlinedButton(
                                onClick = onUndoLastRead,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.error
                                ),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f)),
                                modifier = Modifier.height(44.dp)
                            ) {
                                Text(
                                    text = "Undo",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Edit Button
                        OutlinedButton(
                            onClick = onEditTopic,
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Topic Title",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Delete Button
                        OutlinedButton(
                            onClick = onDeleteTopic,
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(0.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.2f)),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Topic"
                            )
                        }
                    }
                }
            }
        }
    }
}

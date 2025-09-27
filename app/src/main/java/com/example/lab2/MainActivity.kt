package com.example.lab2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lab2.ui.theme.Lab2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab2Theme {
                ListScreen()
            }
        }
    }
}

@Composable
fun ListScreen() {
    var items by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedIndices by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var name by remember { mutableStateOf("") }
    val isLandscape = isLandscapeOrientation()
    var currentLanguage by remember { mutableStateOf(if (isLandscape) "en" else "ru") }

    // Меняем язык при смене ориентации
    LaunchedEffect(isLandscape) {
        currentLanguage = if (isLandscape) "en" else "ru"
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CustomTopAppBar(currentLanguage = currentLanguage)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Поле для ввода имени с кнопкой добавления
            NameInputFieldWithButton(
                name = name,
                onNameChange = { name = it },
                onAddItem = {
                    if (name.isNotBlank()) {
                        items = items + name
                        name = "" // Очищаем поле после добавления
                    }
                },
                currentLanguage = currentLanguage,
                modifier = Modifier.fillMaxWidth()
            )

            // Квадрат с элементами массива
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (items.isEmpty()) {
                    // Сообщение о пустом списке
                    EmptyListMessage(
                        currentLanguage = currentLanguage,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    ItemsGrid(
                        items = items,
                        selectedIndices = selectedIndices,
                        onItemClick = { index ->
                            selectedIndices = if (selectedIndices.contains(index)) {
                                selectedIndices - index
                            } else {
                                selectedIndices + index
                            }
                        },
                        currentLanguage = currentLanguage,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Вертикальные кнопки (только если есть элементы)
            if (items.isNotEmpty()) {
                VerticalButtons(
                    onSelectAll = {
                        selectedIndices = items.indices.toSet()
                    },
                    onClearSelection = {
                        selectedIndices = emptySet()
                    },
                    onSelectEven = {
                        selectedIndices = items.indices.filter { it % 2 == 0 }.toSet()
                    },
                    currentLanguage = currentLanguage,
                    modifier = Modifier.fillMaxWidth()
                )

                // Информация о выборе
                SelectedInfo(
                    selectedCount = selectedIndices.size,
                    currentLanguage = currentLanguage,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun NameInputFieldWithButton(
    name: String,
    onNameChange: (String) -> Unit,
    onAddItem: () -> Unit,
    currentLanguage: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = if (currentLanguage == "ru") "Имя элемента" else "Item name",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(if (currentLanguage == "ru") "Введите имя элемента" else "Enter item name")
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                )
            )

            Button(
                onClick = onAddItem,
                enabled = name.isNotBlank(),
                modifier = Modifier.height(56.dp)
            ) {
                Text(
                    text = if (currentLanguage == "ru") "Добавить" else "Add",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun EmptyListMessage(
    currentLanguage: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (currentLanguage == "ru") "📝" else "📝",
                style = MaterialTheme.typography.displayMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (currentLanguage == "ru") "Список пуст" else "List is empty",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (currentLanguage == "ru")
                    "Добавьте элементы с помощью поля выше"
                else
                    "Add items using the field above",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ItemsGrid(
    items: List<String>,
    selectedIndices: Set<Int>,
    onItemClick: (Int) -> Unit,
    currentLanguage: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = ButtonDefaults.outlinedButtonBorder
    ) {
        val columns = if (isLandscapeOrientation()) 4 else 3

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(items.chunked(columns)) { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    rowItems.forEachIndexed { rowIndex, item ->
                        val index = items.indexOf(item)
                        Box(modifier = Modifier.weight(1f)) {
                            GridItem(
                                item = item,
                                index = index,
                                isSelected = selectedIndices.contains(index),
                                onClick = { onItemClick(index) },
                                currentLanguage = currentLanguage
                            )
                        }
                    }
                    // Добавляем пустые ячейки для выравнивания последней строки
                    if (rowItems.size < columns) {
                        repeat(columns - rowItems.size) {
                            Box(modifier = Modifier.weight(1f)) {
                                Spacer(modifier = Modifier.aspectRatio(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GridItem(
    item: String,
    index: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    currentLanguage: String
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .aspectRatio(1f)
            .padding(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (currentLanguage == "ru") "Эл." else "Item",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = item.take(10), // Ограничиваем длину имени для отображения
                style = MaterialTheme.typography.titleMedium,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                maxLines = 2,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            Text(
                text = "[$index]",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun VerticalButtons(
    onSelectAll: () -> Unit,
    onClearSelection: () -> Unit,
    onSelectEven: () -> Unit,
    currentLanguage: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Кнопка 1 - Выбрать все
        Button(
            onClick = onSelectAll,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = if (currentLanguage == "ru") "Кнопка 1: Выбрать все" else "Button 1: Select All",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        // Кнопка 2 - Сбросить выбор
        Button(
            onClick = onClearSelection,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Text(
                text = if (currentLanguage == "ru") "Кнопка 2: Сбросить выбор" else "Button 2: Clear Selection",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        // Кнопка 3 - Выбрать четные
        Button(
            onClick = onSelectEven,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiary
            )
        ) {
            Text(
                text = if (currentLanguage == "ru") "Кнопка 3: Выбрать четные" else "Button 3: Select Even",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun SelectedInfo(
    selectedCount: Int,
    currentLanguage: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = if (currentLanguage == "ru") "Информация о выборе" else "Selection Info",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (currentLanguage == "ru")
                    "Выбрано элементов: $selectedCount"
                else
                    "Selected items: $selectedCount",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun CustomTopAppBar(currentLanguage: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        color = MaterialTheme.colorScheme.primary,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Lab2 - Динамический список",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                text = if (currentLanguage == "ru") "Русский" else "English",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
            )
        }
    }
}

// Функция для определения ландшафтной ориентации
@Composable
fun isLandscapeOrientation(): Boolean {
    val configuration = LocalContext.current.resources.configuration
    return configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
}

@Preview(showBackground = true)
@Composable
fun ListScreenPreview() {
    Lab2Theme {
        ListScreen()
    }
}
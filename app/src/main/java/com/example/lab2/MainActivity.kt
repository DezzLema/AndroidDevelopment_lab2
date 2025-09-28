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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
        if (isLandscape) {
            // Ландшафтный режим
            LandscapeLayout(
                items = items,
                selectedIndices = selectedIndices,
                onItemClick = { index ->
                    selectedIndices = if (selectedIndices.contains(index)) {
                        selectedIndices - index
                    } else {
                        selectedIndices + index
                    }
                },
                name = name,
                onNameChange = { name = it },
                onAddItem = {
                    if (name.isNotBlank()) {
                        items = items + name
                        name = ""
                    }
                },
                onSelectAll = { selectedIndices = items.indices.toSet() },
                onClearSelection = { selectedIndices = emptySet() },
                onSelectEven = { selectedIndices = items.indices.filter { it % 2 == 0 }.toSet() },
                currentLanguage = currentLanguage,
                innerPadding = innerPadding
            )
        } else {
            // Портретный режим
            PortraitLayout(
                items = items,
                selectedIndices = selectedIndices,
                onItemClick = { index ->
                    selectedIndices = if (selectedIndices.contains(index)) {
                        selectedIndices - index
                    } else {
                        selectedIndices + index
                    }
                },
                name = name,
                onNameChange = { name = it },
                onAddItem = {
                    if (name.isNotBlank()) {
                        items = items + name
                        name = ""
                    }
                },
                onSelectAll = { selectedIndices = items.indices.toSet() },
                onClearSelection = { selectedIndices = emptySet() },
                onSelectEven = { selectedIndices = items.indices.filter { it % 2 == 0 }.toSet() },
                currentLanguage = currentLanguage,
                innerPadding = innerPadding
            )
        }
    }
}

@Composable
fun PortraitLayout(
    items: List<String>,
    selectedIndices: Set<Int>,
    onItemClick: (Int) -> Unit,
    name: String,
    onNameChange: (String) -> Unit,
    onAddItem: () -> Unit,
    onSelectAll: () -> Unit,
    onClearSelection: () -> Unit,
    onSelectEven: () -> Unit,
    currentLanguage: String,
    innerPadding: PaddingValues
) {
    Column(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Список элементов
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (items.isEmpty()) {
                EmptyListMessage(
                    currentLanguage = currentLanguage,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                ItemsGrid(
                    items = items,
                    selectedIndices = selectedIndices,
                    onItemClick = onItemClick,
                    currentLanguage = currentLanguage,
                    modifier = Modifier.fillMaxSize(),
                    columns = 3
                )
            }
        }

        // Поле для ввода имени с кнопкой добавления (знак "+")
        NameInputFieldWithAddButton(
            name = name,
            onNameChange = onNameChange,
            onAddItem = onAddItem,
            currentLanguage = currentLanguage,
            modifier = Modifier.fillMaxWidth()
        )

        // Вертикальные кнопки (показываются всегда)
        VerticalButtons(
            onSelectAll = onSelectAll,
            onClearSelection = onClearSelection,
            onSelectEven = onSelectEven,
            currentLanguage = currentLanguage,
            modifier = Modifier.fillMaxWidth(),
            items = items // Передаем список для управления состоянием кнопок
        )

        // Информация о выборе (показывается всегда)
        SelectedInfo(
            selectedCount = selectedIndices.size,
            currentLanguage = currentLanguage,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun LandscapeLayout(
    items: List<String>,
    selectedIndices: Set<Int>,
    onItemClick: (Int) -> Unit,
    name: String,
    onNameChange: (String) -> Unit,
    onAddItem: () -> Unit,
    onSelectAll: () -> Unit,
    onClearSelection: () -> Unit,
    onSelectEven: () -> Unit,
    currentLanguage: String,
    innerPadding: PaddingValues
) {
    Column(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Горизонтальная компоновка: список слева, кнопки справа
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Список элементов (занимает 70% ширины)
            Box(
                modifier = Modifier
                    .weight(0.7f)
                    .fillMaxHeight()
            ) {
                if (items.isEmpty()) {
                    EmptyListMessage(
                        currentLanguage = currentLanguage,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    ItemsGrid(
                        items = items,
                        selectedIndices = selectedIndices,
                        onItemClick = onItemClick,
                        currentLanguage = currentLanguage,
                        modifier = Modifier.fillMaxSize(),
                        columns = 4
                    )
                }
            }

            // Вертикальные кнопки справа (занимают 30% ширины, показываются всегда)
            Column(
                modifier = Modifier
                    .weight(0.3f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                VerticalButtons(
                    onSelectAll = onSelectAll,
                    onClearSelection = onClearSelection,
                    onSelectEven = onSelectEven,
                    currentLanguage = currentLanguage,
                    modifier = Modifier.fillMaxWidth(),
                    items = items // Передаем список для управления состоянием кнопок
                )

                Spacer(modifier = Modifier.height(8.dp))

                SelectedInfo(
                    selectedCount = selectedIndices.size,
                    currentLanguage = currentLanguage,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Поле для ввода имени с кнопкой добавления (внизу)
        NameInputFieldWithTextButton(
            name = name,
            onNameChange = onNameChange,
            onAddItem = onAddItem,
            currentLanguage = currentLanguage,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
@Composable
fun NameInputFieldWithAddButton(
    name: String,
    onNameChange: (String) -> Unit,
    onAddItem: () -> Unit,
    currentLanguage: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = if (currentLanguage == "ru") "Имя" else "Name",
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

            // Кнопка с иконкой "+"
            IconButton(
                onClick = onAddItem,
                enabled = name.isNotBlank(),
                modifier = Modifier
                    .size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = if (currentLanguage == "ru") "Добавить" else "Add",
                    modifier = Modifier.size(32.dp),
                    tint = if (name.isNotBlank()) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    }
                )
            }
        }
    }
}

@Composable
fun NameInputFieldWithTextButton(
    name: String,
    onNameChange: (String) -> Unit,
    onAddItem: () -> Unit,
    currentLanguage: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = if (currentLanguage == "ru") "Имя" else "Name",
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
                    "Добавьте элементы с помощью поля ниже"
                else
                    "Add items using the field below",
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
    modifier: Modifier = Modifier,
    columns: Int = 3
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = ButtonDefaults.outlinedButtonBorder
    ) {
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
                text = item.take(10),
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
    modifier: Modifier = Modifier,
    items: List<String> = emptyList() // Добавляем параметр для проверки состояния
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Кнопка 1 - Выбрать все (активна только когда есть элементы)
        Button(
            onClick = onSelectAll,
            modifier = Modifier.fillMaxWidth(),
            enabled = items.isNotEmpty(), // Делаем активной только при наличии элементов
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = if (currentLanguage == "ru") "Кнопка 1: Выбрать все" else "Button 1: Select All",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        // Кнопка 2 - Сбросить выбор (активна только когда есть элементы)
        Button(
            onClick = onClearSelection,
            modifier = Modifier.fillMaxWidth(),
            enabled = items.isNotEmpty(), // Делаем активной только при наличии элементов
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Text(
                text = if (currentLanguage == "ru") "Кнопка 2: Сбросить выбор" else "Button 2: Clear Selection",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        // Кнопка 3 - Выбрать четные (активна только когда есть элементы)
        Button(
            onClick = onSelectEven,
            modifier = Modifier.fillMaxWidth(),
            enabled = items.isNotEmpty(), // Делаем активной только при наличии элементов
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

@Preview(showBackground = true, widthDp = 800, heightDp = 400)
@Composable
fun ListScreenLandscapePreview() {
    Lab2Theme {
        ListScreen()
    }
}
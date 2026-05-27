package com.closify.myapplication.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.closify.myapplication.ui.components.ClosifyLogo
import com.closify.myapplication.ui.components.SelectableChip
import com.closify.myapplication.ui.theme.ClosifyTheme
import com.closify.myapplication.ui.theme.LilaPrimary
import com.closify.myapplication.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(),
    onNavigateToEditProfile: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val fontScale by viewModel.fontScale.collectAsState()
    val language by viewModel.language.collectAsState()

    // Función auxiliar para escalar el tamaño de fuente
    fun getScaledSize(baseSize: Int): androidx.compose.ui.unit.TextUnit {
        val multiplier = when (fontScale) {
            0f -> 0.8f
            1f -> 1.0f
            2f -> 1.2f
            3f -> 1.4f
            else -> 1.0f
        }
        return (baseSize * multiplier).sp
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        ClosifyLogo(size = 48.dp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    // Espaciador para centrar el logo ya que navigationIcon ocupa espacio
                    Spacer(modifier = Modifier.size(48.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Configuración",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = getScaledSize(26)
                ),
                color = Color(0xFF333333)
            )

            Spacer(modifier = Modifier.height(24.dp)) // Reducido de 32 a 24

            GeneralSettingsCard(
                isDarkMode = isDarkMode,
                onDarkModeChange = { viewModel.toggleDarkMode(it) },
                fontScale = fontScale,
                onFontScaleChange = { viewModel.updateFontScale(it) },
                language = language,
                onLanguageChange = { viewModel.updateLanguage(it) },
                getScaledSize = ::getScaledSize
            )

            Spacer(modifier = Modifier.height(20.dp)) // Reducido de 32 a 20 para subir los botones

            SettingsActionButton(
                text = "Editar Perfil", 
                onClick = onNavigateToEditProfile,
                fontSize = getScaledSize(18)
            )
            Spacer(modifier = Modifier.height(12.dp)) // Reducido de 16 a 12
            SettingsActionButton(
                text = "Seguridad", 
                onClick = { },
                fontSize = getScaledSize(18)
            )
            Spacer(modifier = Modifier.height(12.dp)) // Reducido de 16 a 12
            SettingsActionButton(
                text = "Notificaciones", 
                onClick = { },
                fontSize = getScaledSize(18)
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun GeneralSettingsCard(
    isDarkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    fontScale: Float,
    onFontScaleChange: (Float) -> Unit,
    language: String,
    onLanguageChange: (String) -> Unit,
    getScaledSize: (Int) -> androidx.compose.ui.unit.TextUnit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "GENERAL",
                style = MaterialTheme.typography.labelLarge.copy(
                    letterSpacing = 1.sp,
                    fontSize = getScaledSize(12)
                ),
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Modo Oscuro
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Modo Oscuro", 
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = getScaledSize(16))
                )
                Switch(
                    checked = isDarkMode,
                    onCheckedChange = onDarkModeChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = LilaPrimary
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tamaño de fuente
            Text(
                text = "Tamaño de fuente", 
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = getScaledSize(16))
            )
            Slider(
                value = fontScale,
                onValueChange = onFontScaleChange,
                valueRange = 0f..3f,
                steps = 2,
                colors = SliderDefaults.colors(
                    thumbColor = LilaPrimary,
                    activeTrackColor = LilaPrimary.copy(alpha = 0.3f),
                    inactiveTrackColor = LilaPrimary.copy(alpha = 0.1f)
                )
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Pequeño", 
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = getScaledSize(12)), 
                    color = Color.Gray
                )
                Text(
                    text = "Grande", 
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = getScaledSize(12)), 
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Idioma
            Text(
                text = "Idioma", 
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = getScaledSize(16))
            )
            Spacer(modifier = Modifier.height(12.dp))
            LanguageSelector(selectedLanguage = language, onLanguageSelected = onLanguageChange)
        }
    }
}

@Composable
fun LanguageSelector(selectedLanguage: String, onLanguageSelected: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf("ESPAÑOL", "ENGLISH").forEach { lang ->
            SelectableChip(
                label = lang,
                selected = selectedLanguage == lang,
                onClick = { onLanguageSelected(lang) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun SettingsActionButton(
    text: String, 
    onClick: () -> Unit,
    fontSize: androidx.compose.ui.unit.TextUnit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = fontSize
                ),
                color = Color(0xFF444444)
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = LilaPrimary,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SettingsScreenPreview() {
    ClosifyTheme {
        SettingsScreen()
    }
}

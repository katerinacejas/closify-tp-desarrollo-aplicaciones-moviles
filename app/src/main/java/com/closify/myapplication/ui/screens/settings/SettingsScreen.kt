package com.closify.myapplication.ui.screens.settings

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import com.closify.myapplication.ui.components.ClosifyTopBar
import com.closify.myapplication.ui.components.SelectableChip
import com.closify.myapplication.ui.theme.ClosifyTheme
import com.closify.myapplication.ui.theme.LilaPrimary
import com.closify.myapplication.ui.viewmodel.SettingsViewModel

// Definimos los posibles estados de la pantalla
enum class SettingsSubScreen {
    MENU,
    EDIT_PROFILE,
    SECURITY
}

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(),
    onLogout: () -> Unit = {},
    onBackToHome: () -> Unit = {}
) {
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val language by viewModel.language.collectAsState()

    SettingsScreenContent(
        isDarkMode = isDarkMode,
        language = language,
        onDarkModeChange = viewModel::toggleDarkMode,
        onLanguageChange = viewModel::updateLanguage,
        onLogout = onLogout,
        onBackToHome = onBackToHome
    )
}

@Composable
fun SettingsScreenContent(
    isDarkMode: Boolean,
    language: String,
    onDarkModeChange: (Boolean) -> Unit,
    onLanguageChange: (String) -> Unit,
    onLogout: () -> Unit,
    onBackToHome: () -> Unit
) {
    // Estado para manejar la navegación interna sin tocar el AppNavGraph
    var currentSubScreen by remember { mutableStateOf(SettingsSubScreen.MENU) }

    Crossfade(targetState = currentSubScreen, label = "SettingsNav") { screen ->
        when (screen) {
            SettingsSubScreen.MENU -> {
                SettingsMenu(
                    isDarkMode = isDarkMode,
                    language = language,
                    onDarkModeChange = onDarkModeChange,
                    onLanguageChange = onLanguageChange,
                    onNavigateToEditProfile = { currentSubScreen = SettingsSubScreen.EDIT_PROFILE },
                    onNavigateToSecurity = { currentSubScreen = SettingsSubScreen.SECURITY },
                    onLogout = onLogout,
                    onBack = onBackToHome
                )
            }
            SettingsSubScreen.EDIT_PROFILE -> {
                EditProfileScreen(
                    onBack = { currentSubScreen = SettingsSubScreen.MENU }
                )
            }
            SettingsSubScreen.SECURITY -> {
                SecurityScreen(
                    onBack = { currentSubScreen = SettingsSubScreen.MENU }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsMenu(
    isDarkMode: Boolean,
    language: String,
    onDarkModeChange: (Boolean) -> Unit,
    onLanguageChange: (String) -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToSecurity: () -> Unit,
    onLogout: () -> Unit,
    onBack: () -> Unit
) {

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            ClosifyTopBar(
                showBackButton = true,
                onBackClick = onBack
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
                    fontSize = 26.sp
                ),
                color = Color(0xFF333333)
            )

            Spacer(modifier = Modifier.height(24.dp))

            GeneralSettingsCard(
                isDarkMode = isDarkMode,
                onDarkModeChange = onDarkModeChange,
                language = language,
                onLanguageChange = onLanguageChange
            )

            Spacer(modifier = Modifier.height(20.dp))

            SettingsActionButton(
                text = "Editar Perfil", 
                onClick = onNavigateToEditProfile,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            SettingsActionButton(
                text = "Seguridad", 
                onClick = onNavigateToSecurity,
                fontSize = 18.sp
            )
            
            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = onLogout,
                modifier = Modifier
                    .width(200.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "Cerrar sesión",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun GeneralSettingsCard(
    isDarkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    language: String,
    onLanguageChange: (String) -> Unit
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
                    fontSize = 12.sp
                ),
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Modo Oscuro", 
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp)
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

            Text(
                text = "Idioma", 
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp)
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
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = LilaPrimary,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    ClosifyTheme {
        SettingsScreenContent(
            isDarkMode = false,
            language = "ESPAÑOL",
            onDarkModeChange = {},
            onLanguageChange = {},
            onLogout = {},
            onBackToHome = {}
        )
    }
}

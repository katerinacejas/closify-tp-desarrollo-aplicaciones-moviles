package com.closify.myapplication.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.closify.myapplication.R
import com.closify.myapplication.navigation.Screen
import com.closify.myapplication.ui.theme.LavandaAccent

data class BottomNavItem(
    val screen: Screen,
    val iconRes: Int,
    val contentDescription: String
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Home,     R.drawable.home,        "Home"),
    BottomNavItem(Screen.Wardrobe, R.drawable.guardarropa, "Guardarropa"),
    BottomNavItem(Screen.Friends,  R.drawable.amigos,      "Amigos"),
    BottomNavItem(Screen.Camera,   R.drawable.camara,      "Cámara"),
    BottomNavItem(Screen.Calendar, R.drawable.calendario,  "Calendario"),
    BottomNavItem(Screen.Profile,  R.drawable.perfil,      "Perfil")
)

@Composable
fun BottomNavBar(
    currentRoute: String?,
    onItemSelected: (Screen) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp
    ) {
        bottomNavItems.forEach { item ->
            val isSelected = currentRoute == item.screen.route
            NavigationBarItem(
                selected = isSelected,
                onClick = { onItemSelected(item.screen) },
                icon = {
                    Icon(
                        painter = painterResource(item.iconRes),
                        contentDescription = item.contentDescription,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(32.dp)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = LavandaAccent,
                    selectedIconColor = Color.Unspecified,
                    unselectedIconColor = Color.Unspecified
                )
            )
        }
    }
}

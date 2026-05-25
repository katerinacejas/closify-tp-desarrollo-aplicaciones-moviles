package com.closify.myapplication.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Unspecified
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.closify.myapplication.R
import com.closify.myapplication.navigation.Screen
import com.closify.myapplication.ui.theme.TextPrimary

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

private val BottomBarShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)

@Composable
fun BottomNavBar(
    currentRoute: String?,
    onItemSelected: (Screen) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = BottomBarShape,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(width = 1.dp, color = TextPrimary)
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            tonalElevation = 0.dp
        ) {
            bottomNavItems.forEach { item ->
                val isSelected = currentRoute == item.screen.route
                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onItemSelected(item.screen) },
                    icon = {
                        Box(
                            modifier = if (isSelected) {
                                Modifier
                                    .background(
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(8.dp)
                            } else {
                                Modifier.padding(8.dp)
                            }
                        ) {
                            Icon(
                                painter = painterResource(item.iconRes),
                                contentDescription = item.contentDescription,
                                tint = Unspecified,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor   = Unspecified,
                        unselectedIconColor = Unspecified,
                        indicatorColor      = Color.Transparent
                    )
                )
            }
        }
    }
}

package com.closify.myapplication.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.closify.myapplication.R
import com.closify.myapplication.navigation.Screen

data class BottomNavItem(
    val screen: Screen,
    val iconRes: Int,
    val contentDescription: String
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Home, R.drawable.home, "Home"),
    BottomNavItem(Screen.Wardrobe, R.drawable.guardarropa, "Guardarropa"),
    BottomNavItem(Screen.Friends, R.drawable.amigos, "Amigos"),
    BottomNavItem(Screen.Camera, R.drawable.camara, "Camara"),
    BottomNavItem(Screen.Calendar, R.drawable.calendario, "Calendario"),
    BottomNavItem(Screen.Profile, R.drawable.perfil, "Perfil")
)

@Composable
fun BottomNavBar(
    currentRoute: String?,
    onItemSelected: (Screen) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(0.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 6.dp,
        shadowElevation = 8.dp
    ) {
        NavigationBar(
            modifier = Modifier.height(76.dp),
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
                            modifier = Modifier.size(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(item.iconRes),
                                contentDescription = item.contentDescription,
                                tint = Color.Unspecified,
                                modifier = Modifier
                                    .size(if (isSelected) 30.dp else 26.dp)
                                    .graphicsLayer {
                                        alpha = if (isSelected) 1f else 0.72f
                                    }
                            )
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                        selectedIconColor = Color.Unspecified,
                        unselectedIconColor = Color.Unspecified
                    )
                )
            }
        }
    }
}

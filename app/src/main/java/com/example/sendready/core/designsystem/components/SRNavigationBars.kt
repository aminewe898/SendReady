package com.example.sendready.core.designsystem.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.sendready.core.designsystem.theme.SendReadyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SRTopBar(
    title: String,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = SendReadyTheme.typography.titleLarge
            )
        },
        modifier = modifier,
        navigationIcon = navigationIcon,
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = SendReadyTheme.colorScheme.surface,
            titleContentColor = SendReadyTheme.colorScheme.onSurface,
            navigationIconContentColor = SendReadyTheme.colorScheme.onSurface,
            actionIconContentColor = SendReadyTheme.colorScheme.onSurface
        )
    )
}

@Composable
fun SRBottomBar(
    items: List<SRBottomNavItem>,
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = SendReadyTheme.colorScheme.surfaceContainer,
        contentColor = SendReadyTheme.colorScheme.onSurfaceVariant
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = SendReadyTheme.typography.labelMedium
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = SendReadyTheme.colorScheme.onPrimaryContainer,
                    unselectedIconColor = SendReadyTheme.colorScheme.onSurfaceVariant,
                    selectedTextColor = SendReadyTheme.colorScheme.onSurface,
                    unselectedTextColor = SendReadyTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = SendReadyTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}

data class SRBottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

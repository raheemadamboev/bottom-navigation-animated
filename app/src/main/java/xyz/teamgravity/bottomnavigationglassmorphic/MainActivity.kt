package xyz.teamgravity.bottomnavigationglassmorphic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import xyz.teamgravity.bottomnavigationglassmorphic.ui.theme.BottomNavigationAnimatedTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BottomNavigationAnimatedTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val state = remember { HazeState() }
                    var selectedNavigation by remember { mutableStateOf(Navigation.entries.first()) }

                    Scaffold(
                        bottomBar = {
                            GlassmorphicBottomNavigation(
                                state = state,
                                selectedNavigation = selectedNavigation,
                                onSelectNavigation = { value ->
                                    selectedNavigation = value
                                }
                            )
                        }
                    ) { padding ->
                        LazyColumn(
                            contentPadding = padding,
                            modifier = Modifier
                                .haze(
                                    state = state,
                                    style = HazeStyle(
                                        tint = Color.Black.copy(alpha = 0.6F),
                                        blurRadius = 30.dp
                                    )
                                )
                                .fillMaxSize()
                        ) {
                            items(50) { index ->
                                Box(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .background(
                                            color = Color.DarkGray,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .border(
                                            width = Dp.Hairline,
                                            color = Color.White.copy(alpha = 0.5F),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                ) {
                                    AsyncImage(
                                        model = "https://source.unsplash.com/random?neon,$index",
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(12.dp))
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private enum class Navigation(
        @DrawableRes val icon: Int,
        @StringRes val label: Int,
        val color: Color
    ) {
        Home(
            icon = R.drawable.ic_home,
            label = R.string.home,
            color = Color(0xFFFFA574)
        ),
        Alerts(
            icon = R.drawable.ic_notification,
            label = R.string.alerts,
            color = Color(0xFFADFF64)
        ),
        Chats(
            icon = R.drawable.ic_email,
            label = R.string.chats,
            color = Color(0xFFFA6FFF)
        ),
        Settings(
            icon = R.drawable.ic_settings,
            label = R.string.settings,
            color = Color(0xFF6AB04C)
        )
    }

    @Composable
    private fun RowScope.NavigationItem(
        navigation: Navigation,
        selected: Boolean,
        onSelect: () -> Unit
    ) {
        val alpha by animateFloatAsState(
            targetValue = if (selected) 1F else 0.35F,
            label = "alpha"
        )
        val scale by animateFloatAsState(
            targetValue = if (selected) 1F else 0.98F,
            visibilityThreshold = 0.000001F,
            animationSpec = spring(
                stiffness = Spring.StiffnessLow,
                dampingRatio = Spring.DampingRatioMediumBouncy
            ),
            label = "scale"
        )

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .scale(scale)
                .alpha(alpha)
                .fillMaxHeight()
                .weight(1F)
                .pointerInput(Unit) {
                    detectTapGestures {
                        onSelect()
                    }
                }
        ) {
            Icon(
                painter = painterResource(id = navigation.icon),
                contentDescription = stringResource(id = navigation.label)
            )
            Text(
                text = stringResource(id = navigation.label)
            )
        }
    }

    @Composable
    private fun GlassmorphicBottomNavigation(
        state: HazeState,
        selectedNavigation: Navigation,
        onSelectNavigation: (navigation: Navigation) -> Unit
    ) {
        val animatedSelectedNavigationIndex by animateFloatAsState(
            targetValue = selectedNavigation.ordinal.toFloat(),
            animationSpec = spring(
                stiffness = Spring.StiffnessLow,
                dampingRatio = Spring.DampingRatioLowBouncy
            ),
            label = "animatedSelectedNavigationIndex"
        )
        val animatedColor by animateColorAsState(
            targetValue = selectedNavigation.color,
            animationSpec = spring(
                stiffness = Spring.StiffnessLow
            ),
            label = "animatedColor"
        )

        Box(
            modifier = Modifier
                .padding(
                    vertical = 24.dp,
                    horizontal = 64.dp
                )
                .fillMaxWidth()
                .height(64.dp)
                .hazeChild(
                    state = state,
                    shape = CircleShape
                )
                .border(
                    width = Dp.Hairline,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.8F),
                            Color.White.copy(alpha = 0.2F)
                        )
                    ),
                    shape = CircleShape
                )
        ) {
            CompositionLocalProvider(
                LocalTextStyle provides LocalTextStyle.current.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                ),
                LocalContentColor provides Color.White
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Navigation.entries.forEach { navigation ->
                        NavigationItem(
                            navigation = navigation,
                            selected = navigation == selectedNavigation,
                            onSelect = {
                                onSelectNavigation(navigation)
                            }
                        )
                    }
                }
            }
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .blur(
                        radius = 50.dp,
                        edgeTreatment = BlurredEdgeTreatment.Unbounded
                    )
            ) {
                val navigationWidth = size.width / Navigation.entries.size
                drawCircle(
                    color = animatedColor.copy(alpha = 0.3F),
                    radius = size.height / 2,
                    center = Offset(
                        x = (navigationWidth * animatedSelectedNavigationIndex) + navigationWidth / 2,
                        y = size.height / 2
                    )
                )
            }
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
            ) {
                val path = Path()
                path.addRoundRect(
                    RoundRect(
                        rect = size.toRect(),
                        cornerRadius = CornerRadius(size.height)
                    )
                )
                val measure = PathMeasure()
                measure.setPath(
                    path = path,
                    forceClosed = false
                )
                val navigationWidth = size.width / Navigation.entries.size

                drawPath(
                    path = path,
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            animatedColor.copy(alpha = 0F),
                            animatedColor.copy(alpha = 1F),
                            animatedColor.copy(alpha = 1F),
                            animatedColor.copy(alpha = 0F)
                        ),
                        startX = navigationWidth * animatedSelectedNavigationIndex,
                        endX = navigationWidth * (animatedSelectedNavigationIndex + 1)
                    ),
                    style = Stroke(
                        width = 6F,
                        pathEffect = PathEffect.dashPathEffect(
                            intervals = floatArrayOf(measure.length / 2F, measure.length)
                        )
                    )
                )
            }
        }
    }
}
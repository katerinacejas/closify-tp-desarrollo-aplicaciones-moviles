package com.closify.myapplication.ui.onboarding

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.closify.myapplication.R
import com.closify.myapplication.ui.theme.LilaPrimary
import com.closify.myapplication.ui.theme.PrimaryDark
import com.closify.myapplication.ui.theme.SurfaceVariantColor
import kotlinx.coroutines.launch

private data class OnboardingPage(
    val title: String,
    val description: String,
    val imageRes: Int
)

private val pages = listOf(
    OnboardingPage(
        title = "Tu estilo,\norganizado",
        description = "Sacá fotos de tus prendas, guardalas en estantes y encontrá todo tu guardarropa desde un solo lugar.",
        imageRes = R.drawable.ic_onboarding_1
    ),
    OnboardingPage(
        title = "Outfits para cada\nmomento",
        description = "Generá combinaciones según el clima, la ocasión o tus planes. También podés guardar favoritos y planificar qué usar cada día.",
        imageRes = R.drawable.ic_onboarding_2
    ),
    OnboardingPage(
        title = "Inspirate con tus\namigos",
        description = "Publicá tus outfits en tu perfil, mirá los de tus amigos y comentá ideas para inspirarse entre ustedes.",
        imageRes = R.drawable.ic_onboarding_3
    )
)

@Composable
fun OnboardingScreen(onFinish: () -> Unit = {}) {
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp, bottom = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                ClosifyLogo()
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PagerIndicator(
                    pageCount = pages.size,
                    currentPage = pagerState.currentPage
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (pagerState.currentPage < pages.size - 1) {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else {
                            onFinish()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryDark)
                ) {
                    Text(
                        text = if (pagerState.currentPage < pages.size - 1) "Continuar" else "Comenzar",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White
                    )
                }
            }
        }
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) { page ->
            OnboardingPageContent(page = pages[page])
        }
    }
}

@Composable
private fun ClosifyLogo() {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(LilaPrimary.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(LilaPrimary)
        )
    }
}

@Composable
private fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = page.imageRes),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = page.title,
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PagerIndicator(pageCount: Int, currentPage: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val width by animateDpAsState(
                targetValue = if (index == currentPage) 24.dp else 8.dp,
                label = "dot_width_$index"
            )
            Box(
                modifier = Modifier
                    .height(8.dp)
                    .width(width)
                    .clip(CircleShape)
                    .background(
                        if (index == currentPage) PrimaryDark else SurfaceVariantColor
                    )
            )
        }
    }
}

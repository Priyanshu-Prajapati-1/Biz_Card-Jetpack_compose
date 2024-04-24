package com.example.jetbizcard

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetbizcard.ui.theme.JetBizCardTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager

    private var x by mutableStateOf("0")
    private var y by mutableStateOf("0")
    private var z by mutableStateOf("0")

    private var theme by mutableStateOf(false)

    private val rainbowColorsBrush = mutableStateOf(
        Brush.sweepGradient(
            listOf(
                Color(0xFF9575CD),
                Color(0xFFBA68C8),
                Color(0xFFE57373),
                Color(0xFFFFB74D),
                Color(0xFFFFF176),
                Color(0xFFAED581),
                Color(0xFF4DD0E1),
                Color(0xFF9575CD)
            )
        )
    )

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {

            this.sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

            val acceleoSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            if (acceleoSensor != null) {
                sensorManager.registerListener(
                    this,
                    acceleoSensor,
                    SensorManager.SENSOR_DELAY_GAME
                )
            }

            val pullToRefresh = rememberPullToRefreshState()

            val scope = rememberCoroutineScope()

            if (pullToRefresh.isRefreshing) {
                LaunchedEffect(true) {
                    scope.launch {
                        delay(1000L)
                        pullToRefresh.endRefresh()
                    }
                }
            }

            JetBizCardTheme(darkTheme = theme) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding(),
                    tonalElevation = 5.dp
                ) {

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .nestedScroll(pullToRefresh.nestedScrollConnection)
                    ) {


                        val fontProvider = GoogleFont.Provider(
                            providerAuthority = "com.google.android.gms.fonts",
                            providerPackage = "com.google.android.gms",
                            certificates = R.array.com_google_android_gms_fonts_certs
                        )

                        val fontName = GoogleFont(name = "Oswald")
                        val fontFamily = FontFamily(
                            Font(
                                googleFont = fontName,
                                fontProvider = fontProvider
                            )
                        )
                        var isCard3D by remember {
                            mutableStateOf(false)
                        }
                        if (!pullToRefresh.isRefreshing) {
                            createBizCard(isCard3D) {
                                pullToRefresh.startRefresh()
                            }
                        }


                        Icon(
                            painter = painterResource(id = R.drawable.icon_3d_rotation),
                            modifier = Modifier
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) { isCard3D = !isCard3D }
                                .size(50.dp)
                                .padding(12.dp)
                                .align(Alignment.TopEnd),
                            contentDescription = null
                        )

                        if (isCard3D) {
                            Text(
                                text = "X: $x \nY: $y \nZ: $z",
                                fontSize = 10.sp,
                                lineHeight = 11.sp,
                                letterSpacing = 1.25.sp,
                                fontFamily = fontFamily,
                                modifier = Modifier
                                    .padding(start = 10.dp)
                                    .width(100.dp)
                                    .align(Alignment.BottomStart),
                                style = TextStyle(
                                    brush = Brush.linearGradient(
                                        colors = listOf(Color.Cyan, Color.Magenta, Color.Green)
                                    )
                                )
                            )
                        }

                        PullToRefreshContainer(
                            state = pullToRefresh,
                            modifier = Modifier.align(Alignment.TopCenter)
                        )
                    }
                }
            }
        }
    }


    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            x = event.values[0].toString()
            y = event.values[1].toString()
            z = event.values[2].toString()
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

    @Composable
    fun createBizCard(isCard3D: Boolean = false, isRefresh: () -> Unit) {


        Surface(
            modifier = Modifier
                .fillMaxSize(),
        ) {

            val rotationX: Float by animateFloatAsState(x.toFloat(), label = "rotationX")
            val rotationY: Float by animateFloatAsState(y.toFloat(), label = "rotationY")



            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(40.dp)
                    .graphicsLayer {
                        if (isCard3D) {
                            this.rotationX = (-rotationY * 4)
                            this.rotationY = (-rotationX * 4)
                        }
                    },
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 15.dp,
                ),
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(25.dp))
                    Surface(
                        modifier = Modifier
                            .clip(CircleShape)
                            .border(
                                width = 2.dp,
                                brush = rainbowColorsBrush.value,
                                shape = CircleShape
                            )
                    ) {
                        createImageProfile(110.dp) { themeMode, refresh ->
                            if (themeMode) {
                                theme = !theme
                            }
                            if (refresh) {
                                isRefresh()
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(15.dp))


                    HorizontalDivider(
                        modifier = Modifier
                            .padding(35.dp, 0.dp),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.tertiary
                    )

                    profileInfo()

                    var buttonClickedState by remember {
                        mutableStateOf(false)
                    }
                    Button(
                        onClick = {
                            buttonClickedState = !buttonClickedState
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary,
                            contentColor = MaterialTheme.colorScheme.onTertiary
                        ),
                        modifier = Modifier
                            .padding(top = 10.dp),
                        shape = RoundedCornerShape(50),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 5.dp,
                            pressedElevation = 8.dp
                        ),
                    ) {
                        Text(
                            text = "Portfolio",
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                        Icon(
                            imageVector = Icons.Filled.Face, contentDescription = "icon",
                            modifier = Modifier
                                .padding(start = 3.dp)
                        )

                    }
                    AnimatedVisibility(buttonClickedState) {
                        portfolioContent(rainbowColorsBrush)
                    }
                }
            }
        }
    }
}

@Composable
private fun portfolioContent(rainbowColorsBrush: MutableState<Brush>) {

    val projects = listOf(
        Project(
            name = "Web Application",
            description = "Description for Web Application",
            imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ2QcDMe_AsqB4U7hUi0kalHsL1I9NdLEOyKico3N60JCv6HDDcQgHQr2Bojg&s"
        ),
        Project(
            name = "Android Application",
            description = "Description for Android Application",
            imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSFFEwpxjrSHnjTsh7QlwyDArwbnN58qYAqj_sh9PLU59sKlGfk5zKxX58R8g&s"
        ),
        Project(
            name = "Flutter Application",
            description = "Description for Flutter Application",
            imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTxX42OtOayI1mJ1G67_7NVlZtOApIIKL6hKhr2dglgE0QLZCKbF56aDLgNcw&s"
        ),
        Project(
            name = "Blockchain",
            description = "Description for Blockchain",
            imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRfE1OGTsW8ChRjsa8YBqfjjtymE9P1ufsKn8n-QAWuHWMbpDsU8KgL06-fqg&s"
        ),
        Project(
            name = "Machine Learning",
            description = "Description for Machine Learning",
            imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQWAA2F_8Wnf9R0jW22aHqSbqP_xDMCuO4x5Q&s"
        ),
        Project(
            name = "Data Science",
            description = "Description for Data Science",
            imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSCcZgzHS5HS02nxVXYM-ZV7LxuHqbUNdCj8A&s"
        ),
        Project(
            name = "Devops",
            description = "Description for Devops",
            imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR9YYh5Fk1u9VsWWr1MhkyQeOzeNbtnnMO96g&s"
        )
    )

    Surface(
        modifier = Modifier
            .padding(top = 10.dp)
            .padding(3.dp)
            .fillMaxSize()
            .border(
                width = 1.dp,
                brush = rainbowColorsBrush.value,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
    ) {
        portFolio(projects)
    }
}
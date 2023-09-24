package com.example.gym.miscellaneous_screens

import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gym.ExerciseStatistic
import com.example.gym.SessionStatistic
import com.example.gym.database.DatabaseViewModel
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.component.shape.shader.fromBrush
import com.patrykandpatrick.vico.compose.component.shapeComponent
import com.patrykandpatrick.vico.compose.component.textComponent
import com.patrykandpatrick.vico.compose.dimensions.dimensionsOf
import com.patrykandpatrick.vico.compose.legend.verticalLegend
import com.patrykandpatrick.vico.compose.legend.verticalLegendItem
import com.patrykandpatrick.vico.compose.style.ChartStyle
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.compose.style.currentChartStyle
import com.patrykandpatrick.vico.core.DefaultAlpha
import com.patrykandpatrick.vico.core.DefaultColors
import com.patrykandpatrick.vico.core.DefaultDimens
import com.patrykandpatrick.vico.core.axis.vertical.VerticalAxis
import com.patrykandpatrick.vico.core.chart.copy
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.component.shape.LineComponent
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.entry.ChartEntryModel
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.entryModelOf
import kotlin.random.Random

@Composable
fun StatisticsScreen(
    repoModel: DatabaseViewModel,
    modifier: Modifier = Modifier
) {
    val sessions = repoModel.retrieveSessionsWithinMonthFromDB().collectAsState(initial = listOf())
    val foundRoutines by remember {
        mutableStateOf(mutableListOf<String>())
    }
    val routinesMap by remember {
        mutableStateOf(mutableMapOf<String, List<String>>())
    }
    var entryModels by remember {
        mutableStateOf(emptyList<ChartEntryModel>())
    }
    LaunchedEffect(key1 = sessions.value) {
        val statistics = mutableListOf<SessionStatistic>()
        sessions.value.forEach { entry ->
                val index = foundRoutines.indexOf(entry.routineName)
                if (index != -1) {
                    for (i in 0 until entry.repCounts.size) {
                        statistics[index].exerciseStatistics[i].repCounts.add(entry.repCounts[i].toFloat())
                    }
                } else {
                    foundRoutines.add(entry.routineName)
                    val routine = repoModel.retrieveRoutineByName(entry.routineName)
                    val exerciseStatistics = routine!!.exercises.mapIndexed { index, exercise ->
                        ExerciseStatistic(
                            exerciseName = exercise.name,
                            repCounts = mutableListOf(entry.repCounts[index].toFloat())
                        )
                    }
                    statistics.add(
                        SessionStatistic(
                            routineName = entry.routineName,
                            exerciseStatistics = exerciseStatistics.toMutableList()
                        )
                    )
                }
        }
        val newModels = mutableListOf<ChartEntryModel>()
        statistics.forEach { statistic ->
            val entries = mutableListOf<List<FloatEntry>>()
            val listExercises = mutableListOf<String>()
            statistic.exerciseStatistics.forEach {
                listExercises.add(it.exerciseName)
                entries.add(it.repCounts.mapIndexed { index, count -> FloatEntry(index.toFloat(), count) })
            }
            routinesMap[statistic.routineName] = listExercises
            newModels.add(entryModelOf(*entries.toTypedArray()))
        }
        entryModels = newModels
        Log.d("Statistics Screen", statistics.toString())
    }
    Column(
        modifier = modifier.padding(top = 16.dp, start = 24.dp, end= 24.dp)
    ) {
        Text(text = "Progress Charts", style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(vertical = 16.dp))
        LazyRow (
            horizontalArrangement = Arrangement.spacedBy(48.dp)
                ){
            item {
                if (entryModels.isEmpty()) {
                    Text(text = "No available data to display", style = MaterialTheme.typography.headlineSmall)
                }
            }
            itemsIndexed(entryModels) { index, item ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = foundRoutines[index], style = MaterialTheme.typography.headlineMedium)
                    val chartColors by remember {
                        mutableStateOf(randomListColors(item.entries.size))
                    }
                    ProvideChartStyle(rememberChartStyle(chartColors)) {
                        val defaultLines = currentChartStyle.lineChart.lines
                        Chart(
                            chart = lineChart(
                                remember(defaultLines) {
                                    defaultLines.map { defaultLine -> defaultLine.copy(lineBackgroundShader = null) }
                                },
                            ),
                            model = item,
                            startAxis = startAxis(
                                horizontalLabelPosition = VerticalAxis.HorizontalLabelPosition.Inside
                            ),
                            bottomAxis = bottomAxis(),
                            legend = routinesMap[foundRoutines[index]]
                                ?.let { rememberLegend(chartColors, it) },
                            modifier = Modifier.width(340.dp).height(280.dp)
                        )
                    }
                }
            }
        }
    }
}

fun randomListColors(numberOfColors: Int): List<Color> {
    val colors = mutableListOf<Color>()
    for (i in 0 until numberOfColors) {
        colors.add(randomColor())
    }
    return colors
}

fun randomColor(): Color {
    val red = Random.nextInt(256)
    val green = Random.nextInt(256)
    val blue = Random.nextInt(256)
    return Color(red, green, blue)
}

@Composable
internal fun rememberChartStyle(columnChartColors: List<Color>, lineChartColors: List<Color>): ChartStyle {
    val isSystemInDarkTheme = isSystemInDarkTheme()
    return remember(columnChartColors, lineChartColors, isSystemInDarkTheme) {
        val defaultColors = if (isSystemInDarkTheme) DefaultColors.Dark else DefaultColors.Light
        ChartStyle(
            ChartStyle.Axis(
                axisLabelColor = Color(defaultColors.axisLabelColor),
                axisGuidelineColor = Color(defaultColors.axisGuidelineColor),
                axisLineColor = Color(defaultColors.axisLineColor),
            ),
            ChartStyle.ColumnChart(
                columnChartColors.map { columnChartColor ->
                    LineComponent(
                        columnChartColor.toArgb(),
                        DefaultDimens.COLUMN_WIDTH,
                        Shapes.roundedCornerShape(DefaultDimens.COLUMN_ROUNDNESS_PERCENT),
                    )
                },
            ),
            ChartStyle.LineChart(
                lineChartColors.map { lineChartColor ->
                    LineChart.LineSpec(
                        lineColor = lineChartColor.toArgb(),
                        lineBackgroundShader = DynamicShaders.fromBrush(
                            Brush.verticalGradient(
                                listOf(
                                    lineChartColor.copy(DefaultAlpha.LINE_BACKGROUND_SHADER_START),
                                    lineChartColor.copy(DefaultAlpha.LINE_BACKGROUND_SHADER_END),
                                ),
                            ),
                        ),
                    )
                },
            ),
            ChartStyle.Marker(),
            Color(defaultColors.elevationOverlayColor),
        )
    }
}

@Composable
internal fun rememberChartStyle(chartColors: List<Color>) =
    rememberChartStyle(columnChartColors = chartColors, lineChartColors = chartColors)

@Composable
private fun rememberLegend(chartColors: List<Color>, exerciseNames: List<String>) = verticalLegend(
    items = chartColors.mapIndexed { index, chartColor ->
        verticalLegendItem(
            icon = shapeComponent(Shapes.pillShape, chartColor),
            label = textComponent(
                color = currentChartStyle.axis.axisLabelColor,
                textSize = legendItemLabelTextSize,
            ),
            labelText = exerciseNames[index],
        )
    },
    iconSize = legendItemIconSize,
    iconPadding = legendItemIconPaddingValue,
    spacing = legendItemSpacing,
    padding = legendPadding,
)

private val legendItemLabelTextSize = 18.sp
private val legendItemIconSize = 8.dp
private val legendItemIconPaddingValue = 10.dp
private val legendItemSpacing = 4.dp
private val legendTopPaddingValue = 8.dp
private val legendPadding = dimensionsOf(top = legendTopPaddingValue)


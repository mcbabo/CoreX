package at.mcbabo.corex.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.OpenInFull
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import at.mcbabo.corex.data.models.WeightProgressionModel
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.ProvideVicoTheme
import com.patrykandpatrick.vico.compose.m3.common.rememberM3VicoTheme
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.common.component.TextComponent
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun WeightProgressionGraph(weightProgressions: List<WeightProgressionModel>, onNavigate: () -> Unit) {
    if (weightProgressions.size <= 1) return

    val dateFormatter = remember { SimpleDateFormat("dd.MM", Locale.getDefault()) }
    val modelProducer = remember { CartesianChartModelProducer() }

    val sorted = weightProgressions.sortedBy { it.date }

    val xValues = sorted.indices.map { it.toFloat() }
    val yValues = sorted.map { it.weight }
    val labels = sorted.map { dateFormatter.format(it.date) }

    LaunchedEffect(xValues, yValues) {
        if (xValues.isNotEmpty() && yValues.isNotEmpty()) {
            modelProducer.runTransaction {
                lineSeries { series(xValues, yValues) }
            }
        }
    }

    val minY = (yValues.minOrNull() ?: 0f) - 5f
    val maxY = (yValues.maxOrNull() ?: 100f) + 5f

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            ProvideVicoTheme(
                rememberM3VicoTheme()
            ) {
                CartesianChartHost(
                    chart = rememberCartesianChart(
                        rememberLineCartesianLayer(
                            rangeProvider = CartesianLayerRangeProvider.fixed(
                                minY = minY.toDouble(),
                                maxY = maxY.toDouble()
                            )
                        ),
                        startAxis = VerticalAxis.rememberStart(),
                        bottomAxis = HorizontalAxis.rememberBottom(
                            itemPlacer = HorizontalAxis.ItemPlacer.aligned(),
                            valueFormatter = { _, value, _ ->
                                labels.getOrNull(value.roundToInt()) ?: " "
                            }
                        ),
                        marker = rememberDefaultCartesianMarker(
                            label = TextComponent(),
                            labelPosition = DefaultCartesianMarker.LabelPosition.AbovePoint
                        )
                    ),
                    modelProducer = modelProducer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Transparent)
                        .padding(8.dp)
                )
            }

            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .size(40.dp)
                    .zIndex(1f),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f),
                tonalElevation = 4.dp
            ) {
                IconButton(
                    onClick = {
                        onNavigate()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.OpenInFull,
                        contentDescription = "Expand",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

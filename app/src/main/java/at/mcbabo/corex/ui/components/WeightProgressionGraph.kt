package at.mcbabo.corex.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import at.mcbabo.corex.data.models.WeightProgressionModel
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
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
fun WeightProgressionGraph(weightProgressions: List<WeightProgressionModel>) {
    val dateFormatter = remember { SimpleDateFormat("dd.MM", Locale.getDefault()) }
    val modelProducer = remember { CartesianChartModelProducer() }

    val (xValues, yValues, labels) = remember(weightProgressions) {
        val dailyData = weightProgressions
            .groupBy { dateFormatter.format(it.date) }
            .map { (dateLabel, entries) ->
                Triple(
                    dateLabel,
                    entries.maxOf { it.weight },
                    entries.minOf { it.date.time }
                )
            }
            .sortedBy { it.third }

        Triple(
            dailyData.indices.map { it.toFloat() },
            dailyData.map { it.second },
            dailyData.map { it.first }
        )
    }

    LaunchedEffect(xValues, yValues) {
        modelProducer.runTransaction {
            lineSeries { series(xValues, yValues) }
        }
    }

    val (minY, maxY) = remember(yValues) {
        if (yValues.isEmpty()) return@remember 0f to 100f
        val min = yValues.minOf { it }
        val max = yValues.maxOf { it }
        val padding = (max - min) * 0.1f
        (min - padding).coerceAtLeast(0f) to max + padding
    }

    val horizontalAxis = HorizontalAxis.rememberBottom(
        valueFormatter = { _, value, _ ->
            labels.getOrNull(value.roundToInt()) ?: ""
        }
    )


    val lineLayer = rememberLineCartesianLayer(
        rangeProvider = CartesianLayerRangeProvider.fixed(
            minY = minY.toDouble(),
            maxY = maxY.toDouble()
        )
    )

    Card {
        CartesianChartHost(
            chart = rememberCartesianChart(
                lineLayer,
                startAxis = VerticalAxis.rememberStart(),
                bottomAxis = horizontalAxis,
                marker = rememberDefaultCartesianMarker(
                    label = TextComponent(),
                    labelPosition = DefaultCartesianMarker.LabelPosition.AbovePoint
                )
            ),
            modelProducer = modelProducer,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
                .padding(8.dp),
        )
    }
}
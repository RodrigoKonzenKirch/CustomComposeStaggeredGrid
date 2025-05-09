package com.example.customcomposestaggeredgrid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.customcomposestaggeredgrid.ui.theme.CustomComposeStaggeredGridTheme
import kotlin.math.max
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CustomComposeStaggeredGridTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    StaggeredGridDemo(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun StaggeredVerticalGrid(
    modifier: Modifier = Modifier,
    maxColumnWidth: Dp,
    content: @Composable () -> Unit
) {
    val scrollState = rememberScrollState(0)
    Layout(
        modifier = modifier.verticalScroll(scrollState),
        content = content
    ) { measurables, constraints ->
        val columnCount = max(1, constraints.maxWidth / maxColumnWidth.roundToPx())
        val columnWidths = IntArray(columnCount) { constraints.maxWidth / columnCount }
        val columnHeights = IntArray(columnCount) { 0 }

        val placeables = measurables.map { measurable ->
            // Find the column with the least height
            val column = columnHeights.withIndex().minByOrNull { it.value }?.index ?: 0
            val placeable = measurable.measure(
                Constraints.fixedWidth(columnWidths[column])
            )
            columnHeights[column] += placeable.height
            Pair(placeable, column)
        }

        val height = columnHeights.maxOrNull()?.coerceIn(constraints.minHeight, constraints.maxHeight) ?: constraints.minHeight

        layout(width = constraints.maxWidth, height = height) {
            val columnY = IntArray(columnCount) { 0 }

            placeables.forEach { (placeable, column) ->
                val x = columnWidths.take(column).sum()
                val y = columnY[column]
                placeable.placeRelative(x = x, y = y)
                columnY[column] += placeable.height
            }
        }
    }
}

@Composable
fun StaggeredGridDemo(modifier: Modifier) {
    val items = (1..50).map { it to Random.nextInt(100, 300) }

    StaggeredVerticalGrid(
        maxColumnWidth = 150.dp,
        modifier = modifier
            .padding(8.dp)
            .fillMaxSize()
    ) {
        items.forEach { (index, height) ->
            Box(
                modifier = Modifier
                    .height(height.dp)
                    .padding(4.dp)
                    .background(
                        color = Color(
                            Random.nextFloat(),
                            Random.nextFloat(),
                            Random.nextFloat(),
                            1f
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("Item $index", color = Color.White)
            }
        }
    }
}

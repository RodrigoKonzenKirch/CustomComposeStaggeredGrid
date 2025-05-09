# Custom Compose Staggered Grid

This project demonstrates a custom Composable function, `StaggeredVerticalGrid`, which arranges its children in a staggered vertical grid layout. This type of layout is useful for displaying items of varying heights in a visually appealing and space-efficient manner, similar to Pinterest.

## Code Overview

The core component is the `StaggeredVerticalGrid` composable:

```kotlin
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
```

Key aspects of the StaggeredVerticalGrid:

    - It uses the Compose Layout composable for custom layout behavior.
    - It calculates the number of columns based on the maxWidth constraint and the provided maxColumnWidth.
    - For each item in the content, it finds the column with the least current height and places the item there.
    - It ensures vertical scrolling using verticalScroll.

Usage

To use the StaggeredVerticalGrid in your Compose project:

    1. Copy the StaggeredVerticalGrid composable function into your codebase.
    2. Call the composable, providing a maxColumnWidth and the content you want to display within the grid.

```kotlin
StaggeredVerticalGrid(
    maxColumnWidth = 200.dp,
    modifier = Modifier.fillMaxSize().padding(16.dp)
) {
    // Your composable items here
    // Example:
    Box(modifier = Modifier.height(100.dp).background(Color.Red)) { /* ... */ }
    Box(modifier = Modifier.height(150.dp).background(Color.Blue)) { /* ... */ }
    Box(modifier = Modifier.height(80.dp).background(Color.Green)) { /* ... */ }
    // ... more items
}
```

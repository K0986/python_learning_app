package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CheatSheetItem
import com.example.data.model.TemplatesAndReferenceData
import com.example.ui.components.CodeSnippetCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.PyLearnViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReferenceScreen(
  viewModel: PyLearnViewModel,
  modifier: Modifier = Modifier
) {
  val searchQuery by viewModel.referenceSearch.collectAsState()
  val selectedCategory by viewModel.selectedReferenceCategory.collectAsState()

  val allItems = TemplatesAndReferenceData.cheatSheetItems
  val categories = remember { allItems.map { it.category }.distinct() }

  val filteredItems = remember(searchQuery, selectedCategory) {
    allItems.filter { item ->
      val matchesCat = selectedCategory == null || item.category == selectedCategory
      val matchesQuery = searchQuery.isBlank() ||
          item.title.contains(searchQuery, ignoreCase = true) ||
          item.syntax.contains(searchQuery, ignoreCase = true) ||
          item.explanation.contains(searchQuery, ignoreCase = true)
      matchesCat && matchesQuery
    }
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
  ) {
    // Header
    item {
      Card(
        modifier = Modifier.fillMaxWidth().testTag("reference_header_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(SurfaceCardBorder))
      ) {
        Row(
          modifier = Modifier.fillMaxWidth().padding(16.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Python Cheat Sheet",
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              color = TextPrimaryDark
            )
            Text(
              text = "Quick syntax references, patterns, and instant code snippets.",
              fontSize = 13.sp,
              color = TextSecondaryDark
            )
          }

          Icon(
            imageVector = Icons.Default.MenuBook,
            contentDescription = null,
            tint = PythonBlue,
            modifier = Modifier.size(32.dp)
          )
        }
      }
    }

    // Search Input
    item {
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { viewModel.updateReferenceSearch(it) },
        placeholder = { Text("Search functions, syntax, or keywords...", fontSize = 13.sp) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondaryDark) },
        trailingIcon = {
          if (searchQuery.isNotEmpty()) {
            IconButton(onClick = { viewModel.updateReferenceSearch("") }) {
              Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextSecondaryDark)
            }
          }
        },
        modifier = Modifier.fillMaxWidth().testTag("search_reference_input"),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = SurfaceDark,
          unfocusedContainerColor = SurfaceDark,
          focusedBorderColor = PythonBlue,
          unfocusedBorderColor = SurfaceCardBorder
        )
      )
    }

    // Category chips
    item {
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        item {
          FilterChip(
            selected = selectedCategory == null,
            onClick = { viewModel.selectReferenceCategory(null) },
            label = { Text("All") },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = PythonBlue,
              selectedLabelColor = BackgroundDark
            ),
            modifier = Modifier.testTag("ref_cat_all")
          )
        }

        items(categories) { cat ->
          FilterChip(
            selected = selectedCategory == cat,
            onClick = { viewModel.selectReferenceCategory(cat) },
            label = { Text(cat) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = PythonYellow,
              selectedLabelColor = BackgroundDark
            ),
            modifier = Modifier.testTag("ref_cat_$cat")
          )
        }
      }
    }

    // Reference cards
    items(filteredItems) { item ->
      ReferenceItemCard(
        item = item,
        onRunExample = { viewModel.loadLessonCodeIntoCompiler(item.example) }
      )
    }
  }
}

@Composable
fun ReferenceItemCard(
  item: CheatSheetItem,
  onRunExample: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier.fillMaxWidth().testTag("cheat_card_${item.id}"),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
    border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(SurfaceCardBorder))
  ) {
    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = item.title,
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimaryDark,
          fontFamily = FontFamily.Monospace
        )

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(PythonBlue.copy(alpha = 0.15f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = item.category,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = PythonBlue
          )
        }
      }

      Text(
        text = item.explanation,
        fontSize = 13.sp,
        color = TextSecondaryDark,
        lineHeight = 18.sp
      )

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(6.dp))
          .background(CodeEditorBg)
          .padding(8.dp)
      ) {
        Text(
          text = "Syntax: ${item.syntax}",
          fontSize = 12.sp,
          fontFamily = FontFamily.Monospace,
          color = PythonYellow
        )
      }

      CodeSnippetCard(
        code = item.example,
        title = "Example Usage",
        onRunClick = onRunExample
      )
    }
  }
}

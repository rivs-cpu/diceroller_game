package com.example.dicerollergame

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "I confirm that I understand what plagiarism is and have read and" +
                                "understood the section on Assessment Offences in the Essential" +
                                "Information for Students." +
                                "The work that I have submitted is" +
                                "entirely my own. Any work from other authors is duly referenced" +
                                "and acknowledged. ",
                        fontSize = 16.sp,
                        textAlign = TextAlign.Start,
                        lineHeight = 24.sp
                    )
                }
            }
        }
    }


@Composable
fun BulletPoint(text: String) {
    Row(
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "• ",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Text(
            text = text,
            fontSize = 16.sp,
            lineHeight = 24.sp
        )
    }
}

package com.rajaryan.offerlens

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.rajaryan.offerlens.ui.theme.OfferLensTheme
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Midnight = Color(0xFF08111F)
private val Panel = Color(0xFF111D2E)
private val PanelLight = Color(0xFF19283D)
private val Cyan = Color(0xFF48D9F5)
private val Amber = Color(0xFFFFC857)
private val Red = Color(0xFFFF647C)
private val Green = Color(0xFF5DE2A5)
private val White = Color(0xFFF3F7FC)
private val Muted = Color(0xFFA5B4C8)

private const val HISTORY_KEY = "offer_lens_history"

data class ScanHistoryItem(
    val message: String,
    val riskLevel: String,
    val riskScore: Int,
    val summary: String,
    val timestamp: Long
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            OfferLensTheme {
                OfferLensApp()
            }
        }
    }
}

@Composable
private fun OfferLensApp() {
    val context = LocalContext.current

    var message by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<OfferAnalysisResult?>(null) }
    var errorMessage by remember { mutableStateOf("") }
    var showHistory by remember { mutableStateOf(false) }
    var showClearHistoryDialog by remember { mutableStateOf(false) }

    val history = remember { mutableStateListOf<ScanHistoryItem>() }
    val checks = remember { mutableStateListOf<Boolean>().apply {
        repeat(7) { add(false) }
    } }

    LaunchedEffect(Unit) {
        history.addAll(loadHistory(context))
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Midnight
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 18.dp,
                end = 18.dp,
                top = 22.dp,
                bottom = 32.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Header(
                    onHistoryClick = { showHistory = !showHistory },
                    historyCount = history.size
                )
            }

            if (showHistory) {
                item {
                    HistoryCard(
                        history = history,
                        onSelect = { item ->
                            message = item.message
                            result = OfferAnalyzer.analyze(item.message)
                            errorMessage = ""
                            showHistory = false
                        },
                        onClear = { showClearHistoryDialog = true }
                    )
                }
            }

            item {
                IntroCard()
            }

            item {
                SectionHeading("CHECK A JOB OFFER", "01 / ANALYZE")
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Panel, RoundedCornerShape(20.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Paste the recruiter message",
                        color = White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 155.dp)
                            .background(Midnight, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        if (message.isBlank()) {
                            Text(
                                "Paste a job offer, email, SMS, or recruiter message here...",
                                color = Muted,
                                fontSize = 14.sp
                            )
                        }

                        BasicTextField(
                            value = message,
                            onValueChange = {
                                message = it
                                errorMessage = ""
                            },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                color = White,
                                fontSize = 14.sp,
                                lineHeight = 21.sp
                            ),
                            minLines = 5
                        )
                    }

                    Text(
                        "${message.length} characters",
                        color = Muted,
                        fontSize = 12.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End
                    )

                    if (errorMessage.isNotBlank()) {
                        Text(errorMessage, color = Red, fontSize = 13.sp)
                    }

                    Button(
                        onClick = {
                            if (message.isBlank()) {
                                errorMessage = "Paste a message before analyzing."
                                result = null
                            } else {
                                val analysis = OfferAnalyzer.analyze(message)
                                result = analysis

                                val item = ScanHistoryItem(
                                    message = message,
                                    riskLevel = analysis.riskLevel,
                                    riskScore = analysis.riskScore,
                                    summary = analysis.summary,
                                    timestamp = System.currentTimeMillis()
                                )

                                history.removeAll {
                                    it.message == item.message
                                }
                                history.add(0, item)

                                while (history.size > 50) {
                                    history.removeAt(history.lastIndex)
                                }

                                saveHistory(context, history)
                                errorMessage = ""
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Cyan,
                            contentColor = Midnight
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            "ANALYZE OFFER  →",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                message = SAMPLE_MESSAGE
                                result = null
                                errorMessage = ""
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Cyan
                            )
                        ) {
                            Text("Load sample")
                        }

                        OutlinedButton(
                            onClick = {
                                message = ""
                                result = null
                                errorMessage = ""
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Muted
                            )
                        ) {
                            Text("Clear input")
                        }
                    }
                }
            }

            item {
                Text(
                    "Analysis runs locally using configured text-matching rules. Avoid entering private personal information.",
                    color = Muted,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }

            if (result != null) {
                item {
                    SectionHeading("ANALYSIS REPORT", "02 / RESULTS")
                }

                item {
                    AnalysisResultCard(
                        result = result!!,
                        originalMessage = message
                    )
                }
            }

            item {
                SectionHeading("RECRUITER CHECKLIST", "03 / VERIFY")
            }

            item {
                VerificationChecklist(checks)
            }

            item {
                SectionHeading("HOW OFFERLENS WORKS", "04 / ABOUT")
            }

            item {
                FeatureCard(
                    "RULE-BASED SCANNING",
                    "Looks for configured patterns such as upfront payment requests, urgency, and sensitive-information requests."
                )
            }

            item {
                FeatureCard(
                    "EVIDENCE YOU CAN REVIEW",
                    "Shows matched phrases and explanations so you can understand why a warning appeared."
                )
            }

            item {
                FeatureCard(
                    "PRIVACY-FIRST DESIGN",
                    "This screen analyzes text locally. Scan history is stored in this app's local preferences."
                )
            }

            item {
                DisclaimerCard()
            }

            item {
                Text(
                    "OFFERLENS  •  VERIFY BEFORE YOU TRUST",
                    color = Muted,
                    fontSize = 11.sp,
                    letterSpacing = 1.2.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
            }
        }
    }

    if (showClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showClearHistoryDialog = false },
            containerColor = Panel,
            title = {
                Text("Clear scan history?", color = White)
            },
            text = {
                Text(
                    "This removes saved scan history from this device.",
                    color = Muted
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        history.clear()
                        saveHistory(context, history)
                        showClearHistoryDialog = false
                    }
                ) {
                    Text("Clear history", color = Red)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showClearHistoryDialog = false }
                ) {
                    Text("Cancel", color = Cyan)
                }
            }
        )
    }
}

private const val SAMPLE_MESSAGE =
    "Congratulations! You are selected for the position. " +
            "Pay a registration fee to secure your job today. " +
            "Act immediately because limited slots are available."

@Composable
private fun Header(
    onHistoryClick: () -> Unit,
    historyCount: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Cyan, RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "O",
                color = Midnight,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                "OfferLens",
                color = White,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                "JOB OFFER SAFETY SCANNER",
                color = Muted,
                fontSize = 10.sp,
                letterSpacing = 1.1.sp
            )
        }

        TextButton(onClick = onHistoryClick) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("History", color = Cyan, fontSize = 13.sp)
                Text("$historyCount saved", color = Muted, fontSize = 10.sp)
            }
        }
    }
}

@Composable
private fun IntroCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                PanelLight,
                RoundedCornerShape(20.dp)
            )
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            "SPOT THE RED FLAGS.",
            color = Cyan,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp
        )

        Text(
            "Think before you trust a job offer.",
            color = White,
            fontSize = 23.sp,
            lineHeight = 29.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            "Review suspicious wording, understand the warning signs, and verify the recruiter independently.",
            color = Muted,
            fontSize = 14.sp,
            lineHeight = 21.sp
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Tag("ON-DEVICE RULES", Cyan)
            Tag("STUDENT SAFETY", Green)
        }
    }
}

@Composable
private fun Tag(text: String, color: Color) {
    Text(
        text = text,
        color = color,
        fontSize = 9.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .background(color.copy(alpha = 0.10f), RoundedCornerShape(8.dp))
            .padding(horizontal = 9.dp, vertical = 7.dp)
    )
}

@Composable
private fun SectionHeading(
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                color = White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            subtitle,
            color = Cyan,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
        )
    }
}

@Composable
private fun AnalysisResultCard(
    result: OfferAnalysisResult,
    originalMessage: String
) {
    val riskColor = when (result.riskLevel.uppercase(Locale.ROOT)) {
        "HIGH", "HIGH RISK" -> Red
        "MEDIUM", "MEDIUM RISK", "CAUTION" -> Amber
        "LOW", "LOW RISK", "LOWER INDICATED RISK" -> Green
        else -> Cyan
    }

    val scoreFraction = (result.riskScore / 100f).coerceIn(0f, 1f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Panel, RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "INDICATED RISK",
                    color = Muted,
                    fontSize = 10.sp,
                    letterSpacing = 1.2.sp
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    result.riskLevel,
                    color = riskColor,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${result.riskScore}",
                    color = riskColor,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
                Text("/ 100 points", color = Muted, fontSize = 11.sp)
            }
        }

        LinearProgressIndicator(
            progress = { scoreFraction },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = riskColor,
            trackColor = Midnight
        )

        Text(
            "This score reflects configured text matches, not a verified probability of fraud.",
            color = Muted,
            fontSize = 11.sp,
            lineHeight = 16.sp
        )

        HorizontalDivider(color = PanelLight)

        Text(
            result.summary,
            color = White,
            fontSize = 14.sp,
            lineHeight = 21.sp
        )

        if (result.evidence.isNotEmpty()) {
            Text(
                "WARNING EVIDENCE",
                color = Red,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            result.evidence.forEach { evidence ->
                EvidenceItem(evidence, originalMessage)
            }
        } else {
            Text(
                "No configured warning phrase matched. This does not guarantee the offer is genuine.",
                color = Green,
                fontSize = 13.sp,
                lineHeight = 19.sp
            )
        }

        if (result.positiveSignals.isNotEmpty()) {
            HorizontalDivider(color = PanelLight)

            Text(
                "POTENTIALLY HELPFUL SIGNALS",
                color = Green,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )

            result.positiveSignals.forEach { signal ->
                BulletText(signal, Green)
            }

            Text(
                "These phrases are not proof of legitimacy; scammers can use them too.",
                color = Muted,
                fontSize = 11.sp
            )
        }

        HorizontalDivider(color = PanelLight)

        Text(
            "RECOMMENDED NEXT STEPS",
            color = Cyan,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
        )

        result.recommendedActions.forEachIndexed { index, action ->
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "${index + 1}.",
                    color = Cyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Text(
                    action,
                    color = White,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )
            }
        }
    }
}

@Composable
private fun EvidenceItem(
    evidence: WarningEvidence,
    originalMessage: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Midnight, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .background(Amber, RoundedCornerShape(50))
            )
            Spacer(Modifier.width(8.dp))
            Text(
                evidence.title,
                color = White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )
            Text(
                "+${evidence.points}",
                color = Amber,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        val annotated = buildAnnotatedString {
            val index = originalMessage.indexOf(
                evidence.matchedPhrase,
                ignoreCase = true
            )

            if (index >= 0) {
                append(originalMessage.substring(0, index))
                pushStyle(
                    SpanStyle(
                        color = Midnight,
                        background = Amber,
                        fontWeight = FontWeight.Bold
                    )
                )
                append(originalMessage.substring(index, index + evidence.matchedPhrase.length))
                pop()
                append(originalMessage.substring(index + evidence.matchedPhrase.length))
            } else {
                append(evidence.matchedPhrase)
            }
        }

        Text(
            "Matched text:",
            color = Muted,
            fontSize = 11.sp
        )

        Text(
            annotated,
            color = White,
            fontSize = 13.sp,
            lineHeight = 20.sp
        )

        Text(
            evidence.explanation,
            color = Muted,
            fontSize = 12.sp,
            lineHeight = 18.sp
        )
    }
}

@Composable
private fun VerificationChecklist(
    checks: SnapshotStateList<Boolean>
) {
    val items = listOf(
        "I found the company's official website independently.",
        "The recruiter uses a verifiable company email or identity.",
        "The job appears on an official careers page.",
        "The role, salary, and responsibilities are clearly explained.",
        "I have not been asked to pay to secure the job.",
        "I have not shared OTPs, passwords, or banking PINs.",
        "I verified the offer through official company contact details."
    )

    val completed = checks.count { it }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Panel, RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Before you accept",
                    color = White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    "$completed of ${items.size} checked",
                    color = Muted,
                    fontSize = 12.sp
                )
            }

            TextButton(
                onClick = {
                    for (i in checks.indices) {
                        checks[i] = false
                    }
                }
            ) {
                Text("Reset", color = Cyan)
            }
        }

        LinearProgressIndicator(
            progress = { completed.toFloat() / items.size },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            color = Green,
            trackColor = Midnight
        )

        items.forEachIndexed { index, item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        checks[index] = !checks[index]
                    },
                verticalAlignment = Alignment.Top
            ) {
                Checkbox(
                    checked = checks[index],
                    onCheckedChange = { checks[index] = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Green,
                        uncheckedColor = Muted,
                        checkmarkColor = Midnight
                    )
                )

                Text(
                    item,
                    color = if (checks[index]) Green else White,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 12.dp)
                )
            }
        }

        Text(
            "A completed checklist helps you review the offer; it cannot guarantee safety.",
            color = Muted,
            fontSize = 11.sp,
            lineHeight = 16.sp
        )
    }
}

@Composable
private fun HistoryCard(
    history: List<ScanHistoryItem>,
    onSelect: (ScanHistoryItem) -> Unit,
    onClear: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Panel, RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Recent scans",
                color = White,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onClear) {
                Text("Clear", color = Red)
            }
        }

        if (history.isEmpty()) {
            Text("No saved scans yet.", color = Muted, fontSize = 13.sp)
        } else {
            history.forEach { item ->
                val color = when (item.riskLevel) {
                    "HIGH RISK" -> Red
                    "CAUTION" -> Amber
                    "LOWER INDICATED RISK" -> Green
                    else -> Cyan
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Midnight, RoundedCornerShape(12.dp))
                        .clickable { onSelect(item) }
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            item.riskLevel,
                            color = color,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            "${item.riskScore}/100",
                            color = color,
                            fontSize = 12.sp
                        )
                    }

                    Text(
                        item.message.take(110) +
                                if (item.message.length > 110) "…" else "",
                        color = White,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )

                    Text(
                        SimpleDateFormat(
                            "dd MMM yyyy, hh:mm a",
                            Locale.getDefault()
                        ).format(Date(item.timestamp)),
                        color = Muted,
                        fontSize = 10.sp
                    )

                    Text("Tap to review this scan", color = Cyan, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun FeatureCard(
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Panel, RoundedCornerShape(16.dp))
            .padding(15.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(Cyan, RoundedCornerShape(50))
        )

        Spacer(Modifier.width(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(
                title,
                color = White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                description,
                color = Muted,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun DisclaimerCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Amber.copy(alpha = 0.08f),
                RoundedCornerShape(16.dp)
            )
            .padding(15.dp),
        verticalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        Text(
            "IMPORTANT SAFETY NOTICE",
            color = Amber,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )

        Text(
            "OfferLens uses simple, configured text-matching rules. It may miss scams or flag legitimate messages. Its score is not a probability of fraud and is not a substitute for independent verification.",
            color = White,
            fontSize = 12.sp,
            lineHeight = 19.sp
        )
    }
}

@Composable
private fun BulletText(
    text: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("•", color = color, fontSize = 14.sp)
        Text(
            text,
            color = White,
            fontSize = 13.sp,
            lineHeight = 19.sp
        )
    }
}

private fun saveHistory(
    context: Context,
    history: List<ScanHistoryItem>
) {
    val array = JSONArray()

    history.forEach { item ->
        val obj = JSONObject()
        obj.put("message", item.message)
        obj.put("riskLevel", item.riskLevel)
        obj.put("riskScore", item.riskScore)
        obj.put("summary", item.summary)
        obj.put("timestamp", item.timestamp)
        array.put(obj)
    }

    context.getSharedPreferences(
        "offer_lens_preferences",
        Context.MODE_PRIVATE
    ).edit()
        .putString(HISTORY_KEY, array.toString())
        .apply()
}

private fun loadHistory(context: Context): List<ScanHistoryItem> {
    val saved = context.getSharedPreferences(
        "offer_lens_preferences",
        Context.MODE_PRIVATE
    ).getString(HISTORY_KEY, "[]") ?: "[]"

    return try {
        val array = JSONArray(saved)
        buildList {
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                add(
                    ScanHistoryItem(
                        message = obj.optString("message", ""),
                        riskLevel = obj.optString("riskLevel", "UNKNOWN"),
                        riskScore = obj.optInt("riskScore", 0),
                        summary = obj.optString("summary", ""),
                        timestamp = obj.optLong("timestamp", 0L)
                    )
                )
            }
        }
    } catch (_: Exception) {
        emptyList()
    }
}

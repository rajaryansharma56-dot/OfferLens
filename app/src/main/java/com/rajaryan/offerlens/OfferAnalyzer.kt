
package com.rajaryan.offerlens

data class WarningEvidence(
    val title: String,
    val matchedPhrase: String,
    val explanation: String,
    val points: Int
)

data class OfferAnalysisResult(
    val riskLevel: String,
    val riskScore: Int,
    val summary: String,
    val warningSigns: List<String>,
    val positiveSignals: List<String>,
    val recommendedActions: List<String>,
    val evidence: List<WarningEvidence> = emptyList()
)

object OfferAnalyzer {

    private data class Rule(
        val title: String,
        val phrases: List<String>,
        val explanation: String,
        val points: Int,
        val critical: Boolean = false
    )

    private val rules = listOf(
        Rule(
            "Upfront payment request",
            listOf(
                "registration fee", "pay a registration",
                "processing fee", "security deposit",
                "refundable fee", "pay to secure",
                "pay to get the job", "training fee",
                "application fee", "pay an advance",
                "deposit to confirm", "payment to confirm"
            ),
            "Requests for money to obtain a job or secure a position are a major warning sign. Verify the employer independently before paying anything.",
            35
        ),
        Rule(
            "Unrealistic earnings promise",
            listOf(
                "guaranteed income", "guaranteed salary",
                "earn ₹50000 per day", "earn 50000 per day",
                "earn ₹50,000 per day", "earn money instantly",
                "no experience required", "easy money",
                "get rich quickly", "guaranteed returns",
                "earn thousands daily"
            ),
            "Unusually high or guaranteed earnings without credible job details deserve careful verification.",
            20
        ),
        Rule(
            "Urgency or pressure tactics",
            listOf(
                "act immediately", "limited slots",
                "limited slot", "offer expires today",
                "urgent response required", "respond immediately",
                "last chance", "only today",
                "within 10 minutes", "hurry up",
                "secure your position now"
            ),
            "Pressure to act quickly can discourage you from verifying the employer and offer.",
            15
        ),
        Rule(
            "Sensitive information request",
            listOf(
                "send your otp", "share your otp",
                "provide your otp", "send otp", "share otp",
                "upi pin", "share your password",
                "send your password", "banking password",
                "card pin", "cvv number", "one time password"
            ),
            "Never share OTPs, passwords, UPI PINs, or card security details with a recruiter. A request for these details is a critical warning sign.",
            40,
            true
        ),
        Rule(
            "Suspicious contact method",
            listOf(
                "message only on telegram",
                "contact only on telegram",
                "whatsapp only", "pay through gift card",
                "send cryptocurrency", "pay in bitcoin",
                "contact through personal account"
            ),
            "Unusual or unverifiable communication and payment methods should be checked through the company's official contact channels.",
            15
        ),
        Rule(
            "Unusual hiring claim",
            listOf(
                "selected without interview", "job guaranteed",
                "100% selection guaranteed", "no interview required",
                "instant job offer",
                "hired immediately without interview"
            ),
            "A job offer without a credible selection process may require additional verification. This alone does not prove fraud.",
            20
        )
    )

    private val positivePhrases = listOf(
        "official careers page", "company website",
        "interview schedule", "job description",
        "company email", "verify the employer",
        "never pay a fee", "do not share your otp",
        "do not share your password"
    )

    private val protectivePhrases = listOf(
        "never pay a registration fee",
        "do not pay a registration fee",
        "don't pay a registration fee",
        "never pay to obtain a job",
        "do not pay to get a job",
        "don't pay to get a job",
        "never share your otp",
        "do not share your otp",
        "don't share your otp",
        "never share your password",
        "do not share your password",
        "don't share your password",
        "never share your upi pin",
        "do not share your upi pin",
        "don't share your upi pin",
        "beware of job scams",
        "avoid job scams",
        "verify the employer first"
    )

    fun analyze(message: String): OfferAnalysisResult {
        val normalized = message.lowercase()
            .replace(Regex("\\s+"), " ")
            .trim()

        if (normalized.isBlank()) {
            return OfferAnalysisResult(
                riskLevel = "UNKNOWN",
                riskScore = 0,
                summary = "Enter a message before running the analysis.",
                warningSigns = emptyList(),
                positiveSignals = emptyList(),
                recommendedActions = listOf(
                    "Paste the job offer or recruiter message and analyze it again."
                )
            )
        }

        val protectiveContext = protectivePhrases.any {
            normalized.contains(it)
        }

        val evidence = mutableListOf<WarningEvidence>()

        for (rule in rules) {
            val matchedPhrase = rule.phrases.firstOrNull { phrase ->
                normalized.contains(phrase)
            }

            if (matchedPhrase != null) {
                val clearlyProtective =
                    protectiveContext &&
                            isProtectiveMatch(normalized, matchedPhrase)

                if (!clearlyProtective) {
                    evidence.add(
                        WarningEvidence(
                            title = rule.title,
                            matchedPhrase = matchedPhrase,
                            explanation = rule.explanation,
                            points = rule.points
                        )
                    )
                }
            }
        }

        val score = evidence.sumOf { it.points }.coerceIn(0, 100)

        val hasCriticalWarning = evidence.any {
            it.title == "Sensitive information request"
        }

        val riskLevel = when {
            hasCriticalWarning || score >= 50 -> "HIGH RISK"
            score >= 25 -> "CAUTION"
            else -> "LOWER INDICATED RISK"
        }

        val positiveSignals = positivePhrases
            .filter { phrase -> normalized.contains(phrase) }
            .map { phrase ->
                when (phrase) {
                    "official careers page" ->
                        "The message mentions an official careers page."
                    "company website" ->
                        "The message mentions a company website."
                    "interview schedule" ->
                        "The message mentions an interview schedule."
                    "job description" ->
                        "The message includes a job-description reference."
                    "company email" ->
                        "The message mentions company email."
                    "verify the employer" ->
                        "The message encourages employer verification."
                    else ->
                        "The message contains advice about avoiding a potential risk."
                }
            }
            .distinct()

        val actions = when {
            hasCriticalWarning -> listOf(
                "Do not share OTPs, passwords, UPI PINs or card security details.",
                "Stop the conversation if the sender continues requesting sensitive details.",
                "Contact the company through its independently verified official website.",
                "If you have already shared financial credentials, contact your bank immediately."
            )

            score >= 50 -> listOf(
                "Do not pay money or share sensitive information until the offer is verified.",
                "Find the company's official website independently.",
                "Contact the company using contact details from its official website.",
                "Keep screenshots and report suspicious messages through the relevant platform."
            )

            score >= 25 -> listOf(
                "Verify the recruiter and job listing through official company channels.",
                "Do not pay an upfront fee to secure the position.",
                "Check the sender's email domain and independently confirm the offer."
            )

            else -> listOf(
                "Verify the company and recruiter independently.",
                "Read the job description and check the company's official careers page.",
                "Never share OTPs, passwords or banking PINs with a recruiter."
            )
        }

        val summary = when {
            hasCriticalWarning ->
                "This message contains a critical warning sign involving sensitive information. Do not share the requested details."

            score >= 50 ->
                "Several warning signs were detected. Pause before responding, paying money, or sharing personal information."

            score >= 25 ->
                "Some warning signs were detected. Verify the employer and offer independently before proceeding."

            else ->
                "Few warning patterns were detected by these rules. This does not prove that the offer is genuine or safe."
        }

        return OfferAnalysisResult(
            riskLevel = riskLevel,
            riskScore = score,
            summary = summary,
            warningSigns = evidence.map { it.title },
            positiveSignals = positiveSignals,
            recommendedActions = actions,
            evidence = evidence
        )
    }

    private fun isProtectiveMatch(
        message: String,
        matchedPhrase: String
    ): Boolean {
        val index = message.indexOf(matchedPhrase)
        if (index < 0) return false

        val start = (index - 65).coerceAtLeast(0)
        val end = (index + matchedPhrase.length + 65)
            .coerceAtMost(message.length)

        val context = message.substring(start, end)

        val negationPhrases = listOf(
            "never", "do not", "don't", "dont",
            "avoid", "beware", "not share", "not pay"
        )

        return negationPhrases.any { context.contains(it) }
    }
}

package com.sharfu.shaalevikasa

enum class Priority {
    HIGH, MEDIUM, LOW
}

enum class Status {
    PENDING, IN_PROGRESS, COMPLETED
}

data class TimelineEvent(
    val date: String,
    val title: String,
    val description: String
)

data class Pledge(
    val donorName: String,
    val contributionType: String, // e.g., "Sponsor Funds", "Donate Paint", "Arrange Desks"
    val date: String
)

data class SchoolNeed(
    val id: String,
    val title: String,
    val description: String,
    val estimatedCost: Double,
    val currentAmount: Double,
    val priority: Priority,
    val status: Status,
    val imageUrl: String? = null,
    val beforeImageUrl: String? = null,
    val afterImageUrl: String? = null,
    val testimonial: String? = null,
    val appreciationNote: String? = null,
    val timeline: List<TimelineEvent> = emptyList(),
    val pledges: List<Pledge> = emptyList()
) {
    val progress: Int
        get() = if (estimatedCost > 0) ((currentAmount / estimatedCost) * 100).toInt() else 0
}

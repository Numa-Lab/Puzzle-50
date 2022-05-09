package net.numalab.puzzle.team

import org.bukkit.entity.Player
import java.util.UUID

data class TeamSessionData(
    val sessionId: UUID,
    val team: Pair<String,List<Player>>
)
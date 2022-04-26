package net.numalab.puzzle.test

import com.github.bun133.testfly.assert
import net.numalab.puzzle.PuzzlePlugin

class PuzzleAssertion(val plugin: PuzzlePlugin) {
    val drop = plugin.assert("drop", true)
    val transfer = plugin.assert("transfer", true)
    val pickUp = plugin.assert("pickUp", true)
    val place = plugin.assert("place", true)
    val quit = plugin.assert("quit", true)
    val remove = plugin.assert("remove", true)
    val rotate = plugin.assert("rotate", true)
}
package Day04

import readInput
import kotlin.math.pow

data class Card(val numbers: List<Int>, val winners: List<Int>) {
  fun getWinningNums(): List<Int> {
    return winners.intersect(numbers.toSet()).toList()
  }

  fun getScore(): Long {
    getWinningNums().let {
      if (it.isEmpty()) {
        return 0
      }
      return 2.toDouble().pow(it.size - 1).toLong()
    }
  }
}

fun parseCard(line: String): Card {
  val (_, nums) = line.split(":").filter { it.isNotEmpty() }
  val (winningNums, cardNums) = nums.split("|").filter { it.isNotEmpty() }

  fun getInts(nums: String): List<Int> {
    return nums.split(" ").filter { it.isNotEmpty() }.map { it.toInt() }
  }

  return Card(getInts(cardNums), getInts(winningNums))
}

fun getTotal(fileName: String): Long {
  val input = readInput(fileName)
  val cards = input.map { parseCard(it) }
  val scores = cards.map { it.getScore() }
  return scores.sum()
}

fun getTotalCards(fileName: String): Long {
  val input = readInput(fileName)
  val cards = input.map { parseCard(it) }

  val numCards = mutableMapOf<Card, Long>()
  cards.forEach { numCards[it] = 1 }
  cards.forEachIndexed { i, card ->
    val numMatches = card.getWinningNums().size
    for (j in i + 1..i + numMatches) {
      if (j >= cards.size) {
        break
      }
      val nextCard = cards[j]
      numCards[nextCard] = numCards[nextCard]!! + numCards[card]!!
    }
  }
  return numCards.values.sum()
}

fun part1() {
  val testTotal = getTotal("Day04/test_input")
  println(testTotal)
  check(testTotal == 13L)
  println()

  val total = getTotal("Day04/input")
  println(total)

  println()
  println()
}

fun part2() {

  val testTotal = getTotalCards("Day04/test_input")
  println(testTotal)
  check(testTotal == 30L)
  println()

  val total = getTotalCards("Day04/input")
  println(total)

  println()
  println()
}

fun main() {
  part1()
  part2()
}

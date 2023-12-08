package Day07

import readInput

data class Card(val label: Char, val jokerRule: Boolean = false) {
  val strength: Int = (if (jokerRule) JOKER_RULE_RANKING else RANKING).indexOf(label)

  companion object {
    const val RANKING = "23456789TJQKA"
    const val JOKER_RULE_RANKING = "J23456789TQKA"

    val Joker = Card('J', true)
  }
}

class Hand(val cards: List<Card>, val bid: Int, jokerRule: Boolean = false) {
  val type = HandType.getType(cards, jokerRule)

  override fun toString(): String {
    return cardsAsString(cards) + " $bid"
  }

  companion object {
    fun cardsAsString(c: List<Card>): String {
      return c.fold("") { acc, card -> acc + card.label }
    }
  }
}

fun List<Card>.toCardsMap(jokerRule: Boolean = false): Map<Card, Int> {
  val cardsMap = mutableMapOf<Card, Int>()
  for (card in this) {
    if (card !in cardsMap) {
      cardsMap[card] = 0
    }
    cardsMap[card] = cardsMap[card]!! + 1
  }
  if (jokerRule) {
    if (Card.Joker in cardsMap && cardsMap.size != 1) {
      val numJokers = cardsMap[Card.Joker]!!
      cardsMap.remove(Card.Joker)
      val maxCard = cardsMap.maxBy { it.value }.key
      cardsMap[maxCard] = cardsMap[maxCard]!! + numJokers
    }
  }
  return cardsMap
}

fun List<Card>.getCount(jokerRule: Boolean = false): List<Int> {
  return this.toCardsMap(jokerRule).values.toList().sortedDescending()
}

enum class HandType(val strength: Int) {
  FIVE(6) {
    override fun isType(cards: List<Card>, jokerRule: Boolean): Boolean {
      return cards.getCount(jokerRule) == listOf(5)
    }
  },
  FOUR(5) {
    override fun isType(cards: List<Card>, jokerRule: Boolean): Boolean {
      return cards.getCount(jokerRule) == listOf(4, 1)
    }
  },
  FULL_HOUSE(4) {
    override fun isType(cards: List<Card>, jokerRule: Boolean): Boolean {
      return cards.getCount(jokerRule) == listOf(3, 2)
    }
  },
  THREE(3) {
    override fun isType(cards: List<Card>, jokerRule: Boolean): Boolean {
      return cards.getCount(jokerRule) == listOf(3, 1, 1)
    }
  },
  TWO_PAIR(2) {
    override fun isType(cards: List<Card>, jokerRule: Boolean): Boolean {
      return cards.getCount(jokerRule) == listOf(2, 2, 1)
    }
  },
  ONE_PAIR(1) {
    override fun isType(cards: List<Card>, jokerRule: Boolean): Boolean {
      return cards.getCount(jokerRule) == listOf(2, 1, 1, 1)
    }
  },
  HIGH(0) {
    override fun isType(cards: List<Card>, jokerRule: Boolean): Boolean {
      return cards.getCount(jokerRule) == listOf(1, 1, 1, 1, 1)
    }
  };

  abstract fun isType(cards: List<Card>, jokerRule: Boolean = false): Boolean

  companion object {
    fun getType(cards: List<Card>, jokerRule: Boolean = false): HandType {
      entries.forEach {
        if (it.isType(cards, jokerRule)) {
          return it
        }
      }
      throw Exception(
          "Hand type must exist: " + Hand.cardsAsString(cards) + ", " + cards.getCount(jokerRule))
    }
  }
}

fun parseHands(input: List<String>, jokerRule: Boolean = false): List<Hand> {
  return input.map { line ->
    val (cardLabels, bid) = line.split(" ")
    val cards = cardLabels.map { Card(it, jokerRule) }
    return@map Hand(cards, bid.toInt(), jokerRule)
  }
}

fun getWinnings(fileName: String, jokerRule: Boolean = false): Long {

  val input = readInput(fileName)
  val hands = parseHands(input, jokerRule)
  val sortedHands =
      hands.sortedWith { hand1, hand2 ->
        if (hand1.type.strength != hand2.type.strength) {
          return@sortedWith hand1.type.strength - hand2.type.strength
        }

        for ((card1, card2) in hand1.cards zip hand2.cards) {
          if (card1.strength != card2.strength) {
            return@sortedWith card1.strength - card2.strength
          }
        }
        return@sortedWith 0
      }

  return sortedHands.foldIndexed(0L) { i, acc, hand -> acc + ((i + 1) * hand.bid) }
}

fun part1() {
  val testWinnings = getWinnings("Day07/test_input")
  println(testWinnings)
  check(testWinnings == 6440L)
  println()

  val winnings = getWinnings("Day07/input")
  println(winnings)

  println()
  println()
}

fun part2() {
  val testWinnings = getWinnings("Day07/test_input", true)
  println(testWinnings)
  check(testWinnings == 5905L)
  println()

  val winnings = getWinnings("Day07/input", true)
  println(winnings)
}

fun main() {
  part1()
  part2()
}

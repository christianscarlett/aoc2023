package Day01

import println
import readInput

enum class Digit(val stringName: String, val intValue: Int) {
  ONE("one", 1),
  TWO("two", 2),
  THREE("three", 3),
  FOUR("four", 4),
  FIVE("five", 5),
  SIX("six", 6),
  SEVEN("seven", 7),
  EIGHT("eight", 8),
  NINE("nine", 9),
}

fun main() {

  fun part1(input: List<String>): Int {

    fun getCalibrationValue(input: String): Int {
      val firstDigit = input.first { it.isDigit() }
      val lastDigit = input.last { it.isDigit() }
      return (firstDigit.toString() + lastDigit.toString()).toInt()
    }

    return input.sumOf { getCalibrationValue(it) }
  }

  fun part2(input: List<String>): Int {

    fun matchesDigit(input: String, i: Int, digit: Digit): Boolean {
      return input[i].toString() == digit.intValue.toString() ||
          (i + digit.stringName.length <= input.length &&
              input.substring(i, i + digit.stringName.length) == digit.stringName)
    }

    fun getDigit(input: String, indices: IntProgression): String {
      for (i in indices) {
        for (digit in Digit.entries) {
          if (matchesDigit(input, i, digit)) {
            return digit.intValue.toString()
          }
        }
      }
      throw Exception("Digit not found!")
    }

    fun getCalibrationValue(input: String): Int {
      val firstDigit = getDigit(input, input.indices)
      val lastDigit = getDigit(input, input.indices.reversed())
      return (firstDigit + lastDigit).toInt()
    }

    return input.sumOf { getCalibrationValue(it) }
  }

  // test if implementation meets criteria from the description, like:
  val testInput = readInput("Day01/Day01_test")
  println(part1(testInput))
  check(part1(testInput) == 142)

  println()

  val testInput2 = readInput("Day01/Day01_test2")
  println(part2(testInput2))
  check(part2(testInput2) == 281)

  println()

  val input = readInput("Day01/Day01")
  part1(input).println()
  part2(input).println()
}

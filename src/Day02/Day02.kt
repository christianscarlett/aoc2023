package Day02

import readInput

data class Round(val numRed: Int, val numGreen: Int, val numBlue: Int) {
  fun isPossible(maxRound: Round): Boolean {
    return numRed <= maxRound.numRed && numGreen <= maxRound.numGreen && numBlue <= maxRound.numBlue
  }

  fun getPower(): Int {
    return numRed * numGreen * numBlue
  }
}

data class Game(val id: Int, val rounds: List<Round>) {
  fun isPossible(maxRound: Round): Boolean {
    return rounds.all { it.isPossible(maxRound) }
  }

  fun getMinPossibleCubes(): Round =
      Round(rounds.maxOf { it.numRed }, rounds.maxOf { it.numGreen }, rounds.maxOf { it.numBlue })
}

fun parseGame(inputGame: String): Game {
  val (gameName, roundValues) = inputGame.split(":")
  val id = gameName.filter { it.isDigit() }.toInt()
  val rounds =
      roundValues.split(";").map { roundValue ->
        var numRed = 0
        var numGreen = 0
        var numBlue = 0
        roundValue.split(",").forEach { numColorString ->
          val num = numColorString.filter { it.isDigit() }.toInt()
          when {
            numColorString.contains("red") -> {
              numRed = num
            }
            numColorString.contains("green") -> {
              numGreen = num
            }
            numColorString.contains("blue") -> {
              numBlue = num
            }
          }
        }
        return@map Round(numRed, numGreen, numBlue)
      }
  return Game(id, rounds)
}

fun parseGames(fileName: String): List<Game> {
  val input = readInput(fileName)
  return input.map { parseGame(it) }
}

fun getIdSum(games: List<Game>, maxRound: Round): Int =
    games.sumOf {
      if (it.isPossible(maxRound)) {
        return@sumOf it.id
      }
      return@sumOf 0
    }

fun getPowerSum(games: List<Game>): Int = games.sumOf { it.getMinPossibleCubes().getPower() }

fun part1() {
  val maxRound = Round(12, 13, 14)

  val testGames = parseGames("Day02/test_input")
  val testSol = getIdSum(testGames, maxRound)
  println(testSol)
  check(testSol == 8)

  println()

  val games = parseGames("Day02/input")
  val sol = getIdSum(games, maxRound)
  println(sol)
  println()
  println()
}

fun part2() {
  val testGames = parseGames("Day02/test_input")
  val testSol = getPowerSum(testGames)
  println(testSol)
  check(testSol == 2286)

  val games = parseGames("Day02/input")
  val sol = getPowerSum(games)
  println(sol)
}

fun main() {
  part1()
  part2()
}

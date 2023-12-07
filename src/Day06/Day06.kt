package Day06

import readInput
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt

data class Race(val time: Long, val distance: Long) {
  fun getNumWays(): Long {

    val t_min = (time - sqrt((time * time - 4 * distance).toDouble())) / 2
    val t_max = (time + sqrt((time * time - 4 * distance).toDouble())) / 2

    return ((ceil(t_max) - 1) - (floor(t_min) + 1) + 1).toLong()
  }
}

fun getInputNums(row: String) = row.split(":")[1].split(" ").filter { it.isNotEmpty() }

fun getNumWays(fileName: String): Long {
  val input = readInput(fileName)
  val times = getInputNums(input[0]).map { it.toLong() }
  val distances = getInputNums(input[1]).map { it.toLong() }
  check(times.size == distances.size)
  val races = times.indices.map { i -> Race(times[i], distances[i]) }
  val allNumWays = races.map { it.getNumWays() }
  val totalWays = allNumWays.reduce { acc, numWays -> acc * numWays }

  return totalWays
}

fun getNumWaysPart2(fileName: String): Long {
  val input = readInput(fileName)
  val times = getInputNums(input[0])
  val distances = getInputNums(input[1])

  val time = times.reduce { acc, t -> acc + t }.toLong()
  val distance = distances.reduce { acc, t -> acc + t }.toLong()

  return Race(time, distance).getNumWays()
}

fun part1() {
  val testSol = getNumWays("Day06/test_input")
  println(testSol)
  check(testSol == 288L)

  println()

  val sol = getNumWays("Day06/input")
  println(sol)

  println()
  println()
}

fun part2() {
  val testSol = getNumWaysPart2("Day06/test_input")
  println(testSol)
  check(testSol == 71503L)

  println()

  val sol = getNumWaysPart2("Day06/input")
  println(sol)
}

fun main() {
  part1()
  part2()
}

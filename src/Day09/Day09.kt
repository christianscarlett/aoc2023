package Day09

import readInput

class Sequence(val values: List<Int>) {
  val isZeroes = values.all { it == 0 }

  fun getDerivative(): Sequence {
    val newValues = mutableListOf<Int>()
    for (i in 0 ..< values.size - 1) {
      newValues.add(values[i + 1] - values[i])
    }
    return Sequence(newValues)
  }

  fun getPredictive(): Sequence {
    if (isZeroes) {
      return Sequence(values + listOf(0))
    }
    return Sequence(values + listOf(values.last() + getDerivative().getPredictive().values.last()))
  }

  fun getPredictivePast(): Sequence {
    if (isZeroes) {
      return Sequence(values + listOf(0))
    }
    return Sequence(
        listOf(values.first() - getDerivative().getPredictivePast().values.first()) + values)
  }
}

fun getValues(input: List<String>): List<List<Int>> {
  return input.map { line -> line.split(" ").filter { it.isNotEmpty() }.map { it.toInt() } }
}

fun part1() {
  println("-----")
  println("Part1")
  println("-----")
  for (fileName in
      listOf(
          "Day09/test_input",
          "Day09/input",
      )) {
    println(fileName)
    val input = readInput(fileName)
    val sequences = getValues(input).map { Sequence(it) }
    val predictives = sequences.map { it.getPredictive() }
    val predictions = predictives.map { it.values.last() }
    val total = predictions.sum()
    println(total)
    println()
  }
  println()
  println("--")
  println()
}

fun part2() {
  println("-----")
  println("Part2")
  println("-----")
  for (fileName in
      listOf(
          "Day09/test_input",
          "Day09/input",
      )) {
    println(fileName)
    val input = readInput(fileName)
    val sequences = getValues(input).map { Sequence(it) }
    val predictives = sequences.map { it.getPredictivePast() }
    val predictions = predictives.map { it.values.first() }
    val total = predictions.sum()
    println(total)
    println()
  }
  println()
  println("--")
  println()
}

fun main() {
  part1()
  part2()
}

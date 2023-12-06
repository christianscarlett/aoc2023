package Day03

import readInput

data class Index(val col: Int, val row: Int)

data class Number(val value: Int, val startIdx: Index) {
  fun getAdjacentIndices(width: Int, height: Int): List<Index> {
    val length = value.toString().length
    val left = startIdx.col - 1
    val right = startIdx.col + length

    val adjacentIndices =
        mutableListOf<Index>(Index(left, startIdx.row), Index(right, startIdx.row))

    val horizontalSpan = (left..right).toList()
    horizontalSpan.forEach { col ->
      adjacentIndices.add(Index(col, startIdx.row - 1))
      adjacentIndices.add(Index(col, startIdx.row + 1))
    }
    return adjacentIndices.filter { it.col in 0 ..< width && it.row in 0 ..< height }
  }
}

data class Gear(val location: Index)

fun Char.isSymbol(): Boolean {
  return !this.isDigit() && this != '.'
}

fun isPartNumber(num: Number, input: List<String>): Boolean {
  val adjIdxs = num.getAdjacentIndices(input[0].length, input.size)

  for (adjIdx in adjIdxs) {
    val c = input[adjIdx.row][adjIdx.col]
    if (c.isSymbol()) {
      return true
    }
  }
  return false
}

fun getNumbers(input: List<String>): List<Number> {

  val inputNums = input.map { it.split(Regex("\\D")).filter { it.isNotEmpty() } }

  val finalNums = mutableListOf<Number>()
  inputNums.forEachIndexed { row, nums ->
    val inputRow = input[row]
    val seenNums = mutableSetOf<String>()
    for (num in nums) {
      if (num in seenNums) {
        continue
      }
      seenNums.add(num)
      val numMatchCols =
          Regex("\\b" + num + "\\b").findAll(inputRow).map { it.range.first }.toList()
      numMatchCols.forEach { finalNums.add(Number(num.toInt(), Index(it, row))) }
    }
  }
  return finalNums
}

fun getGearSum(fileName: String): Int {
  val input = readInput(fileName)
  val nums = getNumbers(input)

  val gears = mutableSetOf<Gear>()
  val gearToPartNums = mutableMapOf<Gear, MutableSet<Number>>()
  for (num in nums) {
    for (idx in num.getAdjacentIndices(input[0].length, input.size)) {
      val c = input[idx.row][idx.col]
      if (c == '*') {
        val gear = Gear(idx)
        gears.add(gear)
        if (gearToPartNums[gear] == null) {
          gearToPartNums[gear] = mutableSetOf()
        }
        gearToPartNums[gear]?.add(num)
      }
    }
  }

  return gears.sumOf { gear ->
    val partNums = gearToPartNums[gear]
    if (partNums?.size == 2) {
      return@sumOf partNums.toList().let { it[0].value * it[1].value }
    }
    return@sumOf 0
  }
}

fun getSum(fileName: String): Int {

  val input = readInput(fileName)
  val finalNums = getNumbers(input)
  val partNums = finalNums.filter { isPartNumber(it, input) }

  return partNums.sumOf { it.value }
}

fun part2() {
  val testSum = getGearSum("Day03/test_input")
  println(testSum)
  check(testSum == 467835)
  println()

  val s = getGearSum("Day03/input")
  println(s)
}

fun part1() {
  val testSum = getSum("Day03/test_input")
  println(testSum)
  check(testSum == 4361)
  println()

  val s = getSum("Day03/input")
  println(s)

  println()
  println()
}

fun main() {
  part1()
  part2()
}

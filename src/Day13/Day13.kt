package Day13

import readInput

class Pattern(val rows: List<String>) {
  val cols = getAsCols(rows)

  fun findReflection(lines: List<String>, numSmudges: Int): Int? {
    return (0 ..< lines.size - 1).find { getReflectionDifferences(it, lines) == numSmudges }
  }

  fun getReflectionDifferences(split: Int, lines: List<String>): Int {
    var i = split
    var j = split + 1
    var differences = 0
    while (0 <= i && j < lines.size) {
      differences += getNumDifferences(lines[i], lines[j])
      i--
      j++
    }
    return differences
  }

  fun getNumDifferences(line1: String, line2: String): Int {
    return line1.mapIndexedNotNull { i, c -> if (c != line2[i]) c else null }.size
  }

  fun findHorizontalReflection(numSmudges: Int): Int? {
    return findReflection(rows, numSmudges)
  }

  fun findVerticalReflection(numSmudges: Int): Int? {
    return findReflection(cols, numSmudges)
  }

  fun getSummary(numSmudges: Int): Int {
    return 100 * ((findHorizontalReflection(numSmudges) ?: -1) + 1) +
        ((findVerticalReflection(numSmudges) ?: -1) + 1)
  }

  override fun toString(): String {
    return rows.joinToString("\n")
  }

  fun colsToString(): String {
    return cols.joinToString("\n")
  }

  companion object {
    fun getAsCols(rows: List<String>): List<String> {
      val numCols = rows[0].length
      return (0 until numCols).map { col -> rows.map { it[col] } }.map { it.joinToString("") }
    }

    fun fromInput(input: List<String>): List<Pattern> {
      val emptyRows =
          input.mapIndexedNotNull { i, row -> if (row.any { it != ' ' }) null else i } +
              listOf(input.size)
      return emptyRows.mapIndexed { emptyRowsIdx, i ->
        val j = if (emptyRowsIdx == 0) 0 else emptyRows[emptyRowsIdx - 1] + 1
        val rows = input.subList(j, i)
        return@mapIndexed Pattern(rows)
      }
    }
  }
}

fun part1() {
  println("-----")
  println("Part1")
  println("-----")
  for (fileName in
      listOf(
          "Day13/test_input",
          "Day13/input",
      )) {
    println(fileName)
    val input = readInput(fileName)
    val patterns = Pattern.fromInput(input)
    val sol = patterns.sumOf { it.getSummary(0) }
    println(sol)
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
          "Day13/test_input",
          "Day13/input",
      )) {
    println(fileName)
    val input = readInput(fileName)
    val patterns = Pattern.fromInput(input)
    val sol = patterns.sumOf { it.getSummary(1) }
    println(sol)
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

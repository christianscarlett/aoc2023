package Day11

import readInput
import kotlin.math.absoluteValue

data class Coord(val row: Int, val col: Int)

data class Galaxy(val pos: Coord)

class Universe(val galaxies: Set<Galaxy>, val height: Int, val width: Int) {

  val galaxiesMap = galaxies.associateBy { it.pos }

  fun getAllPairs(): Set<Pair<Galaxy, Galaxy>> {
    val pairs = mutableSetOf<Set<Galaxy>>()
    for (g1 in galaxies) {
      for (g2 in galaxies) {
        if (g1 == g2) continue
        pairs.add(setOf(g1, g2))
      }
    }
    return pairs.map { Pair(it.first(), it.last()) }.toSet()
  }

  fun getExpandedDistance(g1: Galaxy, g2: Galaxy, expansion: Int): Long {
    var totalDistance: Long = 0

    val emptyRows = getEmptyRows().toSet()
    val emptyCols = getEmptyCols().toSet()

    val largeRow = Math.max(g1.pos.row, g2.pos.row)
    val smallRow = Math.min(g1.pos.row, g2.pos.row)

    for (row in smallRow ..< largeRow) {
      if (row in emptyRows) {
        totalDistance += expansion
      } else {
        totalDistance++
      }
    }

    val largeCol = Math.max(g1.pos.col, g2.pos.col)
    val smallCol = Math.min(g1.pos.col, g2.pos.col)

    for (col in smallCol ..< largeCol) {
      if (col in emptyCols) {
        totalDistance += expansion
      } else {
        totalDistance++
      }
    }

    return totalDistance
  }

  fun getTotalExpandedDistance(expansion: Int): Long {
    return getAllPairs().fold(0) { acc, pair ->
      acc + getExpandedDistance(pair.first, pair.second, expansion)
    }
  }

  fun getDistance(g1: Galaxy, g2: Galaxy): Int {
    return (g1.pos.row - g2.pos.row).absoluteValue + (g1.pos.col - g2.pos.col).absoluteValue
  }

  fun getTotalDistance(): Int {
    return getAllPairs().fold(0) { acc, pair -> acc + getDistance(pair.first, pair.second) }
  }

  fun getEmptyRows(): List<Int> {
    val galaxyRows = galaxies.map { it.pos.row }.toSet()
    return (0 ..< height).toList().filter { it !in galaxyRows }
  }

  fun getEmptyCols(): List<Int> {
    val galaxyCols = galaxies.map { it.pos.col }.toSet()
    return (0 ..< width).toList().filter { it !in galaxyCols }
  }

  fun getExpanded(): Universe {
    val emptyRows = getEmptyRows().toSet()
    val emptyCols = getEmptyCols().toSet()

    val newGalaxies = mutableSetOf<Galaxy>()

    var rowOffset = 0
    for (row in 0 ..< height) {
      if (row in emptyRows) {
        rowOffset++
        continue
      }
      var colOffset = 0
      for (col in 0 ..< width) {
        if (col in emptyCols) {
          colOffset++
          continue
        }
        val c = Coord(row, col)
        if (c in galaxiesMap) {
          newGalaxies.add(Galaxy(Coord(c.row + rowOffset, c.col + colOffset)))
        }
      }
    }
    return Universe(newGalaxies, height + emptyRows.size, width + emptyCols.size)
  }

  fun print() {
    for (row in 0 ..< height) {
      var line = ""
      for (col in 0 ..< width) {
        val c = Coord(row, col)
        if (c in galaxiesMap) {
          line += '#'
        } else {
          line += '.'
        }
      }
      println(line)
    }
  }

  companion object {
    fun fromInput(input: List<String>): Universe {
      val galaxies = mutableSetOf<Galaxy>()
      for (row in input.indices) {
        val line = input[row]
        for (col in line.indices) {
          if (line[col] == '#') {
            galaxies.add(Galaxy(Coord(row, col)))
          }
        }
      }
      return Universe(galaxies, input.size, input[0].length)
    }
  }
}

fun part1() {
  println("-----")
  println("Part1")
  println("-----")
  for (fileName in
      listOf(
          "Day11/test_input",
          "Day11/input",
      )) {
    println(fileName)
    val input = readInput(fileName)
    val u = Universe.fromInput(input)
    val u2 = u.getExpanded()
    println(u2.getTotalDistance())
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
          //          "Day11/test_input",
          "Day11/input",
      )) {
    println(fileName)
    val input = readInput(fileName)
    val u = Universe.fromInput(input)
    println(u.getTotalExpandedDistance(1_000_000))
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

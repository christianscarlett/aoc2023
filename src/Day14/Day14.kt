package Day14

import readInput

data class Platform(val rows: List<String>) {

  fun getTiltedLine(line: String, tiltLeft: Boolean): String {
    var sections = line.split('#')
    sections =
        sections.map {
          val numRocks = it.count { it == 'O' }
          val rocks = "O".repeat(numRocks)
          val empty = ".".repeat(it.length - numRocks)
          return@map if (tiltLeft) rocks + empty else empty + rocks
        }
    return sections.joinToString("#")
  }

  fun tiltCol(north: Boolean): Platform {
    val cols = getAsCols(rows)
    val tiltedCols = cols.map { getTiltedLine(it, north) }
    val newRows = getAsCols(tiltedCols)
    return Platform(newRows)
  }

  fun tiltRow(west: Boolean): Platform {
    val tiltedRows = rows.map { getTiltedLine(it, west) }
    return Platform(tiltedRows)
  }

  fun tiltNorth(): Platform {
    return tiltCol(true)
  }

  fun tiltSouth(): Platform {
    return tiltCol(false)
  }

  fun tiltWest(): Platform {
    return tiltRow(true)
  }

  fun tiltEast(): Platform {
    return tiltRow(false)
  }

  fun cycle(): Platform {
    return tiltNorth().tiltWest().tiltSouth().tiltEast()
  }

  fun getLoad(): Int {
    var total = 0
    for (i in rows.indices) {
      total += (rows.size - i) * rows[i].count { it == 'O' }
    }
    return total
  }

  fun spin(numSpins: Int): Int {
    var p = this
    val seenPlatforms = mutableSetOf(p)
    val idxToPlatform = mutableMapOf(0 to p)
    var loopEnd = 0
    var loopEndPlatform = Platform(listOf())
    for (i in 0 ..< numSpins) {
      p = p.cycle()
      if (p in seenPlatforms) {
        loopEnd = i
        loopEndPlatform = p
        break
      }
      seenPlatforms.add(p)
      idxToPlatform[i] = p
    }

    val loopStart = idxToPlatform.map { (k, v) -> v to k }.toMap()[loopEndPlatform]!!
    val loopLength = loopEnd - loopStart

    val loopingSpins = numSpins - loopEnd
    val remainder = loopingSpins % loopLength

    return idxToPlatform[loopStart + remainder - 1]!!.getLoad()
  }

  fun print() {
    println(rows.joinToString("\n"))
  }

  companion object {

    fun getAsCols(rows: List<String>): List<String> {
      val numCols = rows[0].length
      return (0 until numCols).map { col -> rows.map { it[col] } }.map { it.joinToString("") }
    }
  }
}

fun part1() {
  println("-----")
  println("Part1")
  println("-----")
  for (fileName in
      listOf(
          "Day14/test_input",
          "Day14/input",
      )) {
    println(fileName)
    val input = readInput(fileName)
    val p = Platform(input)
    val p2 = p.tiltNorth()
    println(p2.getLoad())
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
          "Day14/test_input",
          "Day14/input",
      )) {
    println(fileName)
    val input = readInput(fileName)
    val p = Platform(input)
    val sol = p.spin(1000000000)
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

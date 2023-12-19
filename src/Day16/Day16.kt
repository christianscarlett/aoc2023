package Day16

import readInput

data class Coord(val row: Int, val col: Int)

class EnergyState {
  val beamDirs = mutableSetOf<Direction>()

  fun contains(el: Direction) = beamDirs.contains(el)

  fun add(el: Direction) = beamDirs.add(el)

  fun isEnergized() = beamDirs.isNotEmpty()
}

enum class Direction {
  UP {
    override fun getNext(pos: Coord): Coord {
      return Coord(pos.row - 1, pos.col)
    }
  },
  DOWN {
    override fun getNext(pos: Coord): Coord {
      return Coord(pos.row + 1, pos.col)
    }
  },
  LEFT {
    override fun getNext(pos: Coord): Coord {
      return Coord(pos.row, pos.col - 1)
    }
  },
  RIGHT {
    override fun getNext(pos: Coord): Coord {
      return Coord(pos.row, pos.col + 1)
    }
  };

  abstract fun getNext(pos: Coord): Coord
}

operator fun List<String>.get(coord: Coord): Char {
  return this[coord.row][coord.col]
}

enum class Tile {
  EMPTY {
    override fun getNext(pos: Coord, dir: Direction): List<Pair<Coord, Direction>> {
      return listOf(Pair(dir.getNext(pos), dir))
    }
  },
  VERTICAL {
    override fun getNext(pos: Coord, dir: Direction): List<Pair<Coord, Direction>> {
      if (dir in setOf(Direction.LEFT, Direction.RIGHT)) {
        return listOf(
            Pair(Coord(pos.row - 1, pos.col), Direction.UP),
            Pair(Coord(pos.row + 1, pos.col), Direction.DOWN))
      }
      return listOf(Pair(dir.getNext(pos), dir))
    }
  },
  HORIZONTAL {
    override fun getNext(pos: Coord, dir: Direction): List<Pair<Coord, Direction>> {
      if (dir in setOf(Direction.UP, Direction.DOWN)) {
        return listOf(
            Pair(Coord(pos.row, pos.col - 1), Direction.LEFT),
            Pair(Coord(pos.row, pos.col + 1), Direction.RIGHT))
      }
      return listOf(Pair(dir.getNext(pos), dir))
    }
  },
  SLASH {
    override fun getNext(pos: Coord, dir: Direction): List<Pair<Coord, Direction>> {
      val newDir =
          when (dir) {
            Direction.RIGHT -> Direction.UP
            Direction.UP -> Direction.RIGHT
            Direction.DOWN -> Direction.LEFT
            Direction.LEFT -> Direction.DOWN
          }
      return listOf(Pair(newDir.getNext(pos), newDir))
    }
  },
  BACKSLASH {
    override fun getNext(pos: Coord, dir: Direction): List<Pair<Coord, Direction>> {
      val newDir =
          when (dir) {
            Direction.RIGHT -> Direction.DOWN
            Direction.DOWN -> Direction.RIGHT
            Direction.UP -> Direction.LEFT
            Direction.LEFT -> Direction.UP
          }
      return listOf(Pair(newDir.getNext(pos), newDir))
    }
  };

  abstract fun getNext(pos: Coord, dir: Direction): List<Pair<Coord, Direction>>
}

class Energizer(val input: List<String>) {
  val energized = mutableMapOf<Coord, EnergyState>()

  init {
    reset()
  }

  fun reset() {
    for (i in input.indices) {
      for (j in input[0].indices) {
        energized[Coord(i, j)] = EnergyState()
      }
    }
  }

  fun getTile(c: Char): Tile {
    return when (c) {
      '.' -> Tile.EMPTY
      '|' -> Tile.VERTICAL
      '-' -> Tile.HORIZONTAL
      '/' -> Tile.SLASH
      '\\' -> Tile.BACKSLASH
      else -> throw Exception("Invalid tile char: '$c' " + (c == '.'))
    }
  }

  fun getNext(pos: Coord, dir: Direction): List<Pair<Coord, Direction>> {
    val t = getTile(input[pos])
    return t.getNext(pos, dir)
  }

  fun beam(pos: Coord, dir: Direction) {
    if (pos !in energized) {
      return
    }
    val energyState = energized.getValue(pos)
    if (energyState.contains(dir)) {
      return
    }
    energyState.add(dir)
    getNext(pos, dir).forEach { beam(it.first, it.second) }
  }

  fun getNumEnergized(): Int {
    return energized.count { it.value.isEnergized() }
  }
}

fun simAll(input: List<String>): Int {
  val e = Energizer(input)
  val maxRow = input.size - 1
  val maxCol = input[0].length - 1
  val starts =
      input.indices
          .map { row ->
            listOf(Pair(Coord(row, 0), Direction.RIGHT), Pair(Coord(row, maxCol), Direction.LEFT))
          }
          .flatten() +
          input[0]
              .indices
              .map { col ->
                listOf(Pair(Coord(0, col), Direction.DOWN), Pair(Coord(maxRow, col), Direction.UP))
              }
              .flatten()
  return starts.maxOf { (coord, dir) ->
    e.beam(coord, dir)
    val numEnergized = e.getNumEnergized()
    e.reset()
    return@maxOf numEnergized
  }
}

fun part1() {
  println("-----")
  println("Part1")
  println("-----")
  for (fileName in
      listOf(
          "Day16/test_input",
          "Day16/input",
      )) {
    println(fileName)
    val input = readInput(fileName)
    val e = Energizer(input)
    e.beam(Coord(0, 0), Direction.RIGHT)
    println(e.getNumEnergized())
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
          "Day16/test_input",
          "Day16/input",
      )) {
    println(fileName)
    val input = readInput(fileName)
    println(simAll(input))
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

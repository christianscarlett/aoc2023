package Day10

import readInput

class Network(val pipeMap: Map<Coord, Pipe>) {

  val pipes = pipeMap.values.toList()

  val minRow = pipeMap.values.minOf { it.pos.row }
  val maxRow = pipeMap.values.maxOf { it.pos.row }
  val minCol = pipeMap.values.minOf { it.pos.col }
  val maxCol = pipeMap.values.maxOf { it.pos.col }

  fun getStartPipeType(start: Coord): PipeType {
    val startAdj =
        getStartAdj(start)
            .mapNotNull { pipeMap[it] }
            .filter { it.isValidConnection(start) }
            .map { it.pos }
            .toSet()
    PipeType.entries.forEach {
      val pipeAdj = it.getAdj(start).toList().toSet()
      if (pipeAdj == startAdj) {
        return it
      }
    }
    throw Exception("Start must have pipe type.")
  }

  fun countEnclosed(start: Coord): Int {
    val pipeMapWithStart = pipeMap + (start to Pipe(start, getStartPipeType(start)))
    var totalEnclosed = 0
    for (row in minRow..maxRow) {
      var isInside = false
      for (col in minCol..maxCol) {
        val c = Coord(row, col)
        val pipe = pipeMapWithStart[c]
        if (pipe != null) {
          if (pipe.pointsNorth) {
            isInside = !isInside
          }
          continue
        }
        if (isInside) {
          totalEnclosed++
        }
      }
    }

    return totalEnclosed
  }

  fun findFarthest(start: Coord): Int {
    val distances = mutableMapOf<Pipe, Int>()
    var distance = 1

    var nextPipes =
        getStartAdj(start).mapNotNull { pipeMap[it] }.filter { it.isValidConnection(start) }
    nextPipes.forEach { distances[it] = distance }

    val visitedPos = mutableSetOf<Coord>()
    visitedPos.addAll(nextPipes.map { it.pos })

    while (nextPipes.isNotEmpty()) {
      nextPipes.forEach { distances[it] = distance }
      distance++
      nextPipes =
          nextPipes
              .map { pipe ->
                return@map getNext(visitedPos, pipe)
                    .filter { it in pipeMap.keys }
                    .map { pipeMap[it]!! }
                    .filter { pipe.isValidJoint(it) }
              }
              .flatten()
      visitedPos.addAll(nextPipes.map { it.pos })
    }
    return distances.values.max()
  }

  fun getStartAdj(start: Coord): List<Coord> {
    return listOf(
        Coord(start.row + 1, start.col),
        Coord(start.row - 1, start.col),
        Coord(start.row, start.col + 1),
        Coord(start.row, start.col - 1))
  }

  fun getNext(visitedPos: Set<Coord>, pipe: Pipe): List<Coord> {
    return pipe.adj.toList().filter { it !in visitedPos }
  }

  fun getEnds(): List<Pipe> {
    var nextPipes = listOf(pipes[0]) // random start
    val finalPipes = mutableListOf<Pipe>()
    val visitedPos = mutableSetOf(pipes[0].pos)
    while (finalPipes.size < 2 && nextPipes.isNotEmpty()) {
      nextPipes =
          nextPipes
              .map { pipe ->
                val nextCoords = getNext(visitedPos, pipe)
                if (nextCoords.any { it !in pipeMap.keys }) {
                  finalPipes.add(pipe)
                }
                return@map nextCoords.filter { it in pipeMap.keys }
              }
              .flatten()
              .map { pipeMap[it]!! }
      visitedPos.addAll(nextPipes.map { it.pos })
    }
    //    println(finalPipes)
    return finalPipes
  }

  fun getPotentialStart(): Coord? {
    val ends = getEnds()

    if (ends.size != 2) {
      return null
    }
    return ends[0].adj.toList().toSet().intersect(ends[1].adj.toList().toSet()).firstOrNull()
  }

  override fun toString(): String {
    return "N{" + pipes.fold("") { acc, pipe -> acc + " " + pipe.type.symbol } + " }"
  }

  companion object {
    fun fromPipe(allPipeMap: Map<Coord, Pipe>, startingPipe: Pipe): Network {
      val pipeMap = mutableMapOf(startingPipe.pos to startingPipe)
      var nextPipes = listOf(startingPipe)
      while (nextPipes.isNotEmpty()) {
        nextPipes =
            nextPipes
                .map { pipe ->
                  pipe.adj
                      .toList()
                      .mapNotNull { coord -> allPipeMap[coord] }
                      .filter { pipe.isValidJoint(it) }
                }
                .flatten()
                .filter { it.pos !in pipeMap }
        nextPipes.forEach { pipeMap[it.pos] = it }
      }
      return Network(pipeMap)
    }
  }
}

data class Coord(val row: Int, val col: Int)

class Pipe(val pos: Coord, val type: PipeType) {

  val adj = type.getAdj(pos)

  val pointsNorth = adj.toList().any { it.row == pos.row - 1 }

  fun isValidConnection(coord: Coord): Boolean {
    return adj.toList().contains(coord)
  }

  fun isValidJoint(pipe: Pipe): Boolean {
    return this.adj.toList().contains(pipe.pos) && pipe.adj.toList().contains(this.pos)
  }

  override fun toString(): String {
    return "(" + pos.row + ", " + pos.col + ", " + type.symbol + ")"
  }
}

enum class PipeType {
  Vertical {
    override fun getAdj(pos: Coord): Pair<Coord, Coord> {
      return Pair(Coord(pos.row - 1, pos.col), Coord(pos.row + 1, pos.col))
    }

    override val symbol: Char = '|'
  },
  Horizontal {
    override fun getAdj(pos: Coord): Pair<Coord, Coord> {
      return Pair(Coord(pos.row, pos.col - 1), Coord(pos.row, pos.col + 1))
    }

    override val symbol: Char = '-'
  },
  NorthEast {
    override fun getAdj(pos: Coord): Pair<Coord, Coord> {
      return Pair(Coord(pos.row - 1, pos.col), Coord(pos.row, pos.col + 1))
    }

    override val symbol: Char = 'L'
  },
  NorthWest {
    override fun getAdj(pos: Coord): Pair<Coord, Coord> {
      return Pair(Coord(pos.row - 1, pos.col), Coord(pos.row, pos.col - 1))
    }

    override val symbol: Char = 'J'
  },
  SouthWest {
    override fun getAdj(pos: Coord): Pair<Coord, Coord> {
      return Pair(Coord(pos.row + 1, pos.col), Coord(pos.row, pos.col - 1))
    }

    override val symbol: Char = '7'
  },
  SouthEast {
    override fun getAdj(pos: Coord): Pair<Coord, Coord> {
      return Pair(Coord(pos.row + 1, pos.col), Coord(pos.row, pos.col + 1))
    }

    override val symbol: Char = 'F'
  };

  abstract fun getAdj(pos: Coord): Pair<Coord, Coord>

  abstract val symbol: Char

  companion object {
    fun getPipeType(symbol: Char): PipeType {
      entries.forEach {
        if (it.symbol == symbol) {
          return it
        }
      }
      throw Exception("Pipe symbol must be valid: $symbol")
    }
  }
}

fun getNetworksFromPipeMap(pipeMap: Map<Coord, Pipe>): List<Network> {
  val visitedPipes = pipeMap.mapValues { false }.toMutableMap()
  val networks = mutableListOf<Network>()
  for ((coord, pipe) in pipeMap.entries) {
    if (visitedPipes[coord] == true) {
      continue
    }
    val n = Network.fromPipe(pipeMap, pipe)
    networks.add(n)
    n.pipes.forEach { visitedPipes[it.pos] = true }
  }
  return networks
}

fun getNetworks(input: List<String>): Pair<List<Network>, Coord?> {
  val pipeMap = mutableMapOf<Coord, Pipe>()
  var start: Coord? = null
  input.forEachIndexed { row, line ->
    line.forEachIndexed { col, c ->
      if (c == 'S') {
        start = Coord(row, col)
      } else if (c != '.') {
        pipeMap[Coord(row, col)] = Pipe(Coord(row, col), PipeType.getPipeType(c))
      }
    }
  }

  val networks = getNetworksFromPipeMap(pipeMap)

  return Pair(networks, start)
}

fun part1() {
  println("-----")
  println("Part1")
  println("-----")
  for (fileName in
      listOf(
          "Day10/test_input",
          "Day10/test_input2",
          "Day10/test_input3",
          "Day10/input",
      )) {
    println(fileName)
    val input = readInput(fileName)
    val (ns, start) = getNetworks(input)
    //    println(ns)
    //    println(ns.map { it.getEnds() })
    //    println(ns.map { it.getPotentialStart() })
    //    println(ns.mapNotNull { it.getPotentialStart() }.filter { it == start })
    val actualNetwork = ns.find { it.getPotentialStart() == start!! }!!
    //    println(actualNetwork)
    val sol = actualNetwork.findFarthest(start!!)
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
          "Day10/test_input",
          "Day10/test_input2",
          "Day10/test_input3",
          "Day10/input",
      )) {
    println(fileName)
    val input = readInput(fileName)
    val (ns, start) = getNetworks(input)
    //    println(ns)
    val actualNetwork = ns.find { it.getPotentialStart() == start!! }!!
    //    println(actualNetwork)
    val sol = actualNetwork.countEnclosed(start!!)
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

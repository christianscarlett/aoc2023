package Day12

import readInput

class DP(val record: String, val pattern: List<Int>) {

  val memo: MutableMap<Pair<Int, Int>, Long> = mutableMapOf()

  fun canSatisfy(k: Int, n: Int): Boolean {
    if (k + n > record.length) {
      return false
    }
    return record.substring(k, k + n).all { it == '?' || it == '#' } &&
        isBorder(k + n) &&
        isBorder(k - 1)
  }

  fun isBorder(l: Int): Boolean {
    return l == -1 || l == record.length || record[l] == '.' || record[l] == '?'
  }

  /** Returns the number of ways we can satisfy record[i:] with pattern[j:] */
  fun getCombosEfficient(i: Int, j: Int): Set<String> {

    if (j == pattern.size) {
      if (record.substring(i).contains('#')) {
        return setOf()
      }
      return setOf(record.substring(i).replace('?', '.'))
    }
    if (i == record.length) {
      return setOf()
    }

    val n = pattern[j]
    val allCombos = mutableSetOf<String>()
    for (k in (i ..< record.length)) {
      if (record.substring((k - 2).coerceAtLeast(i), k).let { it == "#." || it == "#?" }) {
        break
      }
      if (canSatisfy(k, n)) {
        val c = getCombosEfficient((k + n + 1).coerceAtMost(record.length), j + 1)
        allCombos.addAll(
            c.map {
              (record.substring(i, k) +
                      "#".repeat(n) +
                      (if (k + n < record.length) record[k + n] else "") +
                      it)
                  .replace('?', '.')
            })
      }
    }
    return allCombos
  }

  fun getAllCombosEfficient(): Set<String> {
    return getCombosEfficient(0, 0)
  }

  /** Returns the number of ways we can satisfy record[i:] with pattern[j:] */
  fun getNumCombosEfficient(i: Int, j: Int): Long {
    memo[Pair(i, j)]?.let {
      return it
    }

    if (j == pattern.size) {
      if (record.substring(i).contains('#')) {
        memo[Pair(i, j)] = 0
        return 0
      }
      memo[Pair(i, j)] = 1
      return 1
    }
    if (i == record.length) {
      memo[Pair(i, j)] = 0
      return 0
    }

    val n = pattern[j]
    var totalCombos = 0L
    for (k in (i ..< record.length)) {
      if (record.substring((k - 2).coerceAtLeast(i), k).let { it == "#." || it == "#?" }) {
        break
      }
      if (canSatisfy(k, n)) {
        val c = getNumCombosEfficient((k + n + 1).coerceAtMost(record.length), j + 1)
        totalCombos += c
      }
    }
    memo[Pair(i, j)] = totalCombos
    return totalCombos
  }

  fun getNumValidCombosEfficient(): Long {
    return getNumCombosEfficient(0, 0)
  }

  fun getCombos(r: String): List<String> {
    if (r.isEmpty()) {
      return listOf("")
    }

    if (r[0] == '?') {
      val allCombos = mutableListOf<String>()
      val springCombos = getCombos(r.substring(1))
      allCombos.addAll(springCombos.map { "#$it" })
      val dotCombos = getCombos(r.substring(1))
      allCombos.addAll(dotCombos.map { ".$it" })
      return allCombos
    } else {
      val dotCombos = getCombos(r.substring(1))
      return dotCombos.map { r[0] + it }
    }
  }

  fun isValid(combo: String): Boolean {
    val comboPattern = combo.split('.').filter { it.isNotEmpty() }.map { it.length }
    return comboPattern == pattern
  }

  fun getNumValidCombos(): Int {
    val combos = getCombos(record)
    val validCombos = combos.filter { isValid(it) }
    return validCombos.size
  }

  fun unfolded(n: Int): DP {
    return DP(record + ("?$record").repeat(n - 1), (0 until n).map { pattern }.flatten())
  }

  override fun toString(): String {
    return "$record, $pattern"
  }

  companion object {
    fun fromLine(line: String): DP {
      val (record, p) = line.split(' ')
      val pattern = p.split(',').map { it.toInt() }
      return DP(record, pattern)
    }
  }
}

fun part1() {
  println("-----")
  println("Part1")
  println("-----")
  for (fileName in
      listOf(
          "Day12/test_input",
          "Day12/input",
      )) {
    println(fileName)
    val input = readInput(fileName)
    val dps = input.map { DP.fromLine(it) }
    val sol = dps.map { it.getNumValidCombos() }.sum()
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
          "Day12/test_input",
          "Day12/input",
      )) {
    println(fileName)
    val input = readInput(fileName)
    val dps = input.map { DP.fromLine(it) }

    val sol = dps.sumOf { it.unfolded(5).getNumValidCombosEfficient() }
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

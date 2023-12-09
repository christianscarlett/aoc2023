package Day08

import readInput

data class Relation(val name: String, val left: String, val right: String) {
  companion object {
    fun fromLine(line: String): Relation {
      val (name, lr) = line.split(" = ")
      val (l, r) = lr.split(", ")
      return Relation(name, l.substring(1), r.substring(0, 3))
    }
  }
}

class Graph(relations: List<Relation>) {
  val relationMap: Map<String, Relation> = relations.associateBy { it.name }

  fun getStartingNodes(): List<String> {
    return relationMap.keys.filter { it[2] == 'A' }
  }

  fun getEndingNodes(): List<String> {
    return relationMap.keys.filter { it[2] == 'Z' }
  }

  fun gcd(a: Long, b: Long): Long {
    var a = a
    var b = b
    while (b > 0) {
      val t = b
      b = a % b
      a = t
    }
    return a
  }

  fun lcm(a: Long, b: Long): Long {
    return a * (b / gcd(a, b))
  }

  fun countSimSteps(steps: String): Long {
    var startingNodes = getStartingNodes()
    val numSteps = startingNodes.map { countSteps(steps, it) { s -> s[2] == 'Z' }.toLong() }
    println(numSteps)
    val lcm = numSteps.reduce { acc, num -> lcm(acc, num) }
    return lcm
  }

  fun countSteps(
      steps: String,
      startingNode: String = "AAA",
      endCondition: ((String) -> Boolean) = { s -> s == "ZZZ" }
  ): Int {
    var currentNode = startingNode
    var currentStep = 0
    var totalSteps = 0
    while (!endCondition.invoke(currentNode)) {
      currentNode = takeStep(currentNode, steps[currentStep])
      currentStep = (currentStep + 1) % steps.length
      totalSteps++
    }
    return totalSteps
  }

  fun takeStep(start: String, step: Char): String {
    val relation = relationMap[start]!!
    if (step == 'L') {
      return relation.left
    } else if (step == 'R') {
      return relation.right
    }
    throw Exception("Step must be L or R: $step")
  }
}

fun part1() {
  println("part1")
  for (fileName in
      listOf(
          "Day08/test_input",
          "Day08/test_input2",
          "Day08/input",
      )) {
    println(fileName)
    val input = readInput(fileName)

    val steps = input[0]
    val relations = input.subList(2, input.size).map { Relation.fromLine(it) }
    val graph = Graph(relations)
    println(graph.countSteps(steps))

    println("--")
    println()
  }
}

fun part2() {
  println("-----")
  println("part2")
  println("-----")
  for (fileName in
      listOf(
          "Day08/test_input3",
          "Day08/input",
      )) {
    val input = readInput(fileName)

    val steps = input[0]
    val relations = input.subList(2, input.size).map { Relation.fromLine(it) }
    val graph = Graph(relations)
    println(graph.countSimSteps(steps))

    println("--")
    println()
  }
}

fun main() {
  part1()
  part2()
}

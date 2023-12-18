package Day15

import readInput

class Lens(val label: String, val focal: Int) {
  fun getFocusingPower(boxIdx: Int, slot: Int): Int {
    return (1 + boxIdx) * slot * focal
  }
}

class Box(val idx: Int) {
  val lenses = mutableListOf<Lens>()

  fun getFocusingPower(): Int {
    return lenses.foldIndexed(0) { i, acc, lens -> acc + lens.getFocusingPower(idx, i + 1) }
  }

  fun removeLens(label: String) {
    lenses.removeIf { it.label == label }
  }

  fun addLens(lens: Lens) {
    val i = lenses.indexOfFirst { it.label == lens.label }
    if (i == -1) {
      lenses.add(lens)
    } else {
      lenses[i] = lens
    }
  }
}

class Sequence(seq: String) {
  val steps = seq.split(',').map { Step(it) }

  fun getFocusingPower(): Int {
    val boxes = mutableMapOf<Int, Box>()
    steps.forEach { step ->
      val boxIdx = getHash(step.label)
      val box = boxes[boxIdx] ?: Box(boxIdx).also { boxes[boxIdx] = it }
      when (step.type) {
        Step.Type.DASH -> box.removeLens(step.label)
        Step.Type.EQUAL -> box.addLens(Lens(step.label, step.focal))
      }
    }
    return boxes.values.sumOf { box -> box.getFocusingPower() }
  }

  fun getHashSum(): Int {
    return steps.sumOf { it.stepHash }
  }
}

class Step(step: String) {
  enum class Type {
    EQUAL,
    DASH,
  }

  val stepHash = getHash(step)
  val label: String
  val focal: Int
  val type: Type

  init {
    if (step.contains('=')) {
      val (l, f) = step.split('=')
      label = l
      focal = f.toInt()
      type = Type.EQUAL
    } else {
      label = step.filter { it != '-' }
      focal = -1
      type = Type.DASH
    }
  }
}

fun getHash(str: String): Int {
  return str.fold(0) { acc, c -> ((acc + c.code) * 17) % 256 }
}

fun part1() {
  println("-----")
  println("Part1")
  println("-----")
  for (fileName in
      listOf(
          "Day15/test_input",
          "Day15/input",
      )) {
    println(fileName)
    val input = readInput(fileName)
    val s = Sequence(input[0])
    println(s.getHashSum())
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
          "Day15/test_input",
          "Day15/input",
      )) {
    println(fileName)
    val input = readInput(fileName)
    val s = Sequence(input[0])
    println(s.getFocusingPower())
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

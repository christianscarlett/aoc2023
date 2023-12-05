package Day05

import readInput

data class Span(val start: Long, val length: Long) {
  val end = start + length

  fun contains(value: Long): Boolean {
    return value in start ..< end
  }

  fun cut(cuts: List<Long>): List<Span> {
    val cutSpans = mutableListOf<Span>()
    var runningSpan = this
    for (cut in cuts) {
      if (contains(cut)) {
        cutSpans.add(Span(runningSpan.start, cut - runningSpan.start))
        runningSpan = Span(cut, end - cut)
      }
    }
    cutSpans.add(runningSpan)
    return cutSpans
  }
}

data class SpanSeries(val spans: List<Span>)

data class RangeMap(val source: Long, val dest: Long, val range: Long) {

  val sourceEnd = source + range

  fun isMapped(inputSource: Long): Boolean {
    return inputSource in source ..< sourceEnd
  }

  fun getDest(inputSource: Long): Long {
    if (isMapped(inputSource)) {
      return dest + (inputSource - source)
    }
    return inputSource
  }
}

data class RangeMapSeries(val ranges: List<RangeMap>) {

  fun getDest(inputSource: Long): Long {
    ranges.forEach { range ->
      if (range.isMapped(inputSource)) {
        return range.getDest(inputSource)
      }
    }
    return inputSource
  }
}

interface AlmanacIntf {
  fun findMinLoc(): Long
}

data class Almanac(
    val seeds: List<Long>,
    val seedToSoil: RangeMapSeries,
    val soilToFert: RangeMapSeries,
    val fertToWater: RangeMapSeries,
    val waterToLight: RangeMapSeries,
    val lightToTemp: RangeMapSeries,
    val tempToHumid: RangeMapSeries,
    val humidToLoc: RangeMapSeries
) : AlmanacIntf {

  fun getLoc(seed: Long): Long {
    var source = seed
    listOf(seedToSoil, soilToFert, fertToWater, waterToLight, lightToTemp, tempToHumid, humidToLoc)
        .forEach { rms -> source = rms.getDest(source) }

    return source
  }

  override fun findMinLoc(): Long {
    return seeds.minOf { seed ->
      return@minOf getLoc(seed)
    }
  }
}

data class AlmanacV2(
    val seeds: SpanSeries,
    val seedToSoil: RangeMapSeries,
    val soilToFert: RangeMapSeries,
    val fertToWater: RangeMapSeries,
    val waterToLight: RangeMapSeries,
    val lightToTemp: RangeMapSeries,
    val tempToHumid: RangeMapSeries,
    val humidToLoc: RangeMapSeries
) : AlmanacIntf {

  fun mapSpanSeries(spanSeries: SpanSeries, rangeMapSeries: RangeMapSeries): SpanSeries {
    val spans = spanSeries.spans.sortedBy { it.start }
    val ranges = rangeMapSeries.ranges.sortedBy { it.source }

    val cuts = mutableListOf<Long>()
    ranges.forEach { rangeMap ->
      cuts.add(rangeMap.source)
      cuts.add(rangeMap.sourceEnd)
    }

    val cutSpans = mutableListOf<Span>()
    spans.forEach { span -> cutSpans.addAll(span.cut(cuts)) }

    val transformedSpans = mutableListOf<Span>()
    for (span in cutSpans) {
      var transformed = false
      for (range in ranges) {
        if (range.isMapped(span.start)) {
          transformedSpans.add(Span(range.getDest(span.start), span.length))
          transformed = true
          break
        }
      }
      if (!transformed) {
        transformedSpans.add(span)
      }
    }

    return SpanSeries(transformedSpans)
  }

  override fun findMinLoc(): Long {
    var spanSeries = seeds
    listOf(seedToSoil, soilToFert, fertToWater, waterToLight, lightToTemp, tempToHumid, humidToLoc)
        .forEach { rms -> spanSeries = mapSpanSeries(spanSeries, rms) }

    return spanSeries.spans.minOf { it.start }
  }
}

fun List<String>.findTitleIdx(title: String): Int {
  return this.indexOfFirst { it.contains(title) }
}

fun getRangeMapSeries(input: List<String>, titleIdx: Int, nextTitleIdx: Int): RangeMapSeries {
  val ranges = mutableListOf<RangeMap>()
  for (i in titleIdx + 1 until nextTitleIdx - 1) {
    val rangeInput = input[i].split(" ").filter { it.isNotEmpty() }
    val dest = rangeInput[0]
    val source = rangeInput[1]
    val r = rangeInput[2]
    ranges.add(RangeMap(source.toLong(), dest.toLong(), r.toLong()))
  }
  return RangeMapSeries(ranges)
}

fun getSeeds(input: List<String>): List<Long> {
  val seedsInput = input[0].split(":")[1].split(" ").filter { it.isNotEmpty() }
  return seedsInput.map { it.toLong() }
}

fun getSeedsAsSpans(input: List<String>): SpanSeries {
  val seedsInput = input[0].split(":")[1].split(" ").filter { it.isNotEmpty() }

  val seedSpans = mutableListOf<Span>()
  for (i in 0 until seedsInput.size / 2) {
    val start = seedsInput[2 * i].toLong()
    val range = seedsInput[2 * i + 1].toLong()
    seedSpans.add(Span(start, range))
  }
  return SpanSeries(seedSpans)
}

fun parseAlmanac(fileName: String, /* for part 2 */ part2: Boolean): AlmanacIntf {
  val input = readInput(fileName)

  val seedToSoilIdx = input.findTitleIdx("seed-to-soil")
  val soilToFertIdx = input.findTitleIdx("soil-to-fertilizer")
  val fertToWaterIdx = input.findTitleIdx("fertilizer-to-water")
  val waterToLightIdx = input.findTitleIdx("water-to-light")
  val lightToTempIdx = input.findTitleIdx("light-to-temperature")
  val tempToHumidIdx = input.findTitleIdx("temperature-to-humidity")
  val humidToLocIdx = input.findTitleIdx("humidity-to-location")

  val seedToSoilRms = getRangeMapSeries(input, seedToSoilIdx, soilToFertIdx)
  val soilToFertRms = getRangeMapSeries(input, soilToFertIdx, fertToWaterIdx)
  val fertToWaterRms = getRangeMapSeries(input, fertToWaterIdx, waterToLightIdx)
  val waterToLightRms = getRangeMapSeries(input, waterToLightIdx, lightToTempIdx)
  val lightToTempRms = getRangeMapSeries(input, lightToTempIdx, tempToHumidIdx)
  val tempToHumidRms = getRangeMapSeries(input, tempToHumidIdx, humidToLocIdx)
  val humidToLocRms = getRangeMapSeries(input, humidToLocIdx, input.size + 1)

  if (!part2) {
    return Almanac(
        getSeeds(input),
        seedToSoilRms,
        soilToFertRms,
        fertToWaterRms,
        waterToLightRms,
        lightToTempRms,
        tempToHumidRms,
        humidToLocRms)
  } else {
    return AlmanacV2(
        getSeedsAsSpans(input),
        seedToSoilRms,
        soilToFertRms,
        fertToWaterRms,
        waterToLightRms,
        lightToTempRms,
        tempToHumidRms,
        humidToLocRms)
  }
}

fun part1() {
  val testAlmanac = parseAlmanac("Day05/test_input", false)
  val testLowestLoc = testAlmanac.findMinLoc()
  println(testLowestLoc)
  check(testLowestLoc == 35.toLong())

  val almanac = parseAlmanac("Day05/input", false)
  val lowestLoc = almanac.findMinLoc()
  println(lowestLoc)

  println()
  println()
}

fun part2() {
  val testAlmanac = parseAlmanac("Day05/test_input", true)
  val testLowestLoc = testAlmanac.findMinLoc()
  println(testLowestLoc)
  check(testLowestLoc == 46.toLong())

  val almanac = parseAlmanac("Day05/input", true)
  val lowestLoc = almanac.findMinLoc()
  println(lowestLoc)

  println()
  println()
}

fun main() {
  part1()
  part2()
}

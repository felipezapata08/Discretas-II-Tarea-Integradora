package closest

import scala.annotation.tailrec

object ClosestPoints:

  type Point = List[Int]

  /**
   * Returns the x-coordinate of a point.
   *
   * @param point point represented as a list [x, y]
   * @return x-coordinate of the point
   */
  def getX(point: Point): Int =
    point match
      case x :: _ => x
      case Nil => 0

  /**
   * Returns the y-coordinate of a point.
   *
   * @param point point represented as a list [x, y]
   * @return y-coordinate of the point
   */
  def getY(point: Point): Int =
    point match
      case _ :: y :: _ => y
      case _ => 0

  /**
   * Calculates the Euclidean distance between two points.
   *
   * @param point1 first point
   * @param point2 second point
   * @return Euclidean distance between the two points
   */
  def distance(point1: Point, point2: Point): Double =
    point1 match
      case x1 :: y1 :: Nil =>
        point2 match
          case x2 :: y2 :: Nil =>
            val dx = x1.toDouble - x2.toDouble
            val dy = y1.toDouble - y2.toDouble

            math.sqrt(dx * dx + dy * dy)
          case _ =>
            Double.PositiveInfinity
      case _ =>
        Double.PositiveInfinity

  /**
   * Calculates the number of elements in a list using tail recursion.
   *
   * @param points list of points
   * @return number of points
   */
  @tailrec
  def lengthTR(points: List[Point], accumulator: Int = 0): Int =
    points match
      case Nil =>
        accumulator
      case _ :: tail =>
        lengthTR(tail, accumulator + 1)

  /**
   * Splits a list into two approximately equal parts.
   *
   * @param points list of points
   * @return a pair containing the left and right parts
   */
  def splitPoints(points: List[Point]): (List[Point], List[Point]) =
    val middle = lengthTR(points) / 2
    @tailrec
    def loop(remaining: List[Point], left: List[Point], count: Int): (List[Point], List[Point]) =
      if count == 0 then
        (reverseTR(left), remaining)
      else
        remaining match
          case Nil =>
            (reverseTR(left), Nil)
          case head :: tail =>
            loop(tail, head :: left, count - 1)
    loop(points, Nil, middle)

  /**
   * Reverses a list using tail recursion.
   *
   * @param points list of points
   * @return reversed list
   */
  @tailrec
  def reverseTR(points: List[Point], accumulator: List[Point] = Nil): List[Point] =
    points match
      case Nil =>
        accumulator
      case head :: tail =>
        reverseTR(tail, head :: accumulator)

  /**
   * Merges two lists of points ordered by their x-coordinate.
   *
   * @param left first sorted list
   * @param right second sorted list
   * @return merged sorted list
   */
  def mergeByX(left: List[Point], right: List[Point]): List[Point] =
    @tailrec
    def loop(first: List[Point], second: List[Point], accumulator: List[Point]): List[Point] =
      (first, second) match
        case (Nil, Nil) =>
          reverseTR(accumulator)
        case (Nil, remaining) =>
          reverseTR(accumulator, remaining)
        case (remaining, Nil) =>
          reverseTR(accumulator, remaining)
        case (head1 :: tail1, head2 :: tail2) =>
          if getX(head1) <= getX(head2) then
            loop(tail1, second, head1 :: accumulator)
          else
            loop(first, tail2, head2 :: accumulator)
    loop(left, right, Nil)

  /**
   * Sorts points according to their x-coordinate
   * using Merge Sort.
   *
   * @param points list of points
   * @return points ordered by x-coordinate
   */
  def sortByX(points: List[Point]): List[Point] =
    points match
      case Nil => Nil
      case _ :: Nil =>
        points
      case _ =>
        val (left, right) =
          splitPoints(points)
        val sortedLeft =
          sortByX(left)
        val sortedRight =
          sortByX(right)
        mergeByX(sortedLeft, sortedRight)

  /**
   * Recursively finds the closest pair distance.
   *
   * @param points points ordered by x-coordinate
   * @return minimum distance between two different points
   */
  def closestRecursive(points: List[Point]): Double =
    points match
      case Nil =>
        Double.PositiveInfinity
      case _ :: Nil =>
        Double.PositiveInfinity
      case p1 :: p2 :: Nil =>
        distance(p1, p2)
      case _ =>
        val (left, right) =
          splitPoints(points)
        val leftDistance =
          closestRecursive(left)
        val rightDistance =
          closestRecursive(right)
        val delta =
          if leftDistance < rightDistance then
            leftDistance
          else
            rightDistance
        val strip = buildStrip(points, right, delta)
        val stripDistance =
          stripMinDistance(strip)
        if delta < stripDistance then
          delta
        else
          stripDistance

  /**
   * Builds the strip containing points close
   * to the middle line.
   *
   * @param points all points ordered by x-coordinate
   * @param right right half of the points
   * @param delta minimum distance found in both halves
   * @return points whose x-distance from the middle line is at most delta
   */
  def buildStrip(points: List[Point], right: List[Point], delta: Double): List[Point] =
    right match
      case Nil => Nil
      case middlePoint :: _ =>
        val middleX =
          getX(middlePoint)
        @tailrec
        def loop(remaining: List[Point], accumulator: List[Point]): List[Point] =
          remaining match
            case Nil =>
              sortByY(reverseTR(accumulator))
            case head :: tail =>
              val xDistance =
                math.abs(getX(head) - middleX)
              if xDistance <= delta then
                loop(tail, head :: accumulator)
              else
                loop(tail, accumulator)
        loop(points, Nil)

  /**
   * Merges two lists of points ordered by their y-coordinate.
   *
   * @param left first sorted list
   * @param right second sorted list
   * @return merged sorted list
   */
  def mergeByY(left: List[Point], right: List[Point]): List[Point] =
    @tailrec
    def loop(first: List[Point], second: List[Point], accumulator: List[Point]): List[Point] =
      (first, second) match
        case (Nil, Nil) =>
          reverseTR(accumulator)
        case (Nil, remaining) =>
          reverseTR(accumulator, remaining)
        case (remaining, Nil) =>
          reverseTR(accumulator, remaining)
        case (head1 :: tail1, head2 :: tail2) =>
          if getY(head1) <= getY(head2) then
            loop(tail1, second, head1 :: accumulator)
          else
            loop(first, tail2, head2 :: accumulator)
    loop(left, right, Nil)

  /**
   * Sorts points according to their y-coordinate
   * using Merge Sort.
   *
   * @param points list of points
   * @return points ordered by y-coordinate
   */
  def sortByY(points: List[Point]): List[Point] =
    points match
      case Nil => Nil
      case _ :: Nil =>
        points
      case _ =>
        val (left, right) =
          splitPoints(points)
        val sortedLeft =
          sortByY(left)
        val sortedRight =
          sortByY(right)
        mergeByY(sortedLeft, sortedRight)

  /**
   * Finds the minimum distance inside the strip.
   *
   * The strip must be ordered by y-coordinate.
   *
   * @param strip points contained in the strip
   * @return minimum distance found in the strip
   */
  def stripMinDistance(strip: List[Point]): Double =
    @tailrec
    def compareWithFollowing(point: Point, remaining: List[Point], currentMin: Double): Double =
      remaining match
        case Nil =>
          currentMin
        case next :: tail =>
          val yDifference =
            getY(next) - getY(point)
          if yDifference > currentMin then
            currentMin
          else
            val currentDistance =
              distance(point, next)
            val newMin =
              if currentDistance < currentMin then
                currentDistance
              else
                currentMin
            if newMin == 0.0 then
              0.0
            else
              compareWithFollowing(point, tail, newMin)
    @tailrec
    def scan(remaining: List[Point], currentMin: Double): Double =
      remaining match
        case Nil =>
          currentMin
        case head :: tail =>
          val newMin =
            compareWithFollowing(head, tail, currentMin)
          if newMin == 0.0 then
            0.0
          else
            scan(tail, newMin)
    scan(strip, Double.PositiveInfinity)

  /**
   * Finds the minimum Euclidean distance between
   * two different points.
   *
   * @param points list of points represented as [x, y]
   * @return minimum distance between two different points,
   *         rounded to at most 4 decimal digits
   *         (Double.PositiveInfinity if there are fewer than two points)
   */
  def closestPoints(points: List[Point]): Double =
    val sortedPoints =
      sortByX(points)
    val result =
      closestRecursive(sortedPoints)
    if result.isInfinite then
      result
    else
      math.round(result * 10000.0) / 10000.0
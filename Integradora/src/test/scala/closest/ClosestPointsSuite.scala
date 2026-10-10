package closest

import munit.FunSuite

class ClosestPointsSuite extends FunSuite:

  test("distance between two points"):

    val point1 = List(0, 0)
    val point2 = List(3, 4)

    val result = ClosestPoints.distance(point1, point2)

    assertEquals(result, 5.0)

  test("distance between identical points"):

    val point1 = List(2, 3)
    val point2 = List(2, 3)

    val result = ClosestPoints.distance(point1, point2)

    assertEquals(result, 0.0)


  test("split points into two parts"):

    val points = List(
      List(1, 1),
      List(2, 2),
      List(3, 3),
      List(4, 4)
    )

    val result = ClosestPoints.splitPoints(points)

    assertEquals(
      result,
      (
        List(
          List(1, 1),
          List(2, 2)
        ),
        List(
          List(3, 3),
          List(4, 4)
        )
      )
    )


  test("sort points by x-coordinate"):

    val points = List(
      List(5, 2),
      List(1, 8),
      List(3, 4),
      List(2, 7)
    )

    val result = ClosestPoints.sortByX(points)

    assertEquals(
      result,
      List(
        List(1, 8),
        List(2, 7),
        List(3, 4),
        List(5, 2)
      )
    )


  test("sort points by y-coordinate"):

    val points = List(
      List(5, 2),
      List(1, 8),
      List(3, 4),
      List(2, 1)
    )

    val result = ClosestPoints.sortByY(points)

    assertEquals(
      result,
      List(
        List(2, 1),
        List(5, 2),
        List(3, 4),
        List(1, 8)
      )
    )


  test("closest distance between three points"):

    val points = List(
      List(0, 0),
      List(3, 4),
      List(1, 1)
    )

    val result = ClosestPoints.closestPoints(points)

    assertEqualsDouble(
      result,
      math.sqrt(2),
      0.0001
    )


  test("closest distance with duplicate points"):

    val points = List(
      List(0, 0),
      List(3, 4),
      List(0, 0),
      List(10, 10)
    )

    val result = ClosestPoints.closestPoints(points)

    assertEquals(result, 0.0)


  test("closest distance with negative coordinates"):

    val points = List(
      List(-5, -5),
      List(0, 0),
      List(-4, -4),
      List(10, 10)
    )

    val result = ClosestPoints.closestPoints(points)

    assertEqualsDouble(
      result,
      math.sqrt(2),
      0.0001
    )


  test("closest distance with two points"):

    val points = List(
      List(0, 0),
      List(3, 4)
    )

    val result = ClosestPoints.closestPoints(points)

    assertEquals(result, 5.0)

  test("closest distance example 2 is rounded to 4 decimals"):

    val points = List(
      List(0, 0),
      List(3, 4),
      List(1, 1)
    )

    assertEquals(ClosestPoints.closestPoints(points), 1.4142)


  test("distance does not overflow with large coordinates"):

    assertEquals(
      ClosestPoints.distance(List(0, 0), List(100000, 0)),
      100000.0
    )

    assertEquals(
      ClosestPoints.closestPoints(
        List(List(-2000000000, 0), List(2000000000, 0))
      ),
      4000000000.0
    )


  test("closest pair crosses the middle line"):

    val points = List(
      List(0, 0),
      List(10, 10),
      List(11, 10),
      List(30, 30)
    )

    assertEquals(ClosestPoints.closestPoints(points), 1.0)


  test("closest distance with all points on the same vertical line"):

    val points = List(
      List(0, 0),
      List(0, 5),
      List(0, 2),
      List(0, 9)
    )

    assertEquals(ClosestPoints.closestPoints(points), 2.0)


  test("closest distance with fewer than two points"):

    assertEquals(
      ClosestPoints.closestPoints(Nil),
      Double.PositiveInfinity
    )

    assertEquals(
      ClosestPoints.closestPoints(List(List(1, 1))),
      Double.PositiveInfinity
    )


  test("length and reverse with tail recursion"):

    val points = List(List(1, 1), List(2, 2), List(3, 3))

    assertEquals(ClosestPoints.lengthTR(points), 3)
    assertEquals(
      ClosestPoints.reverseTR(points),
      List(List(3, 3), List(2, 2), List(1, 1))
    )


  test("closestPoints matches the brute force algorithm"):

    val random = new scala.util.Random(42)

    (1 to 200).foreach: _ =>
      val size = 2 + random.nextInt(40)
      val points =
        List.fill(size)(List(random.nextInt(200) - 100, random.nextInt(200) - 100))
      val expected =
        (for
          i <- points.indices
          j <- (i + 1) until points.size
        yield ClosestPoints.distance(points(i), points(j))).min

      assertEqualsDouble(
        ClosestPoints.closestPoints(points),
        expected,
        0.0001
      )
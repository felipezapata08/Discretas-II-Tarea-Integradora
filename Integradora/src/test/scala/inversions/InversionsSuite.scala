package inversions

class InversionsSuite extends munit.FunSuite {

  test("example 1 from the assignment") {
    val result = Inversions.countInversions(List(2, 3, 9, 2, 9))
    assertEquals(result, 2L)
  }

  test("example 2 from the assignment, strictly decreasing") {
    val result = Inversions.countInversions(List(9, 7, 5, 3))
    assertEquals(result, 6L)
  }

  test("empty list has no inversions") {
    val result = Inversions.countInversions(List())
    assertEquals(result, 0L)
  }

  test("list with one element has no inversions") {
    val result = Inversions.countInversions(List(5))
    assertEquals(result, 0L)
  }

  test("sorted list has no inversions") {
    val result = Inversions.countInversions(List(1, 2, 3, 4, 5))
    assertEquals(result, 0L)
  }

  test("equal elements are not inversions") {
    val result = Inversions.countInversions(List(4, 4, 4, 4))
    assertEquals(result, 0L)
  }

  test("list with negative numbers") {
    val result = Inversions.countInversions(List(-1, -5, 3, -2))
    assertEquals(result, 3L)
  }

  test("decreasing list of 5 elements has n(n-1)/2 inversions") {
    val result = Inversions.countInversions(List(5, 4, 3, 2, 1))
    assertEquals(result, 10L)
  }

  test("merge counts the cross inversions") {
    val result = Inversions.merge(List(3, 5), List(1, 4))
    assertEquals(result, (List(1, 3, 4, 5), 3L))
  }

  test("mergeSortCount returns the sorted list") {
    val result = Inversions.mergeSortCount(List(2, 3, 9, 2, 9))._1
    assertEquals(result, List(2, 2, 3, 9, 9))
  }
}
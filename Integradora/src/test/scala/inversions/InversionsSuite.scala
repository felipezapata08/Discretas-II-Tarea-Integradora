package inversions

class InversionsSuite extends munit.FunSuite {

  // Checks the first example from the assignment: (2,3,9,2,9) has 2 inversions
  test("example 1 from the assignment") {
    val result = Inversions.countInversions(List(2, 3, 9, 2, 9))
    assertEquals(result, 2L)
  }

  // Checks the second example from the assignment: (9,7,5,3) has 6 inversions
  test("example 2 from the assignment") {
    val result = Inversions.countInversions(List(9, 7, 5, 3))
    assertEquals(result, 6L)
  }

  // An empty list has no pairs, so it has 0 inversions
  test("empty list") {
    val result = Inversions.countInversions(List())
    assertEquals(result, 0L)
  }

  // A list with one element has no pairs, so it has 0 inversions
  test("one element") {
    val result = Inversions.countInversions(List(5))
    assertEquals(result, 0L)
  }

  // A list that is already sorted has 0 inversions
  test("sorted list") {
    val result = Inversions.countInversions(List(1, 2, 3, 4, 5))
    assertEquals(result, 0L)
  }

  // Equal elements are not inversions because the rule is strictly greater
  test("equal elements") {
    val result = Inversions.countInversions(List(4, 4, 4, 4))
    assertEquals(result, 0L)
  }

  // Negative numbers are compared like any other number: (-1,-5), (3,-2), (-1,-2)
  test("negative numbers") {
    val result = Inversions.countInversions(List(-1, -5, 3, -2))
    assertEquals(result, 3L)
  }

  // A decreasing list of 5 elements has the maximum: 5*4/2 = 10 inversions
  test("decreasing list of 5 elements") {
    val result = Inversions.countInversions(List(5, 4, 3, 2, 1))
    assertEquals(result, 10L)
  }

  // An odd length makes the two halves different in size, so it checks the split
  test("odd number of elements") {
    val result = Inversions.countInversions(List(3, 1, 2))
    assertEquals(result, 2L)
  }

  // n(n-1)/2 with n = 100000 is 4999950000, bigger than Int.MaxValue, so it needs Long
  test("large decreasing list") {
    val result = Inversions.countInversions((100000 to 1 by -1).toList)
    assertEquals(result, 4999950000L)
  }

  // Calls merge directly: 3 and 5 are each bigger than 1, and 5 is bigger than 4, so 3 cross inversions
  test("merge counts the cross inversions") {
    val result = Inversions.merge(List(3, 5), List(1, 4))
    assertEquals(result, (List(1, 3, 4, 5), 3L))
  }

  // If the left list is empty there is nothing to cross, so the count is 0
  test("merge with empty left list") {
    val result = Inversions.merge(List(), List(1, 2))
    assertEquals(result, (List(1, 2), 0L))
  }

  // If the right list is empty there is nothing to cross, so the count is 0
  test("merge with empty right list") {
    val result = Inversions.merge(List(1, 2), List())
    assertEquals(result, (List(1, 2), 0L))
  }

  // When the heads are equal, the left one goes first and it is not counted
  test("merge with equal heads") {
    val result = Inversions.merge(List(2), List(2))
    assertEquals(result, (List(2, 2), 0L))
  }

  // Every right element is smaller than every left one: 3 * 3 = 9 cross inversions
  test("merge when every right element is smaller") {
    val result = Inversions.merge(List(4, 5, 6), List(1, 2, 3))
    assertEquals(result, (List(1, 2, 3, 4, 5, 6), 9L))
  }

  // The elements alternate between the lists: 2 passes 5 and 9, and 6 passes 9, so 3 inversions
  test("merge with interleaved elements") {
    val result = Inversions.merge(List(1, 5, 9), List(2, 6))
    assertEquals(result, (List(1, 2, 5, 6, 9), 3L))
  }

  // Checks that mergeSortCount also returns the list sorted, not only the count
  test("mergeSortCount returns the sorted list") {
    val result = Inversions.mergeSortCount(List(2, 3, 9, 2, 9))._1
    assertEquals(result, List(2, 2, 3, 9, 9))
  }
}
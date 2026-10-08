package quicksort

import munit.FunSuite

class QuickSort3WaySuite extends FunSuite {

  test("QuickSort 3- lista vacía y lista con un solo elemento") {
    assertEquals(QuickSort3Way.quickSort(Nil), Nil)
    assertEquals(QuickSort3Way.quickSort(List(5)), List(5))
  }

  test("QuickSort 3- ejemplo 1 del enunciado") {
    val input = List(2, 3, 9, 2, 2)
    val expected = List(2, 2, 2, 3, 9)
    assertEquals(QuickSort3Way.quickSort(input), expected)
  }

  test("QuickSort 3- ejemplo 2 del enunciado") {
    val input = List(4, 4, 1, 9, 4, 1, 4, 4)
    val expected = List(1, 1, 4, 4, 4, 4, 4, 9)
    assertEquals(QuickSort3Way.quickSort(input), expected)
  }

  test("QuickSort 3- Con lista con muchos elementos repetidos") {
    val input = List(7, 7, 7, 7, 7, 7)
    val expected = List(7, 7, 7, 7, 7, 7)
    assertEquals(QuickSort3Way.quickSort(input), expected)
  }

  test("QuickSort 3- con la lista que ya está ordenada") {
    val input = List(1, 2, 3, 4, 5, 6)
    val expected = List(1, 2, 3, 4, 5, 6)
    assertEquals(QuickSort3Way.quickSort(input), expected)
  }
}

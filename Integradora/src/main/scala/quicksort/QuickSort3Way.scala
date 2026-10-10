package quicksort

import scala.annotation.tailrec

/**
 * QuickSort algorithm using 3-way partitioning.
 */

object QuickSort3Way {

  /**
   * Splits a list into numbers smaller, equal, and bigger than a pivot.
   *
   * @param list    Input list to split.
   * @param pivot   Number used to compare.
   * @param less    List of numbers smaller than pivot.
   * @param equal   List of numbers equal to pivot.
   * @param greater List of numbers bigger than pivot.
   * @return Tuple with three lists (less, equal, greater).
   */

  @tailrec
  private def partition3Way(
                             list: List[Int],
                             pivot: Int,
                             less: List[Int],
                             equal: List[Int],
                             greater: List[Int]
                           ): (List[Int], List[Int], List[Int]) = {
    list match {
      case Nil => (less, equal, greater)
      case head :: tail =>
        if (head < pivot) {
          partition3Way(tail, pivot, head :: less, equal, greater)
        } else if (head == pivot) {
          partition3Way(tail, pivot, less, head :: equal, greater)
        } else {
          partition3Way(tail, pivot, less, equal, head :: greater)
        }
    }
  }

  /**
   * Sorts a list of numbers from smallest to largest.
   *
   * @param list List of numbers to sort.
   * @return The sorted list.
   */
  def quickSort(list: List[Int]): List[Int] = {
    list match {
      case Nil => Nil
      case pivot :: tail =>
        val (less, equal, greater) = partition3Way(tail, pivot, Nil, List(pivot), Nil)
        quickSort(less) ++ equal ++ quickSort(greater)
    }
  }
}
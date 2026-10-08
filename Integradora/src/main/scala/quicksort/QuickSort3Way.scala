package quicksort

import scala.annotation.tailrec

object QuickSort3Way {

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


  def quickSort(list: List[Int]): List[Int] = {
    list match {
      case Nil => Nil
      case pivot :: tail =>
        val (less, equal, greater) = partition3Way(tail, pivot, Nil, List(pivot), Nil)
        quickSort(less) ++ equal ++ quickSort(greater)
    }
  }
}
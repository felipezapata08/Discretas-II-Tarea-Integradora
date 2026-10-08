package inversions

import scala.annotation.tailrec

object Inversions {

  /**
   * Merges two sorted lists and counts the cross inversions.
   *
   * @param left  sorted list B
   * @param right sorted list C
   * @return the merged list and the number of pairs (b, c) with b > c
   */
  def merge(left: List[Int], right: List[Int]): (List[Int], Long) = {

    @tailrec
    def loop(
              leftList: List[Int],
              rightList: List[Int],
              leftSize: Int,
              acc: List[Int],
              count: Long
            ): (List[Int], Long) = {
      leftList match {
        case Nil => (acc.reverse ::: rightList, count)
        case leftHead :: leftTail =>
          rightList match {
            case Nil => (acc.reverse ::: leftList, count)
            case rightHead :: rightTail =>
              if (leftHead <= rightHead) {
                loop(leftTail, rightList, leftSize - 1, leftHead :: acc, count)
              } else {
                // I add one inversion for each element left on the left list
                loop(leftList, rightTail, leftSize, rightHead :: acc, count + leftSize)
              }
          }
      }
    }

    loop(left, right, left.length, Nil, 0L)
  }

  /**
   * Sorts a list and counts its inversions using merge sort.
   *
   * @param seq the input sequence
   * @return the sorted list and the total number of inversions
   */
  def mergeSortCount(seq: List[Int]): (List[Int], Long) = {
    seq match {
      case Nil => (Nil, 0L)
      case _ :: tail =>
        if (tail.isEmpty) {
          (seq, 0L)
        } else {
          val (leftHalf, rightHalf) = seq.splitAt(seq.length / 2)
          val (sortedLeft, leftCount) = mergeSortCount(leftHalf)
          val (sortedRight, rightCount) = mergeSortCount(rightHalf)
          val (merged, crossCount) = merge(sortedLeft, sortedRight)
          // The total is the left count plus the right count plus the merge count
          (merged, leftCount + rightCount + crossCount)
        }
    }
  }

  /**
   * Entry point for problem 1.
   *
   * @param seq the input sequence
   * @return the number of inversions
   */
  // I use Long because with a million elements the count can pass the Int limit
  def countInversions(seq: List[Int]): Long = {
    mergeSortCount(seq)._2
  }
}
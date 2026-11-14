package apsd.interfaces.containers.sequences.ordered;

import apsd.classes.utilities.Natural;

/** Interface: RemovableAtSequence & OrderedSequence. */
public interface SortedRemovableAtSequence<T extends SortedRemovableAtSequence<T, Data>, Data extends Comparable<? super Data>> extends SortedSequence<T, Data>, OrderedRemovableAtSequence<T, Data> {

  /* ************************************************************************ */
  /* Override specific member functions from OrderedRemovableContainer        */
  /* ************************************************************************ */

  @Override
  default Data MinNRemove() {
    return IsEmpty() ? null : FirstNRemove();
  }

  @Override
  default Data MaxNRemove() {
    return IsEmpty() ? null : LastNRemove();
  }

  @Override
  default Data PredecessorNRemove(Data dat) {
    Natural pos = SearchPredecessor(dat);
    return (pos == null) ? null : AtNRemove(pos);
  }

  @Override
  default Data SuccessorNRemove(Data dat) {
    Natural pos = SearchSuccessor(dat);
    return (pos == null) ? null : AtNRemove(pos);
  }

}

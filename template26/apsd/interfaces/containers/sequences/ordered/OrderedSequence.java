package apsd.interfaces.containers.sequences.ordered;

import apsd.classes.utilities.Natural;
import apsd.interfaces.containers.base.ordered.OrderedIterableContainer;
import apsd.interfaces.containers.sequences.Sequence;

/** Interface: Sequence & OrderedIterableContainer. */
public interface OrderedSequence<T extends OrderedSequence<T, Data>, Data extends Comparable<? super Data>> extends Sequence<T, Data>, OrderedIterableContainer<T, Data> {

  /* ************************************************************************ */
  /* Specific member functions of OrderedSequence                             */
  /* ************************************************************************ */

  /** Posizione (prima occorrenza) del predecessore di dat, oppure null se assente. */
  default Natural SearchPredecessor(Data dat) {
    Data pred = Predecessor(dat);
    return (pred == null) ? null : Search(pred);
  }

  /** Posizione (prima occorrenza) del successore di dat, oppure null se assente. */
  default Natural SearchSuccessor(Data dat) {
    Data succ = Successor(dat);
    return (succ == null) ? null : Search(succ);
  }

}

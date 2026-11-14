package apsd.interfaces.containers.sequences.ordered;

import apsd.classes.utilities.Natural;
import apsd.interfaces.containers.base.ordered.SortedIterableContainer;
import apsd.interfaces.containers.iterators.ForwardIterator;

/** Interface: OrderedSequence & SortedIterableContainer. */
public interface SortedSequence<T extends SortedSequence<T, Data>, Data extends Comparable<? super Data>> extends OrderedSequence<T, Data>, SortedIterableContainer<T, Data> {

  /* ************************************************************************ */
  /* Override specific member functions from MembershipContainer              */
  /* ************************************************************************ */

  @Override
  default boolean Exists(Data dat) {
    return Search(dat) != null;
  }

  /* ************************************************************************ */
  /* Override specific member functions from OrderedContainer                 */
  /* ************************************************************************ */

  @Override
  default Data Min() {
    return IsEmpty() ? null : GetFirst();
  }

  @Override
  default Data Max() {
    return IsEmpty() ? null : GetLast();
  }

  @Override
  default Data Predecessor(Data dat) {
    Natural pos = SearchPredecessor(dat);
    return (pos == null) ? null : GetAt(pos);
  }

  @Override
  default Data Successor(Data dat) {
    Natural pos = SearchSuccessor(dat);
    return (pos == null) ? null : GetAt(pos);
  }

  /* ************************************************************************ */
  /* Override specific member functions from Sequence                         */
  /* ************************************************************************ */

  /** Posizione della prima occorrenza di dat (null se assente); si ferma al primo dato maggiore. */
  @Override
  default Natural Search(Data dat) {
    ForwardIterator<Data> it = FIterator();
    long idx = 0L;
    while (it.IsValid()) {
      int cmp = it.DataNNext().compareTo(dat);
      if (cmp == 0) return Natural.Of(idx);
      if (cmp > 0) return null;
      idx++;
    }
    return null;
  }

  /* ************************************************************************ */
  /* Override specific member functions from OrderedSequence                  */
  /* ************************************************************************ */

  /** Posizione dell'ultimo dato strettamente minore di dat, oppure null. */
  @Override
  default Natural SearchPredecessor(Data dat) {
    ForwardIterator<Data> it = FIterator();
    long idx = 0L;
    while (it.IsValid() && it.DataNNext().compareTo(dat) < 0) idx++;
    return (idx == 0L) ? null : Natural.Of(idx - 1L);
  }

  /** Posizione del primo dato strettamente maggiore di dat, oppure null. */
  @Override
  default Natural SearchSuccessor(Data dat) {
    ForwardIterator<Data> it = FIterator();
    long idx = 0L;
    while (it.IsValid()) {
      if (it.DataNNext().compareTo(dat) > 0) return Natural.Of(idx);
      idx++;
    }
    return null;
  }

}

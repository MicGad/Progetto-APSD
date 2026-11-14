package apsd.interfaces.containers.iterators;

import apsd.classes.utilities.Natural;
import apsd.interfaces.traits.Predicate;

/** Interface: Iteratore all'indietro. */
public interface BackwardIterator<Data> extends Iterator<Data> {

  /* ************************************************************************ */
  /* Specific member functions of BackwardIterator                            */
  /* ************************************************************************ */

  default void Prev() {
    Prev(1L);
  }

  default void Prev(Natural steps) {
    Prev(steps.ToLong());
  }

  default void Prev(long steps) {
    if (steps < 0) throw new IllegalArgumentException("Negative number of steps!");
    for (long i = 0; i < steps && IsValid(); i++) {
      Prev();
    }
  }

  Data DataNPrev();

  default boolean ForEachBackward(Predicate<Data> pred) {
    if (pred == null) return false;
    while (IsValid()) {
      if (pred.Apply(DataNPrev())) return true;
    }
    return false;
  }

}

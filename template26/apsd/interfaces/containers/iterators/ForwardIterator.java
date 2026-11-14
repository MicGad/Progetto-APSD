package apsd.interfaces.containers.iterators;

import apsd.classes.utilities.Natural;
import apsd.interfaces.traits.Predicate;

/** Interface: Iteratore in avanti. */
public interface ForwardIterator<Data> extends Iterator<Data> {

  /* ************************************************************************ */
  /* Specific member functions of ForwardIterator                             */
  /* ************************************************************************ */

  default void Next() {
    Next(1L);
  }

  default void Next(Natural steps) {
    Next(steps.ToLong());
  }

  default void Next(long steps) {
    if (steps < 0) throw new IllegalArgumentException("Negative number of steps!");
    for (long i = 0; i < steps && IsValid(); i++) {
      Next();
    }
  }

  Data DataNNext();

  default boolean ForEachForward(Predicate<Data> pred) {
    if (pred == null) return false;
    while (IsValid()) {
      if (pred.Apply(DataNNext())) return true;
    }
    return false;
  }

}

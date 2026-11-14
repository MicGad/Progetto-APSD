package apsd.interfaces.containers.sequences;

import apsd.classes.utilities.Natural;
import apsd.interfaces.containers.base.InsertableContainer;

/** Interface: Sequence con supporto all'inserimento di un dato tramite posizione. */
public interface InsertableAtSequence<T extends InsertableAtSequence<T, Data>, Data> extends Sequence<T, Data>, InsertableContainer<T, Data> {

  void InsertAt(Data dat, Natural pos);

  default void InsertFirst(Data dat) {
    InsertAt(dat, Natural.ZERO);
  }

  default void InsertLast(Data dat) {
    InsertAt(dat, Size());
  }

  @Override
  default boolean Insert(Data dat) {
    InsertLast(dat);
    return true;
  }

  @Override
  default T SubSequence(Natural from, Natural to) {
    T res = Empty();
    long size = Size().ToLong();
    long last = Math.min(to.ToLong(), size - 1);
    for (long i = from.ToLong(); i <= last; i++) {
      res.InsertLast(GetAt(Natural.Of(i)));
    }
    return res;
  }

}

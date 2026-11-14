package apsd.interfaces.containers.sequences;

import apsd.classes.utilities.Natural;
import apsd.interfaces.containers.base.MutableIterableContainer;
import apsd.interfaces.containers.iterators.MutableForwardIterator;

/** Interface: Sequence con dati modificabili per posizione. */
public interface MutableSequence<T extends MutableSequence<T, Data>, Data> extends Sequence<T, Data>, MutableIterableContainer<T, Data> {

  default void SetAt(Data dat, Natural pos) {
    long idx = ExcIfOutOfBound(pos);
    MutableForwardIterator<Data> it = FIterator();
    it.Next(idx);
    it.SetCurrent(dat);
  }

  default Data GetNSetAt(Data newdat, Natural pos) {
    Data old = GetAt(pos);
    SetAt(newdat, pos);
    return old;
  }

  default void SetFirst(Data dat) {
    SetAt(dat, Natural.ZERO);
  }

  default Data GetNSetFirst(Data newdat) {
    return GetNSetAt(newdat, Natural.ZERO);
  }

  default void SetLast(Data dat) {
    if (IsEmpty()) throw new IndexOutOfBoundsException("The sequence is empty!");
    SetAt(dat, Size().Decrement());
  }

  default Data GetNSetLast(Data newdat) {
    if (IsEmpty()) throw new IndexOutOfBoundsException("The sequence is empty!");
    return GetNSetAt(newdat, Size().Decrement());
  }

  default void Swap(Natural fst, Natural snd) {
    Data tmp = GetAt(fst);
    SetAt(GetAt(snd), fst);
    SetAt(tmp, snd);
  }

}

package apsd.interfaces.containers.sequences;

import apsd.classes.utilities.Natural;
import apsd.interfaces.containers.base.ResizableContainer;

/** Interface: Vettore con size logica ridimensionabile. */
public interface DynVector<T extends DynVector<T, Data>, Data> extends ResizableContainer<T>, InsertableAtSequence<T, Data>, RemovableAtSequence<T, Data>, Vector<T, Data> {

  @Override
  default void InsertAt(Data dat, Natural pos) {
    if (pos.ToLong() > Size().ToLong()) {
      throw new IndexOutOfBoundsException("Index: " + pos + "; Size: " + Size() + "!");
    }
    Expand();
    ShiftRight(pos);
    SetAt(dat, pos);
  }

  @Override
  default Data AtNRemove(Natural pos) {
    Data dat = GetAt(pos);
    ShiftLeft(pos.Increment());
    Reduce();
    return dat;
  }

  @Override
  Natural Size();

  @Override
  default T SubSequence(Natural from, Natural to) {
    T res = Empty();
    long size = Size().ToLong();
    long f = from.ToLong();
    long l = Math.min(to.ToLong(), size - 1);
    if (f > l) return res;
    res.Expand(Natural.Of(l - f + 1));
    for (long i = f; i <= l; i++) {
      res.SetAt(GetAt(Natural.Of(i)), Natural.Of(i - f));
    }
    return res;
  }

}

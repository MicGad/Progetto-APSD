package apsd.interfaces.containers.sequences;

import apsd.classes.utilities.Natural;
import apsd.interfaces.containers.base.ReallocableContainer;

/** Interface: Sequence mutabile con capacita' riallocabile (vettore). */
public interface Vector<T extends Vector<T, Data>, Data> extends ReallocableContainer<T>, MutableSequence<T, Data> {

  /**
   * Sposta a sinistra di num posizioni gli elementi nell'intervallo [srt, end),
   * inserendo null al posto dei valori spostati.
   */
  default void ShiftLeft(Natural srt, Natural end, Natural num) {
    long s = srt.ToLong();
    long e = Math.min(end.ToLong(), Size().ToLong());
    long n = num.ToLong();
    if (s >= e || n == 0) return;
    for (long i = s; i < e; i++) {
      long d = i - n;
      if (d >= 0) SetAt(GetAt(Natural.Of(i)), Natural.Of(d));
    }
    for (long i = Math.max(s, e - n); i < e; i++) {
      SetAt(null, Natural.Of(i));
    }
  }

  default void ShiftLeft(Natural pos, Natural num) {
    ShiftLeft(pos, Size(), num);
  }

  default void ShiftLeft(Natural pos) {
    ShiftLeft(pos, Natural.ONE);
  }

  default void ShiftFirstLeft() {
    ShiftLeft(Natural.ZERO);
  }

  default void ShiftLastLeft() {
    if (IsEmpty()) return;
    ShiftLeft(Size().Decrement());
  }

  /**
   * Sposta a destra di num posizioni gli elementi nell'intervallo [srt, end),
   * inserendo null al posto dei valori spostati.
   */
  default void ShiftRight(Natural srt, Natural end, Natural num) {
    long size = Size().ToLong();
    long s = srt.ToLong();
    long e = Math.min(end.ToLong(), size);
    long n = num.ToLong();
    if (s >= e || n == 0) return;
    for (long i = e - 1; i >= s; i--) {
      long d = i + n;
      if (d < size) SetAt(GetAt(Natural.Of(i)), Natural.Of(d));
    }
    for (long i = s; i < Math.min(s + n, e); i++) {
      SetAt(null, Natural.Of(i));
    }
  }

  default void ShiftRight(Natural pos, Natural num) {
    ShiftRight(pos, Size(), num);
  }

  default void ShiftRight(Natural pos) {
    ShiftRight(pos, Natural.ONE);
  }

  default void ShiftFirstRight() {
    ShiftRight(Natural.ZERO);
  }

  default void ShiftLastRight() {
    if (IsEmpty()) return;
    ShiftRight(Size().Decrement());
  }

  @Override
  default Natural Size() {
    return Capacity();
  }

  @Override
  default T SubSequence(Natural from, Natural to) {
    T res = Empty();
    long size = Size().ToLong();
    long f = from.ToLong();
    long l = Math.min(to.ToLong(), size - 1);
    if (f > l) return res;
    res.Realloc(Natural.Of(l - f + 1));
    for (long i = f; i <= l; i++) {
      res.SetAt(GetAt(Natural.Of(i)), Natural.Of(i - f));
    }
    return res;
  }

}

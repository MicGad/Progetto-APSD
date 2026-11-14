package apsd.interfaces.containers.sequences;

import java.util.Objects;

import apsd.classes.utilities.Natural;
import apsd.interfaces.containers.base.IterableContainer;
import apsd.interfaces.containers.iterators.ForwardIterator;

/** Interface: Container con dati organizzati in sequenza (accesso per posizione). */
public interface Sequence<T extends Sequence<T, Data>, Data> extends IterableContainer<T, Data> {

  default Data GetAt(Natural pos) {
    long idx = ExcIfOutOfBound(pos);
    ForwardIterator<Data> it = FIterator();
    it.Next(idx);
    return it.GetCurrent();
  }

  default Data GetFirst() {
    return GetAt(Natural.ZERO);
  }

  default Data GetLast() {
    if (IsEmpty()) throw new IndexOutOfBoundsException("The sequence is empty!");
    return GetAt(Size().Decrement());
  }

  /** Posizione della prima occorrenza di dat, oppure null se assente. */
  default Natural Search(Data dat) {
    ForwardIterator<Data> it = FIterator();
    long idx = 0L;
    while (it.IsValid()) {
      if (Objects.equals(it.DataNNext(), dat)) return Natural.Of(idx);
      idx++;
    }
    return null;
  }

  default boolean IsInBound(Natural pos) {
    return pos != null && pos.ToLong() < Size().ToLong();
  }

  default long ExcIfOutOfBound(Natural pos) {
    if (!IsInBound(pos)) {
      throw new IndexOutOfBoundsException("Index: " + pos + "; Size: " + Size() + "!");
    }
    return pos.ToLong();
  }

  default boolean IsEqual(Sequence<?, Data> seq) {
    if (seq == null) return false;
    if (!Size().equals(seq.Size())) return false;
    ForwardIterator<Data> fst = FIterator();
    ForwardIterator<Data> snd = seq.FIterator();
    while (fst.IsValid() && snd.IsValid()) {
      if (!Objects.equals(fst.DataNNext(), snd.DataNNext())) return false;
    }
    return true;
  }

  /** Sottosequenza degli elementi tra le posizioni from e to (estremi inclusi). */
  T SubSequence(Natural from, Natural to);

}

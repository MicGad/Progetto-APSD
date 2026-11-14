package apsd.interfaces.containers.sequences;

import apsd.classes.utilities.Natural;
import apsd.interfaces.containers.base.RemovableContainer;

/** Interface: Sequence con supporto alla rimozione di un dato tramite posizione. */
public interface RemovableAtSequence<T extends RemovableAtSequence<T, Data>, Data> extends Sequence<T, Data>, RemovableContainer<T, Data> {

  default void RemoveAt(Natural pos) {
    AtNRemove(pos);
  }

  Data AtNRemove(Natural pos);

  default void RemoveFirst() {
    RemoveAt(Natural.ZERO);
  }

  default Data FirstNRemove() {
    return AtNRemove(Natural.ZERO);
  }

  default void RemoveLast() {
    LastNRemove();
  }

  default Data LastNRemove() {
    if (IsEmpty()) throw new IndexOutOfBoundsException("The sequence is empty!");
    return AtNRemove(Size().Decrement());
  }

  /** Rimuove la prima occorrenza di dat; true se presente. */
  @Override
  default boolean Remove(Data dat) {
    Natural pos = Search(dat);
    if (pos == null) return false;
    RemoveAt(pos);
    return true;
  }

}

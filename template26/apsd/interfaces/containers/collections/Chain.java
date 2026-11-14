package apsd.interfaces.containers.collections;

import apsd.classes.utilities.Natural;
import apsd.interfaces.containers.sequences.RemovableAtSequence;

/** Interface: Collection con dati in sequenza (rimovibili per posizione). */
public interface Chain<T extends Chain<T, Data>, Data> extends Collection<T, Data>, RemovableAtSequence<T, Data> {

  /* ************************************************************************ */
  /* Override specific member functions from RemovableContainer               */
  /* ************************************************************************ */

  /** Rimuove la prima occorrenza di dat; true se presente. */
  @Override
  default boolean Remove(Data dat) {
    return RemovableAtSequence.super.Remove(dat);
  }

  /* ************************************************************************ */
  /* Override specific member functions from Sequence                         */
  /* ************************************************************************ */

  @Override
  default Natural Search(Data dat) {
    return RemovableAtSequence.super.Search(dat);
  }

}

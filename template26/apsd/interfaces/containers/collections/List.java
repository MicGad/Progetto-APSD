package apsd.interfaces.containers.collections;

import apsd.interfaces.containers.sequences.InsertableAtSequence;
import apsd.interfaces.containers.sequences.MutableSequence;

/** Interface: Chain mutabile e con inserimento per posizione. */
public interface List<T extends List<T, Data>, Data> extends Chain<T, Data>, MutableSequence<T, Data>, InsertableAtSequence<T, Data> {

  /* ************************************************************************ */
  /* Override specific member functions from InsertableContainer              */
  /* ************************************************************************ */

  /** Inserisce in coda. */
  @Override
  default boolean Insert(Data dat) {
    return InsertableAtSequence.super.Insert(dat);
  }

}

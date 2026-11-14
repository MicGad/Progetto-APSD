package apsd.interfaces.containers.base;

import apsd.classes.utilities.Natural;

/** Interface: Contenitore generico. */
public interface Container<T extends Container<T>> {

  /** Restituisce un nuovo contenitore vuoto dello stesso tipo. */
  T Empty();

  /** Numero di elementi contenuti. */
  Natural Size();

  default boolean IsEmpty() {
    return Size().IsZero();
  }

}

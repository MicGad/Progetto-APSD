package apsd.interfaces.containers.deqs;

import apsd.interfaces.containers.base.ClearableContainer;
import apsd.interfaces.containers.base.InsertableContainer;

/** Interface: Coda (FIFO). */
public interface Queue<T extends Queue<T, Data>, Data> extends ClearableContainer<T>, InsertableContainer<T, Data> {

  /* ************************************************************************ */
  /* Specific member functions of Queue                                       */
  /* ************************************************************************ */

  /** Dato in testa, oppure null se la coda e vuota. */
  Data Head();

  /** Rimuove il dato in testa (nessun effetto se vuota). */
  void Dequeue();

  default Data HeadNDequeue() {
    Data head = Head();
    Dequeue();
    return head;
  }

  void Enqueue(Data dat);

  /* ************************************************************************ */
  /* Override specific member functions from ClearableContainer               */
  /* ************************************************************************ */

  @Override
  default void Clear() {
    while (!IsEmpty()) Dequeue();
  }

  /* ************************************************************************ */
  /* Override specific member functions from InsertableContainer              */
  /* ************************************************************************ */

  @Override
  default boolean Insert(Data dat) {
    Enqueue(dat);
    return true;
  }

}

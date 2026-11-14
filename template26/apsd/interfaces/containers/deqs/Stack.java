package apsd.interfaces.containers.deqs;

import apsd.interfaces.containers.base.ClearableContainer;
import apsd.interfaces.containers.base.InsertableContainer;

/** Interface: Pila (LIFO). */
public interface Stack<T extends Stack<T, Data>, Data> extends ClearableContainer<T>, InsertableContainer<T, Data> {

  /* ************************************************************************ */
  /* Specific member functions of Stack                                       */
  /* ************************************************************************ */

  /** Dato in cima, oppure null se la pila e vuota. */
  Data Top();

  /** Rimuove il dato in cima (nessun effetto se vuota). */
  void Pop();

  default Data TopNPop() {
    Data top = Top();
    Pop();
    return top;
  }

  /** Rimpiazza il dato in cima con dat. */
  default void SwapTop(Data dat) {
    Pop();
    Push(dat);
  }

  default Data TopNSwap(Data newdat) {
    Data top = Top();
    SwapTop(newdat);
    return top;
  }

  void Push(Data dat);

  /* ************************************************************************ */
  /* Override specific member functions from ClearableContainer               */
  /* ************************************************************************ */

  @Override
  default void Clear() {
    while (!IsEmpty()) Pop();
  }

  /* ************************************************************************ */
  /* Override specific member functions from InsertableContainer              */
  /* ************************************************************************ */

  @Override
  default boolean Insert(Data dat) {
    Push(dat);
    return true;
  }

}

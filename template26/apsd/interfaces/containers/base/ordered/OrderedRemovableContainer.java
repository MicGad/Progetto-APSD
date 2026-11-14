package apsd.interfaces.containers.base.ordered;

import apsd.interfaces.containers.base.RemovableContainer;

/** Interface: OrderedContainer & RemovableContainer. */
public interface OrderedRemovableContainer<T extends OrderedRemovableContainer<T, Data>, Data extends Comparable<? super Data>> extends OrderedContainer<T, Data>, RemovableContainer<T, Data> {

  /* ************************************************************************ */
  /* Specific member functions of OrderedRemovableContainer                   */
  /* ************************************************************************ */

  default void RemoveMin() {
    MinNRemove();
  }

  /** Rimuove e restituisce il minimo (null se vuoto). */
  default Data MinNRemove() {
    Data min = Min();
    if (min != null) Remove(min);
    return min;
  }

  default void RemoveMax() {
    MaxNRemove();
  }

  /** Rimuove e restituisce il massimo (null se vuoto). */
  default Data MaxNRemove() {
    Data max = Max();
    if (max != null) Remove(max);
    return max;
  }

  default void RemovePredecessor(Data dat) {
    PredecessorNRemove(dat);
  }

  /** Rimuove e restituisce il predecessore di dat (null se assente). */
  default Data PredecessorNRemove(Data dat) {
    Data pred = Predecessor(dat);
    if (pred != null) Remove(pred);
    return pred;
  }

  default void RemoveSuccessor(Data dat) {
    SuccessorNRemove(dat);
  }

  /** Rimuove e restituisce il successore di dat (null se assente). */
  default Data SuccessorNRemove(Data dat) {
    Data succ = Successor(dat);
    if (succ != null) Remove(succ);
    return succ;
  }

}

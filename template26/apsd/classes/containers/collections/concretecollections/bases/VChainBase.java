package apsd.classes.containers.collections.concretecollections.bases;

import apsd.classes.containers.sequences.DynCircularVector;
import apsd.classes.utilities.Natural;
import apsd.interfaces.containers.base.TraversableContainer;
import apsd.interfaces.containers.collections.Chain;
import apsd.interfaces.containers.iterators.BackwardIterator;
import apsd.interfaces.containers.iterators.ForwardIterator;
import apsd.interfaces.containers.sequences.DynVector;
import apsd.interfaces.traits.Predicate;

/** Object: Abstract list base implementation on (dynamic circular) vector. */
abstract public class VChainBase<T extends VChainBase<T, Data>, Data> implements Chain<T, Data> {

  /* ************************************************************************ */
  /* Fields and constructors                                                  */
  /* ************************************************************************ */

  protected DynVector<?, Data> vec = new DynCircularVector<>();

  public VChainBase() {}

  public VChainBase(TraversableContainer<?, Data> con) {
    if (con == null) throw new NullPointerException("Container cannot be null!");
    AllInserted(con);
  }

  /* ************************************************************************ */
  /* Override specific member functions from Container                        */
  /* ************************************************************************ */

  @Override
  public Natural Size() {
    return vec.Size();
  }

  /* ************************************************************************ */
  /* Override specific member functions from ClearableContainer               */
  /* ************************************************************************ */

  @Override
  public void Clear() {
    vec.Clear();
  }

  /* ************************************************************************ */
  /* Override specific member functions from IterableContainer                */
  /* ************************************************************************ */

  @Override
  public ForwardIterator<Data> FIterator() {
    return vec.FIterator();
  }

  @Override
  public BackwardIterator<Data> BIterator() {
    return vec.BIterator();
  }

  /* ************************************************************************ */
  /* Override specific member functions from Sequence                         */
  /* ************************************************************************ */

  @Override
  public Data GetAt(Natural pos) {
    return vec.GetAt(pos);
  }

  @Override
  public T SubSequence(Natural from, Natural to) {
    T res = Empty();
    res.vec = vec.SubSequence(from, to);
    return res;
  }

  /* ************************************************************************ */
  /* Override specific member functions from RemovableAtSequence              */
  /* ************************************************************************ */

  @Override
  public Data AtNRemove(Natural pos) {
    return vec.AtNRemove(pos);
  }

  /* ************************************************************************ */
  /* Override specific member functions from Collection                       */
  /* ************************************************************************ */

  /** Rimuove i dati che non soddisfano fun compattando il vettore in un solo passaggio. */
  @Override
  public boolean Filter(Predicate<Data> fun) {
    if (fun == null) return false;
    long size = vec.Size().ToLong();
    long kept = 0L;
    for (long i = 0L; i < size; i++) {
      Data dat = vec.GetAt(Natural.Of(i));
      if (fun.Apply(dat)) {
        if (kept != i) vec.SetAt(dat, Natural.Of(kept));
        kept++;
      }
    }
    if (kept == size) return false;
    vec.Reduce(Natural.Of(size - kept));
    return true;
  }

}

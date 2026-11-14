package apsd.classes.containers.collections.abstractcollections.bases;

import apsd.classes.utilities.Natural;
import apsd.interfaces.containers.base.TraversableContainer;
import apsd.interfaces.containers.collections.Chain;
import apsd.interfaces.containers.collections.Collection;
import apsd.interfaces.containers.collections.Set;
import apsd.interfaces.containers.iterators.BackwardIterator;
import apsd.interfaces.containers.iterators.ForwardIterator;
import apsd.interfaces.traits.Predicate;

/** Object: Abstract wrapper set base implementation via chain. */
abstract public class WSetBase<T extends WSetBase<T, Data, Chn>, Data, Chn extends Chain<?, Data>> implements Set<T, Data> {

  /* ************************************************************************ */
  /* Fields and constructors                                                  */
  /* ************************************************************************ */

  protected Chn chn;

  public WSetBase() {
    ChainAlloc();
  }

  public WSetBase(TraversableContainer<?, Data> con) {
    if (con == null) throw new NullPointerException("Container cannot be null!");
    ChainAlloc();
    AllInserted(con);
  }

  public WSetBase(Chn chn, TraversableContainer<?, Data> con) {
    if (chn == null) throw new NullPointerException("Chain cannot be null!");
    if (con == null) throw new NullPointerException("Container cannot be null!");
    this.chn = chn;
    AllInserted(con);
  }

  /* ************************************************************************ */
  /* Specific member functions of WSetBase                                    */
  /* ************************************************************************ */

  /** Crea una catena vuota e la assegna al campo chn. */
  abstract protected void ChainAlloc();

  /* ************************************************************************ */
  /* Override specific member functions from Container                        */
  /* ************************************************************************ */

  @Override
  public Natural Size() {
    return chn.Size();
  }

  /* ************************************************************************ */
  /* Override specific member functions from ClearableContainer               */
  /* ************************************************************************ */

  @Override
  public void Clear() {
    chn.Clear();
  }

  /* ************************************************************************ */
  /* Override specific member functions from InsertableContainer              */
  /* ************************************************************************ */

  @Override
  public boolean Insert(Data dat) {
    return chn.InsertIfAbsent(dat);
  }

  /* ************************************************************************ */
  /* Override specific member functions from RemovableContainer               */
  /* ************************************************************************ */

  @Override
  public boolean Remove(Data dat) {
    return chn.Remove(dat);
  }

  /* ************************************************************************ */
  /* Override specific member functions from IterableContainer                */
  /* ************************************************************************ */

  @Override
  public ForwardIterator<Data> FIterator() {
    return chn.FIterator();
  }

  @Override
  public BackwardIterator<Data> BIterator() {
    return chn.BIterator();
  }

  /* ************************************************************************ */
  /* Override specific member functions from Collection                       */
  /* ************************************************************************ */

  @Override
  public boolean Filter(Predicate<Data> fun) {
    return chn.Filter(fun);
  }

  @Override
  public void Intersection(Collection<?, Data> col) {
    if (col == null || col == this) return;
    chn.Filter(col::Exists);
  }

}

package apsd.classes.containers.collections.abstractcollections.bases;

import apsd.interfaces.containers.base.TraversableContainer;
import apsd.interfaces.containers.collections.ordered.OrderedSet;
import apsd.interfaces.containers.collections.ordered.SortedChain;

/** Object: Abstract wrapper set base implementation via sorted chain. */
abstract public class WOrderedSetBase<T extends WOrderedSetBase<T, Data, Chn>, Data extends Comparable<? super Data>, Chn extends SortedChain<?, Data>> extends WSetBase<T, Data, Chn> implements OrderedSet<T, Data> {

  /* ************************************************************************ */
  /* Fields and constructors                                                  */
  /* ************************************************************************ */

  public WOrderedSetBase() {
    super();
  }

  public WOrderedSetBase(TraversableContainer<?, Data> con) {
    super(con);
  }

  public WOrderedSetBase(Chn chn, TraversableContainer<?, Data> con) {
    super(chn, con);
  }

  /* ************************************************************************ */
  /* Specific member functions of WOrderedSetBase                             */
  /* ************************************************************************ */

  /** Confronto elemento per elemento delle due catene ordinate. */
  public boolean IsEqual(WOrderedSetBase<?, Data, ?> set) {
    if (set == null) return false;
    return chn.IsEqual(set.chn);
  }

  /* ************************************************************************ */
  /* Override specific member functions from OrderedSet                       */
  /* ************************************************************************ */

  @Override
  public Data Min() {
    return chn.Min();
  }

  @Override
  public void RemoveMin() {
    chn.RemoveMin();
  }

  @Override
  public Data MinNRemove() {
    return chn.MinNRemove();
  }

  @Override
  public Data Max() {
    return chn.Max();
  }

  @Override
  public void RemoveMax() {
    chn.RemoveMax();
  }

  @Override
  public Data MaxNRemove() {
    return chn.MaxNRemove();
  }

  @Override
  public Data Predecessor(Data dat) {
    return chn.Predecessor(dat);
  }

  @Override
  public void RemovePredecessor(Data dat) {
    chn.RemovePredecessor(dat);
  }

  @Override
  public Data PredecessorNRemove(Data dat) {
    return chn.PredecessorNRemove(dat);
  }

  @Override
  public Data Successor(Data dat) {
    return chn.Successor(dat);
  }

  @Override
  public void RemoveSuccessor(Data dat) {
    chn.RemoveSuccessor(dat);
  }

  @Override
  public Data SuccessorNRemove(Data dat) {
    return chn.SuccessorNRemove(dat);
  }

}

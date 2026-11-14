package apsd.classes.containers.collections.concretecollections;

import apsd.classes.containers.collections.concretecollections.bases.VChainBase;
import apsd.classes.utilities.Natural;
import apsd.interfaces.containers.base.TraversableContainer;
import apsd.interfaces.containers.collections.List;
import apsd.interfaces.containers.iterators.MutableBackwardIterator;
import apsd.interfaces.containers.iterators.MutableForwardIterator;

/** Object: Concrete list implementation on (dynamic circular) vector. */
public class VList<Data> extends VChainBase<VList<Data>, Data> implements List<VList<Data>, Data> {

  /* ************************************************************************ */
  /* Fields and constructors                                                  */
  /* ************************************************************************ */

  public VList() {
    super();
  }

  public VList(TraversableContainer<?, Data> con) {
    super(con);
  }

  /* ************************************************************************ */
  /* Override specific member functions from Container                        */
  /* ************************************************************************ */

  @Override
  public VList<Data> Empty() {
    return new VList<>();
  }

  /* ************************************************************************ */
  /* Override specific member functions from MutableIterableContainer         */
  /* ************************************************************************ */

  @Override
  public MutableForwardIterator<Data> FIterator() {
    return vec.FIterator();
  }

  @Override
  public MutableBackwardIterator<Data> BIterator() {
    return vec.BIterator();
  }

  /* ************************************************************************ */
  /* Override specific member functions from MutableSequence                  */
  /* ************************************************************************ */

  @Override
  public void SetAt(Data dat, Natural pos) {
    vec.SetAt(dat, pos);
  }

  /* ************************************************************************ */
  /* Override specific member functions from InsertableAtSequence             */
  /* ************************************************************************ */

  @Override
  public void InsertAt(Data dat, Natural pos) {
    vec.InsertAt(dat, pos);
  }

}

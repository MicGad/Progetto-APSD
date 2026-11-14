package apsd.classes.containers.sequences.abstractbases;

import apsd.classes.utilities.Natural;
import apsd.interfaces.containers.base.TraversableContainer;

/** Object: Abstract (static) linear vector base implementation. */
abstract public class LinearVectorBase<T extends LinearVectorBase<T, Data>, Data> extends VectorBase<T, Data> {

  public LinearVectorBase() {
    super();
  }

  public LinearVectorBase(Natural inisize) {
    super(inisize);
  }

  public LinearVectorBase(TraversableContainer<?, Data> con) {
    super(con);
  }

  /* ************************************************************************ */
  /* Override specific member functions from ReallocableContainer             */
  /* ************************************************************************ */

  @Override
  public void Realloc(Natural newsize) {
    Data[] old = arr;
    int num = (int) Math.min(old.length, newsize.ToLong());
    ArrayAlloc(newsize);
    System.arraycopy(old, 0, arr, 0, num);
  }

  /* ************************************************************************ */
  /* Override specific member functions from Sequence                         */
  /* ************************************************************************ */

  @Override
  public Data GetAt(Natural pos) {
    return arr[(int) ExcIfOutOfBound(pos)];
  }

  /* ************************************************************************ */
  /* Override specific member functions from MutableSequence                  */
  /* ************************************************************************ */

  @Override
  public void SetAt(Data dat, Natural pos) {
    arr[(int) ExcIfOutOfBound(pos)] = dat;
  }

}

package apsd.classes.containers.sequences.abstractbases;

import apsd.classes.utilities.Natural;
import apsd.interfaces.containers.base.ReallocableContainer;
import apsd.interfaces.containers.base.ResizableContainer;
import apsd.interfaces.containers.base.TraversableContainer;
import apsd.interfaces.containers.sequences.DynVector;

/** Object: Abstract dynamic linear vector base implementation. */
abstract public class DynLinearVectorBase<T extends DynLinearVectorBase<T, Data>, Data> extends LinearVectorBase<T, Data> implements DynVector<T, Data> {

  protected long size = 0L;

  public DynLinearVectorBase() {
    super();
  }

  public DynLinearVectorBase(Natural inicapacity) {
    super(inicapacity);
  }

  public DynLinearVectorBase(TraversableContainer<?, Data> con) {
    super(con);
    size = arr.length;
  }

  /* ************************************************************************ */
  /* Specific member functions of DynLinearVectorBase                         */
  /* ************************************************************************ */

  /** Un nuovo array e' sempre logicamente vuoto. */
  @Override
  protected void ArrayAlloc(Natural newsize) {
    super.ArrayAlloc(newsize);
    size = 0L;
  }

  /* ************************************************************************ */
  /* Override specific member functions from Container                        */
  /* ************************************************************************ */

  @Override
  public Natural Size() {
    return Natural.Of(size);
  }

  /* ************************************************************************ */
  /* Override specific member functions from ClearableContainer               */
  /* ************************************************************************ */

  @Override
  public void Clear() {
    Realloc(Natural.ZERO);
  }

  /* ************************************************************************ */
  /* Override specific member functions from ReallocableContainer             */
  /* ************************************************************************ */

  @Override
  public void Realloc(Natural newsize) {
    long oldsize = size;
    super.Realloc(newsize);
    size = Math.min(oldsize, newsize.ToLong());
  }

  /* ************************************************************************ */
  /* Override specific member functions from ResizableContainer               */
  /* ************************************************************************ */

  @Override
  public void Expand(Natural num) {
    long newsize = size + num.ToLong();
    if (newsize >= arr.length) Grow(num);
    size = newsize;
  }

  @Override
  public void Reduce(Natural num) {
    long newsize = size - num.ToLong();
    if (newsize < 0) throw new IllegalArgumentException("Cannot reduce by " + num + " a vector of size " + size + "!");
    for (long i = newsize; i < size; i++) arr[(int) i] = null;
    size = newsize;
    if (ResizableContainer.THRESHOLD_FACTOR * ReallocableContainer.SHRINK_FACTOR * newsize <= arr.length) Shrink();
  }

}

package apsd.classes.containers.sequences.abstractbases;

import apsd.classes.utilities.Natural;
import apsd.interfaces.containers.base.TraversableContainer;

/** Object: Abstract (static) circular vector base implementation. */
abstract public class CircularVectorBase<T extends CircularVectorBase<T, Data>, Data> extends VectorBase<T, Data> {

  protected long start = 0L;

  public CircularVectorBase() {
    super();
  }

  public CircularVectorBase(Natural inisize) {
    super(inisize);
  }

  public CircularVectorBase(TraversableContainer<?, Data> con) {
    super(con);
  }

  /* ************************************************************************ */
  /* Specific member functions of CircularVectorBase                          */
  /* ************************************************************************ */

  @Override
  protected void ArrayAlloc(Natural newsize) {
    super.ArrayAlloc(newsize);
    start = 0L;
  }

  /** Indice fisico nell'array della posizione logica idx. */
  private int PhysicalIndex(long idx) {
    return (int) ((start + idx) % arr.length);
  }

  /* ************************************************************************ */
  /* Override specific member functions from ReallocableContainer             */
  /* ************************************************************************ */

  @Override
  public void Realloc(Natural newsize) {
    Data[] old = arr;
    long oldstart = start;
    int num = (int) Math.min(old.length, newsize.ToLong());
    ArrayAlloc(newsize);
    for (int i = 0; i < num; i++) {
      arr[i] = old[(int) ((oldstart + i) % old.length)];
    }
  }

  /* ************************************************************************ */
  /* Override specific member functions from Sequence                         */
  /* ************************************************************************ */

  @Override
  public Data GetAt(Natural pos) {
    return arr[PhysicalIndex(ExcIfOutOfBound(pos))];
  }

  /* ************************************************************************ */
  /* Override specific member functions from MutableSequence                  */
  /* ************************************************************************ */

  @Override
  public void SetAt(Data dat, Natural pos) {
    arr[PhysicalIndex(ExcIfOutOfBound(pos))] = dat;
  }

  /* ************************************************************************ */
  /* Specific member functions of Vector                                      */
  /* ************************************************************************ */

  /** Per pos = 0 ruota semplicemente l'inizio del vettore, altrimenti caso generale. */
  @Override
  public void ShiftLeft(Natural pos, Natural num) {
    long size = Size().ToLong();
    long n = Math.min(num.ToLong(), size);
    if (pos.ToLong() != 0L || n == 0L) {
      super.ShiftLeft(pos, num);
      return;
    }
    for (long i = 0; i < n; i++) arr[PhysicalIndex(i)] = null;
    start = (start + n) % arr.length;
    for (long i = size - n; i < size; i++) arr[PhysicalIndex(i)] = null;
  }

  /** Per pos = 0 ruota semplicemente l'inizio del vettore, altrimenti caso generale. */
  @Override
  public void ShiftRight(Natural pos, Natural num) {
    long size = Size().ToLong();
    long n = Math.min(num.ToLong(), size);
    if (pos.ToLong() != 0L || n == 0L) {
      super.ShiftRight(pos, num);
      return;
    }
    for (long i = size - n; i < size; i++) arr[PhysicalIndex(i)] = null;
    start = ((start - n) % arr.length + arr.length) % arr.length;
    for (long i = 0; i < n; i++) arr[PhysicalIndex(i)] = null;
  }

}

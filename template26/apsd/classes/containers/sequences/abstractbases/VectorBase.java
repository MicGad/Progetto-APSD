package apsd.classes.containers.sequences.abstractbases;

import apsd.classes.utilities.MutableNatural;
import apsd.classes.utilities.Natural;
import apsd.interfaces.containers.base.TraversableContainer;
import apsd.interfaces.containers.iterators.MutableBackwardIterator;
import apsd.interfaces.containers.iterators.MutableForwardIterator;
import apsd.interfaces.containers.sequences.Vector;

/** Object: Abstract vector base implementation. */
abstract public class VectorBase<T extends VectorBase<T, Data>, Data> implements Vector<T, Data> {

  protected Data[] arr = null;

  /* ************************************************************************ */
  /* Constructors                                                             */
  /* ************************************************************************ */

  public VectorBase() {
    ArrayAlloc(Natural.ZERO);
  }

  public VectorBase(Natural inisize) {
    ArrayAlloc(inisize);
  }

  public VectorBase(TraversableContainer<?, Data> con) {
    if (con == null) throw new NullPointerException("Container cannot be null!");
    ArrayAlloc(con.Size());
    MutableNatural idx = new MutableNatural();
    con.TraverseForward(dat -> {
      arr[(int) idx.GetLongNIncrement()] = dat;
      return false;
    });
  }

  /* ************************************************************************ */
  /* Specific member functions of VectorBase                                  */
  /* ************************************************************************ */

  /** Alloca un nuovo array vuoto della dimensione indicata. */
  @SuppressWarnings("unchecked")
  protected void ArrayAlloc(Natural newsize) {
    arr = (Data[]) new Object[Math.toIntExact(newsize.ToLong())];
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
  public Natural Capacity() {
    return Natural.Of(arr.length);
  }

  /* ************************************************************************ */
  /* Override specific member functions from IterableContainer                */
  /* ************************************************************************ */

  protected class VectorFIterator implements MutableForwardIterator<Data> {

    protected long idx = 0L;

    @Override
    public boolean IsValid() {
      return idx < Size().ToLong();
    }

    @Override
    public void Reset() {
      idx = 0L;
    }

    @Override
    public Data GetCurrent() {
      if (!IsValid()) throw new IndexOutOfBoundsException("Iterator is not valid (end of iteration or empty container)!");
      return GetAt(Natural.Of(idx));
    }

    @Override
    public void SetCurrent(Data dat) {
      if (!IsValid()) throw new IndexOutOfBoundsException("Iterator is not valid (end of iteration or empty container)!");
      SetAt(dat, Natural.Of(idx));
    }

    @Override
    public void Next() {
      if (IsValid()) idx++;
    }

    @Override
    public Data DataNNext() {
      Data dat = GetCurrent();
      Next();
      return dat;
    }

  }

  @Override
  public MutableForwardIterator<Data> FIterator() {
    return new VectorFIterator();
  }

  protected class VectorBIterator implements MutableBackwardIterator<Data> {

    protected long idx = Size().ToLong() - 1L;

    @Override
    public boolean IsValid() {
      return idx >= 0L && idx < Size().ToLong();
    }

    @Override
    public void Reset() {
      idx = Size().ToLong() - 1L;
    }

    @Override
    public Data GetCurrent() {
      if (!IsValid()) throw new IndexOutOfBoundsException("Iterator is not valid (end of iteration or empty container)!");
      return GetAt(Natural.Of(idx));
    }

    @Override
    public void SetCurrent(Data dat) {
      if (!IsValid()) throw new IndexOutOfBoundsException("Iterator is not valid (end of iteration or empty container)!");
      SetAt(dat, Natural.Of(idx));
    }

    @Override
    public void Prev() {
      if (IsValid()) idx--;
    }

    @Override
    public Data DataNPrev() {
      Data dat = GetCurrent();
      Prev();
      return dat;
    }

  }

  @Override
  public MutableBackwardIterator<Data> BIterator() {
    return new VectorBIterator();
  }

  /* ************************************************************************ */
  /* Override specific member functions from Sequence                         */
  /* ************************************************************************ */

  @Override
  abstract public Data GetAt(Natural pos);

  /* ************************************************************************ */
  /* Override specific member functions from MutableSequence                  */
  /* ************************************************************************ */

  @Override
  abstract public void SetAt(Data dat, Natural pos);

}

package apsd.classes.containers.collections.concretecollections;

import apsd.classes.containers.collections.concretecollections.bases.LLChainBase;
import apsd.classes.containers.collections.concretecollections.bases.LLNode;
import apsd.classes.utilities.Box;
import apsd.classes.utilities.Natural;
import apsd.interfaces.containers.base.TraversableContainer;
import apsd.interfaces.containers.collections.List;
import apsd.interfaces.containers.iterators.ForwardIterator;
import apsd.interfaces.containers.iterators.MutableBackwardIterator;
import apsd.interfaces.containers.iterators.MutableForwardIterator;

/** Object: Concrete list implementation on linked-list. */
public class LLList<Data> extends LLChainBase<LLList<Data>, Data> implements List<LLList<Data>, Data> {

  /* ************************************************************************ */
  /* Fields and constructors                                                  */
  /* ************************************************************************ */

  public LLList() {
    super();
  }

  public LLList(TraversableContainer<?, Data> con) {
    super(con);
  }

  /* ************************************************************************ */
  /* Override specific member functions from Container                        */
  /* ************************************************************************ */

  @Override
  public LLList<Data> Empty() {
    return new LLList<>();
  }

  /* ************************************************************************ */
  /* Override specific member functions from MutableIterableContainer         */
  /* ************************************************************************ */

  protected class ListMutableFIterator extends ListFIterator implements MutableForwardIterator<Data> {

    @Override
    public void SetCurrent(Data dat) {
      if (!IsValid()) throw new IndexOutOfBoundsException("Iterator is not valid (end of iteration or empty container)!");
      it.GetCurrent().Get().Set(dat);
    }

  }

  @Override
  public MutableForwardIterator<Data> FIterator() {
    return new ListMutableFIterator();
  }

  protected class ListMutableBIterator extends ListBIterator implements MutableBackwardIterator<Data> {

    @Override
    public void SetCurrent(Data dat) {
      if (!IsValid()) throw new IndexOutOfBoundsException("Iterator is not valid (end of iteration or empty container)!");
      it.GetCurrent().Get().Set(dat);
    }

  }

  @Override
  public MutableBackwardIterator<Data> BIterator() {
    return new ListMutableBIterator();
  }

  /* ************************************************************************ */
  /* Override specific member functions from MutableSequence                  */
  /* ************************************************************************ */

  @Override
  public void SetAt(Data dat, Natural pos) {
    long idx = ExcIfOutOfBound(pos);
    ForwardIterator<Box<LLNode<Data>>> it = FRefIterator();
    it.Next(idx);
    it.GetCurrent().Get().Set(dat);
  }

  @Override
  public void SetFirst(Data dat) {
    if (IsEmpty()) throw new IndexOutOfBoundsException("The sequence is empty!");
    headref.Get().Set(dat);
  }

  @Override
  public void SetLast(Data dat) {
    if (IsEmpty()) throw new IndexOutOfBoundsException("The sequence is empty!");
    tailref.Get().Set(dat);
  }

  /* ************************************************************************ */
  /* Override specific member functions from InsertableAtSequence             */
  /* ************************************************************************ */

  /** Inserisce un nuovo nodo con dato dat subito dopo prev (in testa se prev e null). */
  private void LinkAfter(LLNode<Data> prev, Data dat) {
    Box<LLNode<Data>> ref = (prev == null) ? headref : prev.GetNext();
    LLNode<Data> node = new LLNode<>(dat, ref.Get());
    ref.Set(node);
    if (node.GetNext().Get() == null) tailref.Set(node);
    size.Increment();
  }

  @Override
  public void InsertAt(Data dat, Natural pos) {
    if (pos == null || pos.ToLong() > size.ToLong()) {
      throw new IndexOutOfBoundsException("Index: " + pos + "; Size: " + Size() + "!");
    }
    LLNode<Data> prev = null;
    ForwardIterator<Box<LLNode<Data>>> it = FRefIterator();
    for (long i = 0L; i < pos.ToLong(); i++) prev = it.DataNNext().Get();
    LinkAfter(prev, dat);
  }

  @Override
  public void InsertFirst(Data dat) {
    LinkAfter(null, dat);
  }

  @Override
  public void InsertLast(Data dat) {
    LinkAfter(tailref.Get(), dat);
  }

}

package apsd.classes.containers.collections.concretecollections.bases;

import java.util.Objects;

import apsd.classes.containers.sequences.Vector;
import apsd.classes.utilities.Box;
import apsd.classes.utilities.MutableNatural;
import apsd.classes.utilities.Natural;
import apsd.interfaces.containers.base.TraversableContainer;
import apsd.interfaces.containers.collections.Chain;
import apsd.interfaces.containers.iterators.BackwardIterator;
import apsd.interfaces.containers.iterators.ForwardIterator;
import apsd.interfaces.containers.iterators.MutableBackwardIterator;
import apsd.interfaces.containers.iterators.MutableForwardIterator;
import apsd.interfaces.traits.Predicate;

/** Object: Abstract chain base implementation on linked-list. */
abstract public class LLChainBase<T extends LLChainBase<T, Data>, Data> implements Chain<T, Data> {

  /* ************************************************************************ */
  /* Fields and constructors                                                  */
  /* ************************************************************************ */

  protected final MutableNatural size = new MutableNatural();
  protected final Box<LLNode<Data>> headref = new Box<>();
  protected final Box<LLNode<Data>> tailref = new Box<>();

  public LLChainBase() {}

  public LLChainBase(TraversableContainer<?, Data> con) {
    if (con == null) throw new NullPointerException("Container cannot be null!");
    AllInserted(con);
  }

  /* ************************************************************************ */
  /* Specific member functions of LLChainBase                                 */
  /* ************************************************************************ */

  /**
   * Inserisce un nuovo nodo con dato dat subito dopo il nodo prev
   * (in testa alla lista se prev e null) e lo restituisce.
   */
  protected LLNode<Data> LinkAfter(LLNode<Data> prev, Data dat) {
    Box<LLNode<Data>> ref = (prev == null) ? headref : prev.GetNext();
    LLNode<Data> node = new LLNode<>(dat, ref.Get());
    ref.Set(node);
    if (node.GetNext().Get() == null) tailref.Set(node);
    size.Increment();
    return node;
  }

  /**
   * Scollega il nodo che segue prev (la testa se prev e null)
   * e ne restituisce il dato.
   */
  protected Data UnlinkAfter(LLNode<Data> prev) {
    Box<LLNode<Data>> ref = (prev == null) ? headref : prev.GetNext();
    LLNode<Data> node = ref.Get();
    ref.Set(node.GetNext().Get());
    if (node == tailref.Get()) tailref.Set(prev);
    size.Decrement();
    return node.Get();
  }

  /** Iteratore forward sui riferimenti ai nodi: la testa della lista, poi i next dei nodi. */
  protected class ListFRefIterator implements MutableForwardIterator<Box<LLNode<Data>>> {

    protected Box<LLNode<Data>> cur = headref;

    @Override
    public boolean IsValid() {
      return cur.Get() != null;
    }

    @Override
    public void Reset() {
      cur = headref;
    }

    @Override
    public Box<LLNode<Data>> GetCurrent() {
      return cur;
    }

    @Override
    public void SetCurrent(Box<LLNode<Data>> ref) {
      cur.Set(ref.Get());
    }

    @Override
    public void Next() {
      if (IsValid()) cur = cur.Get().GetNext();
    }

    @Override
    public Box<LLNode<Data>> DataNNext() {
      Box<LLNode<Data>> ref = cur;
      Next();
      return ref;
    }

  }

  protected ForwardIterator<Box<LLNode<Data>>> FRefIterator() {
    return new ListFRefIterator();
  }

  /** Iteratore backward sui riferimenti ai nodi (lista semplice: si usa un vettore di appoggio). */
  protected class ListBRefIterator implements MutableBackwardIterator<Box<LLNode<Data>>> {

    protected final Vector<Box<LLNode<Data>>> refs = new Vector<>(Natural.Of(size.ToLong()));
    protected long idx;

    protected ListBRefIterator() {
      ForwardIterator<Box<LLNode<Data>>> it = FRefIterator();
      long i = 0L;
      while (it.IsValid()) refs.SetAt(it.DataNNext(), Natural.Of(i++));
      idx = i - 1L;
    }

    @Override
    public boolean IsValid() {
      return idx >= 0L;
    }

    @Override
    public void Reset() {
      idx = refs.Size().ToLong() - 1L;
    }

    @Override
    public Box<LLNode<Data>> GetCurrent() {
      return refs.GetAt(Natural.Of(idx));
    }

    @Override
    public void SetCurrent(Box<LLNode<Data>> ref) {
      GetCurrent().Set(ref.Get());
    }

    @Override
    public void Prev() {
      if (IsValid()) idx--;
    }

    @Override
    public Box<LLNode<Data>> DataNPrev() {
      Box<LLNode<Data>> ref = GetCurrent();
      Prev();
      return ref;
    }

  }

  protected BackwardIterator<Box<LLNode<Data>>> BRefIterator() {
    return new ListBRefIterator();
  }

  /* ************************************************************************ */
  /* Override specific member functions from Container                        */
  /* ************************************************************************ */

  @Override
  public Natural Size() {
    return size.ToNatural();
  }

  /* ************************************************************************ */
  /* Override specific member functions from ClearableContainer               */
  /* ************************************************************************ */

  @Override
  public void Clear() {
    headref.Set(null);
    tailref.Set(null);
    size.Zero();
  }

  /* ************************************************************************ */
  /* Override specific member functions from RemovableContainer               */
  /* ************************************************************************ */

  /** Rimuove la prima occorrenza di dat in un solo passaggio. */
  @Override
  public boolean Remove(Data dat) {
    LLNode<Data> prev = null;
    ForwardIterator<Box<LLNode<Data>>> it = FRefIterator();
    while (it.IsValid()) {
      LLNode<Data> node = it.DataNNext().Get();
      if (Objects.equals(node.Get(), dat)) {
        UnlinkAfter(prev);
        return true;
      }
      prev = node;
    }
    return false;
  }

  /* ************************************************************************ */
  /* Override specific member functions from IterableContainer                */
  /* ************************************************************************ */

  protected class ListFIterator implements ForwardIterator<Data> {

    protected final ForwardIterator<Box<LLNode<Data>>> it = FRefIterator();

    @Override
    public boolean IsValid() {
      return it.IsValid();
    }

    @Override
    public void Reset() {
      it.Reset();
    }

    @Override
    public Data GetCurrent() {
      return it.GetCurrent().Get().Get();
    }

    @Override
    public void Next() {
      it.Next();
    }

    @Override
    public Data DataNNext() {
      return it.DataNNext().Get().Get();
    }

  }

  @Override
  public ForwardIterator<Data> FIterator() {
    return new ListFIterator();
  }

  protected class ListBIterator implements BackwardIterator<Data> {

    protected final BackwardIterator<Box<LLNode<Data>>> it = BRefIterator();

    @Override
    public boolean IsValid() {
      return it.IsValid();
    }

    @Override
    public void Reset() {
      it.Reset();
    }

    @Override
    public Data GetCurrent() {
      return it.GetCurrent().Get().Get();
    }

    @Override
    public void Prev() {
      it.Prev();
    }

    @Override
    public Data DataNPrev() {
      return it.DataNPrev().Get().Get();
    }

  }

  @Override
  public BackwardIterator<Data> BIterator() {
    return new ListBIterator();
  }

  /* ************************************************************************ */
  /* Override specific member functions from Sequence                         */
  /* ************************************************************************ */

  @Override
  public Data GetFirst() {
    if (IsEmpty()) throw new IndexOutOfBoundsException("The sequence is empty!");
    return headref.Get().Get();
  }

  @Override
  public Data GetLast() {
    if (IsEmpty()) throw new IndexOutOfBoundsException("The sequence is empty!");
    return tailref.Get().Get();
  }

  /** Sottosequenza [from, to] (to troncato all ultima posizione); i dati sono accodati senza riordinarli. */
  @Override
  public T SubSequence(Natural from, Natural to) {
    T res = Empty();
    long last = Math.min(to.ToLong(), size.ToLong() - 1L);
    long idx = 0L;
    LLNode<Data> tail = null;
    ForwardIterator<Data> it = FIterator();
    while (it.IsValid() && idx <= last) {
      Data dat = it.DataNNext();
      if (idx >= from.ToLong()) tail = res.LinkAfter(tail, dat);
      idx++;
    }
    return res;
  }

  /* ************************************************************************ */
  /* Override specific member functions from RemovableAtSequence              */
  /* ************************************************************************ */

  @Override
  public Data AtNRemove(Natural pos) {
    long idx = ExcIfOutOfBound(pos);
    LLNode<Data> prev = null;
    ForwardIterator<Box<LLNode<Data>>> it = FRefIterator();
    for (long i = 0L; i < idx; i++) prev = it.DataNNext().Get();
    return UnlinkAfter(prev);
  }

  @Override
  public Data FirstNRemove() {
    ExcIfOutOfBound(Natural.ZERO);
    return UnlinkAfter(null);
  }

  @Override
  public Data LastNRemove() {
    if (IsEmpty()) throw new IndexOutOfBoundsException("The sequence is empty!");
    return AtNRemove(size.ToNatural().Decrement());
  }

  /* ************************************************************************ */
  /* Override specific member functions from Collection                       */
  /* ************************************************************************ */

  /** Rimuove i dati che non soddisfano fun in un solo passaggio. */
  @Override
  public boolean Filter(Predicate<Data> fun) {
    if (fun == null) return false;
    boolean removed = false;
    LLNode<Data> prev = null;
    Box<LLNode<Data>> ref = headref;
    while (ref.Get() != null) {
      LLNode<Data> node = ref.Get();
      if (fun.Apply(node.Get())) {
        prev = node;
        ref = node.GetNext();
      } else {
        UnlinkAfter(prev);
        removed = true;
      }
    }
    return removed;
  }

}

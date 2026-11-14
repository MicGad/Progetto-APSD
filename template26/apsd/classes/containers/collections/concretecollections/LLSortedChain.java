package apsd.classes.containers.collections.concretecollections;

import apsd.classes.containers.collections.concretecollections.bases.LLChainBase;
import apsd.classes.containers.collections.concretecollections.bases.LLNode;
import apsd.classes.utilities.Box;
import apsd.classes.utilities.Natural;
import apsd.interfaces.containers.base.TraversableContainer;
import apsd.interfaces.containers.collections.ordered.SortedChain;
import apsd.interfaces.containers.iterators.ForwardIterator;

/** Object: Concrete sorted chain implementation on linked-list. */
public class LLSortedChain<Data extends Comparable<? super Data>> extends LLChainBase<LLSortedChain<Data>, Data> implements SortedChain<LLSortedChain<Data>, Data> {

  /* ************************************************************************ */
  /* Fields and constructors                                                  */
  /* ************************************************************************ */

  public LLSortedChain() {
    super();
  }

  /** Costruttore di copia: i dati sono gia ordinati, quindi vengono solo accodati. */
  public LLSortedChain(LLSortedChain<Data> chn) {
    super();
    if (chn == null) throw new NullPointerException("Chain cannot be null!");
    LLNode<Data> tail = null;
    ForwardIterator<Data> it = chn.FIterator();
    while (it.IsValid()) {
      LinkAfter(tail, it.DataNNext());
      tail = tailref.Get();
    }
  }

  public LLSortedChain(TraversableContainer<?, Data> con) {
    super(con);
  }

  /* ************************************************************************ */
  /* Override specific member functions from Container                        */
  /* ************************************************************************ */

  @Override
  public LLSortedChain<Data> Empty() {
    return new LLSortedChain<>();
  }

  /* ************************************************************************ */
  /* Specific member functions of LLSortedChain                               */
  /* ************************************************************************ */

  /**
   * Ultimo nodo con dato strettamente minore di dat, ossia il nodo che contiene
   * il predecessore di dat (null se non esiste). Il nodo successivo e il primo
   * con dato maggiore o uguale a dat.
   */
  protected LLNode<Data> PredFind(Data dat) {
    LLNode<Data> prev = null;
    LLNode<Data> cur = headref.Get();
    while (cur != null && cur.Get().compareTo(dat) < 0) {
      prev = cur;
      cur = cur.GetNext().Get();
    }
    return prev;
  }

  /**
   * Nodo che precede il nodo restituito da PredFind (null se il predecessore
   * e la testa, oppure non esiste). Serve a rimuovere il predecessore di dat.
   */
  protected LLNode<Data> PredPredFind(Data dat) {
    LLNode<Data> prevprev = null;
    LLNode<Data> prev = null;
    LLNode<Data> cur = headref.Get();
    while (cur != null && cur.Get().compareTo(dat) < 0) {
      prevprev = prev;
      prev = cur;
      cur = cur.GetNext().Get();
    }
    return prevprev;
  }

  /**
   * Ultimo nodo con dato minore o uguale a dat (null se non esiste). Il nodo
   * successivo contiene il successore di dat: serve a rimuoverlo.
   */
  protected LLNode<Data> PredSuccFind(Data dat) {
    LLNode<Data> prev = null;
    LLNode<Data> cur = headref.Get();
    while (cur != null && cur.Get().compareTo(dat) <= 0) {
      prev = cur;
      cur = cur.GetNext().Get();
    }
    return prev;
  }

  /** Inserisce un nuovo nodo con dato dat subito dopo prev (in testa se prev e null). */
  private void LinkAfter(LLNode<Data> prev, Data dat) {
    Box<LLNode<Data>> ref = (prev == null) ? headref : prev.GetNext();
    LLNode<Data> node = new LLNode<>(dat, ref.Get());
    ref.Set(node);
    if (node.GetNext().Get() == null) tailref.Set(node);
    size.Increment();
  }

  /** Scollega il nodo che segue prev (la testa se prev e null) e ne restituisce il dato. */
  private Data UnlinkAfter(LLNode<Data> prev) {
    Box<LLNode<Data>> ref = (prev == null) ? headref : prev.GetNext();
    LLNode<Data> node = ref.Get();
    ref.Set(node.GetNext().Get());
    if (node == tailref.Get()) tailref.Set(prev);
    size.Decrement();
    return node.Get();
  }

  /** Nodo che segue prev (la testa se prev e null). */
  private LLNode<Data> NodeAfter(LLNode<Data> prev) {
    Box<LLNode<Data>> ref = (prev == null) ? headref : prev.GetNext();
    return ref.Get();
  }

  /* ************************************************************************ */
  /* Override specific member functions from InsertableContainer              */
  /* ************************************************************************ */

  /** Inserisce dat dopo tutti i dati minori o uguali (inserimento stabile). */
  @Override
  public boolean Insert(Data dat) {
    if (dat == null) throw new NullPointerException("Data cannot be null!");
    LinkAfter(PredSuccFind(dat), dat);
    return true;
  }

  /* ************************************************************************ */
  /* Override specific member functions from RemovableContainer               */
  /* ************************************************************************ */

  @Override
  public boolean Remove(Data dat) {
    if (dat == null) return false;
    LLNode<Data> prev = PredFind(dat);
    LLNode<Data> node = NodeAfter(prev);
    if (node == null || node.Get().compareTo(dat) != 0) return false;
    UnlinkAfter(prev);
    return true;
  }

  /* ************************************************************************ */
  /* Override specific member functions from Sequence                         */
  /* ************************************************************************ */

  @Override
  public Natural Search(Data dat) {
    if (dat == null) return null;
    long idx = 0L;
    LLNode<Data> cur = headref.Get();
    while (cur != null && cur.Get().compareTo(dat) < 0) {
      idx++;
      cur = cur.GetNext().Get();
    }
    return (cur != null && cur.Get().compareTo(dat) == 0) ? Natural.Of(idx) : null;
  }

  /* ************************************************************************ */
  /* Override specific member functions from SortedSequence                   */
  /* ************************************************************************ */

  @Override
  public Natural SearchPredecessor(Data dat) {
    if (dat == null) return null;
    long idx = 0L;
    LLNode<Data> cur = headref.Get();
    while (cur != null && cur.Get().compareTo(dat) < 0) {
      idx++;
      cur = cur.GetNext().Get();
    }
    return (idx == 0L) ? null : Natural.Of(idx - 1L);
  }

  @Override
  public Natural SearchSuccessor(Data dat) {
    if (dat == null) return null;
    long idx = 0L;
    LLNode<Data> cur = headref.Get();
    while (cur != null && cur.Get().compareTo(dat) <= 0) {
      idx++;
      cur = cur.GetNext().Get();
    }
    return (cur == null) ? null : Natural.Of(idx);
  }

  /* ************************************************************************ */
  /* Override specific member functions from OrderedSet                       */
  /* ************************************************************************ */

  @Override
  public Data Predecessor(Data dat) {
    if (dat == null) return null;
    LLNode<Data> node = PredFind(dat);
    return (node == null) ? null : node.Get();
  }

  @Override
  public void RemovePredecessor(Data dat) {
    PredecessorNRemove(dat);
  }

  @Override
  public Data PredecessorNRemove(Data dat) {
    if (dat == null) return null;
    if (PredFind(dat) == null) return null;
    return UnlinkAfter(PredPredFind(dat));
  }

  @Override
  public Data Successor(Data dat) {
    if (dat == null) return null;
    LLNode<Data> node = NodeAfter(PredSuccFind(dat));
    return (node == null) ? null : node.Get();
  }

  @Override
  public void RemoveSuccessor(Data dat) {
    SuccessorNRemove(dat);
  }

  @Override
  public Data SuccessorNRemove(Data dat) {
    if (dat == null) return null;
    LLNode<Data> prev = PredSuccFind(dat);
    if (NodeAfter(prev) == null) return null;
    return UnlinkAfter(prev);
  }

  /* ************************************************************************ */
  /* Override specific member functions from Chain                            */
  /* ************************************************************************ */

  @Override
  public boolean InsertIfAbsent(Data dat) {
    if (dat == null) throw new NullPointerException("Data cannot be null!");
    LLNode<Data> prev = PredFind(dat);
    LLNode<Data> node = NodeAfter(prev);
    if (node != null && node.Get().compareTo(dat) == 0) return false;
    LinkAfter(prev, dat);
    return true;
  }

  @Override
  public void RemoveOccurrences(Data dat) {
    if (dat == null) return;
    LLNode<Data> prev = PredFind(dat);
    LLNode<Data> node = NodeAfter(prev);
    while (node != null && node.Get().compareTo(dat) == 0) {
      UnlinkAfter(prev);
      node = NodeAfter(prev);
    }
  }

}

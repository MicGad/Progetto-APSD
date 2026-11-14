package apsd.classes.containers.collections.concretecollections;

import apsd.classes.containers.collections.concretecollections.bases.VChainBase;
import apsd.classes.utilities.Natural;
import apsd.interfaces.containers.base.TraversableContainer;
import apsd.interfaces.containers.collections.ordered.SortedChain;

/** Object: Concrete sorted chain implementation via (dynamic circular) vector. */
public class VSortedChain<Data extends Comparable<? super Data>> extends VChainBase<VSortedChain<Data>, Data> implements SortedChain<VSortedChain<Data>, Data> {

  /* ************************************************************************ */
  /* Fields and constructors                                                  */
  /* ************************************************************************ */

  public VSortedChain() {
    super();
  }

  public VSortedChain(TraversableContainer<?, Data> con) {
    super(con);
  }

  /* ************************************************************************ */
  /* Override specific member functions from Container                        */
  /* ************************************************************************ */

  @Override
  public VSortedChain<Data> Empty() {
    return new VSortedChain<>();
  }

  /* ************************************************************************ */
  /* Specific member functions of VSortedChain                                */
  /* ************************************************************************ */

  /** Prima posizione con dato maggiore o uguale a dat (ricerca binaria). */
  protected long LowerBound(Data dat) {
    long lo = 0L;
    long hi = vec.Size().ToLong();
    while (lo < hi) {
      long mid = (lo + hi) >>> 1;
      if (vec.GetAt(Natural.Of(mid)).compareTo(dat) < 0) lo = mid + 1L;
      else hi = mid;
    }
    return lo;
  }

  /** Prima posizione con dato strettamente maggiore di dat (ricerca binaria). */
  protected long UpperBound(Data dat) {
    long lo = 0L;
    long hi = vec.Size().ToLong();
    while (lo < hi) {
      long mid = (lo + hi) >>> 1;
      if (vec.GetAt(Natural.Of(mid)).compareTo(dat) <= 0) lo = mid + 1L;
      else hi = mid;
    }
    return lo;
  }

  /* ************************************************************************ */
  /* Override specific member functions from InsertableContainer              */
  /* ************************************************************************ */

  /** Inserisce dat dopo tutti i dati minori o uguali (inserimento stabile). */
  @Override
  public boolean Insert(Data dat) {
    vec.InsertAt(dat, Natural.Of(UpperBound(dat)));
    return true;
  }

  /* ************************************************************************ */
  /* Override specific member functions from Chain                            */
  /* ************************************************************************ */

  @Override
  public boolean InsertIfAbsent(Data dat) {
    long pos = LowerBound(dat);
    if (pos < vec.Size().ToLong() && vec.GetAt(Natural.Of(pos)).compareTo(dat) == 0) return false;
    vec.InsertAt(dat, Natural.Of(pos));
    return true;
  }

  @Override
  public void RemoveOccurrences(Data dat) {
    long lo = LowerBound(dat);
    long hi = UpperBound(dat);
    if (lo == hi) return;
    vec.ShiftLeft(Natural.Of(hi), Natural.Of(hi - lo));
    vec.Reduce(Natural.Of(hi - lo));
  }

}

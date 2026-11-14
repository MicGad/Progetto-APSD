package apsd.classes.containers.deqs;

import apsd.classes.containers.collections.concretecollections.VList;
import apsd.classes.utilities.Natural;
import apsd.interfaces.containers.base.TraversableContainer;
import apsd.interfaces.containers.collections.List;
import apsd.interfaces.containers.deqs.Queue;

/** Object: Wrapper queue implementation (la testa e il primo elemento della lista). */
public class WQueue<Data> implements Queue<WQueue<Data>, Data> {

  /* ************************************************************************ */
  /* Fields and constructors                                                  */
  /* ************************************************************************ */

  protected final List<?, Data> lst;

  public WQueue() {
    this.lst = new VList<>();
  }

  public WQueue(List<?, Data> lst) {
    if (lst == null) throw new NullPointerException("List cannot be null!");
    this.lst = lst;
  }

  public WQueue(TraversableContainer<?, Data> con) {
    this(new VList<Data>(), con);
  }

  public WQueue(List<?, Data> lst, TraversableContainer<?, Data> con) {
    this(lst);
    if (con == null) throw new NullPointerException("Container cannot be null!");
    AllInserted(con);
  }

  /* ************************************************************************ */
  /* Override specific member functions from Container                        */
  /* ************************************************************************ */

  @Override
  public Natural Size() {
    return lst.Size();
  }

  /** Nuova coda vuota, basata sullo stesso tipo di lista. */
  @Override
  public WQueue<Data> Empty() {
    return new WQueue<Data>(lst.Empty());
  }

  /* ************************************************************************ */
  /* Override specific member functions from ClearableContainer               */
  /* ************************************************************************ */

  @Override
  public void Clear() {
    lst.Clear();
  }

  /* ************************************************************************ */
  /* Override specific member functions from Queue                            */
  /* ************************************************************************ */

  @Override
  public Data Head() {
    return lst.IsEmpty() ? null : lst.GetFirst();
  }

  @Override
  public void Dequeue() {
    if (!lst.IsEmpty()) lst.RemoveFirst();
  }

  @Override
  public Data HeadNDequeue() {
    return lst.IsEmpty() ? null : lst.FirstNRemove();
  }

  @Override
  public void Enqueue(Data dat) {
    lst.InsertLast(dat);
  }

}

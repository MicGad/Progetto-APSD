package apsd.classes.containers.deqs;

import apsd.classes.containers.collections.concretecollections.VList;
import apsd.classes.utilities.Natural;
import apsd.interfaces.containers.base.TraversableContainer;
import apsd.interfaces.containers.collections.List;
import apsd.interfaces.containers.deqs.Stack;

/** Object: Wrapper stack implementation (la cima e il primo elemento della lista). */
public class WStack<Data> implements Stack<WStack<Data>, Data> {

  /* ************************************************************************ */
  /* Fields and constructors                                                  */
  /* ************************************************************************ */

  protected final List<?, Data> lst;

  public WStack() {
    this.lst = new VList<>();
  }

  public WStack(List<?, Data> lst) {
    if (lst == null) throw new NullPointerException("List cannot be null!");
    this.lst = lst;
  }

  public WStack(TraversableContainer<?, Data> con) {
    this(new VList<Data>(), con);
  }

  public WStack(List<?, Data> lst, TraversableContainer<?, Data> con) {
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

  /** Nuova pila vuota, basata sullo stesso tipo di lista. */
  @Override
  public WStack<Data> Empty() {
    return new WStack<Data>(lst.Empty());
  }

  /* ************************************************************************ */
  /* Override specific member functions from ClearableContainer               */
  /* ************************************************************************ */

  @Override
  public void Clear() {
    lst.Clear();
  }

  /* ************************************************************************ */
  /* Override specific member functions from Stack                            */
  /* ************************************************************************ */

  @Override
  public Data Top() {
    return lst.IsEmpty() ? null : lst.GetFirst();
  }

  @Override
  public void Pop() {
    if (!lst.IsEmpty()) lst.RemoveFirst();
  }

  @Override
  public Data TopNPop() {
    return lst.IsEmpty() ? null : lst.FirstNRemove();
  }

  @Override
  public void SwapTop(Data dat) {
    if (lst.IsEmpty()) lst.InsertFirst(dat);
    else lst.SetFirst(dat);
  }

  @Override
  public Data TopNSwap(Data newdat) {
    if (lst.IsEmpty()) {
      lst.InsertFirst(newdat);
      return null;
    }
    return lst.GetNSetFirst(newdat);
  }

  @Override
  public void Push(Data dat) {
    lst.InsertFirst(dat);
  }

}

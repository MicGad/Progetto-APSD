package apsd.interfaces.containers.base.ordered;

import apsd.classes.utilities.Box;
import apsd.interfaces.containers.base.TraversableContainer;

/** Interface: OrderedContainer & TraversableContainer. */
public interface OrderedTraversableContainer<T extends OrderedTraversableContainer<T, Data>, Data extends Comparable<? super Data>> extends OrderedContainer<T, Data>, TraversableContainer<T, Data> {

  /* ************************************************************************ */
  /* Specific member functions of OrderedTraversableContainer                 */
  /* ************************************************************************ */

  @Override
  default Data Min() {
    Box<Data> min = new Box<>();
    TraverseForward(dat -> {
      if (min.IsNull() || dat.compareTo(min.Get()) < 0) min.Set(dat);
      return false;
    });
    return min.Get();
  }

  @Override
  default Data Max() {
    Box<Data> max = new Box<>();
    TraverseForward(dat -> {
      if (max.IsNull() || dat.compareTo(max.Get()) > 0) max.Set(dat);
      return false;
    });
    return max.Get();
  }

  @Override
  default Data Predecessor(Data dat) {
    if (dat == null) return null;
    Box<Data> pred = new Box<>();
    TraverseForward(cur -> {
      if (cur.compareTo(dat) < 0 && (pred.IsNull() || cur.compareTo(pred.Get()) > 0)) pred.Set(cur);
      return false;
    });
    return pred.Get();
  }

  @Override
  default Data Successor(Data dat) {
    if (dat == null) return null;
    Box<Data> succ = new Box<>();
    TraverseForward(cur -> {
      if (cur.compareTo(dat) > 0 && (succ.IsNull() || cur.compareTo(succ.Get()) < 0)) succ.Set(cur);
      return false;
    });
    return succ.Get();
  }

}

package apsd.classes.containers.collections.abstractcollections;

import apsd.classes.containers.collections.abstractcollections.bases.WOrderedSetBase;
import apsd.classes.containers.collections.concretecollections.VSortedChain;
import apsd.interfaces.containers.base.TraversableContainer;
import apsd.interfaces.containers.collections.ordered.SortedChain;

/** Object: Wrapper ordered set implementation via sorted chain. */
public class WOrderedSet<Data extends Comparable<? super Data>> extends WOrderedSetBase<WOrderedSet<Data>, Data, SortedChain<?, Data>> {

  /* ************************************************************************ */
  /* Fields and constructors                                                  */
  /* ************************************************************************ */

  public WOrderedSet() {
    super();
  }

  public WOrderedSet(TraversableContainer<?, Data> con) {
    super(con);
  }

  public WOrderedSet(SortedChain<?, Data> chn, TraversableContainer<?, Data> con) {
    super(chn, con);
  }

  /* ************************************************************************ */
  /* Specific member functions of WOrderedSet                                 */
  /* ************************************************************************ */

  @Override
  protected void ChainAlloc() {
    chn = new VSortedChain<>();
  }

  /* ************************************************************************ */
  /* Override specific member functions from Container                        */
  /* ************************************************************************ */

  /** Nuovo insieme vuoto, basato sulla catena di default. */
  @Override
  public WOrderedSet<Data> Empty() {
    return new WOrderedSet<>();
  }

}

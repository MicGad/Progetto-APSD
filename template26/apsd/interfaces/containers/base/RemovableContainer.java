package apsd.interfaces.containers.base;

import apsd.classes.utilities.Box;

/** Interface: Container con supporto alla rimozione di un dato. */
public interface RemovableContainer<T extends RemovableContainer<T, Data>, Data> extends Container<T> {

  boolean Remove(Data dat);

  /** True se tutti gli elementi di con sono stati rimossi. */
  default boolean AllRemoved(TraversableContainer<?, Data> con) {
    if (con == null) return false;
    return !con.TraverseForward(dat -> !Remove(dat));
  }

  /** True se almeno un elemento di con e' stato rimosso. */
  default boolean SomeRemoved(TraversableContainer<?, Data> con) {
    if (con == null) return false;
    Box<Boolean> some = new Box<>(false);
    con.TraverseForward(dat -> {
      if (Remove(dat)) some.Set(true);
      return false;
    });
    return some.Get();
  }

}

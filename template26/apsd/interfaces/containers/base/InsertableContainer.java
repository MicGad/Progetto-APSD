package apsd.interfaces.containers.base;

import apsd.classes.utilities.Box;

/** Interface: Container con supporto all'inserimento di un dato. */
public interface InsertableContainer<T extends InsertableContainer<T, Data>, Data> extends Container<T> {

  boolean Insert(Data dat);

  /** True se tutti gli elementi di con sono stati inseriti. */
  default boolean AllInserted(TraversableContainer<?, Data> con) {
    if (con == null) return false;
    return !con.TraverseForward(dat -> !Insert(dat));
  }

  /** True se almeno un elemento di con e' stato inserito. */
  default boolean SomeInserted(TraversableContainer<?, Data> con) {
    if (con == null) return false;
    Box<Boolean> some = new Box<>(false);
    con.TraverseForward(dat -> {
      if (Insert(dat)) some.Set(true);
      return false;
    });
    return some.Get();
  }

}

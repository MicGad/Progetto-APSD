package apsd.interfaces.containers.collections;

import apsd.interfaces.containers.base.TraversableContainer;

/** Interface: Collection senza duplicati. */
public interface Set<T extends Set<T, Data>, Data> extends Collection<T, Data> {

  /* ************************************************************************ */
  /* Specific member functions of Set                                         */
  /* ************************************************************************ */

  /** True se i due insiemi hanno gli stessi elementi. */
  default boolean IsEqual(Set<?, Data> set) {
    if (set == null) return false;
    if (!Size().equals(set.Size())) return false;
    return !TraverseForward(dat -> !set.Exists(dat));
  }

  /* ************************************************************************ */
  /* Override specific member functions from TraversableContainer             */
  /* ************************************************************************ */

  @Override
  default boolean IsEqual(TraversableContainer<?, Data> con) {
    if (con instanceof Set<?, Data> set) return IsEqual(set);
    return Collection.super.IsEqual(con);
  }

  /* ************************************************************************ */
  /* Override specific member functions from Collection                       */
  /* ************************************************************************ */

  /** Mantiene solo i dati presenti anche in col. */
  @Override
  default void Intersection(Collection<?, Data> col) {
    if (col == null || col == this) return;
    Filter(col::Exists);
  }

}

package apsd.interfaces.containers.collections;

import java.util.ArrayList;
import java.util.Objects;

import apsd.interfaces.containers.base.ClearableContainer;
import apsd.interfaces.containers.base.InsertableContainer;
import apsd.interfaces.containers.base.IterableContainer;
import apsd.interfaces.containers.base.RemovableContainer;
import apsd.interfaces.traits.Predicate;

/** Interface: Container con inserimento, rimozione e iterazione dei dati. */
public interface Collection<T extends Collection<T, Data>, Data> extends ClearableContainer<T>, InsertableContainer<T, Data>, RemovableContainer<T, Data>, IterableContainer<T, Data> {

  /* ************************************************************************ */
  /* Specific member functions of Collection                                  */
  /* ************************************************************************ */

  /** Inserisce dat solo se assente; true se inserito. */
  default boolean InsertIfAbsent(Data dat) {
    return !Exists(dat) && Insert(dat);
  }

  /** Rimuove tutte le occorrenze di dat. */
  default void RemoveOccurrences(Data dat) {
    Filter(cur -> !Objects.equals(cur, dat));
  }

  /** Rimuove tutti i dati che NON soddisfano fun; true se ne e stato rimosso almeno uno. */
  default boolean Filter(Predicate<Data> fun) {
    if (fun == null) return false;
    ArrayList<Data> rejected = new ArrayList<>();
    TraverseForward(dat -> {
      if (!fun.Apply(dat)) rejected.add(dat);
      return false;
    });
    for (Data dat : rejected) Remove(dat);
    return !rejected.isEmpty();
  }

  /** Unione sul posto con col: inserisce i dati di col non ancora presenti. */
  default void Union(Collection<?, Data> col) {
    if (col == null || col == this) return;
    col.TraverseForward(dat -> {
      InsertIfAbsent(dat);
      return false;
    });
  }

  /** Differenza sul posto con col: rimuove una occorrenza per ogni occorrenza dei dati di col. */
  default void Difference(Collection<?, Data> col) {
    if (col == null) return;
    if (col == this) {
      Clear();
      return;
    }
    col.TraverseForward(dat -> {
      Remove(dat);
      return false;
    });
  }

  /** Intersezione sul posto con col: ogni dato resta con molteplicita al piu pari a quella in col. */
  default void Intersection(Collection<?, Data> col) {
    if (col == null || col == this) return;
    ArrayList<Data> snapshot = new ArrayList<>();
    TraverseForward(dat -> {
      snapshot.add(dat);
      return false;
    });
    Clear();
    for (Data dat : snapshot) {
      if (col.Multiplicity(dat).compareTo(Multiplicity(dat)) > 0) Insert(dat);
    }
  }

  /* ************************************************************************ */
  /* Override specific member functions from ClearableContainer               */
  /* ************************************************************************ */

  @Override
  default void Clear() {
    Filter(dat -> false);
  }

}

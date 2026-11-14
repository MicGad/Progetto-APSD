package zapsdtest.simpletest.apsd.classes.extended;

import java.util.ArrayList;
import java.util.List;

import apsd.classes.utilities.Natural;
import apsd.interfaces.containers.base.TraversableContainer;
import apsd.interfaces.containers.iterators.BackwardIterator;
import apsd.interfaces.containers.iterators.ForwardIterator;

/** Piccole utilita condivise dai test estesi (non contiene test). */
public final class Helpers {

  private Helpers() {}

  public static Natural N(long n) { return Natural.Of(n); }

  /** Contenuto del contenitore in ordine di attraversamento in avanti. */
  public static <D> List<D> toList(TraversableContainer<?, D> con) {
    List<D> res = new ArrayList<>();
    con.TraverseForward(d -> { res.add(d); return false; });
    return res;
  }

  /** Contenuto del contenitore in ordine di attraversamento all indietro. */
  public static <D> List<D> toListBackward(TraversableContainer<?, D> con) {
    List<D> res = new ArrayList<>();
    con.TraverseBackward(d -> { res.add(d); return false; });
    return res;
  }

  /** Esaurisce un iteratore in avanti raccogliendo i dati. */
  public static <D> List<D> drain(ForwardIterator<D> it) {
    List<D> res = new ArrayList<>();
    while (it.IsValid()) res.add(it.DataNNext());
    return res;
  }

  /** Esaurisce un iteratore all indietro raccogliendo i dati. */
  public static <D> List<D> drain(BackwardIterator<D> it) {
    List<D> res = new ArrayList<>();
    while (it.IsValid()) res.add(it.DataNPrev());
    return res;
  }

  /** Lista modificabile che ammette anche elementi null. */
  @SafeVarargs
  public static <D> List<D> list(D... xs) { return new ArrayList<>(java.util.Arrays.asList(xs)); }

  public static List<Integer> range(int from, int toExcl) {
    List<Integer> res = new ArrayList<>();
    for (int i = from; i < toExcl; i++) res.add(i);
    return res;
  }
}

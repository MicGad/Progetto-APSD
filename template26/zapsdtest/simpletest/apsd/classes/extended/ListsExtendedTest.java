package zapsdtest.simpletest.apsd.classes.extended;

import static org.junit.jupiter.api.Assertions.*;
import static zapsdtest.simpletest.apsd.classes.extended.Helpers.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import apsd.classes.containers.collections.concretecollections.LLList;
import apsd.classes.containers.collections.concretecollections.VList;
import apsd.classes.utilities.Natural;
import apsd.interfaces.containers.base.TraversableContainer;
import apsd.interfaces.containers.iterators.MutableBackwardIterator;
import apsd.interfaces.containers.iterators.MutableForwardIterator;

@DisplayName("Extended tests - lists (VList, LLList)")
public class ListsExtendedTest {

  /* ---------------------------------------------------------------------- */
  /* Factories                                                               */
  /* ---------------------------------------------------------------------- */

  interface LFactory {
    apsd.interfaces.containers.collections.List<?, Integer> New();
    apsd.interfaces.containers.collections.List<?, Integer> From(TraversableContainer<?, Integer> con);
  }

  static Stream<Arguments> lists() {
    return Stream.of(
      Arguments.of("VList", new LFactory() {
        public apsd.interfaces.containers.collections.List<?, Integer> New() { return new VList<Integer>(); }
        public apsd.interfaces.containers.collections.List<?, Integer> From(TraversableContainer<?, Integer> c) { return new VList<Integer>(c); }
      }),
      Arguments.of("LLList", new LFactory() {
        public apsd.interfaces.containers.collections.List<?, Integer> New() { return new LLList<Integer>(); }
        public apsd.interfaces.containers.collections.List<?, Integer> From(TraversableContainer<?, Integer> c) { return new LLList<Integer>(c); }
      }));
  }

  static apsd.interfaces.containers.collections.List<?, Integer> of(LFactory f, Integer... xs) {
    apsd.interfaces.containers.collections.List<?, Integer> l = f.New();
    for (Integer x : xs) l.InsertLast(x);
    return l;
  }

  /* ---------------------------------------------------------------------- */
  /* Lista vuota                                                             */
  /* ---------------------------------------------------------------------- */

  @ParameterizedTest(name = "{0}") @MethodSource("lists")
  void emptyList(String name, LFactory f) {
    var l = f.New();
    assertTrue(l.IsEmpty());
    assertEquals(Natural.ZERO, l.Size());
    assertThrows(IndexOutOfBoundsException.class, () -> l.GetFirst());
    assertThrows(IndexOutOfBoundsException.class, () -> l.GetLast());
    assertThrows(IndexOutOfBoundsException.class, () -> l.GetAt(Natural.ZERO));
    assertThrows(IndexOutOfBoundsException.class, () -> l.SetAt(1, Natural.ZERO));
    assertThrows(IndexOutOfBoundsException.class, () -> l.SetFirst(1));
    assertThrows(IndexOutOfBoundsException.class, () -> l.SetLast(1));
    assertThrows(IndexOutOfBoundsException.class, () -> l.RemoveFirst());
    assertThrows(IndexOutOfBoundsException.class, () -> l.RemoveLast());
    assertThrows(IndexOutOfBoundsException.class, () -> l.FirstNRemove());
    assertThrows(IndexOutOfBoundsException.class, () -> l.LastNRemove());
    assertThrows(IndexOutOfBoundsException.class, () -> l.AtNRemove(Natural.ZERO));
    assertThrows(IndexOutOfBoundsException.class, () -> l.InsertAt(1, N(1)));
    assertFalse(l.Remove(1));
    assertFalse(l.Exists(1));
    assertNull(l.Search(1));
    assertEquals(Natural.ZERO, l.Multiplicity(1));
    assertFalse(l.Filter(d -> true));
    assertFalse(l.Filter(d -> false));
    assertFalse(l.FIterator().IsValid());
    assertFalse(l.BIterator().IsValid());
    assertTrue(toList(l).isEmpty());
    assertTrue(toListBackward(l).isEmpty());
    assertTrue(l.SubSequence(Natural.ZERO, N(3)).IsEmpty());
    l.Clear();
    assertTrue(l.IsEmpty());
    assertTrue(l.Empty().IsEmpty());
  }

  @ParameterizedTest(name = "{0}") @MethodSource("lists")
  void constructors(String name, LFactory f) {
    var l = f.From(of(f, 3, 1, 2));
    assertEquals(list(3, 1, 2), toList(l));
    assertTrue(f.From(f.New()).IsEmpty());
    assertThrows(NullPointerException.class, () -> f.From(null));
    // la copia e indipendente dall originale
    var src = of(f, 1, 2, 3);
    var cp = f.From(src);
    cp.SetFirst(100);
    cp.InsertLast(4);
    assertEquals(list(1, 2, 3), toList(src));
    assertEquals(list(100, 2, 3, 4), toList(cp));
    // copia da un contenitore di tipo diverso
    TraversableContainer<?, Integer> other = (f.New() instanceof VList) ? new LLList<Integer>(src) : new VList<Integer>(src);
    assertEquals(list(1, 2, 3), toList(other));
  }

  /* ---------------------------------------------------------------------- */
  /* Inserimenti / rimozioni posizionali                                     */
  /* ---------------------------------------------------------------------- */

  @ParameterizedTest(name = "{0}") @MethodSource("lists")
  void insertPositions(String name, LFactory f) {
    var l = f.New();
    assertTrue(l.Insert(2)); // Insert == InsertLast
    l.InsertFirst(0);
    l.InsertAt(1, N(1));
    l.InsertAt(3, N(3)); // pos == Size e valido
    assertEquals(list(0, 1, 2, 3), toList(l));
    assertThrows(IndexOutOfBoundsException.class, () -> l.InsertAt(9, N(5)));
    assertEquals(list(0, 1, 2, 3), toList(l));
    assertEquals(N(4), l.Size());
    l.InsertAt(null, N(2)); // null come dato
    assertEquals(list(0, 1, null, 2, 3), toList(l));
    assertNull(l.GetAt(N(2)));
    assertTrue(l.Exists(null));
    assertEquals(N(2), l.Search(null));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("lists")
  void removePositions(String name, LFactory f) {
    var l = of(f, 0, 1, 2, 3, 4, 5);
    assertEquals(0, l.FirstNRemove());
    assertEquals(5, l.LastNRemove());
    assertEquals(2, l.AtNRemove(N(1)));
    assertEquals(list(1, 3, 4), toList(l));
    assertEquals(N(3), l.Size());
    assertThrows(IndexOutOfBoundsException.class, () -> l.RemoveAt(N(3)));
    assertThrows(IndexOutOfBoundsException.class, () -> l.AtNRemove(null));
    l.RemoveFirst();
    l.RemoveLast();
    assertEquals(list(3), toList(l));
    assertEquals(3, l.GetFirst());
    assertEquals(3, l.GetLast()); // un solo elemento: testa == coda
    l.RemoveAt(N(0));
    assertTrue(l.IsEmpty());
    assertThrows(IndexOutOfBoundsException.class, () -> l.GetFirst());
  }

  @ParameterizedTest(name = "{0}") @MethodSource("lists")
  void headAndTailStayValidAfterRemovals(String name, LFactory f) {
    var l = of(f, 1, 2, 3);
    l.RemoveLast();
    l.InsertLast(9); // la coda deve essere stata aggiornata correttamente
    assertEquals(list(1, 2, 9), toList(l));
    assertEquals(9, l.GetLast());
    l.RemoveFirst();
    l.InsertFirst(7);
    assertEquals(list(7, 2, 9), toList(l));
    assertEquals(7, l.GetFirst());
    l.Remove(9);
    assertEquals(2, l.GetLast());
    l.Remove(7);
    l.Remove(2);
    assertTrue(l.IsEmpty());
    l.InsertLast(5); // dopo lo svuotamento testa e coda ripartono da capo
    assertEquals(5, l.GetFirst());
    assertEquals(5, l.GetLast());
    assertEquals(list(5), toList(l));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("lists")
  void setters(String name, LFactory f) {
    var l = of(f, 1, 2, 3);
    l.SetFirst(10);
    l.SetLast(30);
    l.SetAt(20, N(1));
    assertEquals(list(10, 20, 30), toList(l));
    assertEquals(20, l.GetNSetAt(2, N(1)));
    assertEquals(10, l.GetNSetFirst(1));
    assertEquals(30, l.GetNSetLast(3));
    assertEquals(list(1, 2, 3), toList(l));
    assertThrows(IndexOutOfBoundsException.class, () -> l.SetAt(0, N(3)));
    assertThrows(IndexOutOfBoundsException.class, () -> l.GetNSetAt(0, N(3)));
    l.Swap(N(0), N(2));
    assertEquals(list(3, 2, 1), toList(l));
    assertEquals(N(3), l.Size());
  }

  /* ---------------------------------------------------------------------- */
  /* Ricerca e rimozione per valore                                          */
  /* ---------------------------------------------------------------------- */

  @ParameterizedTest(name = "{0}") @MethodSource("lists")
  void searchRemoveFilter(String name, LFactory f) {
    apsd.interfaces.containers.collections.List<?, Integer> l = of(f, 1, 2, 1, 3, 1);
    assertEquals(N(0), l.Search(1));
    assertEquals(N(3), l.Search(3));
    assertNull(l.Search(9));
    assertEquals(N(3), l.Multiplicity(1));
    assertTrue(l.Remove(1)); // solo la prima occorrenza
    assertEquals(list(2, 1, 3, 1), toList(l));
    assertFalse(l.Remove(9));
    l.RemoveOccurrences(1);
    assertEquals(list(2, 3), toList(l));
    l.RemoveOccurrences(42); // nessun effetto
    assertEquals(list(2, 3), toList(l));

    l = of(f, 1, 2, 3, 4, 5, 6);
    assertTrue(l.Filter(d -> d % 2 == 0));
    assertEquals(list(2, 4, 6), toList(l));
    assertFalse(l.Filter(d -> true));
    assertFalse(l.Filter(null));
    assertEquals(list(2, 4, 6), toList(l));
    assertTrue(l.Filter(d -> false)); // rimuove tutto
    assertTrue(l.IsEmpty());
    l.InsertLast(1);
    assertEquals(list(1), toList(l));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("lists")
  void filterRemovingHeadTailAndMiddle(String name, LFactory f) {
    apsd.interfaces.containers.collections.List<?, Integer> l = of(f, 0, 1, 2, 3, 4);
    assertTrue(l.Filter(d -> d == 2));
    assertEquals(list(2), toList(l));
    l.InsertFirst(1);
    l.InsertLast(3);
    assertEquals(list(1, 2, 3), toList(l));
    assertEquals(3, l.GetLast());
    l = of(f, 1, 1, 2, 2, 3, 3);
    l.Filter(d -> d != 1 && d != 3);
    assertEquals(list(2, 2), toList(l));
    assertEquals(2, l.GetLast());
  }

  @ParameterizedTest(name = "{0}") @MethodSource("lists")
  void bulkInsertRemove(String name, LFactory f) {
    var l = f.New();
    assertTrue(l.AllInserted(of(f, 1, 2, 3)));
    assertEquals(list(1, 2, 3), toList(l));
    assertTrue(l.AllInserted(f.New())); // contenitore vuoto
    assertFalse(l.SomeInserted(f.New()));
    assertFalse(l.AllInserted(null));
    assertFalse(l.SomeInserted(null));
    assertTrue(l.SomeInserted(of(f, 4)));
    assertEquals(list(1, 2, 3, 4), toList(l));
    assertTrue(l.AllRemoved(of(f, 1, 4)));
    assertEquals(list(2, 3), toList(l));
    assertFalse(l.AllRemoved(of(f, 2, 9, 3))); // si ferma al primo fallimento
    assertEquals(list(3), toList(l));
    assertTrue(l.SomeRemoved(of(f, 9, 3)));
    assertTrue(l.IsEmpty());
    assertFalse(l.SomeRemoved(of(f, 9)));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("lists")
  void insertSelf(String name, LFactory f) {
    // la lista non e un insieme: i duplicati sono ammessi
    var l = f.New();
    for (int i = 0; i < 5; i++) l.Insert(7);
    assertEquals(N(5), l.Size());
    assertEquals(N(5), l.Multiplicity(7));
    assertTrue(l.InsertIfAbsent(8));
    assertFalse(l.InsertIfAbsent(8));
    assertFalse(l.InsertIfAbsent(7));
    assertEquals(N(6), l.Size());
  }

  /* ---------------------------------------------------------------------- */
  /* Operazioni insiemistiche sulle liste (elementi distinti)                */
  /* ---------------------------------------------------------------------- */

  @ParameterizedTest(name = "{0}") @MethodSource("lists")
  void union(String name, LFactory f) {
    var a = of(f, 1, 2, 3);
    a.Union(of(f, 3, 4, 2, 5, 4));
    assertEquals(list(1, 2, 3, 4, 5), toList(a));
    a.Union(f.New());
    assertEquals(list(1, 2, 3, 4, 5), toList(a));
    a.Union(null);
    a.Union(a); // unione con se stessa: nessun effetto
    assertEquals(list(1, 2, 3, 4, 5), toList(a));
    var e = f.New();
    e.Union(of(f, 1, 2));
    assertEquals(list(1, 2), toList(e));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("lists")
  void difference(String name, LFactory f) {
    var a = of(f, 1, 2, 3, 4, 5);
    a.Difference(of(f, 2, 4, 9));
    assertEquals(list(1, 3, 5), toList(a));
    a.Difference(f.New());
    a.Difference(null);
    assertEquals(list(1, 3, 5), toList(a));
    a.Difference(of(f, 1, 3, 5));
    assertTrue(a.IsEmpty());
    a.Difference(of(f, 1)); // differenza su lista vuota
    assertTrue(a.IsEmpty());
    var b = of(f, 1, 2, 3);
    b.Difference(b); // differenza con se stessa: vuota
    assertTrue(b.IsEmpty());
  }

  @ParameterizedTest(name = "{0}") @MethodSource("lists")
  void intersection(String name, LFactory f) {
    var a = of(f, 1, 2, 3, 4, 5);
    a.Intersection(of(f, 5, 3, 9, 1));
    assertEquals(list(1, 3, 5), toList(a)); // l ordine e quello della lista di partenza
    a.Intersection(f.New());
    assertTrue(a.IsEmpty());
    var b = of(f, 1, 2, 3);
    b.Intersection(b); // con se stessa: nessun effetto
    assertEquals(list(1, 2, 3), toList(b));
    b.Intersection(null);
    assertEquals(list(1, 2, 3), toList(b));
    var c = f.New();
    c.Intersection(of(f, 1));
    assertTrue(c.IsEmpty());
    var d = of(f, 1, 2);
    d.Intersection(of(f, 3, 4)); // disgiunti
    assertTrue(d.IsEmpty());
  }

  @ParameterizedTest(name = "{0}") @MethodSource("lists")
  void intersectionWithDuplicatesUsesMinimumMultiplicity(String name, LFactory f) {
    var a = of(f, 1, 1, 1, 2, 3);
    a.Intersection(of(f, 1, 1, 3, 3, 4));
    assertEquals(list(1, 1, 3), toList(a));
    assertEquals(N(2), a.Multiplicity(1));
    assertEquals(N(1), a.Multiplicity(3));
    assertEquals(N(0), a.Multiplicity(2));
    var b = of(f, 5, 5);
    b.Intersection(of(f, 5, 5, 5, 5)); // col con piu copie: restano quelle di b
    assertEquals(list(5, 5), toList(b));
    var c = of(f, 7, 7, 7);
    c.Intersection(of(f, 7)); // col con meno copie: ne resta una
    assertEquals(list(7), toList(c));
    var d = of(f, 1, 1);
    d.Intersection(of(f, 2, 2));
    assertTrue(d.IsEmpty());
  }

  /* ---------------------------------------------------------------------- */
  /* Sequenze: SubSequence / IsEqual                                         */
  /* ---------------------------------------------------------------------- */

  @ParameterizedTest(name = "{0}") @MethodSource("lists")
  void subSequence(String name, LFactory f) {
    var l = of(f, 0, 1, 2, 3, 4);
    assertEquals(list(1, 2, 3), toList(l.SubSequence(N(1), N(3))));
    assertEquals(list(0, 1, 2, 3, 4), toList(l.SubSequence(N(0), N(4))));
    assertEquals(list(3, 4), toList(l.SubSequence(N(3), N(100))));
    assertTrue(l.SubSequence(N(3), N(1)).IsEmpty());
    assertTrue(l.SubSequence(N(9), N(10)).IsEmpty());
    assertEquals(list(0, 1, 2, 3, 4), toList(l));
    var s = l.SubSequence(N(0), N(2));
    s.SetFirst(99);
    s.InsertLast(98); // la sotto-lista e una lista a tutti gli effetti
    assertEquals(0, l.GetFirst());
    assertEquals(list(99, 1, 2, 98), toList(s));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("lists")
  void isEqual(String name, LFactory f) {
    var a = of(f, 1, 2, 3);
    assertTrue(a.IsEqual(of(f, 1, 2, 3)));
    assertFalse(a.IsEqual(of(f, 3, 2, 1))); // l ordine conta per le sequenze
    assertFalse(a.IsEqual(of(f, 1, 2)));
    assertFalse(a.IsEqual(of(f, 1, 2, 4)));
    assertTrue(f.New().IsEqual(f.New()));
    assertFalse(a.IsEqual((apsd.interfaces.containers.sequences.Sequence<?, Integer>) null));
    // confronto tra implementazioni diverse
    assertTrue(a.IsEqual(new VList<Integer>(a)));
    assertTrue(a.IsEqual(new LLList<Integer>(a)));
  }

  /* ---------------------------------------------------------------------- */
  /* Iteratori                                                               */
  /* ---------------------------------------------------------------------- */

  @ParameterizedTest(name = "{0}") @MethodSource("lists")
  void forwardIterator(String name, LFactory f) {
    var l = of(f, 1, 2, 3, 4, 5);
    MutableForwardIterator<Integer> it = l.FIterator();
    assertTrue(it.IsValid());
    assertEquals(1, it.GetCurrent());
    assertEquals(1, it.DataNNext());
    assertEquals(2, it.GetCurrent());
    it.Next(2);
    assertEquals(4, it.GetCurrent());
    it.Next(N(100)); // oltre la fine: ci si ferma
    assertFalse(it.IsValid());
    it.Next(); // innocuo
    assertFalse(it.IsValid());
    it.Reset();
    assertTrue(it.IsValid());
    assertEquals(1, it.GetCurrent());
    assertThrows(IllegalArgumentException.class, () -> it.Next(-1L));
    it.Next(0L);
    assertEquals(1, it.GetCurrent());
    assertEquals(list(1, 2, 3, 4, 5), drain(l.FIterator()));
    // due iteratori indipendenti sulla stessa lista
    var a = l.FIterator();
    var b = l.FIterator();
    a.Next(3);
    assertEquals(4, a.GetCurrent());
    assertEquals(1, b.GetCurrent());
  }

  @ParameterizedTest(name = "{0}") @MethodSource("lists")
  void backwardIterator(String name, LFactory f) {
    var l = of(f, 1, 2, 3, 4, 5);
    MutableBackwardIterator<Integer> it = l.BIterator();
    assertTrue(it.IsValid());
    assertEquals(5, it.GetCurrent());
    assertEquals(5, it.DataNPrev());
    assertEquals(4, it.GetCurrent());
    it.Prev(2);
    assertEquals(2, it.GetCurrent());
    it.Prev(N(100));
    assertFalse(it.IsValid());
    it.Prev();
    assertFalse(it.IsValid());
    it.Reset();
    assertEquals(5, it.GetCurrent());
    assertThrows(IllegalArgumentException.class, () -> it.Prev(-1L));
    assertEquals(list(5, 4, 3, 2, 1), drain(l.BIterator()));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("lists")
  void mutableIterators(String name, LFactory f) {
    var l = of(f, 1, 2, 3, 4);
    MutableForwardIterator<Integer> it = l.FIterator();
    while (it.IsValid()) { it.SetCurrent(it.GetCurrent() * 10); it.Next(); }
    assertEquals(list(10, 20, 30, 40), toList(l));
    MutableBackwardIterator<Integer> bit = l.BIterator();
    int k = 0;
    while (bit.IsValid()) { bit.SetCurrent(k++); bit.Prev(); }
    assertEquals(list(3, 2, 1, 0), toList(l));
    // SetCurrent su un iteratore a meta
    var it2 = l.FIterator();
    it2.Next(2);
    it2.SetCurrent(77);
    assertEquals(list(3, 2, 77, 0), toList(l));
    assertEquals(77, l.GetAt(N(2)));
    assertEquals(N(4), l.Size());
  }

  @ParameterizedTest(name = "{0}") @MethodSource("lists")
  void foreachAndTraverse(String name, LFactory f) {
    var l = of(f, 1, 2, 3, 4);
    List<Integer> seen = new ArrayList<>();
    assertTrue(l.FIterator().ForEachForward(d -> { seen.add(d); return d == 3; }));
    assertEquals(list(1, 2, 3), seen);
    seen.clear();
    assertTrue(l.BIterator().ForEachBackward(d -> { seen.add(d); return d == 2; }));
    assertEquals(list(4, 3, 2), seen);
    assertFalse(l.FIterator().ForEachForward(null));
    assertFalse(l.BIterator().ForEachBackward(null));
    assertFalse(l.TraverseForward(d -> false));
    assertEquals(10, l.FoldForward((d, acc) -> acc + d, 0));
    assertEquals("4321", l.FoldBackward((d, acc) -> acc + d, ""));
    assertEquals("x", l.FoldForward(null, "x"));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("lists")
  void iteratorsOnSingleElement(String name, LFactory f) {
    var l = of(f, 42);
    assertEquals(list(42), drain(l.FIterator()));
    assertEquals(list(42), drain(l.BIterator()));
    var it = l.FIterator();
    it.Next();
    assertFalse(it.IsValid());
    it.Reset();
    assertTrue(it.IsValid());
  }

  /* ---------------------------------------------------------------------- */
  /* Crescita e riduzione, test randomizzato                                 */
  /* ---------------------------------------------------------------------- */

  @ParameterizedTest(name = "{0}") @MethodSource("lists")
  void growThenShrinkThenReuse(String name, LFactory f) {
    var l = f.New();
    for (int i = 0; i < 2000; i++) l.InsertLast(i);
    assertEquals(N(2000), l.Size());
    assertEquals(range(0, 2000), toList(l));
    for (int i = 0; i < 2000; i++) assertEquals(i, l.FirstNRemove());
    assertTrue(l.IsEmpty());
    for (int i = 0; i < 100; i++) l.InsertFirst(i);
    assertEquals(N(100), l.Size());
    assertEquals(99, l.GetFirst());
    assertEquals(0, l.GetLast());
    l.Clear();
    assertTrue(l.IsEmpty());
    assertEquals(Natural.ZERO, l.Size());
    l.Insert(1);
    assertEquals(list(1), toList(l));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("lists")
  void randomizedAgainstModel(String name, LFactory f) {
    var l = f.New();
    List<Integer> model = new ArrayList<>();
    Random rnd = new Random(2024);
    for (int step = 0; step < 4000; step++) {
      int op = rnd.nextInt(12);
      if (op < 4 || model.isEmpty()) {
        int pos = rnd.nextInt(model.size() + 1);
        l.InsertAt(step % 40, N(pos));
        model.add(pos, step % 40);
      } else if (op < 6) {
        int pos = rnd.nextInt(model.size());
        assertEquals(model.remove(pos), l.AtNRemove(N(pos)));
      } else if (op == 6) {
        int pos = rnd.nextInt(model.size());
        l.SetAt(-step, N(pos));
        model.set(pos, -step);
      } else if (op == 7) {
        int v = rnd.nextInt(40);
        assertEquals(model.remove((Integer) v), l.Remove(v));
      } else if (op == 8) {
        assertEquals(model.remove(0), l.FirstNRemove());
      } else if (op == 9) {
        assertEquals(model.remove(model.size() - 1), l.LastNRemove());
      } else if (op == 10) {
        int v = rnd.nextInt(40);
        int idx = model.indexOf(v);
        Natural got = l.Search(v);
        if (idx < 0) assertNull(got); else assertEquals(N(idx), got);
      } else if (rnd.nextInt(20) == 0) {
        int mod = 2 + rnd.nextInt(3);
        boolean removed = l.Filter(d -> d % mod != 0);
        boolean modelRemoved = model.removeIf(d -> d % mod == 0);
        assertEquals(modelRemoved, removed);
      }
      assertEquals(model.size(), l.Size().ToLong());
      if (!model.isEmpty()) {
        assertEquals(model.get(0), l.GetFirst());
        assertEquals(model.get(model.size() - 1), l.GetLast());
      }
      if (step % 83 == 0) {
        assertEquals(model, toList(l), "step " + step);
        List<Integer> back = toListBackward(l);
        Collections.reverse(back);
        assertEquals(model, back, "backward step " + step);
      }
    }
    assertEquals(model, toList(l));
  }

  /* ---------------------------------------------------------------------- */
  /* Altri tipi generici                                                     */
  /* ---------------------------------------------------------------------- */

  @Test
  void stringLists() {
    VList<String> v = new VList<>();
    LLList<String> l = new LLList<>();
    for (String s : new String[] {"pear", "apple", "fig", "apple"}) { v.Insert(s); l.Insert(s); }
    assertEquals(list("pear", "apple", "fig", "apple"), toList(v));
    assertEquals(toList(v), toList(l));
    assertTrue(v.IsEqual(l));
    assertEquals(N(2), v.Multiplicity("apple"));
    assertEquals(N(1), l.Search("apple"));
    assertTrue(v.Remove("apple"));
    assertTrue(l.Remove("apple"));
    assertEquals(list("pear", "fig", "apple"), toList(v));
    v.RemoveOccurrences("apple");
    l.RemoveOccurrences("apple");
    assertEquals(list("pear", "fig"), toList(l));
    assertEquals(toList(v), toList(l));
    v.Union(l); // elementi gia presenti: nessun effetto
    assertEquals(list("pear", "fig"), toList(v));
    v.InsertAt("zzz", N(1));
    assertEquals(list("pear", "zzz", "fig"), toList(v));
    var it = l.BIterator();
    assertEquals("fig", it.DataNPrev());
    assertEquals("pear", it.DataNPrev());
    assertFalse(it.IsValid());
    var sub = v.SubSequence(N(1), N(2));
    assertEquals(list("zzz", "fig"), toList(sub));
    l.Clear();
    assertTrue(l.IsEmpty());
  }
}

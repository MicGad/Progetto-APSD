package zapsdtest.simpletest.apsd.classes.extended;

import static org.junit.jupiter.api.Assertions.*;
import static zapsdtest.simpletest.apsd.classes.extended.Helpers.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import apsd.classes.containers.collections.abstractcollections.WOrderedSet;
import apsd.classes.containers.collections.abstractcollections.WSet;
import apsd.classes.containers.collections.concretecollections.LLList;
import apsd.classes.containers.collections.concretecollections.LLSortedChain;
import apsd.classes.containers.collections.concretecollections.VList;
import apsd.classes.containers.collections.concretecollections.VSortedChain;
import apsd.classes.utilities.Natural;
import apsd.interfaces.containers.base.TraversableContainer;
import apsd.interfaces.containers.collections.Set;
import apsd.interfaces.containers.collections.ordered.OrderedSet;

@DisplayName("Extended tests - sets (WSet, WOrderedSet)")
public class SetsExtendedTest {

  /* ---------------------------------------------------------------------- */
  /* Factories                                                               */
  /* ---------------------------------------------------------------------- */

  interface SetFactory {
    Set<?, Integer> New();
    Set<?, Integer> From(TraversableContainer<?, Integer> con);
  }

  interface OSetFactory {
    OrderedSet<?, Integer> New();
    OrderedSet<?, Integer> From(TraversableContainer<?, Integer> con);
  }

  static Stream<Arguments> orderedSets() {
    return Stream.of(
      Arguments.of("WOrderedSet(default)", new OSetFactory() {
        public OrderedSet<?, Integer> New() { return new WOrderedSet<Integer>(); }
        public OrderedSet<?, Integer> From(TraversableContainer<?, Integer> c) { return new WOrderedSet<Integer>(c); }
      }),
      Arguments.of("WOrderedSet(VSortedChain)", new OSetFactory() {
        public OrderedSet<?, Integer> New() { return new WOrderedSet<Integer>(new VSortedChain<Integer>(), new VList<Integer>()); }
        public OrderedSet<?, Integer> From(TraversableContainer<?, Integer> c) { return new WOrderedSet<Integer>(new VSortedChain<Integer>(), c); }
      }),
      Arguments.of("WOrderedSet(LLSortedChain)", new OSetFactory() {
        public OrderedSet<?, Integer> New() { return new WOrderedSet<Integer>(new LLSortedChain<Integer>(), new VList<Integer>()); }
        public OrderedSet<?, Integer> From(TraversableContainer<?, Integer> c) { return new WOrderedSet<Integer>(new LLSortedChain<Integer>(), c); }
      }));
  }

  static Stream<Arguments> unorderedSets() {
    return Stream.of(
      Arguments.of("WSet(default)", new SetFactory() {
        public Set<?, Integer> New() { return new WSet<Integer>(); }
        public Set<?, Integer> From(TraversableContainer<?, Integer> c) { return new WSet<Integer>(c); }
      }),
      Arguments.of("WSet(VList)", new SetFactory() {
        public Set<?, Integer> New() { return new WSet<Integer>(new VList<Integer>(), new VList<Integer>()); }
        public Set<?, Integer> From(TraversableContainer<?, Integer> c) { return new WSet<Integer>(new VList<Integer>(), c); }
      }),
      Arguments.of("WSet(LLList)", new SetFactory() {
        public Set<?, Integer> New() { return new WSet<Integer>(new LLList<Integer>(), new VList<Integer>()); }
        public Set<?, Integer> From(TraversableContainer<?, Integer> c) { return new WSet<Integer>(new LLList<Integer>(), c); }
      }));
  }

  /** Tutti i tipi di insieme (ordinati e non) visti come Set. */
  static Stream<Arguments> allSets() {
    return Stream.concat(
      unorderedSets(),
      orderedSets().map(a -> {
        OSetFactory o = (OSetFactory) a.get()[1];
        return Arguments.of(a.get()[0], new SetFactory() {
          public Set<?, Integer> New() { return o.New(); }
          public Set<?, Integer> From(TraversableContainer<?, Integer> c) { return o.From(c); }
        });
      }));
  }

  static Set<?, Integer> of(SetFactory f, Integer... xs) {
    Set<?, Integer> s = f.New();
    for (Integer x : xs) s.Insert(x);
    return s;
  }

  static OrderedSet<?, Integer> ofO(OSetFactory f, Integer... xs) {
    OrderedSet<?, Integer> s = f.New();
    for (Integer x : xs) s.Insert(x);
    return s;
  }

  static java.util.Set<Integer> content(TraversableContainer<?, Integer> c) {
    List<Integer> l = toList(c);
    java.util.Set<Integer> res = new HashSet<>(l);
    assertEquals(l.size(), res.size(), "duplicates inside a set: " + l);
    return res;
  }

  static java.util.Set<Integer> hs(Integer... xs) { return new HashSet<>(java.util.Arrays.asList(xs)); }

  static apsd.interfaces.containers.collections.Collection<?, Integer> listOf(Integer... xs) {
    VList<Integer> l = new VList<>();
    for (Integer x : xs) l.Insert(x);
    return l;
  }

  /* ---------------------------------------------------------------------- */
  /* Comportamento generale di un insieme                                    */
  /* ---------------------------------------------------------------------- */

  @ParameterizedTest(name = "{0}") @MethodSource("allSets")
  void emptySet(String name, SetFactory f) {
    Set<?, Integer> s = f.New();
    assertTrue(s.IsEmpty());
    assertEquals(Natural.ZERO, s.Size());
    assertFalse(s.Exists(1));
    assertFalse(s.Exists(null));
    assertEquals(Natural.ZERO, s.Multiplicity(1));
    assertFalse(s.Remove(1));
    assertFalse(s.Filter(d -> true));
    assertFalse(s.Filter(d -> false));
    assertFalse(s.FIterator().IsValid());
    assertFalse(s.BIterator().IsValid());
    assertTrue(toList(s).isEmpty());
    assertTrue(toListBackward(s).isEmpty());
    s.Clear();
    assertTrue(s.IsEmpty());
    assertTrue(s.Empty().IsEmpty());
    s.RemoveOccurrences(3);
    assertTrue(s.IsEmpty());
    assertEquals("none", s.FoldForward((d, acc) -> acc + d, "none"));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("allSets")
  void insertNeverDuplicates(String name, SetFactory f) {
    Set<?, Integer> s = f.New();
    assertTrue(s.Insert(1));
    assertFalse(s.Insert(1));
    assertTrue(s.Insert(2));
    assertFalse(s.Insert(2));
    assertFalse(s.Insert(1));
    assertEquals(N(2), s.Size());
    assertEquals(N(1), s.Multiplicity(1));
    assertEquals(hs(1, 2), content(s));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("orderedSets")
  void orderedInsertNeverDuplicates(String name, OSetFactory f) {
    OrderedSet<?, Integer> s = f.New();
    for (int i = 0; i < 1000; i++) s.Insert(i % 100);
    assertEquals(N(100), s.Size());
    assertEquals(range(0, 100), toList(s));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("unorderedSets")
  void unorderedLargeInsert(String name, SetFactory f) {
    Set<?, Integer> s = f.New();
    for (int i = 0; i < 1000; i++) s.Insert(i % 100);
    assertEquals(N(100), s.Size());
    for (int i = 0; i < 100; i++) assertTrue(s.Exists(i));
    assertFalse(s.Exists(100));
    assertEquals(content(s).size(), 100);
  }

  @ParameterizedTest(name = "{0}") @MethodSource("allSets")
  void removeAndReinsert(String name, SetFactory f) {
    Set<?, Integer> s = of(f, 1, 2, 3, 4);
    assertTrue(s.Remove(2));
    assertFalse(s.Remove(2));
    assertFalse(s.Exists(2));
    assertEquals(N(3), s.Size());
    assertTrue(s.Insert(2)); // di nuovo inseribile
    assertEquals(hs(1, 2, 3, 4), content(s));
    assertTrue(s.Remove(1));
    assertTrue(s.Remove(4));
    assertTrue(s.Remove(2));
    assertTrue(s.Remove(3));
    assertTrue(s.IsEmpty());
    assertFalse(s.Remove(3));
    assertTrue(s.Insert(9));
    assertEquals(hs(9), content(s));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("allSets")
  void constructorsFromContainer(String name, SetFactory f) {
    Set<?, Integer> s = f.From(listOf(5, 1, 2, 3));
    assertEquals(N(4), s.Size());
    assertEquals(hs(1, 2, 3, 5), content(s));
    assertTrue(f.From(listOf()).IsEmpty());
    assertThrows(NullPointerException.class, () -> f.From(null));
    // la copia e indipendente
    Set<?, Integer> cp = f.From(s);
    cp.Insert(100);
    cp.Remove(1);
    assertEquals(hs(1, 2, 3, 5), content(s));
    assertEquals(hs(2, 3, 5, 100), content(cp));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("allSets")
  void constructorsDeduplicateSourceWithRepeatedElements(String name, SetFactory f) {
    Set<?, Integer> s = f.From(listOf(5, 1, 5, 2, 1, 3));
    assertEquals(N(4), s.Size());
    assertEquals(hs(1, 2, 3, 5), content(s));
  }

  @Test
  void constructorsRejectNullArguments() {
    assertThrows(NullPointerException.class, () -> new WSet<Integer>((VList<Integer>) null, new VList<Integer>()));
    assertThrows(NullPointerException.class, () -> new WSet<Integer>(new VList<Integer>(), null));
    assertThrows(NullPointerException.class, () -> new WOrderedSet<Integer>((VSortedChain<Integer>) null, new VList<Integer>()));
    assertThrows(NullPointerException.class, () -> new WOrderedSet<Integer>(new VSortedChain<Integer>(), null));
    assertThrows(NullPointerException.class, () -> new WSet<Integer>((TraversableContainer<?, Integer>) null));
    assertThrows(NullPointerException.class, () -> new WOrderedSet<Integer>((TraversableContainer<?, Integer>) null));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("allSets")
  void filter(String name, SetFactory f) {
    Set<?, Integer> s = of(f, 1, 2, 3, 4, 5, 6);
    assertTrue(s.Filter(d -> d % 2 == 0));
    assertEquals(hs(2, 4, 6), content(s));
    assertFalse(s.Filter(d -> true));
    assertFalse(s.Filter(null));
    assertEquals(N(3), s.Size());
    assertTrue(s.Filter(d -> false));
    assertTrue(s.IsEmpty());
    assertTrue(s.Insert(2));
    s.RemoveOccurrences(2);
    assertTrue(s.IsEmpty());
  }

  @ParameterizedTest(name = "{0}") @MethodSource("allSets")
  void clearAndReuse(String name, SetFactory f) {
    Set<?, Integer> s = of(f, 1, 2, 3);
    s.Clear();
    assertTrue(s.IsEmpty());
    assertFalse(s.Exists(1));
    assertEquals(Natural.ZERO, s.Size());
    assertTrue(s.Insert(1));
    assertEquals(hs(1), content(s));
    Set<?, Integer> e = s.Empty(); // Empty restituisce un insieme nuovo, non la stessa istanza
    assertNotSame(s, e);
    assertTrue(e.IsEmpty());
    e.Insert(5);
    assertEquals(hs(1), content(s));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("allSets")
  void bulkInsertRemove(String name, SetFactory f) {
    Set<?, Integer> s = f.New();
    assertTrue(s.AllInserted(listOf(1, 2, 3)));
    assertTrue(s.AllInserted(listOf()));
    assertFalse(s.AllInserted(listOf(3, 4))); // 3 e gia presente: si ferma e il 4 non viene inserito
    assertEquals(hs(1, 2, 3), content(s));
    assertFalse(s.AllInserted(null));
    assertFalse(s.SomeInserted(null));
    assertFalse(s.SomeInserted(listOf(1, 2, 3))); // tutti gia presenti
    assertTrue(s.SomeInserted(listOf(3, 4)));
    assertEquals(hs(1, 2, 3, 4), content(s));
    assertTrue(s.AllRemoved(listOf(1, 2)));
    assertFalse(s.AllRemoved(listOf(3, 9, 4))); // si ferma al primo assente
    assertEquals(hs(4), content(s));
    assertTrue(s.SomeRemoved(listOf(9, 4)));
    assertFalse(s.SomeRemoved(listOf(9)));
    assertTrue(s.IsEmpty());
  }

  @ParameterizedTest(name = "{0}") @MethodSource("allSets")
  void iterators(String name, SetFactory f) {
    Set<?, Integer> s = of(f, 4, 8, 15, 16, 23, 42);
    List<Integer> fwd = drain(s.FIterator());
    List<Integer> bwd = drain(s.BIterator());
    assertEquals(6, fwd.size());
    assertEquals(hs(4, 8, 15, 16, 23, 42), new HashSet<>(fwd));
    Collections.reverse(bwd);
    assertEquals(fwd, bwd); // all indietro e l ordine inverso dell avanti
    var it = s.FIterator();
    it.Next(10);
    assertFalse(it.IsValid());
    it.Reset();
    assertTrue(it.IsValid());
    assertEquals(fwd.get(0), it.GetCurrent());
    var bit = s.BIterator();
    bit.Prev(100);
    assertFalse(bit.IsValid());
    bit.Reset();
    assertEquals(fwd.get(5), bit.GetCurrent());
  }

  @ParameterizedTest(name = "{0}") @MethodSource("allSets")
  void traversalAndFold(String name, SetFactory f) {
    Set<?, Integer> s = of(f, 1, 2, 3, 4);
    assertEquals(10, s.FoldForward((d, acc) -> acc + d, 0));
    assertEquals(10, s.FoldBackward((d, acc) -> acc + d, 0));
    int[] count = {0};
    assertTrue(s.TraverseForward(d -> ++count[0] == 2));
    assertEquals(2, count[0]);
    assertFalse(s.TraverseBackward(d -> false));
    assertTrue(s.Exists(3));
  }

  /* ---------------------------------------------------------------------- */
  /* Uguaglianza                                                             */
  /* ---------------------------------------------------------------------- */

  @ParameterizedTest(name = "{0}") @MethodSource("allSets")
  void isEqualIgnoresInsertionOrder(String name, SetFactory f) {
    Set<?, Integer> a = of(f, 1, 2, 3);
    Set<?, Integer> b = of(f, 3, 1, 2);
    assertTrue(a.IsEqual(b));
    assertTrue(b.IsEqual(a));
    assertFalse(a.IsEqual(of(f, 1, 2)));
    assertFalse(a.IsEqual(of(f, 1, 2, 4)));
    assertFalse(a.IsEqual(of(f, 1, 2, 3, 4)));
    assertFalse(a.IsEqual((Set<?, Integer>) null));
    assertTrue(f.New().IsEqual(f.New()));
    assertFalse(f.New().IsEqual(a));
    assertTrue(a.IsEqual(a));
  }

  @Test
  void isEqualAcrossImplementations() {
    WSet<Integer> u = new WSet<>(listOf(3, 2, 1));
    WOrderedSet<Integer> o = new WOrderedSet<>(listOf(1, 2, 3));
    assertTrue(u.IsEqual((Set<?, Integer>) o));
    assertTrue(o.IsEqual((Set<?, Integer>) u));
    o.Remove(3);
    assertFalse(u.IsEqual((Set<?, Integer>) o));
    // un insieme confrontato con una lista (non Set) usa le molteplicita
    VList<Integer> l = new VList<>();
    l.Insert(1); l.Insert(2); l.Insert(2);
    assertFalse(u.IsEqual((TraversableContainer<?, Integer>) l));
  }

  /* ---------------------------------------------------------------------- */
  /* Operazioni insiemistiche                                                */
  /* ---------------------------------------------------------------------- */

  @ParameterizedTest(name = "{0}") @MethodSource("allSets")
  void union(String name, SetFactory f) {
    Set<?, Integer> a = of(f, 1, 2, 3);
    a.Union(of(f, 3, 4, 5));
    assertEquals(hs(1, 2, 3, 4, 5), content(a));
    a.Union(f.New());
    a.Union(null);
    a.Union(a);
    assertEquals(hs(1, 2, 3, 4, 5), content(a));
    Set<?, Integer> e = f.New();
    e.Union(of(f, 7, 8));
    assertEquals(hs(7, 8), content(e));
    e.Union(listOf(8, 8, 9, 9)); // da un altra collezione, con duplicati
    assertEquals(hs(7, 8, 9), content(e));
    Set<?, Integer> sub = of(f, 7);
    sub.Union(e); // un sottoinsieme diventa uguale al sovrainsieme
    assertTrue(sub.IsEqual(e));
    e.Union(sub); // superinsieme: nessun effetto
    assertEquals(hs(7, 8, 9), content(e));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("allSets")
  void intersection(String name, SetFactory f) {
    Set<?, Integer> a = of(f, 1, 2, 3, 4, 5);
    a.Intersection(of(f, 4, 5, 6, 7));
    assertEquals(hs(4, 5), content(a));
    a.Intersection(a); // con se stesso: nessun effetto
    a.Intersection(null);
    assertEquals(hs(4, 5), content(a));
    a.Intersection(listOf(5, 5, 9));
    assertEquals(hs(5), content(a));
    a.Intersection(of(f, 1, 2)); // disgiunti
    assertTrue(a.IsEmpty());
    Set<?, Integer> b = of(f, 1, 2);
    b.Intersection(f.New());
    assertTrue(b.IsEmpty());
    Set<?, Integer> c = f.New();
    c.Intersection(of(f, 1));
    assertTrue(c.IsEmpty());
    Set<?, Integer> d = of(f, 1, 2);
    d.Intersection(of(f, 1, 2, 3)); // sottoinsieme: invariato
    assertEquals(hs(1, 2), content(d));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("allSets")
  void difference(String name, SetFactory f) {
    Set<?, Integer> a = of(f, 1, 2, 3, 4, 5);
    a.Difference(of(f, 2, 4, 6));
    assertEquals(hs(1, 3, 5), content(a));
    a.Difference(f.New());
    a.Difference(null);
    assertEquals(hs(1, 3, 5), content(a));
    a.Difference(listOf(5, 5, 1));
    assertEquals(hs(3), content(a));
    a.Difference(of(f, 3, 4));
    assertTrue(a.IsEmpty());
    a.Difference(of(f, 3));
    assertTrue(a.IsEmpty());
    Set<?, Integer> b = of(f, 1, 2, 3);
    b.Difference(b); // con se stesso: vuoto
    assertTrue(b.IsEmpty());
    Set<?, Integer> c = of(f, 1, 2, 3);
    c.Difference(of(f, 3, 2, 1, 0)); // il sottraendo contiene tutto
    assertTrue(c.IsEmpty());
  }

  @ParameterizedTest(name = "{0}") @MethodSource("allSets")
  void setAlgebraIdentitiesRandomized(String name, SetFactory f) {
    Random rnd = new Random(5);
    for (int round = 0; round < 60; round++) {
      HashSet<Integer> ma = new HashSet<>(), mb = new HashSet<>();
      Set<?, Integer> a = f.New(), b = f.New();
      for (int i = 0; i < rnd.nextInt(30); i++) { int v = rnd.nextInt(40); a.Insert(v); ma.add(v); }
      for (int i = 0; i < rnd.nextInt(30); i++) { int v = rnd.nextInt(40); b.Insert(v); mb.add(v); }
      Set<?, Integer> u = f.From(a);
      u.Union(b);
      HashSet<Integer> mu = new HashSet<>(ma); mu.addAll(mb);
      assertEquals(mu, content(u), "union round " + round);
      Set<?, Integer> in = f.From(a);
      in.Intersection(b);
      HashSet<Integer> mi = new HashSet<>(ma); mi.retainAll(mb);
      assertEquals(mi, content(in), "intersection round " + round);
      Set<?, Integer> di = f.From(a);
      di.Difference(b);
      HashSet<Integer> md = new HashSet<>(ma); md.removeAll(mb);
      assertEquals(md, content(di), "difference round " + round);
      // |A u B| = |A| + |B| - |A n B|
      assertEquals(ma.size() + mb.size() - mi.size(), u.Size().ToLong());
      // (A \ B) e B sono disgiunti
      di.Intersection(b);
      assertTrue(di.IsEmpty());
      // gli operandi di destra non vengono modificati
      assertEquals(mb, content(b));
      assertEquals(ma, content(a));
    }
  }

  /* ---------------------------------------------------------------------- */
  /* Insiemi ordinati                                                        */
  /* ---------------------------------------------------------------------- */

  @ParameterizedTest(name = "{0}") @MethodSource("orderedSets")
  void orderedEmpty(String name, OSetFactory f) {
    OrderedSet<?, Integer> s = f.New();
    assertNull(s.Min());
    assertNull(s.Max());
    assertNull(s.Predecessor(3));
    assertNull(s.Successor(3));
    assertNull(s.MinNRemove());
    assertNull(s.MaxNRemove());
    assertNull(s.PredecessorNRemove(3));
    assertNull(s.SuccessorNRemove(3));
    s.RemoveMin(); s.RemoveMax(); s.RemovePredecessor(1); s.RemoveSuccessor(1);
    assertTrue(s.IsEmpty());
  }

  @ParameterizedTest(name = "{0}") @MethodSource("orderedSets")
  void orderedIterationIsSorted(String name, OSetFactory f) {
    OrderedSet<?, Integer> s = ofO(f, 9, 3, 7, 1, 5, 3, 9);
    assertEquals(list(1, 3, 5, 7, 9), toList(s));
    assertEquals(list(9, 7, 5, 3, 1), toListBackward(s));
    assertEquals(list(1, 3, 5, 7, 9), drain(s.FIterator()));
    assertEquals(list(9, 7, 5, 3, 1), drain(s.BIterator()));
    s.Insert(0);
    s.Insert(100);
    s.Insert(4);
    assertEquals(list(0, 1, 3, 4, 5, 7, 9, 100), toList(s));
    s.Remove(5);
    assertEquals(list(0, 1, 3, 4, 7, 9, 100), toList(s));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("orderedSets")
  void orderedMinMaxPredSucc(String name, OSetFactory f) {
    OrderedSet<?, Integer> s = ofO(f, 10, 20, 30, 40);
    assertEquals(10, s.Min());
    assertEquals(40, s.Max());
    assertNull(s.Predecessor(10));
    assertNull(s.Predecessor(5));
    assertEquals(10, s.Predecessor(20));
    assertEquals(20, s.Predecessor(25));
    assertEquals(40, s.Predecessor(1000));
    assertEquals(10, s.Successor(-1));
    assertEquals(20, s.Successor(10));
    assertEquals(30, s.Successor(25));
    assertNull(s.Successor(40));
    assertNull(s.Successor(41));
    assertEquals(hs(10, 20, 30, 40), content(s)); // le interrogazioni non modificano
  }

  @ParameterizedTest(name = "{0}") @MethodSource("orderedSets")
  void orderedRemovals(String name, OSetFactory f) {
    OrderedSet<?, Integer> s = ofO(f, 1, 2, 3, 4, 5, 6, 7);
    assertEquals(1, s.MinNRemove());
    assertEquals(7, s.MaxNRemove());
    s.RemoveMin();
    s.RemoveMax();
    assertEquals(list(3, 4, 5), toList(s));
    assertEquals(3, s.PredecessorNRemove(4));
    assertEquals(5, s.SuccessorNRemove(4));
    assertEquals(list(4), toList(s));
    assertNull(s.PredecessorNRemove(4));
    assertNull(s.SuccessorNRemove(4));
    s.RemovePredecessor(100);
    assertTrue(s.IsEmpty());
    assertTrue(s.Insert(4)); // riutilizzabile
    assertEquals(4, s.Min());
    assertEquals(4, s.Max());
    s.RemoveSuccessor(-9);
    assertTrue(s.IsEmpty());
  }

  @ParameterizedTest(name = "{0}") @MethodSource("orderedSets")
  void orderedOperationsKeepOrder(String name, OSetFactory f) {
    OrderedSet<?, Integer> a = ofO(f, 5, 1, 3);
    a.Union(ofO(f, 4, 2, 3, 6));
    assertEquals(list(1, 2, 3, 4, 5, 6), toList(a));
    a.Intersection(ofO(f, 6, 4, 2, 0));
    assertEquals(list(2, 4, 6), toList(a));
    a.Difference(ofO(f, 4));
    assertEquals(list(2, 6), toList(a));
    a.Union(listOf(5, 1, 5));
    assertEquals(list(1, 2, 5, 6), toList(a));
    a.Intersection(listOf(6, 5, 1, 1));
    assertEquals(list(1, 5, 6), toList(a));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("orderedSets")
  void orderedIsEqualAndCopy(String name, OSetFactory f) {
    OrderedSet<?, Integer> a = ofO(f, 3, 1, 2);
    assertTrue(a.IsEqual(ofO(f, 2, 3, 1)));
    assertFalse(a.IsEqual(ofO(f, 2, 3)));
    OrderedSet<?, Integer> cp = f.From(a);
    assertTrue(a.IsEqual(cp));
    cp.Insert(0);
    assertEquals(list(1, 2, 3), toList(a));
    assertEquals(list(0, 1, 2, 3), toList(cp));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("orderedSets")
  void orderedRandomizedAgainstTreeSet(String name, OSetFactory f) {
    OrderedSet<?, Integer> s = f.New();
    TreeSet<Integer> model = new TreeSet<>();
    Random rnd = new Random(77);
    for (int step = 0; step < 3000; step++) {
      int v = rnd.nextInt(80);
      switch (rnd.nextInt(8)) {
        case 0, 1, 2 -> assertEquals(model.add(v), s.Insert(v));
        case 3 -> assertEquals(model.remove(v), s.Remove(v));
        case 4 -> {
          assertEquals(model.lower(v), s.Predecessor(v));
          assertEquals(model.higher(v), s.Successor(v));
        }
        case 5 -> assertEquals(model.isEmpty() ? null : model.pollFirst(), s.MinNRemove());
        case 6 -> assertEquals(model.isEmpty() ? null : model.pollLast(), s.MaxNRemove());
        default -> {
          assertEquals(model.isEmpty() ? null : model.first(), s.Min());
          assertEquals(model.isEmpty() ? null : model.last(), s.Max());
          assertEquals(model.contains(v), s.Exists(v));
        }
      }
      assertEquals(model.size(), s.Size().ToLong(), "size at step " + step);
    }
    assertEquals(new ArrayList<>(model), toList(s));
  }

  /* ---------------------------------------------------------------------- */
  /* Altri tipi generici                                                     */
  /* ---------------------------------------------------------------------- */

  @Test
  void stringSets() {
    WSet<String> u = new WSet<>();
    WOrderedSet<String> o = new WOrderedSet<>();
    for (String s : new String[] {"cat", "dog", "cat", "emu", "ant", "dog"}) {
      assertEquals(!u.Exists(s), u.Insert(s));
      o.Insert(s);
    }
    assertEquals(N(4), u.Size());
    assertEquals(list("ant", "cat", "dog", "emu"), toList(o));
    assertEquals("ant", o.Min());
    assertEquals("emu", o.Max());
    assertEquals("cat", o.Predecessor("dog"));
    assertEquals("emu", o.Successor("dog"));
    assertEquals("dog", o.Successor("cat"));
    assertNull(o.Predecessor("ant"));
    assertEquals("cat", o.Predecessor("cow")); // elemento assente
    assertTrue(u.IsEqual((Set<?, String>) o));

    WSet<String> other = new WSet<>(new LLList<String>(), new VList<String>());
    other.Insert("dog"); other.Insert("fox");
    WSet<String> inter = new WSet<>(u);
    inter.Intersection(other);
    assertEquals(list("dog"), toList(inter));
    u.Union(other);
    assertEquals(N(5), u.Size());
    assertTrue(u.Exists("fox"));
    u.Difference(other);
    assertEquals(N(3), u.Size());
    assertFalse(u.Exists("dog"));
    o.Difference(other);
    assertEquals(list("ant", "cat", "emu"), toList(o));
    assertEquals("ant", o.MinNRemove());
    assertEquals("emu", o.MaxNRemove());
    assertEquals(list("cat"), toList(o));
  }

  @Test
  void orderedSetOfStringsUsesNaturalOrder() {
    WOrderedSet<String> o = new WOrderedSet<>(new LLSortedChain<String>(), new VList<String>());
    for (String s : new String[] {"b", "B", "a", "A", "b"}) o.Insert(s);
    assertEquals(list("A", "B", "a", "b"), toList(o));
    assertEquals(list("b", "a", "B", "A"), toListBackward(o));
  }

  @Test
  void emptyKeepsChainKind() {
    // Empty() deve produrre un insieme dello stesso tipo e funzionante
    WSet<Integer> u = new WSet<>(new LLList<Integer>(), new VList<Integer>());
    u.Insert(1);
    WSet<Integer> e = u.Empty();
    assertTrue(e.IsEmpty());
    e.Insert(2);
    e.Insert(2);
    assertEquals(N(1), e.Size());
    WOrderedSet<Integer> o = new WOrderedSet<>(new LLSortedChain<Integer>(), new VList<Integer>());
    o.Insert(2);
    o.Insert(1);
    WOrderedSet<Integer> oe = o.Empty();
    assertTrue(oe.IsEmpty());
    oe.Insert(5);
    oe.Insert(3);
    assertEquals(list(3, 5), toList(oe));
  }
}

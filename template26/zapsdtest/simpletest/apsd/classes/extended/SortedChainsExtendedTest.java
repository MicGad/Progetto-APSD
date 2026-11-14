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
import apsd.classes.containers.collections.concretecollections.LLSortedChain;
import apsd.classes.containers.collections.concretecollections.VList;
import apsd.classes.containers.collections.concretecollections.VSortedChain;
import apsd.classes.utilities.Natural;
import apsd.interfaces.containers.base.TraversableContainer;
import apsd.interfaces.containers.collections.Collection;
import apsd.interfaces.containers.collections.ordered.SortedChain;

@DisplayName("Extended tests - sorted chains (VSortedChain, LLSortedChain)")
public class SortedChainsExtendedTest {

  interface SFactory {
    SortedChain<?, Integer> New();
    SortedChain<?, Integer> From(TraversableContainer<?, Integer> con);
  }

  static Stream<Arguments> chains() {
    return Stream.of(
      Arguments.of("VSortedChain", new SFactory() {
        public SortedChain<?, Integer> New() { return new VSortedChain<Integer>(); }
        public SortedChain<?, Integer> From(TraversableContainer<?, Integer> c) { return new VSortedChain<Integer>(c); }
      }),
      Arguments.of("LLSortedChain", new SFactory() {
        public SortedChain<?, Integer> New() { return new LLSortedChain<Integer>(); }
        public SortedChain<?, Integer> From(TraversableContainer<?, Integer> c) { return new LLSortedChain<Integer>(c); }
      }));
  }

  static SortedChain<?, Integer> of(SFactory f, Integer... xs) {
    SortedChain<?, Integer> c = f.New();
    for (Integer x : xs) c.Insert(x);
    return c;
  }

  /* ---------------------------------------------------------------------- */

  @ParameterizedTest(name = "{0}") @MethodSource("chains")
  void emptyChain(String name, SFactory f) {
    SortedChain<?, Integer> c = f.New();
    assertTrue(c.IsEmpty());
    assertEquals(Natural.ZERO, c.Size());
    assertNull(c.Min());
    assertNull(c.Max());
    assertNull(c.Predecessor(5));
    assertNull(c.Successor(5));
    assertNull(c.MinNRemove());
    assertNull(c.MaxNRemove());
    assertNull(c.PredecessorNRemove(5));
    assertNull(c.SuccessorNRemove(5));
    c.RemoveMin(); c.RemoveMax(); c.RemovePredecessor(5); c.RemoveSuccessor(5);
    assertTrue(c.IsEmpty());
    assertNull(c.Search(1));
    assertNull(c.SearchPredecessor(1));
    assertNull(c.SearchSuccessor(1));
    assertFalse(c.Exists(1));
    assertFalse(c.Remove(1));
    c.RemoveOccurrences(1);
    assertFalse(c.Filter(d -> true));
    assertFalse(c.FIterator().IsValid());
    assertFalse(c.BIterator().IsValid());
    assertThrows(IndexOutOfBoundsException.class, () -> c.GetFirst());
    assertThrows(IndexOutOfBoundsException.class, () -> c.GetLast());
    assertThrows(IndexOutOfBoundsException.class, () -> c.FirstNRemove());
    c.Clear();
    assertTrue(c.Empty().IsEmpty());
  }

  @ParameterizedTest(name = "{0}") @MethodSource("chains")
  void insertKeepsOrder(String name, SFactory f) {
    SortedChain<?, Integer> c = f.New();
    for (int x : new int[] {5, 1, 9, 3, 7, 3, 1, 9, 5, 5}) assertTrue(c.Insert(x));
    assertEquals(list(1, 1, 3, 3, 5, 5, 5, 7, 9, 9), toList(c));
    assertEquals(list(9, 9, 7, 5, 5, 5, 3, 3, 1, 1), toListBackward(c));
    assertEquals(N(10), c.Size());
    assertEquals(N(3), c.Multiplicity(5));
    assertEquals(1, c.GetFirst());
    assertEquals(9, c.GetLast());
    // inserimenti agli estremi
    c.Insert(0);
    c.Insert(100);
    assertEquals(0, c.GetFirst());
    assertEquals(100, c.GetLast());
  }

  @ParameterizedTest(name = "{0}") @MethodSource("chains")
  void insertIfAbsent(String name, SFactory f) {
    SortedChain<?, Integer> c = f.New();
    assertTrue(c.InsertIfAbsent(5));
    assertFalse(c.InsertIfAbsent(5));
    assertTrue(c.InsertIfAbsent(1));
    assertTrue(c.InsertIfAbsent(9));
    assertTrue(c.InsertIfAbsent(7));
    assertFalse(c.InsertIfAbsent(1));
    assertFalse(c.InsertIfAbsent(9));
    assertEquals(list(1, 5, 7, 9), toList(c));
    c.Insert(5); // Insert ammette duplicati, InsertIfAbsent no
    assertEquals(list(1, 5, 5, 7, 9), toList(c));
    assertFalse(c.InsertIfAbsent(5));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("chains")
  void constructorSortsAndRejectsNull(String name, SFactory f) {
    SortedChain<?, Integer> c = f.From(of(f, 3, 1, 2));
    assertEquals(list(1, 2, 3), toList(c));
    // sorgente non ordinata
    SortedChain<?, Integer> d = f.From(new VList<Integer>(new LLList<Integer>(new VList<Integer>())));
    assertTrue(d.IsEmpty());
    LLList<Integer> src = new LLList<>();
    for (int x : new int[] {4, 2, 4, 1}) src.Insert(x);
    assertEquals(list(1, 2, 4, 4), toList(f.From(src)));
    assertThrows(NullPointerException.class, () -> f.From(null));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("chains")
  void minMaxPredecessorSuccessor(String name, SFactory f) {
    SortedChain<?, Integer> c = of(f, 1, 3, 3, 5, 7);
    assertEquals(1, c.Min());
    assertEquals(7, c.Max());
    // predecessore: massimo strettamente minore; successore: minimo strettamente maggiore
    assertNull(c.Predecessor(1));
    assertNull(c.Predecessor(0));
    assertEquals(1, c.Predecessor(3));
    assertEquals(3, c.Predecessor(4));
    assertEquals(3, c.Predecessor(5));
    assertEquals(5, c.Predecessor(7));
    assertEquals(7, c.Predecessor(100));
    assertEquals(1, c.Successor(0));
    assertEquals(1, c.Successor(-50));
    assertEquals(3, c.Successor(1));
    assertEquals(5, c.Successor(3));
    assertEquals(5, c.Successor(4));
    assertEquals(7, c.Successor(5));
    assertNull(c.Successor(7));
    assertNull(c.Successor(100));
    // nessuna modifica dalle interrogazioni
    assertEquals(list(1, 3, 3, 5, 7), toList(c));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("chains")
  void searchFamily(String name, SFactory f) {
    SortedChain<?, Integer> c = of(f, 1, 3, 3, 5, 7);
    assertEquals(N(0), c.Search(1));
    assertEquals(N(1), c.Search(3)); // prima occorrenza
    assertEquals(N(4), c.Search(7));
    assertNull(c.Search(4));
    assertNull(c.Search(0));
    assertNull(c.Search(8));
    assertNull(c.SearchPredecessor(1));
    assertEquals(N(2), c.SearchPredecessor(5)); // ultimo 3
    assertEquals(N(4), c.SearchPredecessor(100));
    assertEquals(N(3), c.SearchSuccessor(3)); // il 5
    assertEquals(N(0), c.SearchSuccessor(-1));
    assertNull(c.SearchSuccessor(7));
    assertTrue(c.Exists(5));
    assertFalse(c.Exists(6));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("chains")
  void removeMinMax(String name, SFactory f) {
    SortedChain<?, Integer> c = of(f, 2, 4, 6, 8);
    assertEquals(2, c.MinNRemove());
    assertEquals(8, c.MaxNRemove());
    assertEquals(list(4, 6), toList(c));
    c.RemoveMin();
    assertEquals(list(6), toList(c));
    c.RemoveMax(); // un solo elemento: min == max
    assertTrue(c.IsEmpty());
    assertNull(c.MinNRemove());
    assertNull(c.MaxNRemove());
    // con duplicati se ne rimuove uno solo
    c = of(f, 1, 1, 9, 9);
    assertEquals(1, c.MinNRemove());
    assertEquals(9, c.MaxNRemove());
    assertEquals(list(1, 9), toList(c));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("chains")
  void removePredecessorSuccessor(String name, SFactory f) {
    SortedChain<?, Integer> c = of(f, 1, 3, 3, 5, 7, 9);
    assertEquals(3, c.PredecessorNRemove(5)); // uno dei due 3
    assertEquals(list(1, 3, 5, 7, 9), toList(c));
    assertEquals(7, c.SuccessorNRemove(5));
    assertEquals(list(1, 3, 5, 9), toList(c));
    assertNull(c.PredecessorNRemove(1)); // niente sotto il minimo
    assertNull(c.SuccessorNRemove(9)); // niente sopra il massimo
    assertEquals(list(1, 3, 5, 9), toList(c));
    assertEquals(9, c.PredecessorNRemove(1000));
    assertEquals(1, c.SuccessorNRemove(-1000));
    assertEquals(list(3, 5), toList(c));
    c.RemovePredecessor(5); // 3
    assertEquals(list(5), toList(c));
    c.RemoveSuccessor(5); // niente
    assertEquals(list(5), toList(c));
    c.RemoveSuccessor(0);
    assertTrue(c.IsEmpty());
  }

  @ParameterizedTest(name = "{0}") @MethodSource("chains")
  void removeByValue(String name, SFactory f) {
    SortedChain<?, Integer> c = of(f, 1, 2, 2, 2, 3);
    assertTrue(c.Remove(2));
    assertEquals(list(1, 2, 2, 3), toList(c));
    assertFalse(c.Remove(9));
    assertFalse(c.Remove(0));
    c.RemoveOccurrences(2);
    assertEquals(list(1, 3), toList(c));
    c.RemoveOccurrences(2); // gia assente
    c.RemoveOccurrences(100);
    assertEquals(list(1, 3), toList(c));
    c.RemoveOccurrences(1); // il minimo
    c.RemoveOccurrences(3); // il massimo
    assertTrue(c.IsEmpty());
    c.Insert(4); // riutilizzabile
    assertEquals(list(4), toList(c));
    c = of(f, 5, 5, 5);
    c.RemoveOccurrences(5); // tutta la catena
    assertTrue(c.IsEmpty());
  }

  @ParameterizedTest(name = "{0}") @MethodSource("chains")
  void removeAtPositions(String name, SFactory f) {
    SortedChain<?, Integer> c = of(f, 10, 20, 30, 40);
    assertEquals(10, c.FirstNRemove());
    assertEquals(40, c.LastNRemove());
    assertEquals(20, c.AtNRemove(N(0)));
    assertEquals(list(30), toList(c));
    assertThrows(IndexOutOfBoundsException.class, () -> c.AtNRemove(N(1)));
    c.RemoveFirst();
    assertTrue(c.IsEmpty());
    assertThrows(IndexOutOfBoundsException.class, () -> c.RemoveLast());
  }

  @ParameterizedTest(name = "{0}") @MethodSource("chains")
  void filterAndClear(String name, SFactory f) {
    SortedChain<?, Integer> c = of(f, 1, 2, 3, 4, 5, 6);
    assertTrue(c.Filter(d -> d % 2 == 0));
    assertEquals(list(2, 4, 6), toList(c));
    assertFalse(c.Filter(d -> true));
    assertFalse(c.Filter(null));
    c.Insert(3);
    assertEquals(list(2, 3, 4, 6), toList(c)); // l ordinamento sopravvive al filtro
    c.Clear();
    assertTrue(c.IsEmpty());
    assertEquals(Natural.ZERO, c.Size());
    c.Insert(2);
    c.Insert(1);
    assertEquals(list(1, 2), toList(c));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("chains")
  void iterators(String name, SFactory f) {
    SortedChain<?, Integer> c = of(f, 3, 1, 2);
    var it = c.FIterator();
    assertEquals(1, it.DataNNext());
    assertEquals(2, it.GetCurrent());
    it.Next();
    assertEquals(3, it.GetCurrent());
    it.Next();
    assertFalse(it.IsValid());
    it.Next();
    assertFalse(it.IsValid());
    it.Reset();
    assertEquals(1, it.GetCurrent());
    assertEquals(list(1, 2, 3), drain(c.FIterator()));
    var bit = c.BIterator();
    assertEquals(3, bit.GetCurrent());
    bit.Prev(2);
    assertEquals(1, bit.GetCurrent());
    bit.Prev();
    assertFalse(bit.IsValid());
    bit.Reset();
    assertTrue(bit.IsValid());
    assertEquals(list(3, 2, 1), drain(c.BIterator()));
    assertThrows(IllegalArgumentException.class, () -> c.FIterator().Next(-1L));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("chains")
  void subSequenceStaysSorted(String name, SFactory f) {
    SortedChain<?, Integer> c = of(f, 5, 1, 4, 2, 3);
    SortedChain<?, Integer> s = (SortedChain<?, Integer>) c.SubSequence(N(1), N(3));
    assertEquals(list(2, 3, 4), toList(s));
    s.Insert(0);
    s.Insert(10);
    s.Insert(3);
    assertEquals(list(0, 2, 3, 3, 4, 10), toList(s));
    assertEquals(list(1, 2, 3, 4, 5), toList(c));
    assertTrue(c.SubSequence(N(4), N(1)).IsEmpty());
    assertEquals(list(4, 5), toList(c.SubSequence(N(3), N(99))));
  }

  /* ---------------------------------------------------------------------- */
  /* Operazioni insiemistiche                                                */
  /* ---------------------------------------------------------------------- */

  @ParameterizedTest(name = "{0}") @MethodSource("chains")
  void unionKeepsOrder(String name, SFactory f) {
    SortedChain<?, Integer> a = of(f, 1, 3, 5);
    a.Union(of(f, 2, 3, 4, 6));
    assertEquals(list(1, 2, 3, 4, 5, 6), toList(a));
    a.Union(f.New());
    a.Union(null);
    a.Union(a);
    assertEquals(list(1, 2, 3, 4, 5, 6), toList(a));
    SortedChain<?, Integer> e = f.New();
    e.Union(of(f, 9, 7, 8));
    assertEquals(list(7, 8, 9), toList(e));
    // unione con una collezione non ordinata
    e.Union(new LLList<Integer>(new VList<Integer>()));
    e.Union(listOf(1, 20, 8));
    assertEquals(list(1, 7, 8, 9, 20), toList(e));
  }

  static Collection<?, Integer> listOf(Integer... xs) {
    LLList<Integer> l = new LLList<>();
    for (Integer x : xs) l.Insert(x);
    return l;
  }

  @ParameterizedTest(name = "{0}") @MethodSource("chains")
  void differenceKeepsOrder(String name, SFactory f) {
    SortedChain<?, Integer> a = of(f, 1, 2, 3, 4, 5, 6);
    a.Difference(of(f, 2, 4, 9, 0));
    assertEquals(list(1, 3, 5, 6), toList(a));
    a.Difference(f.New());
    a.Difference(null);
    assertEquals(list(1, 3, 5, 6), toList(a));
    a.Difference(listOf(6, 1));
    assertEquals(list(3, 5), toList(a));
    // un solo esemplare per ogni elemento di col
    SortedChain<?, Integer> d = of(f, 2, 2, 2);
    d.Difference(of(f, 2));
    assertEquals(list(2, 2), toList(d));
    d.Difference(d);
    assertTrue(d.IsEmpty());
  }

  @ParameterizedTest(name = "{0}") @MethodSource("chains")
  void intersectionBetweenSortedChains(String name, SFactory f) {
    SortedChain<?, Integer> a = of(f, 1, 2, 3, 4, 5, 6);
    a.Intersection(of(f, 0, 2, 4, 6, 8));
    assertEquals(list(2, 4, 6), toList(a));
    a.Intersection(of(f, 7, 8));
    assertTrue(a.IsEmpty());
    SortedChain<?, Integer> b = of(f, 1, 2, 3);
    b.Intersection(f.New());
    assertTrue(b.IsEmpty());
    SortedChain<?, Integer> c = f.New();
    c.Intersection(of(f, 1));
    assertTrue(c.IsEmpty());
    c = of(f, 1, 2);
    c.Intersection((SortedChain<?, Integer>) null); // null: nessun effetto
    assertEquals(list(1, 2), toList(c));
    // con duplicati: molteplicita minima
    SortedChain<?, Integer> d = of(f, 1, 1, 2, 3, 3, 3);
    d.Intersection(of(f, 1, 3, 3, 4));
    assertEquals(list(1, 3, 3), toList(d));
    // identico a se stesso
    SortedChain<?, Integer> e = of(f, 1, 2, 3);
    e.Intersection(e);
    assertEquals(list(1, 2, 3), toList(e));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("chains")
  void intersectionWithGenericCollection(String name, SFactory f) {
    SortedChain<?, Integer> a = of(f, 1, 2, 3, 4, 5);
    a.Intersection(listOf(5, 3, 9, 1));
    assertEquals(list(1, 3, 5), toList(a));
    a.Intersection(listOf());
    assertTrue(a.IsEmpty());
  }

  /* ---------------------------------------------------------------------- */
  /* Altri tipi, casi misti, test randomizzato                               */
  /* ---------------------------------------------------------------------- */

  @ParameterizedTest(name = "{0}") @MethodSource("chains")
  void growAndShrinkLarge(String name, SFactory f) {
    SortedChain<?, Integer> c = f.New();
    Random rnd = new Random(99);
    List<Integer> model = new ArrayList<>();
    for (int i = 0; i < 1500; i++) { int v = rnd.nextInt(500); c.Insert(v); model.add(v); }
    Collections.sort(model);
    assertEquals(model, toList(c));
    while (!model.isEmpty()) {
      assertEquals(model.remove(0), c.MinNRemove());
      if (!model.isEmpty()) assertEquals(model.remove(model.size() - 1), c.MaxNRemove());
    }
    assertTrue(c.IsEmpty());
    assertNull(c.Min());
    c.Insert(1);
    assertEquals(list(1), toList(c));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("chains")
  void randomizedAgainstModel(String name, SFactory f) {
    SortedChain<?, Integer> c = f.New();
    List<Integer> model = new ArrayList<>();
    Random rnd = new Random(31337);
    for (int step = 0; step < 3000; step++) {
      int v = rnd.nextInt(60);
      switch (rnd.nextInt(8)) {
        case 0, 1, 2 -> { c.Insert(v); int p = Collections.binarySearch(model, v); model.add(p >= 0 ? p : -p - 1, v); }
        case 3 -> {
          boolean had = model.contains(v);
          assertEquals(!had, c.InsertIfAbsent(v));
          if (!had) { int p = Collections.binarySearch(model, v); model.add(-p - 1, v); }
        }
        case 4 -> assertEquals(model.remove((Integer) v), c.Remove(v));
        case 5 -> { c.RemoveOccurrences(v); model.removeIf(x -> x == v); }
        case 6 -> {
          Integer pred = null, succ = null;
          for (int x : model) { if (x < v) pred = x; if (x > v && succ == null) succ = x; }
          assertEquals(pred, c.Predecessor(v));
          assertEquals(succ, c.Successor(v));
          assertEquals(model.isEmpty() ? null : model.get(0), c.Min());
          assertEquals(model.isEmpty() ? null : model.get(model.size() - 1), c.Max());
        }
        default -> {
          int idx = model.indexOf(v);
          if (idx < 0) assertNull(c.Search(v)); else assertEquals(N(idx), c.Search(v));
        }
      }
      assertEquals(model.size(), c.Size().ToLong(), "size at step " + step);
    }
    assertEquals(model, toList(c));
    Collections.reverse(model);
    assertEquals(model, toListBackward(c));
  }

  @Test
  void stringChains() {
    VSortedChain<String> v = new VSortedChain<>();
    LLSortedChain<String> l = new LLSortedChain<>();
    for (String s : new String[] {"pear", "apple", "fig", "apple", "Zebra", "banana"}) { v.Insert(s); l.Insert(s); }
    // ordine naturale delle stringhe: le maiuscole precedono le minuscole
    List<String> expected = list("Zebra", "apple", "apple", "banana", "fig", "pear");
    assertEquals(expected, toList(v));
    assertEquals(expected, toList(l));
    assertEquals("Zebra", v.Min());
    assertEquals("pear", l.Max());
    assertEquals("banana", v.Successor("apple"));
    assertEquals("apple", l.Predecessor("banana"));
    assertEquals("fig", v.Predecessor("g"));
    assertNull(l.Successor("pear"));
    assertTrue(v.Remove("apple"));
    assertTrue(l.Remove("apple"));
    assertEquals(N(1), v.Multiplicity("apple"));
    v.RemoveOccurrences("apple");
    l.RemoveOccurrences("apple");
    assertEquals(list("Zebra", "banana", "fig", "pear"), toList(v));
    assertEquals(toList(v), toList(l));
    assertTrue(v.IsEqual(l));
    assertEquals("Zebra", l.MinNRemove());
    assertEquals("pear", v.MaxNRemove());
    LLSortedChain<String> cp = new LLSortedChain<>(l);
    cp.Insert("aaa");
    assertEquals(list("aaa", "banana", "fig", "pear"), toList(cp));
    assertEquals(list("banana", "fig", "pear"), toList(l));
    assertThrows(NullPointerException.class, () -> new LLSortedChain<String>((LLSortedChain<String>) null));
  }

  @Test
  void crossImplementationEquality() {
    VSortedChain<Integer> v = new VSortedChain<>();
    LLSortedChain<Integer> l = new LLSortedChain<>();
    for (int x : new int[] {3, 1, 2, 2}) { v.Insert(x); l.Insert(x); }
    assertTrue(v.IsEqual(l));
    assertTrue(l.IsEqual(v));
    l.Insert(4);
    assertFalse(v.IsEqual(l));
    v.Intersection(l); // fusione tra implementazioni diverse
    assertEquals(list(1, 2, 2, 3), toList(v));
    l.Intersection(v);
    assertEquals(list(1, 2, 2, 3), toList(l));
  }
}

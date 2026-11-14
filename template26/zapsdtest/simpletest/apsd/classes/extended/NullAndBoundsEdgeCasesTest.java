package zapsdtest.simpletest.apsd.classes.extended;

import static org.junit.jupiter.api.Assertions.*;
import static zapsdtest.simpletest.apsd.classes.extended.Helpers.*;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import apsd.classes.containers.collections.abstractcollections.WOrderedSet;
import apsd.classes.containers.collections.abstractcollections.WSet;
import apsd.classes.containers.collections.concretecollections.LLSortedChain;
import apsd.classes.containers.collections.concretecollections.VSortedChain;
import apsd.classes.containers.sequences.DynCircularVector;
import apsd.classes.containers.sequences.DynVector;
import apsd.classes.utilities.Natural;
import apsd.interfaces.containers.collections.ordered.SortedChain;

/** Casi limite: null nelle strutture ordinate, Expand(0), Intersection(this), InsertAt(null). */
@DisplayName("Edge cases - null, Expand(0), self-intersection, InsertAt(null)")
public class NullAndBoundsEdgeCasesTest {

  static Stream<Arguments> chains() {
    return Stream.of(
      Arguments.of("VSortedChain", (java.util.function.Supplier<SortedChain<?, Integer>>) () -> new VSortedChain<Integer>()),
      Arguments.of("LLSortedChain", (java.util.function.Supplier<SortedChain<?, Integer>>) () -> new LLSortedChain<Integer>()));
  }

  static Stream<Arguments> dynVectors() {
    return Stream.of(
      Arguments.of("DynVector", (java.util.function.Supplier<apsd.interfaces.containers.sequences.DynVector<?, Integer>>) () -> new DynVector<Integer>()),
      Arguments.of("DynCircularVector", (java.util.function.Supplier<apsd.interfaces.containers.sequences.DynVector<?, Integer>>) () -> new DynCircularVector<Integer>()));
  }

  /* ---- null nelle catene ordinate --------------------------------------- */

  private static void assertNullQueriesAreHarmless(SortedChain<?, Integer> c, List<Integer> expected) {
    assertFalse(c.Exists(null));
    assertFalse(c.Remove(null));
    assertNull(c.Search(null));
    assertNull(c.SearchPredecessor(null));
    assertNull(c.SearchSuccessor(null));
    assertNull(c.Predecessor(null));
    assertNull(c.Successor(null));
    assertNull(c.PredecessorNRemove(null));
    assertNull(c.SuccessorNRemove(null));
    c.RemovePredecessor(null);
    c.RemoveSuccessor(null);
    c.RemoveOccurrences(null);
    assertEquals(expected, toList(c)); // nessuna modifica
  }

  @ParameterizedTest(name = "{0}") @MethodSource("chains")
  void nullQueriesOnEmptyChain(String name, java.util.function.Supplier<SortedChain<?, Integer>> f) {
    assertNullQueriesAreHarmless(f.get(), List.of());
  }

  @ParameterizedTest(name = "{0}") @MethodSource("chains")
  void nullQueriesOnNonEmptyChain(String name, java.util.function.Supplier<SortedChain<?, Integer>> f) {
    SortedChain<?, Integer> c = f.get();
    for (int x : new int[] {5, 1, 3, 3}) c.Insert(x);
    assertNullQueriesAreHarmless(c, List.of(1, 3, 3, 5));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("chains")
  void nullInsertThrows(String name, java.util.function.Supplier<SortedChain<?, Integer>> f) {
    SortedChain<?, Integer> empty = f.get(); // anche su catena vuota, dove nessun compareTo verrebbe eseguito
    assertThrows(NullPointerException.class, () -> empty.Insert(null));
    assertThrows(NullPointerException.class, () -> empty.InsertIfAbsent(null));
    assertTrue(empty.IsEmpty());
    SortedChain<?, Integer> full = f.get();
    full.Insert(2);
    assertThrows(NullPointerException.class, () -> full.Insert(null));
    assertThrows(NullPointerException.class, () -> full.InsertIfAbsent(null));
    assertEquals(List.of(2), toList(full));
  }

  @Test
  void nullQueriesOnOrderedSet() {
    WOrderedSet<Integer> s = new WOrderedSet<>();
    s.Insert(1);
    s.Insert(4);
    assertFalse(s.Exists(null));
    assertFalse(s.Remove(null));
    assertNull(s.Predecessor(null));
    assertNull(s.Successor(null));
    assertThrows(NullPointerException.class, () -> s.Insert(null));
    assertEquals(List.of(1, 4), toList(s));
  }

  /* ---- Empty() dei set --------------------------------------------------- */

  @Test
  void setEmptyReturnsNewEmptySet() {
    WSet<Integer> s = new WSet<>();
    s.Insert(1);
    WSet<Integer> e = s.Empty();
    assertNotSame(s, e);
    assertTrue(e.IsEmpty());
    assertEquals(Natural.Of(1), s.Size());
    WOrderedSet<Integer> o = new WOrderedSet<>();
    o.Insert(1);
    WOrderedSet<Integer> oe = o.Empty();
    assertNotSame(o, oe);
    assertTrue(oe.IsEmpty());
    assertEquals(Natural.Of(1), o.Size());
  }

  /* ---- Expand(0) / Reduce su vettori vuoti ------------------------------ */

  @ParameterizedTest(name = "{0}") @MethodSource("dynVectors")
  void expandZeroOnEmptyVectorDoesNotAllocate(String name, java.util.function.Supplier<apsd.interfaces.containers.sequences.DynVector<?, Integer>> f) {
    var v = f.get();
    v.Expand(Natural.ZERO);
    assertEquals(Natural.ZERO, v.Size());
    assertEquals(Natural.ZERO, v.Capacity());
    v.Expand(); // da capacita' 0 deve comunque crescere
    assertEquals(Natural.Of(1), v.Size());
    assertTrue(v.Capacity().ToLong() >= 1L);
  }

  @ParameterizedTest(name = "{0}") @MethodSource("dynVectors")
  void expandWithinCapacityDoesNotRealloc(String name, java.util.function.Supplier<apsd.interfaces.containers.sequences.DynVector<?, Integer>> f) {
    var v = f.get();
    v.Realloc(Natural.Of(5));
    v.Expand(Natural.Of(5)); // size 5 == capacita' 5: nessuna crescita necessaria
    assertEquals(Natural.Of(5), v.Size());
    assertEquals(Natural.Of(5), v.Capacity());
    v.Expand(Natural.ZERO);
    assertEquals(Natural.Of(5), v.Capacity());
    v.Expand(); // oltre la capacita': deve crescere
    assertEquals(Natural.Of(6), v.Size());
    assertTrue(v.Capacity().ToLong() >= 6L);
  }

  @ParameterizedTest(name = "{0}") @MethodSource("dynVectors")
  void reduceToEmptyKeepsVectorUsable(String name, java.util.function.Supplier<apsd.interfaces.containers.sequences.DynVector<?, Integer>> f) {
    var v = f.get();
    v.Reduce(Natural.ZERO); // su vettore vuoto a capacita' 0
    assertEquals(Natural.ZERO, v.Capacity());
    assertThrows(IllegalArgumentException.class, () -> v.Reduce());
    v.InsertLast(7);
    v.Reduce();
    assertTrue(v.IsEmpty());
    v.InsertLast(8);
    assertEquals(8, v.GetFirst());
  }

  /* ---- InsertAt(null) ---------------------------------------------------- */

  @ParameterizedTest(name = "{0}") @MethodSource("dynVectors")
  void insertAtNullPositionThrowsIndexOutOfBounds(String name, java.util.function.Supplier<apsd.interfaces.containers.sequences.DynVector<?, Integer>> f) {
    var v = f.get();
    v.InsertLast(1);
    assertThrows(IndexOutOfBoundsException.class, () -> v.InsertAt(9, null));
    assertEquals(List.of(1), toList(v)); // nessuna modifica
  }

  /* ---- Intersection(this) ------------------------------------------------ */

  @ParameterizedTest(name = "{0}") @MethodSource("chains")
  void sortedChainSelfIntersectionIsNoOp(String name, java.util.function.Supplier<SortedChain<?, Integer>> f) {
    SortedChain<?, Integer> c = f.get();
    for (int x : new int[] {4, 2, 2, 9}) c.Insert(x);
    c.Intersection((SortedChain<?, Integer>) c);
    assertEquals(List.of(2, 2, 4, 9), toList(c)); // duplicati inclusi
    SortedChain<?, Integer> e = f.get();
    e.Intersection((SortedChain<?, Integer>) e);
    assertTrue(e.IsEmpty());
  }
}

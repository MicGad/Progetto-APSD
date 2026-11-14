package zapsdtest.simpletest.apsd.classes.extended;

import static org.junit.jupiter.api.Assertions.*;
import static zapsdtest.simpletest.apsd.classes.extended.Helpers.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import apsd.classes.containers.collections.abstractcollections.WOrderedSet;
import apsd.classes.containers.collections.abstractcollections.WSet;
import apsd.classes.containers.collections.concretecollections.LLList;
import apsd.classes.containers.collections.concretecollections.LLSortedChain;
import apsd.classes.containers.collections.concretecollections.VList;
import apsd.classes.containers.collections.concretecollections.VSortedChain;
import apsd.classes.containers.deqs.WQueue;
import apsd.classes.containers.deqs.WStack;
import apsd.classes.containers.sequences.CircularVector;
import apsd.classes.containers.sequences.DynCircularVector;
import apsd.classes.containers.sequences.DynVector;
import apsd.classes.containers.sequences.Vector;
import apsd.classes.utilities.Box;
import apsd.classes.utilities.Natural;
import apsd.interfaces.containers.base.TraversableContainer;
import apsd.interfaces.containers.collections.Collection;
import apsd.interfaces.containers.collections.ordered.SortedChain;
import apsd.interfaces.containers.iterators.BackwardIterator;
import apsd.interfaces.containers.iterators.ForwardIterator;
import apsd.interfaces.containers.sequences.MutableSequence;
import apsd.interfaces.containers.sequences.RemovableAtSequence;
import apsd.interfaces.containers.sequences.Sequence;

/**
 * Simulazione di un fulltest "da professore": eccezioni esatte, stress da 1000 operazioni
 * e coerenza dei confronti (IsEqual / equals / hashCode) tra strutture diverse.
 */
@DisplayName("Professor full test simulation")
public class ProfessorFullSimulationTest {

  private static final int N = 1000;

  /* ====================================================================== */
  /* Fabbriche                                                              */
  /* ====================================================================== */

  interface SeqF { Sequence<?, Integer> New(); Sequence<?, Integer> From(TraversableContainer<?, Integer> c); }

  /** Tutte le sequenze (statiche e dinamiche, vettori e liste, catene ordinate). */
  static Stream<Arguments> sequences() {
    return Stream.of(
      Arguments.of("Vector", new SeqF() {
        public Sequence<?, Integer> New() { return new Vector<Integer>(); }
        public Sequence<?, Integer> From(TraversableContainer<?, Integer> c) { return new Vector<Integer>(c); } }),
      Arguments.of("CircularVector", new SeqF() {
        public Sequence<?, Integer> New() { return new CircularVector<Integer>(); }
        public Sequence<?, Integer> From(TraversableContainer<?, Integer> c) { return new CircularVector<Integer>(c); } }),
      Arguments.of("DynVector", new SeqF() {
        public Sequence<?, Integer> New() { return new DynVector<Integer>(); }
        public Sequence<?, Integer> From(TraversableContainer<?, Integer> c) { return new DynVector<Integer>(c); } }),
      Arguments.of("DynCircularVector", new SeqF() {
        public Sequence<?, Integer> New() { return new DynCircularVector<Integer>(); }
        public Sequence<?, Integer> From(TraversableContainer<?, Integer> c) { return new DynCircularVector<Integer>(c); } }),
      Arguments.of("VList", new SeqF() {
        public Sequence<?, Integer> New() { return new VList<Integer>(); }
        public Sequence<?, Integer> From(TraversableContainer<?, Integer> c) { return new VList<Integer>(c); } }),
      Arguments.of("LLList", new SeqF() {
        public Sequence<?, Integer> New() { return new LLList<Integer>(); }
        public Sequence<?, Integer> From(TraversableContainer<?, Integer> c) { return new LLList<Integer>(c); } }),
      Arguments.of("VSortedChain", new SeqF() {
        public Sequence<?, Integer> New() { return new VSortedChain<Integer>(); }
        public Sequence<?, Integer> From(TraversableContainer<?, Integer> c) { return new VSortedChain<Integer>(c); } }),
      Arguments.of("LLSortedChain", new SeqF() {
        public Sequence<?, Integer> New() { return new LLSortedChain<Integer>(); }
        public Sequence<?, Integer> From(TraversableContainer<?, Integer> c) { return new LLSortedChain<Integer>(c); } }));
  }

  /** Sequenze con rimozione per posizione e dimensione variabile. */
  static Stream<Arguments> removables() {
    return sequences().filter(a -> !((String) a.get()[0]).equals("Vector") && !((String) a.get()[0]).equals("CircularVector"));
  }

  /** Sequenze mutabili e con dimensione variabile (vettori dinamici e liste). */
  static Stream<Arguments> dynMutables() {
    return sequences().filter(a -> {
      String n = (String) a.get()[0];
      return n.startsWith("Dyn") || n.endsWith("List");
    });
  }

  static Stream<Arguments> lists() {
    return sequences().filter(a -> ((String) a.get()[0]).endsWith("List"));
  }

  /** Sequenze mutabili (anche i vettori statici). */
  static Stream<Arguments> mutables() {
    return sequences().filter(a -> !((String) a.get()[0]).endsWith("SortedChain"));
  }

  /** Collezioni su cui fare stress (inserimenti e rimozioni per valore). */
  static Stream<Arguments> collections() {
    return Stream.of(
      Arguments.of("VList", (Supplier<Collection<?, Integer>>) () -> new VList<Integer>()),
      Arguments.of("LLList", (Supplier<Collection<?, Integer>>) () -> new LLList<Integer>()),
      Arguments.of("VSortedChain", (Supplier<Collection<?, Integer>>) () -> new VSortedChain<Integer>()),
      Arguments.of("LLSortedChain", (Supplier<Collection<?, Integer>>) () -> new LLSortedChain<Integer>()),
      Arguments.of("WSet", (Supplier<Collection<?, Integer>>) () -> new WSet<Integer>()),
      Arguments.of("WOrderedSet", (Supplier<Collection<?, Integer>>) () -> new WOrderedSet<Integer>()));
  }

  static Stream<Arguments> dynVectors() {
    return Stream.of(
      Arguments.of("DynVector", (Supplier<apsd.interfaces.containers.sequences.DynVector<?, Integer>>) () -> new DynVector<Integer>()),
      Arguments.of("DynCircularVector", (Supplier<apsd.interfaces.containers.sequences.DynVector<?, Integer>>) () -> new DynCircularVector<Integer>()));
  }

  static Stream<Arguments> sets() {
    return Stream.of(
      Arguments.of("WSet", (Supplier<apsd.interfaces.containers.collections.Set<?, Integer>>) () -> new WSet<Integer>()),
      Arguments.of("WOrderedSet", (Supplier<apsd.interfaces.containers.collections.Set<?, Integer>>) () -> new WOrderedSet<Integer>()));
  }

  /* ====================================================================== */
  /* Utilita                                                                */
  /* ====================================================================== */

  private static void assertIterEnd(Executable e) {
    Throwable t = assertThrows(RuntimeException.class, e);
    assertTrue(t instanceof IndexOutOfBoundsException || t instanceof java.util.NoSuchElementException,
      "Atteso IndexOutOfBounds/NoSuchElement a fine iterazione, ottenuto " + t);
  }

  private static Natural nat(long l) { return Natural.Of(l); }

  private static Sequence<?, Integer> filled(SeqF f, int... xs) {
    Sequence<?, Integer> s = f.New();
    if (s instanceof apsd.interfaces.containers.base.InsertableContainer<?, ?> ic) {
      @SuppressWarnings("unchecked") var c = (apsd.interfaces.containers.base.InsertableContainer<?, Integer>) ic;
      for (int x : xs) c.Insert(x);
    }
    return s;
  }

  /* ====================================================================== */
  /* 1. Eccezioni esatte: indici fuori limite, vuoti                        */
  /* ====================================================================== */

  @ParameterizedTest(name = "{0}") @MethodSource("sequences")
  void getOnEmptySequenceThrowsIndexOutOfBounds(String name, SeqF f) {
    Sequence<?, Integer> s = f.New();
    assertTrue(s.IsEmpty());
    assertThrows(IndexOutOfBoundsException.class, () -> s.GetAt(nat(0)));
    assertThrows(IndexOutOfBoundsException.class, () -> s.GetFirst());
    assertThrows(IndexOutOfBoundsException.class, () -> s.GetLast());
    assertThrows(IndexOutOfBoundsException.class, () -> s.GetAt(null));
    assertNull(s.Search(1));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("sequences")
  void getAtOutOfBoundsOnNonEmpty(String name, SeqF f) {
    Sequence<?, Integer> s = filled(f, 1, 2, 3);
    if (s.Size().ToLong() != 3) return; // vettori statici: vuoti di default, gia coperti
    assertThrows(IndexOutOfBoundsException.class, () -> s.GetAt(nat(3)));
    assertThrows(IndexOutOfBoundsException.class, () -> s.GetAt(nat(Integer.MAX_VALUE)));
    assertThrows(IndexOutOfBoundsException.class, () -> s.GetAt(nat(Long.MAX_VALUE)));
    assertThrows(IndexOutOfBoundsException.class, () -> s.GetAt(null));
    assertEquals(3, s.GetAt(nat(2)));
    assertEquals(3, s.GetLast());
    assertEquals(1, s.GetFirst());
  }

  @ParameterizedTest(name = "{0}") @MethodSource("mutables")
  void setOnEmptyOrOutOfBoundsThrows(String name, SeqF f) {
    MutableSequence<?, Integer> s = (MutableSequence<?, Integer>) f.New();
    assertThrows(IndexOutOfBoundsException.class, () -> s.SetAt(1, nat(0)));
    assertThrows(IndexOutOfBoundsException.class, () -> s.SetFirst(1));
    assertThrows(IndexOutOfBoundsException.class, () -> s.SetLast(1));
    assertThrows(IndexOutOfBoundsException.class, () -> s.GetNSetFirst(1));
    assertThrows(IndexOutOfBoundsException.class, () -> s.GetNSetLast(1));
    assertThrows(IndexOutOfBoundsException.class, () -> s.GetNSetAt(1, nat(0)));
    assertThrows(IndexOutOfBoundsException.class, () -> s.SetAt(1, null));
    assertThrows(IndexOutOfBoundsException.class, () -> s.Swap(nat(0), nat(0)));
    assertTrue(s.IsEmpty(), "un set fallito non deve cambiare la size");
  }

  @ParameterizedTest(name = "{0}") @MethodSource("dynMutables")
  void setAndSwapOutOfBoundsOnNonEmptyLeaveContentUntouched(String name, SeqF f) {
    MutableSequence<?, Integer> s = (MutableSequence<?, Integer>) filled(f, 10, 20, 30);
    assertThrows(IndexOutOfBoundsException.class, () -> s.SetAt(99, nat(3)));
    assertThrows(IndexOutOfBoundsException.class, () -> s.SetAt(99, null));
    assertThrows(IndexOutOfBoundsException.class, () -> s.Swap(nat(0), nat(3)));
    assertThrows(IndexOutOfBoundsException.class, () -> s.Swap(nat(3), nat(0)));
    assertEquals(List.of(10, 20, 30), toList(s));
    s.Swap(nat(0), nat(2));
    assertEquals(List.of(30, 20, 10), toList(s));
    s.Swap(nat(1), nat(1));
    assertEquals(List.of(30, 20, 10), toList(s));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("removables")
  void removeAtOnEmptyOrOutOfBoundsThrows(String name, SeqF f) {
    RemovableAtSequence<?, Integer> s = (RemovableAtSequence<?, Integer>) f.New();
    assertThrows(IndexOutOfBoundsException.class, () -> s.AtNRemove(nat(0)));
    assertThrows(IndexOutOfBoundsException.class, () -> s.RemoveAt(nat(0)));
    assertThrows(IndexOutOfBoundsException.class, () -> s.RemoveFirst());
    assertThrows(IndexOutOfBoundsException.class, () -> s.RemoveLast());
    assertThrows(IndexOutOfBoundsException.class, () -> s.FirstNRemove());
    assertThrows(IndexOutOfBoundsException.class, () -> s.LastNRemove());
    assertThrows(IndexOutOfBoundsException.class, () -> s.AtNRemove(null));
    assertFalse(s.Remove(1));
    assertTrue(s.IsEmpty());
    @SuppressWarnings("unchecked") var ins = (apsd.interfaces.containers.base.InsertableContainer<?, Integer>) s;
    ins.Insert(5);
    ins.Insert(7);
    assertThrows(IndexOutOfBoundsException.class, () -> s.AtNRemove(nat(2)));
    assertThrows(IndexOutOfBoundsException.class, () -> s.AtNRemove(nat(Long.MAX_VALUE)));
    assertEquals(List.of(5, 7), toList(s));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("dynMutables")
  void insertAtOutOfBoundsThrows(String name, SeqF f) {
    var s = (apsd.interfaces.containers.sequences.InsertableAtSequence<?, Integer>) f.New();
    assertThrows(IndexOutOfBoundsException.class, () -> s.InsertAt(1, nat(1)));
    assertThrows(IndexOutOfBoundsException.class, () -> s.InsertAt(1, nat(Long.MAX_VALUE)));
    assertTrue(s.IsEmpty());
    s.InsertAt(1, nat(0)); // posizione == size e valida
    s.InsertAt(3, nat(1));
    s.InsertAt(2, nat(1));
    assertThrows(IndexOutOfBoundsException.class, () -> s.InsertAt(9, nat(4)));
    assertEquals(List.of(1, 2, 3), toList(s));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("dynVectors")
  void dynVectorReduceTooMuchThrowsIllegalArgument(String name, Supplier<apsd.interfaces.containers.sequences.DynVector<?, Integer>> f) {
    var v = f.get();
    assertThrows(IllegalArgumentException.class, () -> v.Reduce(nat(1)));
    v.Expand(nat(3));
    assertThrows(IllegalArgumentException.class, () -> v.Reduce(nat(4)));
    assertEquals(nat(3), v.Size());
    v.Reduce(nat(3));
    assertTrue(v.IsEmpty());
    v.Expand(nat(0));
    v.Reduce(nat(0));
    assertTrue(v.IsEmpty());
  }

  @ParameterizedTest(name = "{0}") @MethodSource("sequences")
  void subSequenceWithNullBoundsThrowsNullPointer(String name, SeqF f) {
    Sequence<?, Integer> s = f.New();
    assertThrows(NullPointerException.class, () -> s.SubSequence(null, nat(0)));
    assertThrows(NullPointerException.class, () -> s.SubSequence(nat(0), null));
  }

  @Test
  void naturalContractExceptions() {
    assertThrows(IllegalArgumentException.class, () -> Natural.Of(-1L));
    assertThrows(IllegalArgumentException.class, () -> new Natural(-5L));
    assertThrows(ArithmeticException.class, () -> Natural.ZERO.Decrement());
    assertThrows(ArithmeticException.class, () -> Natural.Of(Long.MAX_VALUE).Increment());
    assertThrows(NullPointerException.class, () -> new Natural((Natural) null));
    assertThrows(NullPointerException.class, () -> new Natural((apsd.classes.utilities.MutableNatural) null));
    assertThrows(NullPointerException.class, () -> new Box<Integer>((Box<Integer>) null));
    assertEquals(Natural.ZERO, Natural.ONE.Decrement());
    assertTrue(Natural.ZERO.compareTo(Natural.ONE) < 0);
  }

  /* ====================================================================== */
  /* 2. Parametri null                                                      */
  /* ====================================================================== */

  @ParameterizedTest(name = "{0}") @MethodSource("sequences")
  void nullContainerInConstructorThrowsNullPointer(String name, SeqF f) {
    assertThrows(NullPointerException.class, () -> f.From(null));
  }

  @Test
  void nullArgumentsInSetsDequesAndLists() {
    assertThrows(NullPointerException.class, () -> new WSet<Integer>((TraversableContainer<?, Integer>) null));
    assertThrows(NullPointerException.class, () -> new WOrderedSet<Integer>((TraversableContainer<?, Integer>) null));
    assertThrows(NullPointerException.class, () -> new WQueue<Integer>((TraversableContainer<?, Integer>) null));
    assertThrows(NullPointerException.class, () -> new WStack<Integer>((TraversableContainer<?, Integer>) null));
    assertThrows(NullPointerException.class, () -> new WQueue<Integer>((apsd.interfaces.containers.collections.List<?, Integer>) null));
    assertThrows(NullPointerException.class, () -> new WStack<Integer>((apsd.interfaces.containers.collections.List<?, Integer>) null));
    assertThrows(NullPointerException.class, () -> new VList<Integer>((TraversableContainer<?, Integer>) null));
    assertThrows(NullPointerException.class, () -> new LLList<Integer>((TraversableContainer<?, Integer>) null));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("collections")
  void nullPredicateAndNullCollectionOperationsAreNoOps(String name, Supplier<Collection<?, Integer>> f) {
    Collection<?, Integer> c = f.get();
    for (int x : new int[] {3, 1, 2}) c.Insert(x);
    List<Integer> before = toList(c);
    assertFalse(c.Filter(null));
    c.Union(null);
    c.Difference(null);
    c.Intersection(null);
    assertEquals(before, toList(c));
    assertNull(c.FoldForward(null, (Integer) null));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("collections")
  void isEqualWithNullIsFalse(String name, Supplier<Collection<?, Integer>> f) {
    Collection<?, Integer> c = f.get();
    assertFalse(c.IsEqual((TraversableContainer<?, Integer>) null));
    c.Insert(1);
    assertFalse(c.IsEqual((TraversableContainer<?, Integer>) null));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("sequences")
  void sequenceIsEqualWithNullIsFalse(String name, SeqF f) {
    assertFalse(f.New().IsEqual((Sequence<?, Integer>) null));
  }

  /* ====================================================================== */
  /* 3. Code, pile                                                          */
  /* ====================================================================== */

  @Test
  void emptyQueueAndStackAreTolerant() {
    WQueue<Integer> q = new WQueue<>();
    assertNull(q.Head());
    q.Dequeue();
    assertNull(q.HeadNDequeue());
    assertTrue(q.IsEmpty());
    WStack<Integer> s = new WStack<>();
    assertNull(s.Top());
    s.Pop();
    assertNull(s.TopNPop());
    s.SwapTop(4); // su pila vuota: nessun crash, l'effetto e definito dall'implementazione
    s.Clear();
    assertTrue(s.IsEmpty());
  }

  @Test
  void queueAndStackOneThousandOperations() {
    WQueue<Integer> q = new WQueue<>();
    WStack<Integer> s = new WStack<>();
    for (int i = 0; i < N; i++) { q.Enqueue(i); s.Push(i); }
    assertEquals(nat(N), q.Size());
    assertEquals(nat(N), s.Size());
    for (int i = 0; i < N; i++) {
      assertEquals(i, q.Head());
      assertEquals(i, q.HeadNDequeue());
      assertEquals(N - 1 - i, s.Top());
      assertEquals(N - 1 - i, s.TopNPop());
    }
    assertTrue(q.IsEmpty());
    assertTrue(s.IsEmpty());
    assertNull(q.Head());
    assertNull(s.Top());
    // dopo lo svuotamento totale le strutture sono ancora utilizzabili
    q.Enqueue(7); s.Push(8);
    assertEquals(7, q.Head());
    assertEquals(8, s.Top());
  }

  @Test
  void queueNullElementsAreStoredAndPreserved() {
    WQueue<Integer> q = new WQueue<>();
    q.Enqueue(null); q.Enqueue(1); q.Enqueue(null);
    assertEquals(nat(3), q.Size());
    assertNull(q.HeadNDequeue());
    assertEquals(1, q.HeadNDequeue());
    assertNull(q.HeadNDequeue());
    assertTrue(q.IsEmpty());
  }

  /* ====================================================================== */
  /* 4. Iteratori                                                           */
  /* ====================================================================== */

  @ParameterizedTest(name = "{0}") @MethodSource("sequences")
  void iteratorsAtTheEnd(String name, SeqF f) {
    Sequence<?, Integer> s = filled(f, 1, 2, 3);
    ForwardIterator<Integer> it = s.FIterator();
    List<Integer> got = new ArrayList<>();
    while (it.IsValid()) got.add(it.DataNNext());
    assertEquals(s.Size().ToLong() == 3 ? List.of(1, 2, 3) : List.of(), got);
    assertFalse(it.IsValid());
    assertIterEnd(() -> it.GetCurrent());
    assertIterEnd(() -> it.DataNNext());
    it.Next(); // gia terminato: nessun effetto, nessuna eccezione
    it.Next(5);
    assertFalse(it.IsValid());
    assertThrows(IllegalArgumentException.class, () -> it.Next(-1L));
    assertThrows(NullPointerException.class, () -> it.Next((Natural) null));
    it.Reset();
    assertEquals(got.size() > 0, it.IsValid());

    BackwardIterator<Integer> bit = s.BIterator();
    List<Integer> back = new ArrayList<>();
    while (bit.IsValid()) back.add(bit.DataNPrev());
    Collections.reverse(back);
    assertEquals(got, back);
    assertFalse(bit.IsValid());
    assertIterEnd(() -> bit.GetCurrent());
    assertIterEnd(() -> bit.DataNPrev());
    bit.Prev();
    bit.Prev(3);
    assertFalse(bit.IsValid());
    assertThrows(IllegalArgumentException.class, () -> bit.Prev(-1L));
    bit.Reset();
    assertEquals(got.size() > 0, bit.IsValid());
  }

  @ParameterizedTest(name = "{0}") @MethodSource("sequences")
  void iteratorsOnEmptySequenceAreImmediatelyInvalid(String name, SeqF f) {
    Sequence<?, Integer> s = f.New();
    ForwardIterator<Integer> it = s.FIterator();
    BackwardIterator<Integer> bit = s.BIterator();
    assertFalse(it.IsValid());
    assertFalse(bit.IsValid());
    assertIterEnd(() -> it.GetCurrent());
    assertIterEnd(() -> it.DataNNext());
    assertIterEnd(() -> bit.GetCurrent());
    assertIterEnd(() -> bit.DataNPrev());
    assertFalse(it.ForEachForward(x -> true));
    assertFalse(bit.ForEachBackward(x -> true));
    assertFalse(s.FIterator().ForEachForward(null));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("dynMutables")
  void nextWithBigStepsStopsAtTheEnd(String name, SeqF f) {
    Sequence<?, Integer> s = filled(f, 1, 2, 3, 4);
    ForwardIterator<Integer> it = s.FIterator();
    it.Next(2L);
    assertEquals(3, it.GetCurrent());
    it.Next(Long.MAX_VALUE);
    assertFalse(it.IsValid());
    BackwardIterator<Integer> b = s.BIterator();
    b.Prev(Long.MAX_VALUE);
    assertFalse(b.IsValid());
  }

  @ParameterizedTest(name = "{0}") @MethodSource("dynMutables")
  void mutableIteratorSetCurrentAtEndThrows(String name, SeqF f) {
    MutableSequence<?, Integer> s = (MutableSequence<?, Integer>) filled(f, 1, 2);
    var it = s.FIterator();
    it.SetCurrent(10);
    it.Next(2L);
    assertFalse(it.IsValid());
    assertThrows(RuntimeException.class, () -> it.SetCurrent(99));
    assertEquals(List.of(10, 2), toList(s));
  }

  /* ====================================================================== */
  /* 5. Stress: 1000 inserimenti e 1000 rimozioni                           */
  /* ====================================================================== */

  @ParameterizedTest(name = "{0}") @MethodSource("dynVectors")
  void dynVectorsInsertLastThenRemoveLast(String name, Supplier<apsd.interfaces.containers.sequences.DynVector<?, Integer>> f) {
    var v = f.get();
    for (int i = 0; i < N; i++) {
      v.InsertLast(i);
      assertEquals(nat(i + 1), v.Size());
      assertEquals(i, v.GetLast());
    }
    assertEquals(range(0, N), toList(v));
    for (int i = N - 1; i >= 0; i--) {
      assertEquals(i, v.LastNRemove());
      assertEquals(nat(i), v.Size());
    }
    assertTrue(v.IsEmpty());
    assertThrows(IndexOutOfBoundsException.class, () -> v.LastNRemove());
    v.InsertLast(1); // riutilizzabile dopo lo svuotamento
    assertEquals(List.of(1), toList(v));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("dynVectors")
  void dynVectorsInsertFirstThenRemoveFirst(String name, Supplier<apsd.interfaces.containers.sequences.DynVector<?, Integer>> f) {
    var v = f.get();
    for (int i = 0; i < N; i++) v.InsertFirst(i);
    List<Integer> expected = range(0, N);
    Collections.reverse(expected);
    assertEquals(expected, toList(v));
    assertEquals(range(0, N), toListBackward(v));
    for (int i = N - 1; i >= 0; i--) assertEquals(i, v.FirstNRemove());
    assertTrue(v.IsEmpty());
  }

  @ParameterizedTest(name = "{0}") @MethodSource("dynVectors")
  void dynVectorsRandomPositionsAgainstJavaList(String name, Supplier<apsd.interfaces.containers.sequences.DynVector<?, Integer>> f) {
    var v = f.get();
    java.util.List<Integer> model = new ArrayList<>();
    Random r = new Random(2024);
    for (int i = 0; i < N; i++) {
      int pos = r.nextInt(model.size() + 1);
      v.InsertAt(i, nat(pos));
      model.add(pos, i);
    }
    assertEquals(model, toList(v));
    for (int i = 0; i < N; i++) {
      int pos = r.nextInt(model.size());
      assertEquals(model.remove(pos), v.AtNRemove(nat(pos)));
      if (i % 97 == 0) assertEquals(model, toList(v));
    }
    assertTrue(v.IsEmpty());
    assertTrue(model.isEmpty());
  }

  /** Il caso classico che rompe i buffer circolari: start che avanza e si avvolge, con size piccola. */
  @ParameterizedTest(name = "{0}") @MethodSource("dynVectors")
  void dynVectorsQueueLikeWraparound(String name, Supplier<apsd.interfaces.containers.sequences.DynVector<?, Integer>> f) {
    var v = f.get();
    java.util.LinkedList<Integer> model = new java.util.LinkedList<>();
    int next = 0;
    for (int round = 0; round < N; round++) {
      v.InsertLast(next); model.addLast(next++);
      v.InsertLast(next); model.addLast(next++);
      assertEquals(model.removeFirst(), v.FirstNRemove());
      if (round % 3 == 0) { v.InsertFirst(-round); model.addFirst(-round); }
      if (round % 5 == 0) assertEquals(model.removeLast(), v.LastNRemove());
    }
    assertEquals(model, toList(v));
    List<Integer> rev = new ArrayList<>(model);
    Collections.reverse(rev);
    assertEquals(rev, toListBackward(v));
    while (!model.isEmpty()) assertEquals(model.removeFirst(), v.FirstNRemove());
    assertTrue(v.IsEmpty());
  }

  @ParameterizedTest(name = "{0}") @MethodSource("dynMutables")
  void sequencesStressFrontBackAndMiddle(String name, SeqF f) {
    var s = (apsd.interfaces.containers.sequences.InsertableAtSequence<?, Integer>) f.New();
    var rm = (RemovableAtSequence<?, Integer>) s;
    java.util.List<Integer> model = new ArrayList<>();
    for (int i = 0; i < N; i++) {
      switch (i % 3) {
        case 0 -> { s.InsertFirst(i); model.add(0, i); }
        case 1 -> { s.InsertLast(i); model.add(i); }
        default -> { int p = model.size() / 2; s.InsertAt(i, nat(p)); model.add(p, i); }
      }
    }
    assertEquals(nat(N), s.Size());
    assertEquals(model, toList(s));
    List<Integer> rev = new ArrayList<>(model);
    Collections.reverse(rev);
    assertEquals(rev, toListBackward(s));
    for (int i = 0; i < N; i++) {
      switch (i % 3) {
        case 0 -> assertEquals(model.remove(0), rm.FirstNRemove());
        case 1 -> assertEquals(model.remove(model.size() - 1), rm.LastNRemove());
        default -> { int p = model.size() / 2; assertEquals(model.remove(p), rm.AtNRemove(nat(p))); }
      }
    }
    assertTrue(s.IsEmpty());
    assertEquals(nat(0), s.Size());
  }

  @ParameterizedTest(name = "{0}") @MethodSource("lists")
  void removeByValueDuplicatesStress(String name, SeqF f) {
    var s = (Collection<?, Integer>) f.New();
    for (int i = 0; i < N; i++) s.Insert(i % 10);
    assertEquals(nat(100), s.Multiplicity(3));
    for (int i = 0; i < 100; i++) assertTrue(s.Remove(3));
    assertFalse(s.Remove(3));
    assertEquals(nat(N - 100), ((Sequence<?, Integer>) s).Size());
    s.RemoveOccurrences(4);
    assertFalse(s.Exists(4));
    assertEquals(nat(N - 200), ((Sequence<?, Integer>) s).Size());
    s.Clear();
    assertTrue(s.IsEmpty());
  }

  @ParameterizedTest(name = "{0}") @MethodSource("collections")
  void collectionsInsertThousandThenRemoveThousand(String name, Supplier<Collection<?, Integer>> f) {
    Collection<?, Integer> c = f.get();
    List<Integer> vals = range(0, N);
    Collections.shuffle(vals, new Random(7));
    for (int i = 0; i < N; i++) {
      assertTrue(c.Insert(vals.get(i)));
      assertEquals(nat(i + 1), c.Size());
    }
    for (int i = 0; i < N; i++) assertTrue(c.Exists(i), "manca " + i);
    assertFalse(c.Exists(N));
    assertFalse(c.Exists(-1));
    assertFalse(c.Exists(null));
    List<Integer> sorted = new ArrayList<>(toList(c));
    Collections.sort(sorted);
    assertEquals(range(0, N), sorted);
    Collections.shuffle(vals, new Random(99));
    for (int i = 0; i < N; i++) {
      assertTrue(c.Remove(vals.get(i)));
      assertEquals(nat(N - 1 - i), c.Size());
      assertFalse(c.Exists(vals.get(i)));
    }
    assertTrue(c.IsEmpty());
    assertFalse(c.Remove(0));
    assertTrue(toList(c).isEmpty());
  }

  @ParameterizedTest(name = "{0}") @MethodSource("sets")
  void setsRejectDuplicatesUnderStress(String name, Supplier<apsd.interfaces.containers.collections.Set<?, Integer>> f) {
    var s = f.get();
    for (int round = 0; round < 3; round++)
      for (int i = 0; i < N; i++) {
        boolean inserted = s.InsertIfAbsent(i);
        assertEquals(round == 0, inserted);
      }
    assertEquals(nat(N), s.Size());
    for (int i = 0; i < N; i++) assertEquals(nat(1), s.Multiplicity(i));
    for (int i = 0; i < N; i++) assertTrue(s.Remove(i));
    assertTrue(s.IsEmpty());
  }

  @ParameterizedTest(name = "{0}") @MethodSource("sets")
  void setsDuplicatesViaInsert(String name, Supplier<apsd.interfaces.containers.collections.Set<?, Integer>> f) {
    var s = f.get();
    s.Insert(5);
    boolean second = s.Insert(5);
    assertFalse(second, "un insieme non deve ammettere duplicati");
    assertEquals(nat(1), s.Size());
  }

  @Test
  void orderedSetKeepsOrderAfterStress() {
    WOrderedSet<Integer> s = new WOrderedSet<>();
    List<Integer> vals = range(0, N);
    Collections.shuffle(vals, new Random(3));
    for (int x : vals) s.Insert(x);
    assertEquals(range(0, N), toList(s));
    assertEquals(0, s.Min());
    assertEquals(N - 1, s.Max());
    List<Integer> rev = range(0, N);
    Collections.reverse(rev);
    assertEquals(rev, toListBackward(s));
    for (int i = 0; i < N; i += 2) s.Remove(i);
    List<Integer> odd = new ArrayList<>();
    for (int i = 1; i < N; i += 2) odd.add(i);
    assertEquals(odd, toList(s));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("sorted")
  void sortedChainsStayOrderedWithDuplicatesUnderStress(String name, Supplier<SortedChain<?, Integer>> f) {
    SortedChain<?, Integer> c = f.get();
    Random r = new Random(11);
    List<Integer> model = new ArrayList<>();
    for (int i = 0; i < N; i++) {
      int x = r.nextInt(50);
      c.Insert(x);
      model.add(x);
    }
    Collections.sort(model);
    assertEquals(model, toList(c));
    assertEquals(model.get(0), c.Min());
    assertEquals(model.get(N - 1), c.Max());
    for (int i = 0; i < N; i++) {
      int pos = i % 2 == 0 ? 0 : model.size() - 1;
      Integer expected = model.remove(pos);
      assertEquals(expected, pos == 0 ? c.FirstNRemove() : c.LastNRemove());
    }
    assertTrue(c.IsEmpty());
    assertNull(c.Min());
    assertNull(c.Max());
  }

  static Stream<Arguments> sorted() {
    return Stream.of(
      Arguments.of("VSortedChain", (Supplier<SortedChain<?, Integer>>) () -> new VSortedChain<Integer>()),
      Arguments.of("LLSortedChain", (Supplier<SortedChain<?, Integer>>) () -> new LLSortedChain<Integer>()));
  }

  /* ====================================================================== */
  /* 6. Confronti: IsEqual, equals, hashCode                                */
  /* ====================================================================== */

  @ParameterizedTest(name = "{0}") @MethodSource("removables")
  void isEqualIsReflexiveAndFalseOnDifferentSize(String name, SeqF f) {
    Sequence<?, Integer> a = filled(f, 1, 2, 3);
    assertTrue(a.IsEqual(a));
    assertTrue(a.IsEqual((TraversableContainer<?, Integer>) a));
    Sequence<?, Integer> b = filled(f, 1, 2);
    assertFalse(a.IsEqual(b));
    assertFalse(b.IsEqual(a));
    assertTrue(f.New().IsEqual(f.New()));
  }

  @Test
  void isEqualAcrossDifferentSequenceTypes() {
    List<Integer> data = range(0, 50);
    var src = new VList<Integer>();
    for (int x : data) src.Insert(x);
    Sequence<?, Integer> v = new Vector<Integer>(src);
    Sequence<?, Integer> cv = new CircularVector<Integer>(src);
    Sequence<?, Integer> dv = new DynVector<Integer>(src);
    Sequence<?, Integer> dcv = new DynCircularVector<Integer>(src);
    Sequence<?, Integer> vl = new VList<Integer>(src);
    Sequence<?, Integer> ll = new LLList<Integer>(src);
    Sequence<?, Integer> vs = new VSortedChain<Integer>(src);
    Sequence<?, Integer> ls = new LLSortedChain<Integer>(src);
    List<Sequence<?, Integer>> all = List.of(v, cv, dv, dcv, vl, ll, vs, ls);
    for (var a : all)
      for (var b : all) {
        assertTrue(a.IsEqual(b), a.getClass().getSimpleName() + " vs " + b.getClass().getSimpleName());
        assertTrue(a.IsEqual((TraversableContainer<?, Integer>) b));
      }
    // un solo elemento diverso rompe l uguaglianza in ogni coppia
    ((MutableSequence<?, Integer>) ll).SetAt(-1, nat(25));
    for (var a : all) {
      if (a == ll) continue;
      assertFalse(a.IsEqual(ll));
      assertFalse(ll.IsEqual(a));
    }
  }

  @Test
  void isEqualRespectsOrderForSequencesButNotForTraversableMultisets() {
    var a = new VList<Integer>(); var b = new LLList<Integer>();
    for (int x : new int[] {1, 2, 3}) a.Insert(x);
    for (int x : new int[] {3, 2, 1}) b.Insert(x);
    assertFalse(a.IsEqual((Sequence<?, Integer>) b));
    // WSet e WOrderedSet con stessi elementi: confronto da Set, senza ordine
    WSet<Integer> ws = new WSet<>();
    WOrderedSet<Integer> os = new WOrderedSet<>();
    for (int x : new int[] {3, 1, 2}) { ws.Insert(x); }
    for (int x : new int[] {2, 3, 1}) { os.Insert(x); }
    assertTrue(ws.IsEqual((apsd.interfaces.containers.collections.Set<?, Integer>) os));
    assertTrue(os.IsEqual((apsd.interfaces.containers.collections.Set<?, Integer>) ws));
    assertTrue(ws.IsEqual((TraversableContainer<?, Integer>) os));
    os.Remove(2);
    os.Insert(4);
    assertFalse(ws.IsEqual((apsd.interfaces.containers.collections.Set<?, Integer>) os));
    assertFalse(os.IsEqual((apsd.interfaces.containers.collections.Set<?, Integer>) ws));
  }

  @Test
  void multisetComparisonDistinguishesMultiplicitiesWithSameSize() {
    var a = new VList<Integer>(); var b = new LLList<Integer>();
    for (int x : new int[] {1, 1, 2}) a.Insert(x);
    for (int x : new int[] {1, 2, 2}) b.Insert(x);
    assertFalse(a.IsEqual((TraversableContainer<?, Integer>) b));
    assertFalse(b.IsEqual((TraversableContainer<?, Integer>) a));
    var c = new LLSortedChain<Integer>();
    for (int x : new int[] {2, 1, 1}) c.Insert(x);
    assertTrue(a.IsEqual((TraversableContainer<?, Integer>) c)); // stessi multiinsiemi, ordine diverso
    assertTrue(c.IsEqual((TraversableContainer<?, Integer>) a));
  }

  @Test
  void isEqualWithNullElements() {
    var a = new VList<Integer>(); var b = new LLList<Integer>(); var d = new DynVector<Integer>(new LLList<Integer>());
    for (Integer x : list(1, null, 3)) { a.Insert(x); b.Insert(x); d.InsertLast(x); }
    assertTrue(a.IsEqual((Sequence<?, Integer>) b));
    assertTrue(b.IsEqual((Sequence<?, Integer>) d));
    assertTrue(a.IsEqual((TraversableContainer<?, Integer>) d));
    var e = new LLList<Integer>();
    for (Integer x : list(1, 2, 3)) e.Insert(x);
    assertFalse(a.IsEqual((Sequence<?, Integer>) e));
    assertFalse(e.IsEqual((Sequence<?, Integer>) a));
    assertEquals(nat(1), a.Multiplicity(null));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("sequences")
  void copyConstructorProducesEqualButIndependentContainer(String name, SeqF f) {
    Sequence<?, Integer> a = filled(f, 4, 5, 6);
    Sequence<?, Integer> copy = f.From(a);
    assertTrue(a.IsEqual(copy));
    assertNotSame(a, copy);
    if (copy instanceof MutableSequence<?, Integer> m && copy.Size().ToLong() > 0) {
      m.SetAt(99, nat(0));
      assertFalse(a.IsEqual(copy));
      assertNotEquals(99, a.GetFirst());
    }
  }

  /**
   * I contenitori non ridefiniscono equals/hashCode: il contratto Java va comunque rispettato,
   * cioe equals riflessivo, simmetrico, coerente con hashCode e stabile tra chiamate.
   */
  @ParameterizedTest(name = "{0}") @MethodSource("sequences")
  void javaEqualsAndHashCodeContractHolds(String name, SeqF f) {
    Sequence<?, Integer> a = filled(f, 1, 2, 3);
    Sequence<?, Integer> b = filled(f, 1, 2, 3);
    assertEquals(a, a);
    assertEquals(a.hashCode(), a.hashCode());
    assertNotEquals(a, null);
    assertNotEquals(a, "stringa");
    assertEquals(a.equals(b), b.equals(a));
    if (a.equals(b)) assertEquals(a.hashCode(), b.hashCode());
  }

  @Test
  void naturalEqualsAndHashCodeAreConsistent() {
    for (long l : new long[] {0, 1, 2, 999, Integer.MAX_VALUE, Long.MAX_VALUE}) {
      Natural a = Natural.Of(l);
      Natural b = new Natural(l);
      assertEquals(a, b);
      assertEquals(b, a);
      assertEquals(a.hashCode(), b.hashCode());
      assertEquals(0, a.compareTo(b));
      assertNotEquals(a, null);
      assertNotEquals(a, (Object) Long.valueOf(l));
    }
    assertNotEquals(Natural.ZERO, Natural.ONE);
    assertEquals(Natural.ZERO, Natural.ONE.Decrement());
    assertEquals(Natural.ONE, Natural.ZERO.Increment());
    assertEquals(Natural.ZERO.hashCode(), Natural.Of(0).hashCode());
    assertEquals(Natural.Of(5), Natural.Of(new apsd.classes.utilities.MutableNatural(5L)));
    assertTrue(Natural.Of(3).compareTo(Natural.Of(4)) < 0);
    assertTrue(Natural.Of(4).compareTo(Natural.Of(3)) > 0);
  }

  @Test
  void boxEqualsAndHashCodeAreConsistent() {
    Box<Integer> a = new Box<>(7), b = new Box<>(7), c = new Box<>(8), n1 = new Box<>((Integer) null), n2 = new Box<>((Integer) null);
    assertEquals(a, b);
    assertEquals(a.hashCode(), b.hashCode());
    assertNotEquals(a, c);
    assertEquals(n1, n2);
    assertEquals(n1.hashCode(), n2.hashCode());
    assertNotEquals(a, n1);
    assertNotEquals(n1, a);
    assertNotEquals(a, null);
  }

  @Test
  void emptyContainersOfDifferentTypesAreEqual() {
    assertTrue(new VList<Integer>().IsEqual((TraversableContainer<?, Integer>) new LLList<Integer>()));
    assertTrue(new WSet<Integer>().IsEqual((TraversableContainer<?, Integer>) new WOrderedSet<Integer>()));
    assertTrue(new LLSortedChain<Integer>().IsEqual((TraversableContainer<?, Integer>) new DynVector<Integer>()));
    assertTrue(new WSet<Integer>().IsEqual((TraversableContainer<?, Integer>) new VSortedChain<Integer>()));
  }
}

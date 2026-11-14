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

import apsd.classes.utilities.Natural;
import apsd.interfaces.containers.base.TraversableContainer;
import apsd.interfaces.containers.iterators.MutableBackwardIterator;
import apsd.interfaces.containers.iterators.MutableForwardIterator;
import apsd.interfaces.containers.sequences.DynVector;
import apsd.interfaces.containers.sequences.Sequence;
import apsd.interfaces.containers.sequences.Vector;

@DisplayName("Extended tests - vectors (static, dynamic, circular)")
public class VectorsExtendedTest {

  /* ---------------------------------------------------------------------- */
  /* Factories                                                               */
  /* ---------------------------------------------------------------------- */

  interface VFactory {
    Vector<?, Integer> New(Natural size);
    Vector<?, Integer> From(TraversableContainer<?, Integer> con);
  }

  interface DFactory {
    DynVector<?, Integer> New(Natural capacity);
    DynVector<?, Integer> From(TraversableContainer<?, Integer> con);
  }

  static Stream<Arguments> staticVectors() {
    return Stream.of(
      Arguments.of("Vector", new VFactory() {
        public Vector<?, Integer> New(Natural n) { return new apsd.classes.containers.sequences.Vector<Integer>(n); }
        public Vector<?, Integer> From(TraversableContainer<?, Integer> c) { return new apsd.classes.containers.sequences.Vector<Integer>(c); }
      }),
      Arguments.of("CircularVector", new VFactory() {
        public Vector<?, Integer> New(Natural n) { return new apsd.classes.containers.sequences.CircularVector<Integer>(n); }
        public Vector<?, Integer> From(TraversableContainer<?, Integer> c) { return new apsd.classes.containers.sequences.CircularVector<Integer>(c); }
      }));
  }

  static Stream<Arguments> dynVectors() {
    return Stream.of(
      Arguments.of("DynVector", new DFactory() {
        public DynVector<?, Integer> New(Natural n) { return new apsd.classes.containers.sequences.DynVector<Integer>(n); }
        public DynVector<?, Integer> From(TraversableContainer<?, Integer> c) { return new apsd.classes.containers.sequences.DynVector<Integer>(c); }
      }),
      Arguments.of("DynCircularVector", new DFactory() {
        public DynVector<?, Integer> New(Natural n) { return new apsd.classes.containers.sequences.DynCircularVector<Integer>(n); }
        public DynVector<?, Integer> From(TraversableContainer<?, Integer> c) { return new apsd.classes.containers.sequences.DynCircularVector<Integer>(c); }
      }));
  }

  /** Tutti e quattro i tipi di vettore, visti come Vector. */
  static Stream<Arguments> allVectors() {
    return Stream.concat(
      staticVectors(),
      dynVectors().map(a -> {
        DFactory d = (DFactory) a.get()[1];
        return Arguments.of(a.get()[0], new VFactory() {
          public Vector<?, Integer> New(Natural n) {
            DynVector<?, Integer> v = d.New(n);
            v.Expand(n); // un vettore dinamico con n posizioni null
            return v;
          }
          public Vector<?, Integer> From(TraversableContainer<?, Integer> c) { return d.From(c); }
        });
      }));
  }

  static Vector<?, Integer> filled(VFactory f, Integer... xs) {
    Vector<?, Integer> v = f.New(N(xs.length));
    for (int i = 0; i < xs.length; i++) v.SetAt(xs[i], N(i));
    return v;
  }

  static DynVector<?, Integer> dyn(DFactory f, Integer... xs) {
    DynVector<?, Integer> v = f.New(Natural.ZERO);
    for (Integer x : xs) v.InsertLast(x);
    return v;
  }

  /* ---------------------------------------------------------------------- */
  /* Comportamenti comuni a tutti i vettori                                  */
  /* ---------------------------------------------------------------------- */

  @ParameterizedTest(name = "{0}") @MethodSource("allVectors")
  void emptyVector(String name, VFactory f) {
    Vector<?, Integer> v = f.New(Natural.ZERO);
    assertTrue(v.IsEmpty());
    assertEquals(Natural.ZERO, v.Size());
    assertEquals(Natural.ZERO, v.Capacity());
    assertFalse(v.FIterator().IsValid());
    assertFalse(v.BIterator().IsValid());
    assertNull(v.Search(1));
    assertFalse(v.Exists(1));
    assertEquals(Natural.ZERO, v.Multiplicity(1));
    assertThrows(IndexOutOfBoundsException.class, () -> v.GetAt(Natural.ZERO));
    assertThrows(IndexOutOfBoundsException.class, () -> v.GetFirst());
    assertThrows(IndexOutOfBoundsException.class, () -> v.GetLast());
    assertThrows(IndexOutOfBoundsException.class, () -> v.SetAt(1, Natural.ZERO));
    assertThrows(IndexOutOfBoundsException.class, () -> v.SetFirst(1));
    assertThrows(IndexOutOfBoundsException.class, () -> v.SetLast(1));
    assertThrows(IndexOutOfBoundsException.class, () -> v.GetNSetFirst(1));
    assertThrows(IndexOutOfBoundsException.class, () -> v.GetNSetLast(1));
    assertTrue(toList(v).isEmpty());
    assertTrue(toListBackward(v).isEmpty());
    // le operazioni di shift su un vettore vuoto sono innocue
    v.ShiftFirstLeft(); v.ShiftLastLeft(); v.ShiftFirstRight(); v.ShiftLastRight();
    v.ShiftLeft(Natural.ZERO, N(3)); v.ShiftRight(Natural.ZERO, N(3));
    assertTrue(v.IsEmpty());
    assertTrue(v.SubSequence(Natural.ZERO, N(5)).IsEmpty());
    v.Clear();
    assertTrue(v.IsEmpty());
  }

  @ParameterizedTest(name = "{0}") @MethodSource("allVectors")
  void accessAndBounds(String name, VFactory f) {
    Vector<?, Integer> v = filled(f, 10, 20, 30);
    assertEquals(N(3), v.Size());
    assertEquals(10, v.GetFirst());
    assertEquals(30, v.GetLast());
    assertEquals(20, v.GetAt(N(1)));
    assertThrows(IndexOutOfBoundsException.class, () -> v.GetAt(N(3)));
    assertThrows(IndexOutOfBoundsException.class, () -> v.GetAt(N(Integer.MAX_VALUE)));
    assertThrows(IndexOutOfBoundsException.class, () -> v.GetAt(null));
    assertThrows(IndexOutOfBoundsException.class, () -> v.SetAt(1, N(3)));
    assertTrue(v.IsInBound(N(2)));
    assertFalse(v.IsInBound(N(3)));
    assertFalse(v.IsInBound(null));
    // gli accessi fuori limite non alterano il contenuto
    assertEquals(list(10, 20, 30), toList(v));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("allVectors")
  void setAndSwap(String name, VFactory f) {
    Vector<?, Integer> v = filled(f, 1, 2, 3, 4);
    v.SetFirst(100);
    v.SetLast(400);
    v.SetAt(null, N(1)); // i null sono ammessi come dati
    assertEquals(list(100, null, 3, 400), toList(v));
    assertEquals(3, v.GetNSetAt(33, N(2)));
    assertEquals(100, v.GetNSetFirst(0));
    assertEquals(400, v.GetNSetLast(4));
    assertEquals(list(0, null, 33, 4), toList(v));
    v.Swap(N(0), N(3));
    assertEquals(list(4, null, 33, 0), toList(v));
    v.Swap(N(1), N(1));
    assertEquals(list(4, null, 33, 0), toList(v));
    assertThrows(IndexOutOfBoundsException.class, () -> v.Swap(N(0), N(4)));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("allVectors")
  void copyConstructor(String name, VFactory f) {
    Vector<?, Integer> src = filled(f, 5, 6, 7);
    Vector<?, Integer> cp = f.From(src);
    assertEquals(list(5, 6, 7), toList(cp));
    cp.SetFirst(99); // la copia e indipendente
    assertEquals(5, src.GetFirst());
    assertTrue(f.From(f.New(Natural.ZERO)).IsEmpty());
    assertThrows(NullPointerException.class, () -> f.From(null));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("allVectors")
  void searchAndMembership(String name, VFactory f) {
    Vector<?, Integer> v = filled(f, 1, 2, 3, 2, null, 1);
    assertEquals(N(0), v.Search(1)); // prima occorrenza
    assertEquals(N(1), v.Search(2));
    assertEquals(N(4), v.Search(null));
    assertNull(v.Search(42));
    assertTrue(v.Exists(3));
    assertTrue(v.Exists(null));
    assertFalse(v.Exists(42));
    assertEquals(N(2), v.Multiplicity(1));
    assertEquals(N(2), v.Multiplicity(2));
    assertEquals(N(1), v.Multiplicity(null));
    assertEquals(N(0), v.Multiplicity(9));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("allVectors")
  void foldAndTraverse(String name, VFactory f) {
    Vector<?, Integer> v = filled(f, 1, 2, 3, 4);
    assertEquals(10, v.FoldForward((d, acc) -> acc + d, 0));
    assertEquals("4321", v.FoldBackward((d, acc) -> acc + d, ""));
    assertEquals(list(4, 3, 2, 1), toListBackward(v));
    // la visita si ferma al primo predicato vero
    List<Integer> seen = new ArrayList<>();
    assertTrue(v.TraverseForward(d -> { seen.add(d); return d == 2; }));
    assertEquals(list(1, 2), seen);
    seen.clear();
    assertTrue(v.TraverseBackward(d -> { seen.add(d); return d == 3; }));
    assertEquals(list(4, 3), seen);
    assertFalse(v.TraverseForward(d -> false));
    assertFalse(v.TraverseForward(null));
    assertEquals("x", v.FoldForward(null, "x"));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("allVectors")
  void isEqual(String name, VFactory f) {
    Vector<?, Integer> a = filled(f, 1, 2, 3);
    assertTrue(a.IsEqual(filled(f, 1, 2, 3)));
    assertFalse(a.IsEqual(filled(f, 1, 2, 4)));
    assertFalse(a.IsEqual(filled(f, 1, 2)));
    assertFalse(a.IsEqual((Sequence<?, Integer>) null));
    assertTrue(f.New(Natural.ZERO).IsEqual(f.New(Natural.ZERO)));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("allVectors")
  void subSequence(String name, VFactory f) {
    Vector<?, Integer> v = filled(f, 0, 1, 2, 3, 4);
    assertEquals(list(1, 2, 3), toList(v.SubSequence(N(1), N(3))));
    assertEquals(list(0, 1, 2, 3, 4), toList(v.SubSequence(N(0), N(4))));
    assertEquals(list(4), toList(v.SubSequence(N(4), N(4))));
    assertEquals(list(3, 4), toList(v.SubSequence(N(3), N(100)))); // "to" oltre la fine viene troncato
    assertTrue(v.SubSequence(N(3), N(1)).IsEmpty()); // from > to
    assertTrue(v.SubSequence(N(10), N(20)).IsEmpty()); // from oltre la fine
    assertEquals(list(0, 1, 2, 3, 4), toList(v)); // l originale non cambia
    v.SubSequence(N(0), N(1)).SetFirst(77);
    assertEquals(0, v.GetFirst()); // sotto-sequenza indipendente
  }

  @ParameterizedTest(name = "{0}") @MethodSource("allVectors")
  void shiftLeft(String name, VFactory f) {
    Vector<?, Integer> v = filled(f, 0, 1, 2, 3, 4);
    v.ShiftLeft(N(2)); // sovrascrive la posizione 1
    assertEquals(list(0, 2, 3, 4, null), toList(v));

    v = filled(f, 0, 1, 2, 3, 4);
    v.ShiftFirstLeft();
    assertEquals(list(1, 2, 3, 4, null), toList(v));

    v = filled(f, 0, 1, 2, 3, 4);
    v.ShiftLastLeft(); // l ultimo dato va in penultima posizione
    assertEquals(list(0, 1, 2, 4, null), toList(v));

    v = filled(f, 0, 1, 2, 3, 4);
    v.ShiftLeft(N(0), N(2));
    assertEquals(list(2, 3, 4, null, null), toList(v));

    v = filled(f, 0, 1, 2, 3, 4);
    v.ShiftLeft(N(3), N(2));
    assertEquals(list(0, 3, 4, null, null), toList(v));

    v = filled(f, 0, 1, 2, 3, 4);
    v.ShiftLeft(N(0), N(100)); // piu dell ampiezza: tutto null
    assertEquals(list(null, null, null, null, null), toList(v));

    v = filled(f, 0, 1, 2, 3, 4);
    v.ShiftLeft(N(1), N(0)); // zero passi: nessun effetto
    assertEquals(list(0, 1, 2, 3, 4), toList(v));

    v = filled(f, 0, 1, 2, 3, 4);
    v.ShiftLeft(N(1), N(4), N(1)); // intervallo [1,4): fuori dall intervallo resta il dato in coda
    assertEquals(list(1, 2, 3, null, 4), toList(v));
    assertEquals(N(5), v.Size()); // lo shift non cambia mai l ampiezza
  }

  @ParameterizedTest(name = "{0}") @MethodSource("allVectors")
  void shiftRight(String name, VFactory f) {
    Vector<?, Integer> v = filled(f, 0, 1, 2, 3, 4);
    v.ShiftRight(N(1));
    assertEquals(list(0, null, 1, 2, 3), toList(v));

    v = filled(f, 0, 1, 2, 3, 4);
    v.ShiftFirstRight();
    assertEquals(list(null, 0, 1, 2, 3), toList(v));

    v = filled(f, 0, 1, 2, 3, 4);
    v.ShiftLastRight();
    assertEquals(list(0, 1, 2, 3, null), toList(v));

    v = filled(f, 0, 1, 2, 3, 4);
    v.ShiftRight(N(0), N(2));
    assertEquals(list(null, null, 0, 1, 2), toList(v));

    v = filled(f, 0, 1, 2, 3, 4);
    v.ShiftRight(N(2), N(2));
    assertEquals(list(0, 1, null, null, 2), toList(v));

    v = filled(f, 0, 1, 2, 3, 4);
    v.ShiftRight(N(0), N(100));
    assertEquals(list(null, null, null, null, null), toList(v));

    v = filled(f, 0, 1, 2, 3, 4);
    v.ShiftRight(N(1), N(0));
    assertEquals(list(0, 1, 2, 3, 4), toList(v));
    assertEquals(N(5), v.Size());
  }

  @ParameterizedTest(name = "{0}") @MethodSource("allVectors")
  void repeatedShiftsStayConsistent(String name, VFactory f) {
    // Nei vettori circolari ShiftLeft/Right(0) ruotano l origine: ripetute molte volte
    // devono comunque coincidere con un semplice modello su lista.
    Vector<?, Integer> v = filled(f, 0, 1, 2, 3, 4, 5, 6);
    List<Integer> model = list(0, 1, 2, 3, 4, 5, 6);
    Random rnd = new Random(7);
    for (int step = 0; step < 300; step++) {
      int n = rnd.nextInt(3);
      if (rnd.nextBoolean()) {
        v.ShiftLeft(N(0), N(n));
        for (int k = 0; k < n; k++) { model.remove(0); model.add(null); }
      } else {
        v.ShiftRight(N(0), N(n));
        for (int k = 0; k < n; k++) { model.remove(model.size() - 1); model.add(0, null); }
      }
      v.SetFirst(step);
      model.set(0, step);
      v.SetLast(-step);
      model.set(model.size() - 1, -step);
      assertEquals(model, toList(v), "step " + step);
    }
  }

  @ParameterizedTest(name = "{0}") @MethodSource("allVectors")
  void iterators(String name, VFactory f) {
    Vector<?, Integer> v = filled(f, 1, 2, 3, 4, 5);
    MutableForwardIterator<Integer> it = v.FIterator();
    assertTrue(it.IsValid());
    assertEquals(1, it.GetCurrent());
    assertEquals(1, it.DataNNext());
    assertEquals(2, it.GetCurrent());
    it.Next(2);
    assertEquals(4, it.GetCurrent());
    it.Next(Natural.Of(100)); // oltre la fine: si ferma senza lanciare
    assertFalse(it.IsValid());
    it.Next(); // innocuo se non valido
    assertFalse(it.IsValid());
    assertThrows(IndexOutOfBoundsException.class, it::GetCurrent);
    it.Reset();
    assertTrue(it.IsValid());
    assertEquals(1, it.GetCurrent());
    assertThrows(IllegalArgumentException.class, () -> it.Next(-1L));
    it.Next(0L);
    assertEquals(1, it.GetCurrent());
    assertEquals(list(1, 2, 3, 4, 5), drain(v.FIterator()));

    MutableBackwardIterator<Integer> bit = v.BIterator();
    assertEquals(5, bit.GetCurrent());
    bit.Prev(2);
    assertEquals(3, bit.GetCurrent());
    bit.Prev(Natural.Of(100));
    assertFalse(bit.IsValid());
    bit.Prev();
    assertFalse(bit.IsValid());
    bit.Reset();
    assertEquals(5, bit.GetCurrent());
    assertThrows(IllegalArgumentException.class, () -> bit.Prev(-1L));
    assertEquals(list(5, 4, 3, 2, 1), drain(v.BIterator()));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("allVectors")
  void mutableIterators(String name, VFactory f) {
    Vector<?, Integer> v = filled(f, 1, 2, 3, 4);
    MutableForwardIterator<Integer> it = v.FIterator();
    while (it.IsValid()) { it.SetCurrent(it.GetCurrent() * 10); it.Next(); }
    assertEquals(list(10, 20, 30, 40), toList(v));
    MutableBackwardIterator<Integer> bit = v.BIterator();
    int k = 0;
    while (bit.IsValid()) { bit.SetCurrent(k++); bit.Prev(); }
    assertEquals(list(3, 2, 1, 0), toList(v));
    // ForEach con arresto anticipato
    List<Integer> seen = new ArrayList<>();
    assertTrue(v.FIterator().ForEachForward(d -> { seen.add(d); return d == 2; }));
    assertEquals(list(3, 2), seen);
    assertFalse(v.FIterator().ForEachForward(null));
    assertFalse(v.BIterator().ForEachBackward(null));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("allVectors")
  void iteratorsOnEmpty(String name, VFactory f) {
    Vector<?, Integer> v = f.New(Natural.ZERO);
    assertFalse(v.FIterator().IsValid());
    assertFalse(v.BIterator().IsValid());
    assertFalse(v.FIterator().ForEachForward(d -> true));
    assertTrue(drain(v.FIterator()).isEmpty());
    assertTrue(drain(v.BIterator()).isEmpty());
  }

  @ParameterizedTest(name = "{0}") @MethodSource("allVectors")
  void reallocPreservesPrefix(String name, VFactory f) {
    Vector<?, Integer> v = filled(f, 1, 2, 3, 4);
    v.Realloc(N(10));
    assertEquals(N(10), v.Capacity());
    // per i dinamici Size != Capacity: si controlla solo il prefisso utile
    assertEquals(list(1, 2, 3, 4), toList(v).subList(0, 4));
    v.Realloc(N(2));
    assertEquals(N(2), v.Capacity());
    assertEquals(list(1, 2), toList(v));
    v.Realloc(N(2)); // stessa dimensione
    assertEquals(list(1, 2), toList(v));
    v.Realloc(Natural.ZERO);
    assertEquals(Natural.ZERO, v.Capacity());
    assertTrue(v.IsEmpty());
    v.Realloc(N(3)); // di nuovo utilizzabile dopo lo svuotamento
    assertEquals(N(3), v.Capacity());
  }

  @ParameterizedTest(name = "{0}") @MethodSource("allVectors")
  void clearThenReuse(String name, VFactory f) {
    Vector<?, Integer> v = filled(f, 1, 2, 3);
    v.Clear();
    assertTrue(v.IsEmpty());
    assertEquals(Natural.ZERO, v.Capacity());
    assertThrows(IndexOutOfBoundsException.class, () -> v.GetFirst());
    v.Realloc(N(2));
    assertEquals(N(2), v.Capacity());
  }

  @ParameterizedTest(name = "{0}") @MethodSource("staticVectors")
  void staticVectorSizeIsCapacity(String name, VFactory f) {
    Vector<?, Integer> v = f.New(N(4));
    assertEquals(N(4), v.Size());
    assertEquals(v.Capacity(), v.Size());
    assertEquals(list(null, null, null, null), toList(v));
    v.Grow(); // (4+1)*2 = 10
    assertEquals(N(10), v.Capacity());
    assertEquals(N(10), v.Size());
    v.Shrink(); // 10/2 = 5
    assertEquals(N(5), v.Capacity());
    v.Grow(N(5)); // (5+5)*2 = 20
    assertEquals(N(20), v.Capacity());
    for (int i = 0; i < 5; i++) v.Shrink(); // 20 -> 10 -> 5 -> 2 -> 1 -> 0
    assertEquals(N(0), v.Capacity());
    v.Shrink(); // shrink di un vettore vuoto: resta vuoto
    assertEquals(N(0), v.Capacity());
  }

  @ParameterizedTest(name = "{0}") @MethodSource("staticVectors")
  void staticVectorNeverGrowsImplicitly(String name, VFactory f) {
    Vector<?, Integer> v = filled(f, 1, 2, 3);
    assertThrows(IndexOutOfBoundsException.class, () -> v.SetAt(9, N(3)));
    assertEquals(N(3), v.Size());
  }

  @Test
  void circularWrapAround() {
    apsd.classes.containers.sequences.CircularVector<Integer> v = new apsd.classes.containers.sequences.CircularVector<>(N(4));
    for (int i = 0; i < 4; i++) v.SetAt(i, N(i));
    // ruota piu volte di un giro completo
    for (int i = 0; i < 9; i++) {
      v.ShiftLeft(N(0), N(1));
      v.SetLast(100 + i);
    }
    assertEquals(list(105, 106, 107, 108), toList(v));
    for (int i = 0; i < 9; i++) {
      v.ShiftRight(N(0), N(1));
      v.SetFirst(i);
    }
    assertEquals(list(8, 7, 6, 5), toList(v));
    assertEquals(list(5, 6, 7, 8), toListBackward(v));
  }

  /* ---------------------------------------------------------------------- */
  /* Vettori dinamici                                                        */
  /* ---------------------------------------------------------------------- */

  @ParameterizedTest(name = "{0}") @MethodSource("dynVectors")
  void dynEmpty(String name, DFactory f) {
    DynVector<?, Integer> v = f.New(Natural.ZERO);
    assertTrue(v.IsEmpty());
    assertEquals(Natural.ZERO, v.Size());
    assertThrows(IndexOutOfBoundsException.class, v::RemoveFirst);
    assertThrows(IndexOutOfBoundsException.class, v::RemoveLast);
    assertThrows(IndexOutOfBoundsException.class, v::FirstNRemove);
    assertThrows(IndexOutOfBoundsException.class, v::LastNRemove);
    assertThrows(IndexOutOfBoundsException.class, () -> v.RemoveAt(Natural.ZERO));
    assertThrows(IndexOutOfBoundsException.class, () -> v.AtNRemove(Natural.ZERO));
    assertFalse(v.Remove(1));
    assertThrows(IllegalArgumentException.class, () -> v.Reduce());
    v.Shrink();
    assertTrue(v.IsEmpty());
  }

  @ParameterizedTest(name = "{0}") @MethodSource("dynVectors")
  void dynInsertPositions(String name, DFactory f) {
    DynVector<?, Integer> v = f.New(Natural.ZERO);
    v.InsertLast(2);
    v.InsertFirst(0);
    v.InsertAt(1, N(1));
    v.InsertAt(3, N(3)); // pos == Size e valido
    assertEquals(list(0, 1, 2, 3), toList(v));
    assertThrows(IndexOutOfBoundsException.class, () -> v.InsertAt(9, N(5)));
    assertEquals(list(0, 1, 2, 3), toList(v)); // nessun effetto collaterale
    assertEquals(N(4), v.Size());
    assertTrue(v.Insert(4)); // Insert == InsertLast
    assertEquals(list(0, 1, 2, 3, 4), toList(v));
    v.InsertAt(null, N(2));
    assertEquals(list(0, 1, null, 2, 3, 4), toList(v));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("dynVectors")
  void dynRemovePositions(String name, DFactory f) {
    DynVector<?, Integer> v = dyn(f, 0, 1, 2, 3, 4, 5);
    assertEquals(0, v.FirstNRemove());
    assertEquals(5, v.LastNRemove());
    assertEquals(2, v.AtNRemove(N(1)));
    assertEquals(list(1, 3, 4), toList(v));
    v.RemoveFirst();
    v.RemoveLast();
    v.RemoveAt(N(0));
    assertTrue(v.IsEmpty());
    DynVector<?, Integer> w = dyn(f, 1, 2, 3);
    assertThrows(IndexOutOfBoundsException.class, () -> w.RemoveAt(N(3)));
    assertEquals(list(1, 2, 3), toList(w));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("dynVectors")
  void dynRemoveByValue(String name, DFactory f) {
    DynVector<?, Integer> v = dyn(f, 1, 2, 1, 3, 1);
    assertTrue(v.Remove(1)); // solo la prima occorrenza
    assertEquals(list(2, 1, 3, 1), toList(v));
    assertFalse(v.Remove(42));
    assertTrue(v.AllRemoved(dyn(f, 2, 3)));
    assertEquals(list(1, 1), toList(v));
    assertFalse(v.AllRemoved(dyn(f, 1, 7, 1))); // si ferma al primo fallimento
    assertEquals(list(1), toList(v));
    assertTrue(v.SomeRemoved(dyn(f, 7, 1)));
    assertTrue(v.IsEmpty());
    assertFalse(v.SomeRemoved(dyn(f, 7, 8)));
    assertFalse(v.AllRemoved(null));
    assertFalse(v.SomeRemoved(null));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("dynVectors")
  void dynGrowAndShrink(String name, DFactory f) {
    DynVector<?, Integer> v = f.New(Natural.ZERO);
    long maxCap = 0;
    for (int i = 0; i < 1000; i++) {
      v.InsertLast(i);
      assertEquals(i + 1, v.Size().ToLong());
      assertTrue(v.Capacity().ToLong() >= v.Size().ToLong(), "capacity < size at " + i);
      maxCap = Math.max(maxCap, v.Capacity().ToLong());
    }
    assertEquals(range(0, 1000), toList(v));
    // la crescita e geometrica: la capacita non puo essere enormemente maggiore
    assertTrue(maxCap <= 4L * 1000, "capacity too large: " + maxCap);
    for (int i = 0; i < 995; i++) {
      assertEquals(i, v.FirstNRemove());
      assertTrue(v.Capacity().ToLong() >= v.Size().ToLong());
    }
    assertEquals(range(995, 1000), toList(v));
    assertTrue(v.Capacity().ToLong() < maxCap, "il vettore deve ridurre la capacita");
    assertTrue(v.Capacity().ToLong() <= 5L * 8, "capacity not shrunk enough: " + v.Capacity());
    while (!v.IsEmpty()) v.RemoveLast();
    assertTrue(v.IsEmpty());
    v.InsertLast(1); // riutilizzabile dopo lo svuotamento completo
    assertEquals(list(1), toList(v));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("dynVectors")
  void dynInitialCapacityIsNotSize(String name, DFactory f) {
    DynVector<?, Integer> v = f.New(N(10));
    assertTrue(v.IsEmpty());
    assertEquals(N(10), v.Capacity());
    for (int i = 0; i < 5; i++) v.InsertLast(i);
    assertEquals(N(5), v.Size());
    assertEquals(N(10), v.Capacity()); // nessuna riallocazione finche c e spazio
    assertThrows(IndexOutOfBoundsException.class, () -> v.GetAt(N(5)));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("dynVectors")
  void dynExpandReduce(String name, DFactory f) {
    DynVector<?, Integer> v = dyn(f, 1, 2, 3);
    v.Expand(N(2));
    assertEquals(N(5), v.Size());
    assertEquals(list(1, 2, 3, null, null), toList(v));
    v.Expand(Natural.ZERO);
    assertEquals(N(5), v.Size());
    v.Reduce(N(3));
    assertEquals(list(1, 2), toList(v));
    assertThrows(IllegalArgumentException.class, () -> v.Reduce(N(3)));
    assertEquals(list(1, 2), toList(v)); // nessuna modifica dopo il fallimento
    v.Reduce(N(2));
    assertTrue(v.IsEmpty());
    v.Expand();
    assertEquals(N(1), v.Size());
    assertNull(v.GetFirst());
    v.Reduce();
    assertTrue(v.IsEmpty());
  }

  @ParameterizedTest(name = "{0}") @MethodSource("dynVectors")
  void dynReallocBelowSizeTruncates(String name, DFactory f) {
    DynVector<?, Integer> v = dyn(f, 1, 2, 3, 4, 5);
    v.Realloc(N(3));
    assertEquals(list(1, 2, 3), toList(v));
    assertEquals(N(3), v.Size());
    v.Realloc(N(50));
    assertEquals(N(3), v.Size());
    assertEquals(N(50), v.Capacity());
    assertEquals(list(1, 2, 3), toList(v));
    v.Shrink(); // mai sotto la dimensione logica
    assertTrue(v.Capacity().ToLong() >= 3);
    assertEquals(list(1, 2, 3), toList(v));
    v.Clear();
    assertTrue(v.IsEmpty());
    assertEquals(Natural.ZERO, v.Capacity());
  }

  @ParameterizedTest(name = "{0}") @MethodSource("dynVectors")
  void dynCopyConstructor(String name, DFactory f) {
    DynVector<?, Integer> v = f.From(dyn(f, 7, 8, 9));
    assertEquals(N(3), v.Size());
    assertEquals(list(7, 8, 9), toList(v));
    v.InsertLast(10);
    v.InsertFirst(6);
    assertEquals(list(6, 7, 8, 9, 10), toList(v));
    assertEquals(list(10, 9, 8, 7, 6), toListBackward(v));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("dynVectors")
  void dynSubSequence(String name, DFactory f) {
    DynVector<?, Integer> v = dyn(f, 0, 1, 2, 3, 4);
    DynVector<?, Integer> s = (DynVector<?, Integer>) v.SubSequence(N(1), N(3));
    assertEquals(list(1, 2, 3), toList(s));
    assertEquals(N(3), s.Size());
    s.InsertLast(99); // la sotto-sequenza e pienamente dinamica
    assertEquals(list(1, 2, 3, 99), toList(s));
    assertEquals(list(0, 1, 2, 3, 4), toList(v));
    assertTrue(v.SubSequence(N(4), N(2)).IsEmpty());
    assertEquals(list(2, 3, 4), toList(v.SubSequence(N(2), N(50))));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("dynVectors")
  void dynRandomizedAgainstModel(String name, DFactory f) {
    DynVector<?, Integer> v = f.New(Natural.ZERO);
    List<Integer> model = new ArrayList<>();
    Random rnd = new Random(12345);
    for (int step = 0; step < 4000; step++) {
      int op = rnd.nextInt(10);
      if (op < 5 || model.isEmpty()) {
        int pos = rnd.nextInt(model.size() + 1);
        v.InsertAt(step, N(pos));
        model.add(pos, step);
      } else if (op < 8) {
        int pos = rnd.nextInt(model.size());
        assertEquals(model.remove(pos), v.AtNRemove(N(pos)));
      } else if (op == 8) {
        assertEquals(model.remove(0), v.FirstNRemove());
      } else {
        assertEquals(model.remove(model.size() - 1), v.LastNRemove());
      }
      assertEquals(model.size(), v.Size().ToLong());
      assertTrue(v.Capacity().ToLong() >= v.Size().ToLong());
      if (step % 97 == 0) {
        assertEquals(model, toList(v), "step " + step);
        List<Integer> back = toListBackward(v);
        Collections.reverse(back);
        assertEquals(model, back, "backward step " + step);
      }
    }
    assertEquals(model, toList(v));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("dynVectors")
  void dynDequeLikePattern(String name, DFactory f) {
    // inserimenti e rimozioni solo in testa e in coda: pattern da coda doppia
    DynVector<?, Integer> v = f.New(Natural.ZERO);
    List<Integer> model = new ArrayList<>();
    for (int i = 0; i < 200; i++) {
      v.InsertFirst(i); model.add(0, i);
      v.InsertLast(-i); model.add(-i);
      if (i % 3 == 0) assertEquals(model.remove(0), v.FirstNRemove());
      if (i % 5 == 0) assertEquals(model.remove(model.size() - 1), v.LastNRemove());
    }
    assertEquals(model, toList(v));
  }

  /* ---------------------------------------------------------------------- */
  /* Altri tipi generici                                                     */
  /* ---------------------------------------------------------------------- */

  @Test
  void stringVectors() {
    apsd.classes.containers.sequences.Vector<String> v = new apsd.classes.containers.sequences.Vector<>(N(3));
    v.SetAt("b", N(0)); v.SetAt("a", N(1)); v.SetAt("c", N(2));
    assertEquals(list("b", "a", "c"), toList(v));
    assertEquals(N(1), v.Search("a"));
    assertTrue(v.Exists("c"));
    assertEquals("bac", v.FoldForward((d, acc) -> acc + d, ""));

    apsd.classes.containers.sequences.DynCircularVector<String> d = new apsd.classes.containers.sequences.DynCircularVector<>();
    for (String s : new String[] {"x", "y", "z"}) d.InsertLast(s);
    d.InsertFirst("w");
    assertEquals(list("w", "x", "y", "z"), toList(d));
    assertEquals("w", d.FirstNRemove());
    assertEquals(N(3), d.Size());
    assertEquals(N(0), d.Search("x"));
    assertTrue(d.Remove("y"));
    assertEquals(list("x", "z"), toList(d));

    apsd.classes.containers.sequences.DynVector<String> c = new apsd.classes.containers.sequences.DynVector<>(d);
    assertEquals(list("x", "z"), toList(c));
    // copia da un contenitore di tipo diverso
    apsd.classes.containers.sequences.CircularVector<String> cc = new apsd.classes.containers.sequences.CircularVector<>(c);
    assertEquals(list("x", "z"), toList(cc));
  }
}

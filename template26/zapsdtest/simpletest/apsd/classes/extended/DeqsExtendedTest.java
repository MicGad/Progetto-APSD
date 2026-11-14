package zapsdtest.simpletest.apsd.classes.extended;

import static org.junit.jupiter.api.Assertions.*;
import static zapsdtest.simpletest.apsd.classes.extended.Helpers.*;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
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
import apsd.classes.containers.deqs.WQueue;
import apsd.classes.containers.deqs.WStack;
import apsd.classes.utilities.Natural;
import apsd.interfaces.containers.base.TraversableContainer;
import apsd.interfaces.containers.deqs.Queue;
import apsd.interfaces.containers.deqs.Stack;

@DisplayName("Extended tests - stacks and queues (WStack, WQueue)")
public class DeqsExtendedTest {

  /* ---------------------------------------------------------------------- */
  /* Factories                                                               */
  /* ---------------------------------------------------------------------- */

  interface StackFactory {
    Stack<?, Integer> New();
    Stack<?, Integer> From(TraversableContainer<?, Integer> con);
  }

  interface QueueFactory {
    Queue<?, Integer> New();
    Queue<?, Integer> From(TraversableContainer<?, Integer> con);
  }

  static Stream<Arguments> stacks() {
    return Stream.of(
      Arguments.of("WStack(default)", new StackFactory() {
        public Stack<?, Integer> New() { return new WStack<Integer>(); }
        public Stack<?, Integer> From(TraversableContainer<?, Integer> c) { return new WStack<Integer>(c); }
      }),
      Arguments.of("WStack(VList)", new StackFactory() {
        public Stack<?, Integer> New() { return new WStack<Integer>(new VList<Integer>()); }
        public Stack<?, Integer> From(TraversableContainer<?, Integer> c) { return new WStack<Integer>(new VList<Integer>(), c); }
      }),
      Arguments.of("WStack(LLList)", new StackFactory() {
        public Stack<?, Integer> New() { return new WStack<Integer>(new LLList<Integer>()); }
        public Stack<?, Integer> From(TraversableContainer<?, Integer> c) { return new WStack<Integer>(new LLList<Integer>(), c); }
      }));
  }

  static Stream<Arguments> queues() {
    return Stream.of(
      Arguments.of("WQueue(default)", new QueueFactory() {
        public Queue<?, Integer> New() { return new WQueue<Integer>(); }
        public Queue<?, Integer> From(TraversableContainer<?, Integer> c) { return new WQueue<Integer>(c); }
      }),
      Arguments.of("WQueue(VList)", new QueueFactory() {
        public Queue<?, Integer> New() { return new WQueue<Integer>(new VList<Integer>()); }
        public Queue<?, Integer> From(TraversableContainer<?, Integer> c) { return new WQueue<Integer>(new VList<Integer>(), c); }
      }),
      Arguments.of("WQueue(LLList)", new QueueFactory() {
        public Queue<?, Integer> New() { return new WQueue<Integer>(new LLList<Integer>()); }
        public Queue<?, Integer> From(TraversableContainer<?, Integer> c) { return new WQueue<Integer>(new LLList<Integer>(), c); }
      }));
  }

  static VList<Integer> vl(Integer... xs) {
    VList<Integer> l = new VList<>();
    for (Integer x : xs) l.Insert(x);
    return l;
  }

  /* ---------------------------------------------------------------------- */
  /* Pile                                                                    */
  /* ---------------------------------------------------------------------- */

  @ParameterizedTest(name = "{0}") @MethodSource("stacks")
  void stackEmpty(String name, StackFactory f) {
    Stack<?, Integer> s = f.New();
    assertTrue(s.IsEmpty());
    assertEquals(Natural.ZERO, s.Size());
    assertNull(s.Top());
    assertNull(s.TopNPop());
    s.Pop(); // innocuo
    s.Pop();
    assertTrue(s.IsEmpty());
    s.Clear();
    assertTrue(s.IsEmpty());
    assertTrue(s.Empty().IsEmpty());
  }

  @ParameterizedTest(name = "{0}") @MethodSource("stacks")
  void stackLifo(String name, StackFactory f) {
    Stack<?, Integer> s = f.New();
    for (int i = 1; i <= 5; i++) {
      s.Push(i);
      assertEquals(i, s.Top());
      assertEquals(N(i), s.Size());
    }
    for (int i = 5; i >= 1; i--) {
      assertEquals(i, s.Top());
      assertEquals(i, s.TopNPop());
      assertEquals(N(i - 1), s.Size());
    }
    assertTrue(s.IsEmpty());
    assertNull(s.Top());
  }

  @ParameterizedTest(name = "{0}") @MethodSource("stacks")
  void stackPopAndTopDoNotInterfere(String name, StackFactory f) {
    Stack<?, Integer> s = f.New();
    s.Push(1); s.Push(2); s.Push(3);
    assertEquals(3, s.Top());
    assertEquals(3, s.Top()); // Top non rimuove
    assertEquals(N(3), s.Size());
    s.Pop();
    assertEquals(2, s.Top());
    s.Pop();
    s.Pop();
    assertTrue(s.IsEmpty());
    s.Push(9);
    assertEquals(9, s.Top());
    assertEquals(N(1), s.Size());
  }

  @ParameterizedTest(name = "{0}") @MethodSource("stacks")
  void stackSwapTop(String name, StackFactory f) {
    Stack<?, Integer> s = f.New();
    s.SwapTop(7); // su pila vuota equivale a un Push
    assertEquals(7, s.Top());
    assertEquals(N(1), s.Size());
    s.Push(8);
    s.SwapTop(80);
    assertEquals(80, s.Top());
    assertEquals(N(2), s.Size());
    assertEquals(80, s.TopNSwap(81));
    assertEquals(81, s.Top());
    assertEquals(N(2), s.Size());
    s.Pop();
    assertEquals(7, s.Top());
    Stack<?, Integer> e = f.New();
    assertNull(e.TopNSwap(3)); // su pila vuota restituisce null e inserisce
    assertEquals(3, e.Top());
    assertEquals(N(1), e.Size());
  }

  @ParameterizedTest(name = "{0}") @MethodSource("stacks")
  void stackNullElements(String name, StackFactory f) {
    Stack<?, Integer> s = f.New();
    s.Push(null);
    assertEquals(N(1), s.Size()); // null e un dato valido, la pila non e vuota
    assertFalse(s.IsEmpty());
    assertNull(s.Top());
    s.Push(1);
    assertEquals(1, s.TopNPop());
    assertNull(s.TopNPop());
    assertTrue(s.IsEmpty());
  }

  @ParameterizedTest(name = "{0}") @MethodSource("stacks")
  void stackInsertAndBulk(String name, StackFactory f) {
    Stack<?, Integer> s = f.New();
    assertTrue(s.Insert(1)); // Insert == Push
    assertTrue(s.Insert(2));
    assertEquals(2, s.Top());
    assertTrue(s.AllInserted(vl(3, 4, 5)));
    assertEquals(5, s.Top());
    assertEquals(N(5), s.Size());
    assertTrue(s.AllInserted(vl())); // contenitore vuoto
    assertFalse(s.SomeInserted(vl()));
    assertFalse(s.AllInserted(null));
    assertTrue(s.SomeInserted(vl(6, 7)));
    assertEquals(7, s.Top());
  }

  @ParameterizedTest(name = "{0}") @MethodSource("stacks")
  void stackCopyConstructor(String name, StackFactory f) {
    Stack<?, Integer> s = f.From(vl(1, 2, 3)); // l ultimo inserito e la cima
    assertEquals(N(3), s.Size());
    assertEquals(3, s.TopNPop());
    assertEquals(2, s.TopNPop());
    assertEquals(1, s.TopNPop());
    assertTrue(s.IsEmpty());
    assertTrue(f.From(vl()).IsEmpty());
    assertThrows(NullPointerException.class, () -> f.From(null));
  }

  @Test
  void stackConstructorsRejectNull() {
    assertThrows(NullPointerException.class, () -> new WStack<Integer>((VList<Integer>) null));
    assertThrows(NullPointerException.class, () -> new WStack<Integer>(new VList<Integer>(), null));
    assertThrows(NullPointerException.class, () -> new WStack<Integer>((VList<Integer>) null, vl(1)));
  }

  @Test
  void stackWrapsExistingList() {
    VList<Integer> backing = vl(10, 20, 30); // il primo elemento della lista e la cima
    WStack<Integer> s = new WStack<>(backing);
    assertEquals(N(3), s.Size());
    assertEquals(10, s.Top());
    s.Push(5);
    assertEquals(5, backing.GetFirst()); // la lista e condivisa, non copiata
    assertEquals(list(5, 10, 20, 30), toList(backing));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("stacks")
  void stackClearAndReuse(String name, StackFactory f) {
    Stack<?, Integer> s = f.New();
    for (int i = 0; i < 100; i++) s.Push(i);
    s.Clear();
    assertTrue(s.IsEmpty());
    assertEquals(Natural.ZERO, s.Size());
    assertNull(s.Top());
    s.Push(1);
    assertEquals(1, s.Top());
    Stack<?, Integer> e = s.Empty();
    assertNotSame(s, e);
    assertTrue(e.IsEmpty());
    e.Push(2);
    assertEquals(1, s.Top()); // indipendente
    assertEquals(N(1), s.Size());
  }

  @ParameterizedTest(name = "{0}") @MethodSource("stacks")
  void stackGrowAndShrink(String name, StackFactory f) {
    Stack<?, Integer> s = f.New();
    for (int i = 0; i < 5000; i++) s.Push(i);
    assertEquals(N(5000), s.Size());
    for (int i = 4999; i >= 100; i--) assertEquals(i, s.TopNPop());
    assertEquals(N(100), s.Size());
    assertEquals(99, s.Top());
    for (int i = 0; i < 100; i++) s.Pop();
    assertTrue(s.IsEmpty());
    s.Push(1);
    assertEquals(N(1), s.Size());
  }

  @ParameterizedTest(name = "{0}") @MethodSource("stacks")
  void stackRandomizedAgainstDeque(String name, StackFactory f) {
    Stack<?, Integer> s = f.New();
    Deque<Integer> model = new ArrayDeque<>();
    Random rnd = new Random(8);
    for (int step = 0; step < 4000; step++) {
      switch (rnd.nextInt(6)) {
        case 0, 1, 2 -> { s.Push(step); model.push(step); }
        case 3 -> { s.Pop(); if (!model.isEmpty()) model.pop(); }
        case 4 -> { assertEquals(model.peek(), s.TopNPop()); model.poll(); } // null se vuota
        default -> {
          assertEquals(model.peek(), s.Top());
          if (!model.isEmpty()) { Integer old = model.pop(); model.push(-step); assertEquals(old, s.TopNSwap(-step)); }
        }
      }
      assertEquals(model.size(), s.Size().ToLong(), "step " + step);
    }
  }

  /* ---------------------------------------------------------------------- */
  /* Code                                                                    */
  /* ---------------------------------------------------------------------- */

  @ParameterizedTest(name = "{0}") @MethodSource("queues")
  void queueEmpty(String name, QueueFactory f) {
    Queue<?, Integer> q = f.New();
    assertTrue(q.IsEmpty());
    assertEquals(Natural.ZERO, q.Size());
    assertNull(q.Head());
    assertNull(q.HeadNDequeue());
    q.Dequeue(); // innocuo
    assertTrue(q.IsEmpty());
    q.Clear();
    assertTrue(q.Empty().IsEmpty());
  }

  @ParameterizedTest(name = "{0}") @MethodSource("queues")
  void queueFifo(String name, QueueFactory f) {
    Queue<?, Integer> q = f.New();
    for (int i = 1; i <= 5; i++) {
      q.Enqueue(i);
      assertEquals(1, q.Head()); // la testa non cambia finche non si rimuove
      assertEquals(N(i), q.Size());
    }
    for (int i = 1; i <= 5; i++) {
      assertEquals(i, q.Head());
      assertEquals(i, q.HeadNDequeue());
      assertEquals(N(5 - i), q.Size());
    }
    assertTrue(q.IsEmpty());
    assertNull(q.Head());
  }

  @ParameterizedTest(name = "{0}") @MethodSource("queues")
  void queueDequeueAndHead(String name, QueueFactory f) {
    Queue<?, Integer> q = f.New();
    q.Enqueue(1); q.Enqueue(2); q.Enqueue(3);
    q.Dequeue();
    assertEquals(2, q.Head());
    assertEquals(2, q.Head());
    q.Dequeue();
    q.Dequeue();
    assertTrue(q.IsEmpty());
    q.Enqueue(9); // riutilizzabile dopo averla svuotata
    assertEquals(9, q.Head());
    assertEquals(N(1), q.Size());
  }

  @ParameterizedTest(name = "{0}") @MethodSource("queues")
  void queueInterleavedOperations(String name, QueueFactory f) {
    Queue<?, Integer> q = f.New();
    q.Enqueue(1); q.Enqueue(2);
    assertEquals(1, q.HeadNDequeue());
    q.Enqueue(3);
    assertEquals(2, q.HeadNDequeue());
    assertEquals(3, q.HeadNDequeue());
    assertNull(q.HeadNDequeue());
    q.Enqueue(4);
    assertEquals(4, q.Head());
  }

  @ParameterizedTest(name = "{0}") @MethodSource("queues")
  void queueNullElements(String name, QueueFactory f) {
    Queue<?, Integer> q = f.New();
    q.Enqueue(null);
    q.Enqueue(1);
    assertEquals(N(2), q.Size());
    assertNull(q.Head());
    assertNull(q.HeadNDequeue());
    assertEquals(1, q.Head());
    assertEquals(N(1), q.Size());
  }

  @ParameterizedTest(name = "{0}") @MethodSource("queues")
  void queueInsertAndBulk(String name, QueueFactory f) {
    Queue<?, Integer> q = f.New();
    assertTrue(q.Insert(1)); // Insert == Enqueue
    assertTrue(q.Insert(2));
    assertEquals(1, q.Head());
    assertTrue(q.AllInserted(vl(3, 4, 5)));
    assertEquals(N(5), q.Size());
    assertEquals(1, q.Head());
    assertTrue(q.AllInserted(vl()));
    assertFalse(q.SomeInserted(vl()));
    assertFalse(q.AllInserted(null));
    assertTrue(q.SomeInserted(vl(6, 7)));
    List<Integer> out = new ArrayList<>();
    while (!q.IsEmpty()) out.add(q.HeadNDequeue());
    assertEquals(range(1, 8), out);
  }

  @ParameterizedTest(name = "{0}") @MethodSource("queues")
  void queueCopyConstructor(String name, QueueFactory f) {
    Queue<?, Integer> q = f.From(vl(1, 2, 3)); // il primo elemento e la testa
    assertEquals(N(3), q.Size());
    assertEquals(1, q.HeadNDequeue());
    assertEquals(2, q.HeadNDequeue());
    assertEquals(3, q.HeadNDequeue());
    assertTrue(q.IsEmpty());
    assertTrue(f.From(vl()).IsEmpty());
    assertThrows(NullPointerException.class, () -> f.From(null));
  }

  @Test
  void queueConstructorsRejectNull() {
    assertThrows(NullPointerException.class, () -> new WQueue<Integer>((VList<Integer>) null));
    assertThrows(NullPointerException.class, () -> new WQueue<Integer>(new VList<Integer>(), null));
    assertThrows(NullPointerException.class, () -> new WQueue<Integer>((VList<Integer>) null, vl(1)));
  }

  @Test
  void queueWrapsExistingList() {
    VList<Integer> backing = vl(10, 20, 30);
    WQueue<Integer> q = new WQueue<>(backing);
    assertEquals(N(3), q.Size());
    assertEquals(10, q.Head());
    q.Enqueue(40);
    assertEquals(40, backing.GetLast()); // lista condivisa
    assertEquals(10, q.HeadNDequeue());
    assertEquals(list(20, 30, 40), toList(backing));
  }

  @ParameterizedTest(name = "{0}") @MethodSource("queues")
  void queueClearAndReuse(String name, QueueFactory f) {
    Queue<?, Integer> q = f.New();
    for (int i = 0; i < 100; i++) q.Enqueue(i);
    q.Clear();
    assertTrue(q.IsEmpty());
    assertEquals(Natural.ZERO, q.Size());
    assertNull(q.Head());
    q.Enqueue(1);
    assertEquals(1, q.Head());
    Queue<?, Integer> e = q.Empty();
    assertNotSame(q, e);
    assertTrue(e.IsEmpty());
    e.Enqueue(2);
    assertEquals(1, q.Head());
    assertEquals(N(1), q.Size());
  }

  @ParameterizedTest(name = "{0}") @MethodSource("queues")
  void queueGrowAndShrink(String name, QueueFactory f) {
    Queue<?, Integer> q = f.New();
    for (int i = 0; i < 5000; i++) q.Enqueue(i);
    assertEquals(N(5000), q.Size());
    for (int i = 0; i < 4900; i++) assertEquals(i, q.HeadNDequeue());
    assertEquals(N(100), q.Size());
    assertEquals(4900, q.Head());
    // la coda continua a funzionare dopo la riduzione
    for (int i = 5000; i < 5100; i++) q.Enqueue(i);
    for (int i = 4900; i < 5100; i++) assertEquals(i, q.HeadNDequeue());
    assertTrue(q.IsEmpty());
  }

  @ParameterizedTest(name = "{0}") @MethodSource("queues")
  void queueRandomizedAgainstDeque(String name, QueueFactory f) {
    Queue<?, Integer> q = f.New();
    Deque<Integer> model = new ArrayDeque<>();
    Random rnd = new Random(9);
    for (int step = 0; step < 4000; step++) {
      switch (rnd.nextInt(6)) {
        case 0, 1, 2 -> { q.Enqueue(step); model.addLast(step); }
        case 3 -> { q.Dequeue(); model.pollFirst(); }
        case 4 -> assertEquals(model.pollFirst(), q.HeadNDequeue());
        default -> assertEquals(model.peekFirst(), q.Head());
      }
      assertEquals(model.size(), q.Size().ToLong(), "step " + step);
    }
  }

  /* ---------------------------------------------------------------------- */
  /* Altri tipi generici                                                     */
  /* ---------------------------------------------------------------------- */

  @Test
  void stringStackAndQueue() {
    WStack<String> s = new WStack<>();
    WQueue<String> q = new WQueue<>(new LLList<String>());
    for (String w : new String[] {"one", "two", "three"}) { s.Push(w); q.Enqueue(w); }
    assertEquals("three", s.Top());
    assertEquals("one", q.Head());
    assertEquals("three", s.TopNPop());
    assertEquals("one", q.HeadNDequeue());
    assertEquals("two", s.TopNSwap("TWO"));
    assertEquals("TWO", s.Top());
    assertEquals("two", q.Head());
    assertEquals(N(2), s.Size());
    assertEquals(N(2), q.Size());
    s.Clear();
    q.Clear();
    assertNull(s.Top());
    assertNull(q.Head());
    s.Push("x");
    q.Enqueue("y");
    assertEquals("x", s.TopNPop());
    assertEquals("y", q.HeadNDequeue());
    assertTrue(s.IsEmpty() && q.IsEmpty());
  }

  @Test
  void stackAndQueueBuiltFromEachOtherReverseOrder() {
    WQueue<Integer> q = new WQueue<>();
    for (int i = 1; i <= 4; i++) q.Enqueue(i);
    // si travasa la coda in una pila: l ordine si inverte
    WStack<Integer> s = new WStack<>();
    while (!q.IsEmpty()) s.Push(q.HeadNDequeue());
    List<Integer> out = new ArrayList<>();
    while (!s.IsEmpty()) out.add(s.TopNPop());
    assertEquals(list(4, 3, 2, 1), out);
  }
}

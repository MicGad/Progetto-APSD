package zapsdtest.simpletest.apsd.classes.containers.deqs;

import apsd.classes.containers.deqs.WQueue;
import apsd.classes.containers.deqs.WStack;
import apsd.classes.utilities.Natural;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Edge Case Tests")
public class EdgeCaseTest {

  @Nested
  @DisplayName("Stack")
  public class StackEdgeCases {

    @Test
    public void TestEmptyStackAccessors() {
      WStack<Long> stk = new WStack<>();
      assertNull(stk.Top());
      assertNull(stk.TopNPop());
      stk.Pop();
      assertTrue(stk.IsEmpty());
    }

    @Test
    public void TestSwapTopOnEmptyInserts() {
      WStack<Long> stk = new WStack<>();
      stk.SwapTop(7L);
      assertEquals(7L, stk.Top());
      assertEquals(Natural.ONE, stk.Size());
    }

    @Test
    public void TestTopNSwapOnEmptyReturnsNull() {
      WStack<Long> stk = new WStack<>();
      assertNull(stk.TopNSwap(3L));
      assertEquals(3L, stk.Top());
    }

    @Test
    public void TestLifoOrderAndClear() {
      WStack<Long> stk = new WStack<>();
      for (long i = 0; i < 100; i++) stk.Push(i);
      assertEquals(99L, stk.TopNPop());
      assertEquals(98L, stk.TopNSwap(5L));
      assertEquals(5L, stk.Top());
      stk.Clear();
      assertTrue(stk.IsEmpty());
      assertEquals(Natural.ZERO, stk.Size());
    }
  }

  @Nested
  @DisplayName("Queue")
  public class QueueEdgeCases {

    @Test
    public void TestEmptyQueueAccessors() {
      WQueue<Long> que = new WQueue<>();
      assertNull(que.Head());
      assertNull(que.HeadNDequeue());
      que.Dequeue();
      assertTrue(que.IsEmpty());
    }

    @Test
    public void TestFifoOrder() {
      WQueue<Long> que = new WQueue<>();
      for (long i = 0; i < 100; i++) que.Enqueue(i);
      for (long i = 0; i < 100; i++) assertEquals(i, que.HeadNDequeue());
      assertTrue(que.IsEmpty());
    }

    @Test
    public void TestReuseAfterEmptyAndClear() {
      WQueue<Long> que = new WQueue<>();
      que.Enqueue(1L);
      que.Dequeue();
      que.Enqueue(2L);
      assertEquals(2L, que.Head());
      que.Clear();
      assertEquals(Natural.ZERO, que.Size());
      que.Enqueue(9L);
      assertEquals(9L, que.Head());
    }

    @Test
    public void TestEmptyReturnsNewEmptyQueue() {
      WQueue<Long> que = new WQueue<>();
      que.Enqueue(1L);
      WQueue<Long> other = que.Empty();
      assertTrue(other.IsEmpty());
      assertFalse(que.IsEmpty());
    }
  }

  @Nested
  @DisplayName("Natural")
  public class NaturalEdgeCases {

    @Test
    public void TestNegativeRejected() {
      assertThrows(IllegalArgumentException.class, () -> new Natural(-1L));
    }

    @Test
    public void TestDecrementZeroUnderflow() {
      assertThrows(ArithmeticException.class, () -> Natural.ZERO.Decrement());
    }

    @Test
    public void TestIncrementMaxOverflow() {
      assertThrows(ArithmeticException.class, () -> Natural.Of(Long.MAX_VALUE).Increment());
    }

    @Test
    public void TestNullCopyConstructor() {
      assertThrows(NullPointerException.class, () -> new Natural((Natural) null));
    }
  }
}

package apsd.interfaces.containers.base;

import apsd.classes.utilities.Natural;

/** Interface: Container con size logica distinta dalla capacita'. */
public interface ResizableContainer<T extends ResizableContainer<T>> extends ReallocableContainer<T> {

  double THRESHOLD_FACTOR = 2.0; // Must be strictly greater than 1.

  default void Expand() {
    Expand(Natural.ONE);
  }

  void Expand(Natural num);

  default void Reduce() {
    Reduce(Natural.ONE);
  }

  void Reduce(Natural num);

  @Override
  Natural Size();

  /** Nuova capacita': (size corrente + dim) * GROW_FACTOR. */
  @Override
  default void Grow(Natural dim) {
    Realloc(Natural.Of((long) ((Size().ToLong() + dim.ToLong()) * GROW_FACTOR)));
  }

  /** Nuova capacita': capacita' corrente / SHRINK_FACTOR, ma mai sotto la size. */
  @Override
  default void Shrink() {
    long newcap = (long) (Capacity().ToLong() / SHRINK_FACTOR);
    Realloc(Natural.Of(Math.max(newcap, Size().ToLong())));
  }

}

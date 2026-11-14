package apsd.interfaces.containers.base;

import apsd.classes.utilities.Natural;
import apsd.interfaces.traits.Reallocable;

/** Interface: Container con capacita' riallocabile. */
public interface ReallocableContainer<T extends ReallocableContainer<T>> extends ClearableContainer<T>, Reallocable {

  double GROW_FACTOR = 2.0; // Must be strictly greater than 1.
  double SHRINK_FACTOR = 2.0; // Must be strictly greater than 1.

  Natural Capacity();

  default void Grow() {
    Grow(Natural.ONE);
  }

  /** Nuova capacita': (capacita' corrente + dim) * GROW_FACTOR. */
  default void Grow(Natural dim) {
    Realloc(Natural.Of((long) ((Capacity().ToLong() + dim.ToLong()) * GROW_FACTOR)));
  }

  /** Nuova capacita': capacita' corrente / SHRINK_FACTOR. */
  default void Shrink() {
    Realloc(Natural.Of((long) (Capacity().ToLong() / SHRINK_FACTOR)));
  }

  @Override
  default Natural Size() {
    return Capacity();
  }

  @Override
  default void Clear() {
    Realloc(Natural.ZERO);
  }

}

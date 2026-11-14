package apsd.interfaces.containers.base;

import apsd.interfaces.containers.iterators.BackwardIterator;
import apsd.interfaces.containers.iterators.ForwardIterator;
import apsd.interfaces.traits.Predicate;

/** Interface: Container iterabile. */
public interface IterableContainer<T extends IterableContainer<T, Data>, Data> extends TraversableContainer<T, Data> {

  ForwardIterator<Data> FIterator();

  BackwardIterator<Data> BIterator();

  @Override
  default boolean TraverseForward(Predicate<Data> pred) {
    return FIterator().ForEachForward(pred);
  }

  @Override
  default boolean TraverseBackward(Predicate<Data> pred) {
    return BIterator().ForEachBackward(pred);
  }

}

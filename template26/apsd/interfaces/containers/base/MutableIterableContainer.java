package apsd.interfaces.containers.base;

import apsd.interfaces.containers.iterators.MutableBackwardIterator;
import apsd.interfaces.containers.iterators.MutableForwardIterator;

/** Interface: Container iterabile con iteratori mutabili. */
public interface MutableIterableContainer<T extends MutableIterableContainer<T, Data>, Data> extends IterableContainer<T, Data> {

  @Override
  MutableForwardIterator<Data> FIterator();

  @Override
  MutableBackwardIterator<Data> BIterator();

}

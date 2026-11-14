package apsd.interfaces.containers.base.ordered;

import apsd.interfaces.containers.base.IterableContainer;

/** Interface: OrderedTraversableContainer & IterableContainer. */
public interface OrderedIterableContainer<T extends OrderedIterableContainer<T, Data>, Data extends Comparable<? super Data>> extends OrderedTraversableContainer<T, Data>, IterableContainer<T, Data> {
}

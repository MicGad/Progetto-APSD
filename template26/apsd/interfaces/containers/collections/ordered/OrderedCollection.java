package apsd.interfaces.containers.collections.ordered;

import apsd.interfaces.containers.base.ordered.OrderedIterableContainer;
import apsd.interfaces.containers.base.ordered.OrderedRemovableContainer;
import apsd.interfaces.containers.collections.Collection;

/** Interface: Collection con dati ordinati. */
public interface OrderedCollection<T extends OrderedCollection<T, Data>, Data extends Comparable<? super Data>> extends Collection<T, Data>, OrderedRemovableContainer<T, Data>, OrderedIterableContainer<T, Data> {
}

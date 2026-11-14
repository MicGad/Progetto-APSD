package apsd.interfaces.containers.collections.ordered;

import apsd.interfaces.containers.collections.List;

/** Interface: List con dati ordinati. */
public interface OrderedList<T extends OrderedList<T, Data>, Data extends Comparable<? super Data>> extends List<T, Data>, OrderedChain<T, Data> {
}

package apsd.interfaces.containers.base.ordered;

/** Interface: OrderedTraversableContainer con struttura dei dati ordinata. */
public interface SortedTraversableContainer<T extends SortedTraversableContainer<T, Data>, Data extends Comparable<? super Data>> extends OrderedTraversableContainer<T, Data> {
}

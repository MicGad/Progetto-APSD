package apsd.interfaces.containers.base.ordered;

/** Interface: SortedTraversableContainer & OrderedIterableContainer. */
public interface SortedIterableContainer<T extends SortedIterableContainer<T, Data>, Data extends Comparable<? super Data>> extends SortedTraversableContainer<T, Data>, OrderedIterableContainer<T, Data> {
}

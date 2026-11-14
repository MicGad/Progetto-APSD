package apsd.interfaces.containers.collections.ordered;

import apsd.interfaces.containers.collections.Chain;
import apsd.interfaces.containers.sequences.ordered.OrderedRemovableAtSequence;

/** Interface: Chain con dati ordinati. */
public interface OrderedChain<T extends OrderedChain<T, Data>, Data extends Comparable<? super Data>> extends Chain<T, Data>, OrderedCollection<T, Data>, OrderedRemovableAtSequence<T, Data> {
}

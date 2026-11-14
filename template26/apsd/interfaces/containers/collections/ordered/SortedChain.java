package apsd.interfaces.containers.collections.ordered;

import java.util.ArrayList;

import apsd.classes.utilities.Natural;
import apsd.interfaces.containers.iterators.ForwardIterator;
import apsd.interfaces.containers.sequences.ordered.SortedRemovableAtSequence;

/** Interface: OrderedChain con dati mantenuti in ordine crescente. */
public interface SortedChain<T extends SortedChain<T, Data>, Data extends Comparable<? super Data>> extends OrderedChain<T, Data>, SortedRemovableAtSequence<T, Data> {

  /* ************************************************************************ */
  /* Specific member functions of SortedChain                                 */
  /* ************************************************************************ */

  /** Intersezione sul posto per fusione di due catene ordinate. */
  default void Intersection(SortedChain<?, Data> chn) {
    if (chn == null || chn == this) return;
    ArrayList<Data> kept = new ArrayList<>();
    ForwardIterator<Data> fst = FIterator();
    ForwardIterator<Data> snd = chn.FIterator();
    while (fst.IsValid() && snd.IsValid()) {
      int cmp = fst.GetCurrent().compareTo(snd.GetCurrent());
      if (cmp == 0) {
        kept.add(fst.DataNNext());
        snd.Next();
      } else if (cmp < 0) {
        fst.Next();
      } else {
        snd.Next();
      }
    }
    Clear();
    for (Data dat : kept) Insert(dat);
  }

  /* ************************************************************************ */
  /* Override specific member functions from Sequence                         */
  /* ************************************************************************ */

  @Override
  default Natural Search(Data dat) {
    return SortedRemovableAtSequence.super.Search(dat);
  }

}

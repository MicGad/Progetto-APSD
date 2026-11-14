package apsd.interfaces.containers.base.ordered;

import apsd.interfaces.containers.base.MembershipContainer;

/** Interface: MembershipContainer con dati ordinati. */
public interface OrderedContainer<T extends OrderedContainer<T, Data>, Data extends Comparable<? super Data>> extends MembershipContainer<T, Data> {

  /* ************************************************************************ */
  /* Specific member functions of OrderedContainer                            */
  /* ************************************************************************ */

  /** Dato minimo, oppure null se il contenitore e' vuoto. */
  Data Min();

  /** Dato massimo, oppure null se il contenitore e' vuoto. */
  Data Max();

  /** Il piu' grande dato strettamente minore di dat, oppure null se assente. */
  Data Predecessor(Data dat);

  /** Il piu' piccolo dato strettamente maggiore di dat, oppure null se assente. */
  Data Successor(Data dat);

}

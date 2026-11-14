package apsd.interfaces.containers.iterators;

/** Interface: Iteratore mutabile. */
public interface MutableIterator<Data> extends Iterator<Data> {

  /* ************************************************************************ */
  /* Specific member functions of MutableIterator                             */
  /* ************************************************************************ */

  void SetCurrent(Data dat);

}

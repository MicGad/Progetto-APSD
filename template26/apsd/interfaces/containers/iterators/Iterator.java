package apsd.interfaces.containers.iterators;

/** Interface: Iteratore arbitrario. */
public interface Iterator<Data> {

  /* ************************************************************************ */
  /* Specific member functions of Iterator                                    */
  /* ************************************************************************ */

  boolean IsValid();

  void Reset();

  Data GetCurrent();

}

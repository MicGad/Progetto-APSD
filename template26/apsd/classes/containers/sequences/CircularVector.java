package apsd.classes.containers.sequences;

import apsd.classes.containers.sequences.abstractbases.CircularVectorBase;
import apsd.classes.utilities.Natural;
import apsd.interfaces.containers.base.TraversableContainer;

/** Object: Concrete (static) circular vector implementation. */
public class CircularVector<Data> extends CircularVectorBase<CircularVector<Data>, Data> {

  public CircularVector() {
    super();
  }

  public CircularVector(Natural inisize) {
    super(inisize);
  }

  public CircularVector(TraversableContainer<?, Data> con) {
    super(con);
  }

  @Override
  public CircularVector<Data> Empty() {
    return new CircularVector<>();
  }

}

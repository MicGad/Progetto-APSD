package apsd.classes.containers.sequences;

import apsd.classes.containers.sequences.abstractbases.LinearVectorBase;
import apsd.classes.utilities.Natural;
import apsd.interfaces.containers.base.TraversableContainer;

/** Object: Concrete (static) linear vector implementation. */
public class Vector<Data> extends LinearVectorBase<Vector<Data>, Data> {

  public Vector() {
    super();
  }

  public Vector(Natural inisize) {
    super(inisize);
  }

  public Vector(TraversableContainer<?, Data> con) {
    super(con);
  }

  @Override
  public Vector<Data> Empty() {
    return new Vector<>();
  }

}

package apsd.classes.containers.sequences;

import apsd.classes.containers.sequences.abstractbases.DynCircularVectorBase;
import apsd.classes.utilities.Natural;
import apsd.interfaces.containers.base.TraversableContainer;

/** Object: Concrete dynamic circular vector implementation. */
public class DynCircularVector<Data> extends DynCircularVectorBase<DynCircularVector<Data>, Data> {

  public DynCircularVector() {
    super();
  }

  public DynCircularVector(Natural inisize) {
    super(inisize);
  }

  public DynCircularVector(TraversableContainer<?, Data> con) {
    super(con);
  }

  @Override
  public DynCircularVector<Data> Empty() {
    return new DynCircularVector<>();
  }

}

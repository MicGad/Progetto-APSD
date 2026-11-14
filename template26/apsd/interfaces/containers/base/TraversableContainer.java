package apsd.interfaces.containers.base;

import java.util.Objects;

import apsd.classes.utilities.Box;
import apsd.classes.utilities.MutableNatural;
import apsd.classes.utilities.Natural;
import apsd.interfaces.traits.Accumulator;
import apsd.interfaces.traits.Predicate;

/** Interface: Container attraversabile. */
public interface TraversableContainer<T extends TraversableContainer<T, Data>, Data> extends MembershipContainer<T, Data> {

  boolean TraverseForward(Predicate<Data> pred);

  boolean TraverseBackward(Predicate<Data> pred);

  default <Acc> Acc FoldForward(Accumulator<Data, Acc> fun, Acc ini) {
    if (fun == null) return ini;
    Box<Acc> acc = new Box<>(ini);
    TraverseForward(dat -> {
      acc.Set(fun.Apply(dat, acc.Get()));
      return false;
    });
    return acc.Get();
  }

  default <Acc> Acc FoldBackward(Accumulator<Data, Acc> fun, Acc ini) {
    if (fun == null) return ini;
    Box<Acc> acc = new Box<>(ini);
    TraverseBackward(dat -> {
      acc.Set(fun.Apply(dat, acc.Get()));
      return false;
    });
    return acc.Get();
  }

  default Natural Multiplicity(Data dat) {
    MutableNatural mul = new MutableNatural();
    TraverseForward(cur -> {
      if (Objects.equals(cur, dat)) mul.Increment();
      return false;
    });
    return mul.ToNatural();
  }

  default boolean IsEqual(TraversableContainer<?, Data> con) {
    if (con == null) return false;
    if (!Size().equals(con.Size())) return false;
    return !TraverseForward(dat -> !Multiplicity(dat).equals(con.Multiplicity(dat)));
  }

  @Override
  default Natural Size() {
    MutableNatural size = new MutableNatural();
    TraverseForward(dat -> {
      size.Increment();
      return false;
    });
    return size.ToNatural();
  }

  @Override
  default boolean Exists(Data dat) {
    return TraverseForward(cur -> Objects.equals(cur, dat));
  }

}

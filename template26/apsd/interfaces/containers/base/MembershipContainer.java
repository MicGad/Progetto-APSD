package apsd.interfaces.containers.base;

/** Interface: Container con supporto alla verifica di appartenenza. */
public interface MembershipContainer<T extends MembershipContainer<T, Data>, Data> extends Container<T> {

  boolean Exists(Data dat);

}

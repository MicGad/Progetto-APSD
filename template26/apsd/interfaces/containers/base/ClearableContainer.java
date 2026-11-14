package apsd.interfaces.containers.base;

import apsd.interfaces.traits.Clearable;

/** Interface: Container che puo' essere svuotato. */
public interface ClearableContainer<T extends ClearableContainer<T>> extends Container<T>, Clearable {

}

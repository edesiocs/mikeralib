package mikera.persistent;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import mikera.persistent.impl.ArraySet;
import mikera.persistent.impl.SingletonSet;
import mikera.util.TODOException;
import mikera.util.Tools;
import mikera.util.emptyobjects.NullSet;

public class SetFactory {
	// TODO: convert to hash implementations
	
	@SuppressWarnings("unchecked")
	public static <T> PersistentSet<T> create(T value) {
		return SingletonSet.create(value);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> PersistentSet<T> create() {
		return (PersistentSet<T>) NullSet.INSTANCE;
	}
	
	public static <T> PersistentSet<T> createFrom(Set<T> source) {
		return PersistentHashSet.createFromSet(source);
	}
	
	public static <T> PersistentSet<T> createFrom(Iterator<T> source) {
		return createFrom(Tools.buildHashSet(source));
	}
	
	public static <T> PersistentSet<T> create(PersistentSet<T> source) {
		return PersistentHashSet.createFromSet(source);
	}
	
	public static <T> PersistentSet<T> createFrom(Collection<T> value) {
		return createFrom(value.iterator());
	}
	
	public static <T> PersistentSet<T> createFrom(T[] source) {
		return ArraySet.createFromArray(source);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> PersistentSet<T> concat(PersistentSet<T> a, T value) {
		if (a.contains(value)) return a;
		if (a.size()==0) return SingletonSet.create(value);
		return PersistentHashSet.createFromSet(a).include(value);
	}
	
}
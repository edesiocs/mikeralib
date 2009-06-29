package mikera.util.emptyobjects;

import java.util.*;

public class NullSet<T> implements Collection<T>, Set<T> {

	public boolean add(T e) {
		throw new Error("Cannot add to NullSet");
	}

	public boolean addAll(Collection<? extends T> c) {
		return false;
	}

	public void clear() {

	}

	public boolean contains(Object o) {
		return false;
	}

	public boolean containsAll(Collection<?> c) {
		return false;
	}

	public boolean isEmpty() {
		return true;
	}

	public Iterator<T> iterator() {
		return new NullIterator<T>();
	}

	public boolean remove(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean removeAll(Collection<?> c) {
		return false;
	}

	public boolean retainAll(Collection<?> c) {
		return false;
	}

	public int size() {
		return 0;
	}

	public Object[] toArray() {
		return NullArrays.NULL_OBJECTS;
	}

	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] a) {
		// TODO Auto-generated method stub
		return (T[])toArray();
	}


}

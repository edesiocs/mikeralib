package mikera.util;

import java.util.*;

/**
 * Circular Buffer implementation
 * 
 * Supports nulls, variable size buffer, fast random access reads
 * 
 * @author Mike
 *
 * @param <V> Type of object contained in buffer
 */
public class CircularBuffer<V> extends AbstractQueue<V> {
	// ArrayList size is always less than or equal to maxSize
	// ArrayList size equal to maxSize if buffer is full, or if there is any wraparound
	private int maxSize;
	private ArrayList<V> values=new ArrayList<V>();
	
	private int end=0; // end index
	private int count=0;
	
	/**
	 * Construct a new, empty circular buffer
	 * 
	 * @param n Capacity of buffer
	 */
	public CircularBuffer(int n) {
		setMaxSize(n);
	}
	
	public CircularBuffer() {
		setMaxSize(10);
	}
	
	public int getMaxSize() {
		return maxSize;
	}
	
	public int size() {
		return getCount();
	}
	
	public Iterator<V> iterator() {
		return new Iterator<V>() {
			int pos=0;
			
			public boolean hasNext() {
				return (pos<count);
			}
			
			public V next() {
				V value=getLocal(pos);
				pos++;
				return value;
			}
			
			public void remove() {
				if (pos<=0) return; // not yet called
				CircularBuffer.this.remove(get(pos-1));
			}
		};
	}
	
	/**
	 * Resize the circular buffer. Any items at the end of the buffer are discarded.
	 * 
	 * @param newSize New capacity value for buffer
	 */
	public void setMaxSize(int newSize) {
		int vs=values.size();
		if (newSize>=vs) {
			// extend
			// shifting to add nulls if needed (i.e. if there is wrap around)
			// otherwise no need to resize - will grow automatically
			int shift=Math.max(0, count-end);
			if (shift>0) {
				int add=newSize-vs; // number of nulls to add
				values.ensureCapacity(newSize);
				for (int i=0; i<add; i++) {
					values.add(null);
				}
				
				for (int i=newSize-1; i>newSize-1-shift; i--) {
					values.set(i,values.get(i-add));
					values.set(i-add,null);
				}
			}
			
			maxSize=newSize;
			return;
		} else {
			// shrink
			int wrap=Math.max(0,count-end);
			int keepWrap=Math.min(wrap, newSize-end); // number of items rolling around at end of values that we want to keep
			
			if (keepWrap>=0) {
				// we know newSize>=end
				// move wrapped items to fill up to newSize
				for (int i=0; i<keepWrap; i++) {
					values.set(newSize-keepWrap+i,values.get(vs-keepWrap+i));
				}	
			} else {
				// we know either end>=count or end>newSize
				// either way, we just want the items running up to end
				// pull items back to start of array, filling up to newSize = new end position
				for (int i=0; i<newSize; i++) {
					values.set(i,values.get(end-newSize+i));
				}
				end=newSize;
			}
			
			// shorten array by removing end values
			for (int i=vs-1; i>=newSize; i--) {
				values.remove(i);
			}
			
			maxSize=newSize;
			count=Math.min(count,newSize); // number removed
		}
	}
	
	/**
	 * Remove a range of items from the buffer
	 * 
	 * @param startIndex
	 * @param number
	 * @return
	 */
	public int removeRange(int startIndex, int number) {
		if (startIndex<0) throw new ArrayIndexOutOfBoundsException("Negative index: "+startIndex);
		if (startIndex>=count) return 0;
		if (startIndex+number>count) number=count-startIndex;
		if (number<=0) return 0;
		
		int shiftAmount=count-startIndex-number;
		for (int i=0; i<shiftAmount; i++) {
			values.set(positionIndex(startIndex+i), values.get(positionIndex(count-shiftAmount+i)));
		}
		// clear out remainder of buffer
		for (int i=startIndex+shiftAmount; i<count; i++) {
			values.set(positionIndex(i), null);
		}
		
		count-=number;
		return number;
	}
	
	public V remove(int index) {
		if (index<0) throw new ArrayIndexOutOfBoundsException("Negative index: "+index);
		if (index>=count) throw new ArrayIndexOutOfBoundsException("Out of bounds: "+index);
		V value=getLocal(index);
		removeRange(index,1);
		return value;
	}
	
	
	
	/**
	 * Adds the value only if the queue has spare capacity
	 */
	public boolean offer(V value) {
		if (count<maxSize) {
			return add(value);
		} else {
			return false;
		}
	}
	
	/**
	 * Unconditionally adds a value to the buffer
	 * If full, first added item will be deleted to make space
	 */
	public boolean add(V value) {
		if (maxSize<=0) return false;
		
		int vs=values.size();
		if (vs<maxSize) {
			// append
			values.add(value);
			end++;
			count++;
		} else {
			// cycle and replace old elements
			if (end==vs) end=0;
			values.set(end,value);
			end++;
			if (count<maxSize) count++;
			if (end>=vs) end=0;
		}
		return true;
	}
	
	public void clear() {
		count=0;
	}
	
	/**
	 * Remove end value (i.e. first added)
	 * @return true if value removes, false if buffer is empty
	 */
	public boolean tryRemoveEnd() {
		if (count>0) {
			removeFirstAdded();
			return true;
		} else {
			return false;
		}
	}
	
	public V remove() {
		if (count==0) throw new NoSuchElementException("Empty CircularBuffer in CircularBufer.remove()");
		return removeFirstAdded();
	}
	
	public V poll() {
		return removeFirstAdded();
	}
	
	public V removeFirstAdded() {
		if (count>0) {
			V value=get(count-1);
			values.set(positionIndex(count-1),null);
			count-=1;
			return value;
		} else {
			return null;
		}
	}
	
	public V removeLastAdded() {
		if (count>0) {
			int i=positionIndex(0);
			V value=values.get(i);
			values.set(i,null);
			count-=1;
			end-=1;
			
			// handle wrap - this is OK, since values.size()=maxSize when there is any wrap
			if (end<0) end+=maxSize;
			return value;
		} else {
			return null;
		}
	}
	
	/**
	 * Retrieves but does not remove the first added (FI) element of the buffer
	 */
	public V element() {
		if (count<=0) throw new NoSuchElementException("Empty CircularBuffer in CircularBufer.element()");
		return values.get(firstAddedIndex());
	}
	
	/**
	 * Returns the first added (FI) element of the buffer, null if empty
	 */
	public V peek() {
		if (count<=0) return null;
		return values.get(firstAddedIndex());
	}
	
	private int lastAddedIndex() {
		if (end>0) return end-1;
		return values.size()-1; // must be last index, since end=0, or returns -1 if buffer empty
	}
	
	private int firstAddedIndex() {
		if (count==0) return -1;
		return positionIndex(count-1); 
	}
	
	private int positionIndex(int n) {
		int i=end-n-1;
		if (i<0) i+=maxSize;
		return i;
	}
	
	public V getLastAdded() {
		int i=lastAddedIndex();
		if (i<0) return null;
		return values.get(i);
	}
	
	public int getCount() {
		return count;
	}
	
	/**
	 * Get an item from the circular buffer
	 * 
	 * @param n Index of item in circular buffer (0 = most recently added, getCount()-1 = last item)
	 * @return Value at position n from front of buffer, null if beyond end of buffer
	 */
	public V get(int n) {
		if (n>=count) return null;
		if (n<0) throw new ArrayIndexOutOfBoundsException("Negative index in CircularBuffer.get(int) not allowed");
		return getLocal(n);
	}
	
	// assumes bounds already checked
	private V getLocal(int n) {
		int i=positionIndex(n);
		return values.get(i);		
	}
}

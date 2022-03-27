package ks.util.common;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class IterableNodeList implements Iterable<Node> {
    private final NodeList list;

    public IterableNodeList(NodeList list) {
        this.list = list;
    }

    public Iterator<Node> iterator() {
        return new MyIterator();
    }

    private class MyIterator implements Iterator<Node> {
        private int idx = 0;

        public boolean hasNext() {
            return idx < list.getLength();
        }

        public Node next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return list.item(idx++);
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
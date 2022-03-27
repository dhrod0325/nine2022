package ks.util.common;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class IterableElementList implements Iterable<Element> {
    private final IterableNodeList list;

    public IterableElementList(NodeList list) {
        this.list = new IterableNodeList(list);
    }

    public Iterator<Element> iterator() {
        return new MyIterator(list.iterator());
    }

    private static class MyIterator implements Iterator<Element> {
        private final Iterator<Node> itr;

        private Element next = null;

        public MyIterator(Iterator<Node> itr) {
            this.itr = itr;
            updateNextElement();
        }

        private void updateNextElement() {
            while (itr.hasNext()) {
                Node node = itr.next();

                if (node instanceof Element) {
                    next = (Element) node;
                    return;
                }
            }

            next = null;
        }

        public boolean hasNext() {
            return next != null;
        }

        public Element next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            Element result = next;
            updateNextElement();
            return result;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

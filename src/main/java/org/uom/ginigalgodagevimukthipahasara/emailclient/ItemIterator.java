package org.uom.ginigalgodagevimukthipahasara.emailclient;

import java.util.ArrayList;

class ItemIterator<E> implements Iterator<E> {
    private final ArrayList<E> items;

    private int cursorIndex = 0;

    ItemIterator(ArrayList<E> items) {
        this.items = items;
    }

    @Override
    public boolean hasNext() {
        return cursorIndex < items.size();
    }

    @Override
    public E next() {
        E item = items.get(cursorIndex);
        cursorIndex++;
        return item;
    }

    @Override
    public void reset() {
        cursorIndex = 0;
    }
}

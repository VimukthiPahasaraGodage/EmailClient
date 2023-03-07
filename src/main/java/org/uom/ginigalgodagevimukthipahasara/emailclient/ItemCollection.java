package org.uom.ginigalgodagevimukthipahasara.emailclient;

import java.util.ArrayList;

class ItemCollection<E> implements Collection<E> {

    private ArrayList<E> items;

    @Override
    public Iterator<E> createIterator() {
        items = new ArrayList<>();
        return new ItemIterator<>(items);
    }

    @Override
    public void addItem(E item) {
        items.add(item);
    }

    @Override
    public int getNumberOfItems() {
        return items.size();
    }
}

package org.uom.ginigalgodagevimukthipahasara.emailclient;

interface Collection<E> {

    Iterator<E> createIterator();

    void addItem(E item);

    int getNumberOfItems();
}

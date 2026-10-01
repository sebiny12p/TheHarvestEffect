package com.seb.harvesteffect.model.item;

import com.seb.harvesteffect.exception.CargoHoldFullException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Generic bounded storage hold for flagship cargo pods.
 * Demonstrates Java Generics with bounded type parameters.
 *
 * @param <T> resource type extending Resource
 */
public class CargoHold<T extends Resource> {
    private int capacity;
    private final List<T> storage;

    public CargoHold(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Cargo capacity must be strictly positive.");
        }
        this.capacity = capacity;
        this.storage = new ArrayList<T>();
    }

    public void setCapacity(int capacity) {
        if (capacity < this.storage.size()) {
            throw new IllegalArgumentException("New capacity cannot be smaller than current occupied storage.");
        }
        this.capacity = capacity;
    }

    public void store(T item) throws CargoHoldFullException {
        if (storage.size() >= capacity) {
            throw new CargoHoldFullException(capacity);
        }
        storage.add(item);
    }

    public T retrieve(int index) {
        if (index < 0 || index >= storage.size()) {
            throw new IndexOutOfBoundsException("Invalid cargo pod slot index: " + index);
        }
        return storage.remove(index);
    }

    public List<T> getManifest() {
        return Collections.unmodifiableList(storage);
    }

    public int getOccupiedCount() {
        return storage.size();
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isFull() {
        return storage.size() >= capacity;
    }

    public int calculateTotalMass() {
        int total = 0;
        for (T item : storage) {
            total += item.getMassUnits();
        }
        return total;
    }
}

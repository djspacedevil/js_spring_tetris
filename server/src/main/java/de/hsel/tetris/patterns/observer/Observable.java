package de.hsel.tetris.patterns.observer;

public interface Observable<T> {
    void register(Observer<T> observer);

    void unregister(Observer<T> observer);

    void notifyObservers();
}

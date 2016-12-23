package cat.joanpujol.buyingfaces.util;

/**
 * Created by lujop on 23/12/2016.
 */

import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subjects.SerializedSubject;

/**
 * Mantains an observable that always caches the variables last value.
 * @param <T>
 */
public class RxVariable<T> {
    private T value;

    private final SerializedSubject<T, T> serializedSubject;

    public RxVariable(T value) {
        this.value = value;
        serializedSubject = new SerializedSubject<>(BehaviorSubject.create(value));
    }

    public synchronized T get() {
        return value;
    }

    public synchronized void set(T value) {
        this.value = value;
        serializedSubject.onNext(this.value);
    }

    public Observable<T> asObservable() {
        return serializedSubject.asObservable();
    }
}
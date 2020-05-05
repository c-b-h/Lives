package se.ingenuity.lives;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import java.util.concurrent.atomic.AtomicBoolean;

public class DistinctMutableLiveData<T> extends MutableLiveData<T> {
    @NonNull
    private final Object dataLock = new Object();

    @NonNull
    private final AtomicBoolean firstTime;

    public DistinctMutableLiveData() {
        super();
        firstTime = new AtomicBoolean(true);
    }

    public DistinctMutableLiveData(T value) {
        super(value);
        firstTime = new AtomicBoolean(false);
    }

    @Override
    public void setValue(T value) {
        if (canUpdate(value)) {
            super.setValue(value);
        }
    }

    @Override
    public void postValue(T value) {
        synchronized (dataLock) {
            if (canUpdate(value)) {
                super.postValue(value);
            }
        }
    }

    private boolean canUpdate(T currentValue) {
        T previousValue = getValue();
        return (firstTime.compareAndSet(true, false)
                || (previousValue == null && currentValue != null)
                || (previousValue != null && !previousValue.equals(currentValue)));
    }
}
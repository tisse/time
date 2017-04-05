package jm.filter;

import jm.model.BaseEntity;

import java.util.Optional;

/**
 * Created by vk on 05.04.17.
 */
public abstract class BaseFilter<S extends BaseEntity> {

    private Optional<Long> start = Optional.empty();
    private Optional<Long> count = Optional.empty();

    public Optional<Long> getStart() {
        return start;
    }

    public void setStart(Optional<Long> start) {
        this.start = start;
    }

    public Optional<Long> getCount() {
        return count;
    }

    public void setCount(Optional<Long> count) {
        this.count = count;
    }
}

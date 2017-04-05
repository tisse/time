package jm.filter;

import jm.model.Event;

import java.util.Calendar;
import java.util.Optional;

/**
 * Created by vk on 05.04.17.
 */
public class EventFilter extends BaseFilter<Event> {

    private Optional<Calendar> from = Optional.empty();
    private Optional<Calendar> to = Optional.empty();
    private String person;

    public Optional<Calendar> getFrom() {
        return from;
    }

    public void setFrom(Optional<Calendar> from) {
        this.from = from;
    }

    public Optional<Calendar> getTo() {
        return to;
    }

    public void setTo(Optional<Calendar> to) {
        this.to = to;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }
}

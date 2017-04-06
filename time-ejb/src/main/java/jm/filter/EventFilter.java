package jm.filter;

import jm.model.Event;

import java.util.Calendar;

/**
 * Created by vk on 05.04.17.
 */
public class EventFilter extends BaseFilter<Event> {

    private Calendar from;
    private Calendar to;
    private String person;

    public Calendar getFrom() {
        return from;
    }

    public void setFrom(Calendar from) {
        this.from = from;
    }

    public Calendar getTo() {
        return to;
    }

    public void setTo(Calendar to) {
        this.to = to;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }
}

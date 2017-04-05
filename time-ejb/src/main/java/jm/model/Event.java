package jm.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Calendar;

/**
 * Created by vk on 22.03.17.
 */

@Entity
@XmlRootElement
@Table(name = "time_event", indexes = {
        @Index(name = "time_event_ix0000", columnList = "event_name"),
        @Index(name = "time_event_ix0001", columnList = "event_date"),
        @Index(name = "time_event_ix0002", columnList = "person_name"),
        @Index(name = "time_event_ix0003", columnList = "event_hash")
})
public class Event extends BaseEntity {

    @Column(name = "event_name")
    private String event;

    @Column(name = "person_name")
    private String person;

    @Column(name = "event_date")
    private Calendar date;

    @Column(name = "event_hash")
    private String hash;

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append(super.toString())
                .append("event", event)
                .append("person", person)
                .append("date", date)
                .toString();
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}

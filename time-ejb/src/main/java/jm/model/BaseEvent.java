package jm.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.Calendar;

/**
 * Created by vk on 06.04.17.
 */

@MappedSuperclass
public class BaseEvent extends BaseEntity {

    @Column(name = "person_name")
    private String person;

    @Column(name = "event_date")
    private Calendar date;

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
}

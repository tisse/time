package jm.model;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Calendar;

/**
 * Created by vk on 06.04.17.
 */
@Entity
@XmlRootElement
@Table(name = "time_daily_event")
@NamedNativeQueries({
        @NamedNativeQuery(name = "ReportDailyEvent.report", query = "select round(rand()*1000000000) as id, month as event_month, date as event_date, person as person_name, inx as event_in, oux as event_out,timediff(oux, inx) as event_jt from (\n" +
                "select month(te.event_date) month, date(te.event_date) date, person_name person, min(te.event_date) inx, max(te.event_date) oux FROM test.time_event te\n" +
                "group by month(te.event_date), date(te.event_date), person_name\n" +
                "order by month(te.event_date), date(te.event_date), person_name\n" +
                ") a \n" +
                "order by event_month, date, person", resultClass = ReportDailyEvent.class)
})
public class ReportDailyEvent extends BaseEvent {

    @Column(name = "event_month")
    private Integer month;


    @Column(name = "event_in")
    private Calendar dateIn;

    @Column(name = "event_out")
    private Calendar dateOut;

    @Column(name = "event_jt")
    private Calendar dateJt;

    public Calendar getDateIn() {
        return dateIn;
    }

    public void setDateIn(Calendar dateIn) {
        this.dateIn = dateIn;
    }

    public Calendar getDateOut() {
        return dateOut;
    }

    public void setDateOut(Calendar dateOut) {
        this.dateOut = dateOut;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Calendar getDateJt() {
        return dateJt;
    }

    public void setDateJt(Calendar dateJt) {
        this.dateJt = dateJt;
    }
}

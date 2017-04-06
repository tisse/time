package jm.dao;

import jm.filter.ReportDailyEventFilter;
import jm.model.Event;
import jm.model.ReportDailyEvent;
import org.hibernate.Criteria;

import javax.ejb.Stateless;
import java.util.List;

/**
 * Created by vk on 06.04.17.
 */

@Stateless
public class ReportDailyEventDao extends BaseDao<ReportDailyEvent, ReportDailyEventFilter> {

    public List<ReportDailyEvent> report(){
        return getEntityManager().createNamedQuery("ReportDailyEvent.report", ReportDailyEvent.class).getResultList();
    }


    @Override
    protected void prepareOrders(ReportDailyEventFilter filter, Criteria criteria) {

    }

    @Override
    protected Criteria prepareCriteria(ReportDailyEventFilter filter) {
        Criteria criteria = getSession().createCriteria(Event.class);
        return criteria;
    }
}

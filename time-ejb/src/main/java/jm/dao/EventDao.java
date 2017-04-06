package jm.dao;

import jm.filter.EventFilter;
import jm.model.Event;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import javax.ejb.Stateless;

/**
 * Created by vk on 30.03.17.
 */

@Stateless
public class EventDao extends BaseDao<Event, EventFilter> {

    public Boolean existsByHash(String hash) {
        long value = getEntityManager().createQuery("select count(e) as cnt from Event e where hash = :hash", Long.class)
                .setParameter("hash", hash).getSingleResult().longValue();
        System.out.println("hash=" + hash);
        return value > 0;
    }

    @Override
    protected void prepareOrders(EventFilter filter, Criteria criteria) {
        criteria.addOrder(Order.asc("date"));
        criteria.addOrder(Order.asc("person"));
    }

    @Override
    protected Criteria prepareCriteria(EventFilter filter) {

        Criteria criteria = getSession().createCriteria(Event.class);

        if (StringUtils.isNotEmpty(filter.getPerson())) {
            criteria.add(Restrictions.eq("person", filter.getPerson()));
        }

        if (null != filter.getFrom()) {
            criteria.add(Restrictions.ge("date", filter.getFrom()));
        }

        if (null != filter.getTo()) {
            criteria.add(Restrictions.le("date", filter.getTo()));
        }

        return criteria;
    }
}

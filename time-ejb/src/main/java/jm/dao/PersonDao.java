package jm.dao;

import jm.filter.PersonFilter;
import jm.model.Person;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import javax.ejb.Stateless;

/**
 * Created by vk on 10.04.17.
 */

@Stateless
public class PersonDao extends BaseDao<Person, PersonFilter> {
    @Override
    protected void prepareOrders(PersonFilter filter, Criteria criteria) {
        criteria.addOrder(Order.asc("name"));
    }

    @Override
    protected Criteria prepareCriteria(PersonFilter filter) {
        Criteria criteria = getSession().createCriteria(Person.class);

        if (StringUtils.isNotEmpty(filter.getName())) {
            criteria.add(Restrictions.eq("name", filter.getName()));
        }

        if (StringUtils.isNotEmpty(filter.getExtName())) {
            criteria.add(Restrictions.eq("extName", filter.getExtName()));
        }

        return criteria;
    }


}

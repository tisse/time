package jm.dao;

import jm.filter.BaseFilter;
import jm.model.BaseEntity;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by vk on 30.03.17.
 */
public abstract class BaseDao<T extends BaseEntity, F extends BaseFilter<T>> {

    @PersistenceContext
    private EntityManager entityManager;

    protected EntityManager getEntityManager() {
        return entityManager;
    }

    public void save(T entity) {
        if (entity.getId() == null) {
            getEntityManager().persist(entity);
        } else {
            getEntityManager().merge(entity);
        }
    }

    public void flush() {
        getEntityManager().flush();
    }

    public List<T> list(F filter) {
        Criteria criteria = prepareCriteria(filter);

        filter.getStart().ifPresent(start -> {
            criteria.setFirstResult(start.intValue());
        });

        filter.getCount().ifPresent(count -> {
            criteria.setMaxResults(count.intValue());
        });

        prepareOrders(filter, criteria);

        List<T> list = (List<T>) criteria.list().stream()
                .map(entity -> init((T) entity))
                .collect(Collectors.toList());
        return list;
    }

    protected abstract void prepareOrders(F filter, Criteria criteria);

    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    protected T init(T entity) {
        if (entity == null) {
            return null;
        }

        doInit(entity);
        return entity;
    }

    protected void doInit(T entity) {

    }

    public Long count(F filter) {
        Criteria criteria = prepareCriteria(filter);
        criteria.setProjection(Projections.rowCount());
        return (Long) criteria.uniqueResult();
    }



    protected abstract Criteria prepareCriteria(F filter);

    public List<T> list() {
        return getEntityManager().createQuery("from " + getGenericClass().getName(), getGenericClass()).getResultList();
    }

    public T findById(Long id) {
        return getEntityManager().find(getGenericClass(), id);
    }

    protected Class<T> getGenericClass() {
        return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    protected Session getSession() {
        return (Session) getEntityManager().getDelegate();
    }


}

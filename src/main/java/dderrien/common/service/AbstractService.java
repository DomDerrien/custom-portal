package dderrien.common.service;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.googlecode.objectify.Key;

import dderrien.common.dao.AbstractDao;
import dderrien.common.exception.ConflictException;
import dderrien.common.exception.NoContentException;
import dderrien.common.exception.NotFoundException;
import dderrien.common.exception.NotModifiedException;
import dderrien.common.model.AbstractBase;
import dderrien.common.util.Range;

public class AbstractService<T extends AbstractBase<T>> {

    protected AbstractDao<T> dao;

    @Inject
    public AbstractService(AbstractDao<T> dao) {
        this.dao = dao;
    }
    
    public List<T> selectSilent(Map<String, Object> filters, Range range, List<String> orders) {
        return dao.select(filters, range, orders);
    }
    
    public List<T> select(Map<String, Object> filters, Range range, List<String> orders) {
        List<T> entities = selectSilent(filters, range, orders);
        if (entities.size() == 0) {
            throw new NoContentException("Could not find " + dao.getModelClass().getName() + " entities matching the criteria");
        }
        return entities;
    }
    
    public T getSilent(Long id) {
        return dao.get(id);
    }
    
    public T get(Long id) {
        T entity = dao.get(id);
        if (entity == null) {
            throw new NotFoundException("Cannot find " + dao.getModelClass().getName() + " entity with id: "+ id);
        }
        return entity;
    }
    
    public Key<AbstractBase<T>> create (T entity) {
        if (entity.getId() != null) {
            T existing = getSilent(entity.getId());
            if (existing != null) {
                throw new ConflictException(dao.getModelClass().getName() + " entity with id: "+ entity.getId() + " already exists");
            }
        }
        return dao.save(entity);
    }
    
    public T update (T existing, T entity) {
        // Not need to check the id field as it is marked WriteOnceField and updates are ignored
        if (!existing.merge(entity)) {
            throw new NotModifiedException("Nothing to update");
        }
        return dao.get(dao.save(existing).getId());
    }
    
    public T update (Long id, T entity) {
        T existing = get(id); // Will throw NFE if entity does not exists
        return update(existing, entity);
    }

    public void delete(Long id) {
        get(id); // Will throw NFE if entity does not exists
        dao.delete(id);
    }
}
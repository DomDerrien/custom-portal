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

    public T get(Long id, Long version) {
        return get(id, version, false);
    }

    protected T get(Long id, Long version, boolean throwConflictIfVersionDontMatch) {
        T entity = dao.get(id);
        if (entity == null) {
            throw new NotFoundException("Cannot find " + dao.getModelClass().getName() + " entity with id: " + id);
        }
        if (!version.equals(Long.valueOf(0L))) {
            if (throwConflictIfVersionDontMatch && !entity.getVersion().equals(version)) {
                throw new ConflictException("Given version " + version + " does not match the saved one: " + entity.getVersion());
            }
            if (!throwConflictIfVersionDontMatch && entity.getVersion().equals(version)) {
                throw new NotModifiedException("No update since last request");
            }
        }
        return entity;
    }

    public Key<AbstractBase<T>> create(T entity) {
        if (entity.getId() != null) {
            T existing = getSilent(entity.getId());
            if (existing != null) {
                throw new ConflictException(dao.getModelClass().getName() + " entity with id: " + entity.getId() + " already exists");
            }
        }
        return dao.save(entity);
    }

    public T update(T existing, Long version, T entity) {
        // Not need to check the id and version fields as they are marked WriteOnceField and updates are ignored
        if (!existing.merge(entity)) {
            throw new NotModifiedException("Nothing to update");
        }
        return dao.get(dao.save(existing).getId());
    }

    public T update(Long id, Long version, T entity) {
        T existing = get(id, version, true); // Will throw NFE if entity does not exists
        return update(existing, version, entity);
    }

    public void delete(Long id, Long version) {
        get(id, version, true); // Will throw NFE if entity does not exists
        dao.delete(id);
    }
}
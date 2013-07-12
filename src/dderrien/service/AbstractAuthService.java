package dderrien.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.googlecode.objectify.Key;

import dderrien.dao.AbstractDao;
import dderrien.exception.UnauthorizedException;
import dderrien.model.AbstractAuthBase;
import dderrien.model.AbstractBase;
import dderrien.util.Range;

public class AbstractAuthService<T extends AbstractAuthBase<T>> extends AbstractService<T> {
    
    private UserService userService;
    
    @Inject
    public AbstractAuthService(AbstractDao<T> dao, UserService userService) {
        super(dao);
        this.userService = userService;
    }
    
    @Override
    public List<T> select(Map<String, Object> filters, Range range, List<String> orders) {
        if (!userService.isLoggedAdmin()) {
            if (filters == null) {
                filters = new HashMap<String, Object>();
            }
            else {
                Long ownerId = (Long) filters.get("ownerId");
                if (ownerId != null && !ownerId.equals(userService.getLoggedUser().getId())) {
                    throw new UnauthorizedException("Attempt to get data belonging to another user!");
                }
            }
            filters.put("ownerId", userService.getLoggedUser().getId());
        }
        return super.select(filters, range, orders);
    }
    
    @Override
    public T get(Long id) {
        T entity = super.get(id);
        if (!userService.isLoggedAdmin()) {
            if (!userService.getLoggedUser().getId().equals(entity.getOwnerId())) {
                throw new UnauthorizedException(dao.getModelClass().getName() + " entity does not belong to the logged user");
            }
        }
        return entity;
    }    

    @Override
    public Key<AbstractBase<T>> create(T entity) {
        if (!userService.isLoggedAdmin()) {
            if (!userService.getLoggedUser().getId().equals(entity.getOwnerId())) {
                throw new UnauthorizedException(dao.getModelClass().getName() + " entity needs to belong to the logged user");
            }
        }
        return super.create(entity);
    }    

    @Override
    public T update(Long id, T entity) {
        T existing = get(id); // Will throw NFE is missing, UnauthEx is not allowed
        if (!userService.isLoggedAdmin()) {
            if (!userService.getLoggedUser().getId().equals(entity.getOwnerId())) {
                throw new UnauthorizedException(dao.getModelClass().getName() + " entity needs to belong to the logged user");
            }
        }
        return super.update(existing, entity);
    }
}
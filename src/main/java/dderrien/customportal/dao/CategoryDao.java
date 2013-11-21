package dderrien.customportal.dao;

import com.googlecode.objectify.ObjectifyService;

import dderrien.customportal.model.Category;
import dderrien.dao.AbstractDao;
import dderrien.util.IntrospectionHelper;

public class CategoryDao extends AbstractDao<Category> {

    static {
        ObjectifyService.register(IntrospectionHelper.getFirstTypeArgument(CategoryDao.class));
    }
    
    public CategoryDao() {
        super();
    }
}
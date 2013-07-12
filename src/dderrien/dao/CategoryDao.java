package dderrien.dao;

import com.googlecode.objectify.ObjectifyService;

import dderrien.model.Category;
import dderrien.util.IntrospectionHelper;

public class CategoryDao extends AbstractDao<Category> {

    static {
        ObjectifyService.register(IntrospectionHelper.getFirstTypeArgument(CategoryDao.class));
    }
    
    public CategoryDao() {
        super();
    }
}
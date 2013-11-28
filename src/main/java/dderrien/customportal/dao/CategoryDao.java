package dderrien.customportal.dao;

import com.googlecode.objectify.ObjectifyService;

import dderrien.common.dao.AbstractDao;
import dderrien.common.util.IntrospectionHelper;
import dderrien.customportal.model.Category;

public class CategoryDao extends AbstractDao<Category> {

    static {
        ObjectifyService.register(IntrospectionHelper.getFirstTypeArgument(CategoryDao.class));
    }
    
    public CategoryDao() {
        super();
    }
}
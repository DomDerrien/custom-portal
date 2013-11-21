package dderrien.customportal.dao;

import com.googlecode.objectify.ObjectifyService;

import dderrien.customportal.model.Link;
import dderrien.dao.AbstractDao;
import dderrien.util.IntrospectionHelper;

public class LinkDao extends AbstractDao<Link> {

    static {
        ObjectifyService.register(IntrospectionHelper.getFirstTypeArgument(LinkDao.class));
    }

    public LinkDao () {
        super();
    }
}
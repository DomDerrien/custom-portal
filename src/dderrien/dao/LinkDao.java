package dderrien.dao;

import com.googlecode.objectify.ObjectifyService;

import dderrien.model.Link;
import dderrien.util.IntrospectionHelper;

public class LinkDao extends AbstractDao<Link> {

    static {
        ObjectifyService.register(IntrospectionHelper.getFirstTypeArgument(LinkDao.class));
    }

    public LinkDao () {
        super();
    }
}
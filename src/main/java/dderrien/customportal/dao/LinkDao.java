package dderrien.customportal.dao;

import com.googlecode.objectify.ObjectifyService;

import dderrien.common.dao.AbstractDao;
import dderrien.common.util.IntrospectionHelper;
import dderrien.customportal.model.Link;

public class LinkDao extends AbstractDao<Link> {

    static {
        ObjectifyService.register(IntrospectionHelper.getFirstTypeArgument(LinkDao.class));
    }

    public LinkDao() {
        super();
    }
}
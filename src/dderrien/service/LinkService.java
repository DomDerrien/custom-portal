package dderrien.service;

import javax.inject.Inject;

import dderrien.dao.LinkDao;
import dderrien.model.Link;

public class LinkService extends AbstractService<Link> {

    @Inject
    public LinkService(LinkDao dao) {
        super(dao);
    }
}
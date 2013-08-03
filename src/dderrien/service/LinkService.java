package dderrien.service;

import javax.inject.Inject;

import dderrien.dao.LinkDao;
import dderrien.model.Link;

public class LinkService extends AbstractAuthService<Link> {

    @Inject
    public LinkService(LinkDao dao, UserService userService) {
        super(dao, userService);
    }
}
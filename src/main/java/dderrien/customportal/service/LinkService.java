package dderrien.customportal.service;

import javax.inject.Inject;

import dderrien.customportal.dao.LinkDao;
import dderrien.customportal.model.Link;
import dderrien.service.AbstractAuthService;
import dderrien.service.UserService;

public class LinkService extends AbstractAuthService<Link> {

    @Inject
    public LinkService(LinkDao dao, UserService userService) {
        super(dao, userService);
    }
}
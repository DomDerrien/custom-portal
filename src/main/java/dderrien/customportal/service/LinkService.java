package dderrien.customportal.service;

import javax.inject.Inject;

import dderrien.common.service.AbstractAuthService;
import dderrien.common.service.UserService;
import dderrien.customportal.dao.LinkDao;
import dderrien.customportal.model.Link;

public class LinkService extends AbstractAuthService<Link> {

    @Inject
    public LinkService(LinkDao dao, UserService userService) {
        super(dao, userService);
    }
}
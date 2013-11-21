package dderrien.ipaddresscontrol.dao;

import com.googlecode.objectify.ObjectifyService;

import dderrien.dao.AbstractDao;
import dderrien.ipaddresscontrol.model.IPAddress;
import dderrien.util.IntrospectionHelper;

public class IPAddressDao extends AbstractDao<IPAddress> {

	static {
        ObjectifyService.register(IntrospectionHelper.getFirstTypeArgument(IPAddress.class));
	}

	public IPAddressDao() {
		super();
	}
}

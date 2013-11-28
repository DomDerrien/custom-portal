package dderrien.ipaddresscontrol.dao;

import com.googlecode.objectify.ObjectifyService;

import dderrien.common.dao.AbstractDao;
import dderrien.common.util.IntrospectionHelper;
import dderrien.ipaddresscontrol.model.IPAddress;

public class IPAddressDao extends AbstractDao<IPAddress> {

	static {
        ObjectifyService.register(IntrospectionHelper.getFirstTypeArgument(IPAddress.class));
	}

	public IPAddressDao() {
		super();
	}
}

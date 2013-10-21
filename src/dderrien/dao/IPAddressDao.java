package dderrien.dao;

import com.googlecode.objectify.ObjectifyService;

import dderrien.model.IPAddress;
import dderrien.util.IntrospectionHelper;

public class IPAddressDao extends AbstractDao<IPAddress> {

	static {
        ObjectifyService.register(IntrospectionHelper.getFirstTypeArgument(IPAddress.class));
	}

	public IPAddressDao() {
		super();
	}
}

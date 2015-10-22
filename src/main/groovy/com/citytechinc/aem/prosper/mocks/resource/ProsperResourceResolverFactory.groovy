package com.citytechinc.aem.prosper.mocks.resource

import java.util.Map

import javax.jcr.Session

import org.apache.sling.api.resource.LoginException
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.api.resource.ResourceResolverFactory

import com.citytechinc.aem.prosper.adapter.ProsperAdapterManager

public class ProsperResourceResolverFactory implements ResourceResolverFactory{
	private ProsperAdapterManager adapterManager
	private Session session

	ProsperResourceResolverFactory(Session session, ProsperAdapterManager adapterManager){
		this.session = session
		this.adapterManager = adapterManager
	}

	@Override
	public ResourceResolver getResourceResolver(Map<String, Object> authenticationInfo) throws LoginException {
		return new MockResourceResolver(session, adapterManager)
	}

	@Override
	public ResourceResolver getAdministrativeResourceResolver(Map<String, Object> authenticationInfo)
	throws LoginException {
		return new MockResourceResolver(session, adapterManager)
	}

	@Override
	public ResourceResolver getServiceResourceResolver(Map<String, Object> authenticationInfo) throws LoginException {
		return new MockResourceResolver(session, adapterManager)
	}

	@Override
	public ResourceResolver getThreadResourceResolver() {
		return new MockResourceResolver(session, adapterManager)
	}
}

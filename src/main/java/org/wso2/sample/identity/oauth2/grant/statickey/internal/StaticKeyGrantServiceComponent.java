package org.wso2.sample.identity.oauth2.grant.statickey.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.wso2.carbon.user.core.service.RealmService;
import org.wso2.carbon.identity.application.authentication.framework.ApplicationAuthenticator;
import org.wso2.sample.identity.oauth2.grant.statickey.StaticKeyGrant;

@Component(
        name = "org.wso2.sample.identity.oauth2.grant.statickey.StaticKeyGrantHandlerComponent", // Unique name for this OSGi component
        immediate = true // Activate as soon as possible
)
public class StaticKeyGrantServiceComponent {

    private static final Log log = LogFactory.getLog(StaticKeyGrantServiceComponent.class);

    private static RealmService realmService;

    @Activate
    protected void activate(ComponentContext ctxt) {
        try {
            StaticKeyGrant staticKeyGrant = new StaticKeyGrant();
            ctxt.getBundleContext().registerService(
                    ApplicationAuthenticator.class.getName(),
                    staticKeyGrant,
                    null
            );
            if (log.isDebugEnabled()) {
                log.debug("StaticKeyGrant bundle activated and handler registered.");
            }
        } catch (Throwable e) {
            log.error("StaticKeyGrant bundle activation failed.", e);
        }
    }

    @Deactivate
    protected void deactivate(ComponentContext ctxt) {
        log.debug("StaticKeyGrant bundle is deactivated.");
    }

    public static RealmService getRealmService() {
        return realmService;
    }

    @Reference(
            name = "realm.service",
            service = org.wso2.carbon.user.core.service.RealmService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetRealmService"
    )
    protected void setRealmService(RealmService realmService) {
        log.debug("Setting the Realm Service.");
        StaticKeyGrantServiceComponent.realmService = realmService;
    }


    protected void unsetRealmService(RealmService realmService) {
        log.debug("UnSetting the Realm Service.");
        StaticKeyGrantServiceComponent.realmService = null;
    }
}





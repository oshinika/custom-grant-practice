//package org.wso2.sample.identity.oauth2.grant.statickey.internal;
//
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.osgi.service.component.ComponentContext;
//import org.osgi.service.component.annotations.Activate;
//import org.osgi.service.component.annotations.Component;
//import org.osgi.service.component.annotations.Deactivate;
//import org.osgi.service.component.annotations.Reference;
//import org.osgi.service.component.annotations.ReferenceCardinality;
//import org.osgi.service.component.annotations.ReferencePolicy;
//import org.wso2.carbon.user.core.service.RealmService;
//import org.wso2.carbon.identity.application.authentication.framework.ApplicationAuthenticator;
//import org.wso2.sample.identity.oauth2.grant.statickey.StaticKeyGrant;
//
//@Component(
//        name = "org.wso2.sample.identity.oauth2.grant.statickey.StaticKeyGrantHandlerComponent", // Unique name for this OSGi component
//        immediate = true // Activate as soon as possible
//)
//public class StaticKeyGrantServiceComponent {
//
//    private static final Log log = LogFactory.getLog(StaticKeyGrantServiceComponent.class);
//
//    private static RealmService realmService; // Holds the injected RealmService
//
//    @Activate
//    protected void activate(ComponentContext ctxt) {
//        try {
//            StaticKeyGrant staticKeyGrant = new StaticKeyGrant();
//            ctxt.getBundleContext().registerService(
//                    ApplicationAuthenticator.class.getName(), // Register under ApplicationAuthenticator interface
//                    staticKeyGrant, // Your handler instance
//                    null // No properties needed for this registration
//            );
//            if (log.isDebugEnabled()) {
//                log.debug("StaticKeyGrant bundle activated and handler registered.");
//            }
//        } catch (Throwable e) {
//            log.error("StaticKeyGrant bundle activation failed.", e);
//        }
//    }
//
//    @Deactivate
//    protected void deactivate(ComponentContext ctxt) {
//        log.debug("StaticKeyGrant bundle is deactivated.");
//    }
//
//    // Static getter method to provide the injected RealmService to other classes in your bundle
//    public static RealmService getRealmService() {
//        return realmService;
//    }
//
//    // This method is called by OSGi to inject RealmService
//    @Reference(
//            name = "realm.service", // A descriptive name for this service reference
//            service = org.wso2.carbon.user.core.service.RealmService.class, // The service interface you need
//            cardinality = ReferenceCardinality.MANDATORY, // This component requires RealmService to function
//            policy = ReferencePolicy.DYNAMIC, // Allows RealmService to be provided/withdrawn dynamically
//            unbind = "unsetRealmService" // Method to call when RealmService is no longer available
//    )
//    protected void setRealmService(RealmService realmService) {
//        log.debug("Setting the Realm Service.");
//        StaticKeyGrantServiceComponent.realmService = realmService; // Store the injected service
//    }
//
//    // This method is called by OSGi when RealmService becomes unavailable
//    protected void unsetRealmService(RealmService realmService) {
//        log.debug("UnSetting the Realm Service.");
//        StaticKeyGrantServiceComponent.realmService = null;
//    }
//}



//package org.wso2.sample.identity.oauth2.grant.statickey.internal;
//
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.osgi.service.component.ComponentContext;
//import org.osgi.service.component.annotations.*;
//import org.wso2.carbon.identity.oauth2.token.handlers.grant.AuthorizationGrantHandler;
//import org.wso2.carbon.user.core.service.RealmService;
//import org.wso2.sample.identity.oauth2.grant.statickey.StaticKeyGrant;
//
//@Component(
//        name = "org.wso2.sample.identity.oauth2.grant.statickey.StaticKeyGrantHandlerComponent",
//        immediate = true
//)
//public class StaticKeyGrantServiceComponent {
//
//    private static final Log log = LogFactory.getLog(StaticKeyGrantServiceComponent.class);
//    private static RealmService realmService;
//
//    @Activate
//    protected void activate(ComponentContext ctxt) {
//        try {
//            StaticKeyGrant staticKeyGrant = new StaticKeyGrant();
//            ctxt.getBundleContext().registerService(
//                    AuthorizationGrantHandler.class.getName(),  // <--- THIS IS THE CORRECT INTERFACE!
//                    staticKeyGrant,
//                    null
//            );
//            log.debug("StaticKeyGrant bundle activated and handler registered.");
//        } catch (Throwable e) {
//            log.error("StaticKeyGrant bundle activation failed.", e);
//        }
//    }
//
//    @Deactivate
//    protected void deactivate(ComponentContext ctxt) {
//        log.debug("StaticKeyGrant bundle is deactivated.");
//    }
//
//    public static RealmService getRealmService() {
//        return realmService;
//    }
//
//    @Reference(
//            name = "realm.service",
//            service = RealmService.class,
//            cardinality = ReferenceCardinality.MANDATORY,
//            policy = ReferencePolicy.DYNAMIC,
//            unbind = "unsetRealmService"
//    )
//    protected void setRealmService(RealmService realmService) {
//        log.debug("Setting the Realm Service.");
//        StaticKeyGrantServiceComponent.realmService = realmService;
//    }
//
//    protected void unsetRealmService(RealmService realmService) {
//        log.debug("UnSetting the Realm Service.");
//        StaticKeyGrantServiceComponent.realmService = null;
//    }
//}



package org.wso2.sample.identity.oauth2.grant.statickey.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.annotations.*;
import org.wso2.carbon.identity.oauth2.token.handlers.grant.AuthorizationGrantHandler;
import org.wso2.carbon.user.core.service.RealmService;
import org.wso2.sample.identity.oauth2.grant.statickey.StaticKeyGrant;

@Component(
        name = "custom.static.key.grant.component",
        immediate = true
)
public class StaticKeyGrantServiceComponent {

    private static final Log log = LogFactory.getLog(StaticKeyGrantServiceComponent.class);

    private static RealmService realmService;

    @Activate
    protected void activate() {
        try {
            StaticKeyGrant staticKeyGrant = new StaticKeyGrant();
            // No explicit registration needed unless you're doing manual registration
            log.info("StaticKeyGrantServiceComponent activated");
        } catch (Throwable e) {
            log.error("Error activating StaticKeyGrantServiceComponent", e);
        }
    }

    @Reference(
            name = "realm.service",
            service = RealmService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetRealmService"
    )
    protected void setRealmService(RealmService service) {
        log.info("RealmService bound to StaticKeyGrantServiceComponent");
        realmService = service;
    }

    protected void unsetRealmService(RealmService service) {
        log.info("RealmService unbound from StaticKeyGrantServiceComponent");
        realmService = null;
    }

    public static RealmService getRealmService() {
        return realmService;
    }
}

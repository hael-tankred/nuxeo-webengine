/*
 * (C) Copyright 2006-2009 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     bstefanescu
 */
package org.nuxeo.ecm.webengine.gwt.dev;

import java.io.File;
import java.net.URL;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

import org.nuxeo.common.utils.StringUtils;
import org.nuxeo.dev.NuxeoApp;
import org.nuxeo.ecm.platform.ui.web.auth.NuxeoAuthenticationFilter;
import org.nuxeo.ecm.webengine.gwt.GwtBundleActivator;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class NuxeoLauncher extends NuxeoAuthenticationFilter {

    private static final long serialVersionUID = 1L;

    protected static NuxeoApp app;

    /**
     * You can overwrite this to add your own pom artifacts in the graph
     * @param app
     */
    protected void configureBuild(NuxeoApp app) {
        //app.getMaven().getGraph().addRootNode("org.my:my-artifact:1.0:pom").expand(1, null);
    }

    /**
     * You can overwrite this add custom initialization after nuxeo started
     * @param app
     */
    protected void frameworkStarted(NuxeoApp app) {
        // do nothing
    }
    
    /**
     * Get a custom configuration for the Nuxeo to build.
     * By default no custom configuration is used - but bult-in configuration selected through profiles.
     * @return null if no custom configuration is wanted.
     */
    protected URL getConfiguration() {
        return null;
    }
    
    /**
     * Override this if you don;t want to cache the nuxeo build
     * @return
     */
    protected boolean useCache() {
        return true;
    }
    
    @Override
    public synchronized void init(FilterConfig config) throws ServletException {
        if (app != null) {
            return;
        }
        System.setProperty(GwtBundleActivator.GWT_DEV_MODE_PROP, "true");
        app = createApplication(config);
        
        Runtime.getRuntime().addShutdownHook(new Thread("Nuxeo Server Shutdown") {
            @Override
            public void run() {
                try {
                    if (app != null) app.shutdown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        
//        String home = config.getInitParameter("home");
//        String h = config.getInitParameter("host");
//        String p = config.getInitParameter("profile");
//        String v = config.getInitParameter("version");
//        String c = config.getInitParameter("config");
//        if (h == null) h = "localhost:8081";
//        if (v == null) v = "5.3.1-SNAPSHOT";        
//        ArrayList<String> args = new ArrayList<String>();
//        if (home == null) {
//            String userDir = System.getProperty("user.home");
//            String sep = userDir.endsWith("/") ? "" : "/";
//            args.add(userDir+sep+".nxserver-gwt");
//        } else {
//            home = StringUtils.expandVars(home, System.getProperties());
//            args.add(home);
//        }
//        args.add("-h");
//        args.add(h);
//        args.add("-v");
//        args.add(v);    
//        if (c != null) {
//            args.add("-c"); 
//            args.add(c);            
//        } else {
//            if (p == null) p = NuxeoApp.CORE_SERVER;
//            args.add("-p"); 
//            args.add(p);
//        }
//        try {
//            org.nuxeo.dev.Main.main(args.toArray(new String[args.size()]));
//            frameworkStarted();
//        } catch (Exception e) {
//            System.err.println("Failed to start nuxeo");
//            throw new ServletException(e);
//        }
        super.init(config);
    }
    
    protected NuxeoApp createApplication(FilterConfig config) throws ServletException {
        URL cfg = getConfiguration();
        String homeParam = config.getInitParameter("home");
        String hostParam = config.getInitParameter("host");
        String portParam = config.getInitParameter("port");
        String profileParam = config.getInitParameter("profile");
        String versionParam = config.getInitParameter("version");
        
        File home = null;
        String host = hostParam == null ? "localhost" : null;
        int port = portParam == null ? 8081 : Integer.parseInt(portParam);
        String version = versionParam == null ? "5.3.1-SNAPSHOT" : versionParam;
        String profile = profileParam == null ? NuxeoApp.CORE_SERVER :profileParam;
        if (homeParam == null) {
            String userDir = System.getProperty("user.home");
            String sep = userDir.endsWith("/") ? "" : "/";
            home = new File(userDir+sep+".nxserver-gwt");
        } else {
            homeParam = StringUtils.expandVars(homeParam, System.getProperties());
            home = new File(homeParam);
        }

        if (cfg == null) {
            cfg = NuxeoLauncher.class.getResource(profile+".cfg");
            if (cfg == null) {
                throw new ServletException("Unknown build profile: "+profile);
            }
            profile = null;
        }
        
        System.out.println("+---------------------------------------------------------");
        System.out.println("| Nuxeo Server Profile: "+(profile==null?"custom":profile)+"; version: "+version);
        System.out.println("| Home Directory: "+home);
        System.out.println("| HTTP server at: "+host+":"+port);
        System.out.println("+---------------------------------------------------------\n");

        NuxeoApp.setHttpServerAddress(host, port);
        
        try {
            MyNuxeoApp app = new MyNuxeoApp(home);
            if (cfg == null) {
                app.build(profile, version, useCache());
            } else {
                app.build(cfg, version, useCache());
            }
            app.start();
            frameworkStarted(app);
            return app;
        } catch (Exception e) {
            throw new ServletException(e);
        }

    }
    
  
    public class MyNuxeoApp extends NuxeoApp {
        public MyNuxeoApp(File home) throws Exception {
            super (home);
        }
        public MyNuxeoApp(File home, ClassLoader cl) throws Exception {
            super (home, cl);
        }
        @Override
        protected void initializeGraph() throws Exception {
            super.initializeGraph();
            configureBuild(this);
        }
    }
}
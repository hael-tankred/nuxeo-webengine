/*
 * (C) Copyright 2006-2010 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
package org.nuxeo.ecm.webengine.jaxrs.views;

import java.io.File;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.nuxeo.ecm.platform.rendering.api.ResourceLocator;
import org.nuxeo.ecm.platform.rendering.fm.FreemarkerEngine;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class TemplateView extends View {

    private static final FreemarkerEngine engine = new FreemarkerEngine(null, new Locator());
    private static final Map<String, TemplateView> locators = new HashMap<String, TemplateView>();


    public TemplateView(String name) {
        this (null, name);
    }

    public TemplateView(Object owner, String name) {
        super (owner, name);
    }

    @Override
    public void render(Writer writer) throws Exception {
        String id = addLocator(this);
        try {
            engine.render(id, vars, writer);
        } finally {
            removeLocator(id);
        }
    }

    @Override
    public void render(OutputStream out) throws Exception {
        render(new OutputStreamWriter(out, "UTF-8"));
    }

    private static synchronized String addLocator(TemplateView view) {
        String locatorId = "view:/" + view.getUrl().toExternalForm();
        locators.put(locatorId, view);
        return locatorId;
    }

    private static synchronized void removeLocator(String id) {
        locators.remove(id);
    }

    private static synchronized TemplateView getLocator(String id) {
        return locators.get(id);
    }

    static class Locator implements ResourceLocator {
        @Override
        public File getResourceFile(String key) {
            return null;
        }
        @Override
        public URL getResourceURL(String key) {
            TemplateView view = getLocator(key);
            if (view != null) {
                return view.getUrl();
            }
            return null;
        }
    }

}
/*
 * (C) Copyright 2006-2008 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 *
 * $Id$
 */

package org.nuxeo.ecm.platform.site.security.guards;

import org.nuxeo.common.xmap.annotation.XContent;
import org.nuxeo.common.xmap.annotation.XObject;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.core.api.security.Access;
import org.nuxeo.ecm.platform.site.security.Guard;

/**
 * Check access against a built-in permission
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
@XObject("permission")
public class PermissionGuard implements Guard {

    @XContent
    protected String perm;

    protected PermissionGuard() {
    }

    public PermissionGuard(String perm) {
        this.perm = perm;
    }

    public boolean check(CoreSession session, DocumentModel doc) {
        ACP acp = doc.getACP();
        Access access = acp.getAccess(session.getPrincipal().getName(), perm);
        if (access == Access.UNKNOWN) {
            return true; // What is the default policy ?
        }
        return access == Access.GRANT;
    }

    @Override
    public String toString() {
        return "PERM["+perm+"]";
    }
}
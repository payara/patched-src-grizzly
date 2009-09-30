/**
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER. *
 * Copyright 2007-2008 Sun Microsystems, Inc. All rights reserved. *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 *
 * Contributor(s):
 *
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 *
 */
package com.sun.grizzly.http.servlet.deployer;

import com.sun.grizzly.http.deployer.DeploymentConfiguration;
import com.sun.grizzly.http.webxml.schema.WebApp;

import java.net.URLClassLoader;

/**
 * {@link DeploymentConfiguration} for web application (War file).
 * <p/>
 * Configuration for deploying web application:
 * <ul>
 * <li>context to deploy to,</li>
 * <li>server wide available libraries,</li>
 * <li>web default - web application to be merged with application to be deployed.</li>
 * </ul>
 *
 * @author Hubert Iwaniuk
 * @since Sep 28, 2009
 */
public class WarDeploymentConfiguration implements DeploymentConfiguration {
    /** Context to deploy to. */
    public String ctx;
    /** Server Libraries {@link ClassLoader}. */
    public URLClassLoader serverLibLoader;
    /** Web default application, merged with all to be deployed. */
    public WebApp webDefault;

    /**
     * Constructor.
     *
     * @param ctx             Context to deploy to.
     * @param serverLibLoader Server Libraries {@link ClassLoader}.
     * @param webDefault      Web default application, merged with one to be deployed.
     */
    public WarDeploymentConfiguration(String ctx, URLClassLoader serverLibLoader, WebApp webDefault) {
        this.serverLibLoader = serverLibLoader;
        this.webDefault = webDefault;
        this.ctx = ctx;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return String.format("WarDeploymentConfiguration{ctx='%s', serverLibLoader=%s, webDefault=%s}",
                ctx, serverLibLoader, webDefault);
    }
}
/*
 * 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2007-2008 Sun Microsystems, Inc. All rights reserved.
 * 
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

package org.glassfish.grizzly.web.standalone;

import org.glassfish.grizzly.web.Constants;
import org.glassfish.grizzly.web.container.util.Interceptor;
import org.glassfish.grizzly.web.FileCache;
import java.io.IOException;

import org.glassfish.grizzly.streams.StreamReader;
import org.glassfish.grizzly.streams.StreamWriter;
import org.glassfish.grizzly.web.WebFilter;
import org.glassfish.grizzly.web.container.Request;
import org.glassfish.grizzly.web.container.util.buf.Ascii;
import org.glassfish.grizzly.web.container.util.buf.ByteChunk;
import org.glassfish.grizzly.web.container.util.buf.MessageBytes;
import org.glassfish.grizzly.web.container.util.http.MimeHeaders;
/**
 * This {@link Interceptor} is invoked after the request line has been parsed. 
 * 
 * @author Jeanfrancois Arcand
 */
public class StaticHandler implements Interceptor<Request> {
      
    private WebFilter webFilter;
    
    /**
     * The {@link StreamReader} used to send a static resources.
     */
    private StreamReader reader;

    /**
     * The {@link StreamWriter} used to send a static resources.
     */
    private StreamWriter writer;
 
    
    /**
     * The FileCache mechanism used to cache static resources.
     */
    protected FileCache fileCache;     
    
    
    // ----------------------------------------------------- Constructor ----//
    
    
    public StaticHandler(WebFilter webFilter) {
        this.webFilter = webFilter;
    }
    
      
    /**
     * {@inheritDoc}
     */
    public void attach(StreamReader reader, StreamWriter writer) {
        this.reader = reader;
        this.writer = writer;
        
        if (fileCache == null && writer != null){
            fileCache = webFilter.getFileCache();
        }
    }    
    
    
    /**
     * Intercept the request and decide if we cache the static resource. If the
     * static resource is already cached, return it.
     */
    public int handle(Request req, int handlerCode) throws IOException{
        if (fileCache == null) return Interceptor.CONTINUE;
        
        if (handlerCode == Interceptor.RESPONSE_PROCEEDED && fileCache.isEnabled()){
            String docroot = webFilter.getConfig().getWebAppRootPath();
            MessageBytes mb = req.requestURI();
            String uri = req.requestURI().toString();                
            fileCache.add(FileCache.DEFAULT_SERVLET_NAME,docroot,uri,
                          req.getResponse().getMimeHeaders(),false);        
        } else if (handlerCode == Interceptor.REQUEST_LINE_PARSED) {
            ByteChunk requestURI = req.requestURI().getByteChunk(); 
            if (fileCache.sendCache(requestURI.getBytes(), requestURI.getStart(),
                                requestURI.getLength(), writer,
                                keepAlive(req))){
                return Interceptor.BREAK;   
            }
        }     
        return Interceptor.CONTINUE;
    }
       
    
    /**
     * Get the keep-alive header.
     */
    private boolean keepAlive(Request request){
        MimeHeaders headers = request.getMimeHeaders();

        // Check connection header
        MessageBytes connectionValueMB = headers.getValue("connection");
        if (connectionValueMB != null) {
            ByteChunk connectionValueBC = connectionValueMB.getByteChunk();
            if (findBytes(connectionValueBC, Constants.CLOSE_BYTES) != -1) {
                return false;
            } else if (findBytes(connectionValueBC, 
                                 Constants.KEEPALIVE_BYTES) != -1) {
                return true;
            }
        }
        return true;
    }
    
    
    /**
     * Specialized utility method: find a sequence of lower case bytes inside
     * a ByteChunk.
     */
    protected int findBytes(ByteChunk bc, byte[] b) {

        byte first = b[0];
        byte[] buff = bc.getBuffer();
        int start = bc.getStart();
        int end = bc.getEnd();

        // Look for first char 
        int srcEnd = b.length;

        for (int i = start; i <= (end - srcEnd); i++) {
            if (Ascii.toLower(buff[i]) != first) continue;
            // found first char, now look for a match
            int myPos = i+1;
            for (int srcPos = 1; srcPos < srcEnd; ) {
                    if (Ascii.toLower(buff[myPos++]) != b[srcPos++])
                break;
                    if (srcPos == srcEnd) return i - start; // found it
            }
        }
        return -1;
    }
}

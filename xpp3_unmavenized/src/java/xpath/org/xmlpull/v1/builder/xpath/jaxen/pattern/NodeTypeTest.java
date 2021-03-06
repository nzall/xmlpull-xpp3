/*
 * $Header: /l/extreme/cvspub/XPP3/java/src/java/xpath/org/xmlpull/v1/builder/xpath/jaxen/pattern/NodeTypeTest.java,v 1.1 2004/06/16 15:55:41 aslom Exp $
 * $Revision: 1.1 $
 * $Date: 2004/06/16 15:55:41 $
 *
 * ====================================================================
 *
 * Copyright (C) 2000-2002 bob mcwhirter & James Strachan.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions, and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions, and the disclaimer that follows 
 *    these conditions in the documentation and/or other materials 
 *    provided with the distribution.
 *
 * 3. The name "Jaxen" must not be used to endorse or promote products
 *    derived from this software without prior written permission.  For
 *    written permission, please contact license@jaxen.org.
 * 
 * 4. Products derived from this software may not be called "Jaxen", nor
 *    may "Jaxen" appear in their name, without prior written permission
 *    from the Jaxen Project Management (pm@jaxen.org).
 * 
 * In addition, we request (but do not require) that you include in the 
 * end-user documentation provided with the redistribution and/or in the 
 * software itself an acknowledgement equivalent to the following:
 *     "This product includes software developed by the
 *      Jaxen Project (http://www.jaxen.org/)."
 * Alternatively, the acknowledgment may be graphical using the logos 
 * available at http://www.jaxen.org/
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE Jaxen AUTHORS OR THE PROJECT
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 * ====================================================================
 * This software consists of voluntary contributions made by many 
 * individuals on behalf of the Jaxen Project and was originally 
 * created by bob mcwhirter <bob@werken.com> and 
 * James Strachan <jstrachan@apache.org>.  For more information on the 
 * Jaxen Project, please see <http://www.jaxen.org/>.
 * 
 * $Id: NodeTypeTest.java,v 1.1 2004/06/16 15:55:41 aslom Exp $
 */

package org.xmlpull.v1.builder.xpath.jaxen.pattern;

import org.xmlpull.v1.builder.xpath.jaxen.Context;

/** <p><code>NodeTypeTest</code> matches if the node is of a certain type 
  * such as element, attribute, comment, text, processing instruction and so forth.</p>
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision: 1.1 $
  */
public class NodeTypeTest extends NodeTest {
    
    public static final NodeTypeTest DOCUMENT_TEST 
        = new NodeTypeTest( DOCUMENT_NODE );
    
    public static final NodeTypeTest ELEMENT_TEST 
        = new NodeTypeTest( ELEMENT_NODE );
    
    public static final NodeTypeTest ATTRIBUTE_TEST 
        = new NodeTypeTest( ATTRIBUTE_NODE );
    
    public static final NodeTypeTest COMMENT_TEST 
        = new NodeTypeTest( COMMENT_NODE );
    
    public static final NodeTypeTest TEXT_TEST 
        = new NodeTypeTest( TEXT_NODE );
    
    public static final NodeTypeTest PROCESSING_INSTRUCTION_TEST 
        = new NodeTypeTest( PROCESSING_INSTRUCTION_NODE );
    
    public static final NodeTypeTest NAMESPACE_TEST 
        = new NodeTypeTest( NAMESPACE_NODE );
    
    
    private short nodeType;
    
    public NodeTypeTest(short nodeType)   
    {
        this.nodeType = nodeType;
    }
        
    /** @return true if the pattern matches the given node
      */
    public boolean matches( Object node, Context context ) 
    {
        return nodeType == context.getNavigator().getNodeType( node );
    }
    
    public double getPriority() 
    {
        return -0.5;
    }


    public short getMatchType() 
    {
        return nodeType;
    }
    
    public String getText() 
    {
        switch (nodeType) 
        {
            case ELEMENT_NODE:
                return "child()";
            case ATTRIBUTE_NODE:
                return "@*";
            case NAMESPACE_NODE:
                return "namespace()";
            case DOCUMENT_NODE:
                return "/";
            case COMMENT_NODE:
                return "comment()";
            case TEXT_NODE:
                return "text()";
            case PROCESSING_INSTRUCTION_NODE:
                return "processing-instruction()";
        }
        return "";
    }
    
    public String toString()
    {
        return super.toString() + "[ type: " + nodeType + " ]";
    }
}

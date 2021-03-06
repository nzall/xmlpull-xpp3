/*
 * $Header: /l/extreme/cvspub/XPP3/java/src/java/xpath/org/xmlpull/v1/builder/xpath/jaxen/JaxenHandler.java,v 1.2 2005/08/11 22:44:00 aslom Exp $
 * $Revision: 1.2 $
 * $Date: 2005/08/11 22:44:00 $
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
 * $Id: JaxenHandler.java,v 1.2 2005/08/11 22:44:00 aslom Exp $
 */



package org.xmlpull.v1.builder.xpath.jaxen;

import org.xmlpull.v1.builder.xpath.jaxen.expr.XPathFactory;
import org.xmlpull.v1.builder.xpath.jaxen.expr.DefaultXPathFactory;
import org.xmlpull.v1.builder.xpath.jaxen.expr.XPathExpr;
import org.xmlpull.v1.builder.xpath.jaxen.expr.LocationPath;
import org.xmlpull.v1.builder.xpath.jaxen.expr.FilterExpr;
import org.xmlpull.v1.builder.xpath.jaxen.expr.Expr;
import org.xmlpull.v1.builder.xpath.jaxen.expr.Step;
import org.xmlpull.v1.builder.xpath.jaxen.expr.Predicate;
import org.xmlpull.v1.builder.xpath.jaxen.expr.Predicated;
import org.xmlpull.v1.builder.xpath.jaxen.expr.FunctionCallExpr;

import org.xmlpull.v1.builder.xpath.saxpath.XPathHandler;
import org.xmlpull.v1.builder.xpath.saxpath.Operator;

import java.util.LinkedList;
import java.util.Iterator;

/** SAXPath <code>XPathHandler</code> implementation capable
 *  of building Jaxen expression trees which can walk various
 *  different object models.
 *
 *  @author bob mcwhirter (bob@werken.com)
 */
public class JaxenHandler implements XPathHandler
{
    private XPathFactory xpathFactory;
    private XPathExpr xpath;
    protected boolean simplified;

    protected LinkedList stack;

    /** Construct.
     */
    public JaxenHandler()
    {
        this.stack        = new LinkedList();
        this.xpathFactory = new DefaultXPathFactory();
    }
    
    /** Set the Jaxen <code>XPathFactory</code> to use
     *  during the parse to construct the XPath expression tree.
     *
     *  @param xpathFactory The factory to use during the parse.
     */
    public void setXPathFactory(XPathFactory xpathFactory)
    {
        this.xpathFactory = xpathFactory;
    }

    /** Retrieve the Jaxen <code>XPathFactory</code> used
     *  during the parse to construct the XPath expression tree.
     *
     *  @return The <code>XPathFactory</code> used during the parse.
     */
    public XPathFactory getXPathFactory()
    {
        return this.xpathFactory;
    }

    /** Retrieve the simplified Jaxen XPath expression tree.
     *
     *  <p>
     *  This method is only valid once <code>XPathReader.parse(...)</code>
     *  successfully returned.
     *  </p>
     *
     *  @return The XPath expression tree.
     */
    public XPathExpr getXPathExpr()
    {
        return getXPathExpr( true );
    }

    /** Retrieve the Jaxen XPath expression tree, optionally
     *  simplified.
     *
     *  <p>
     *  This method is only valid once <code>XPathReader.parse(...)</code>
     *  successfully returned.
     *  </p>
     *
     *  @return The XPath expression tree.
     */
    public XPathExpr getXPathExpr(boolean shouldSimplify)
    {
        if ( shouldSimplify && ! this.simplified )
        {
            //System.err.println("simplifyin....");
            this.xpath.simplify();
            this.simplified = true;
        }

        return this.xpath;
    }

    public void startXPath() throws JaxenException
    {
        //System.err.println("startXPath()");
        this.simplified = false;
        pushFrame();
    }
    
    public void endXPath() throws JaxenException
    {
        //System.err.println("endXPath()");
        this.xpath = getXPathFactory().createXPath( (Expr) pop() );

        popFrame();
    }

    public void startPathExpr() throws JaxenException
    {
        //System.err.println("startPathExpr()");
        pushFrame();
    }

    public void endPathExpr() throws JaxenException
    {
        //System.err.println("endPathExpr()");

        // PathExpr ::=   LocationPath
        //              | FilterExpr
        //              | FilterExpr / RelativeLocationPath
        //              | FilterExpr // RelativeLocationPath
        //
        // If the current stack-frame has two items, it's a
        // FilterExpr and a LocationPath (of some flavor).
        //
        // If the current stack-frame has one item, it's simply
        // a FilterExpr, and more than like boils down to a
        // primary expr of some flavor.  But that's for another
        // method...

        FilterExpr   filterExpr;
        LocationPath locationPath;

        Object       popped;

        //System.err.println("stackSize() == " + stackSize() );

        if ( stackSize() == 2 )
        {
            locationPath = (LocationPath) pop();
            filterExpr   = (FilterExpr) pop();
        }
        else
        {
            popped = pop();

            if ( popped instanceof LocationPath )
            {
                locationPath = (LocationPath) popped;
                filterExpr   = null;
            }
            else
            {
                locationPath = null;
                filterExpr   = (FilterExpr) popped;
            }
        }
        popFrame();

        push( getXPathFactory().createPathExpr( filterExpr,
                                               locationPath ) );
    }

    public void startAbsoluteLocationPath() throws JaxenException
    {
        //System.err.println("startAbsoluteLocationPath()");
        pushFrame();

        push( getXPathFactory().createAbsoluteLocationPath() );
    }

    public void endAbsoluteLocationPath() throws JaxenException
    {
        //System.err.println("endAbsoluteLocationPath()");
        endLocationPath();
    }

    public void startRelativeLocationPath() throws JaxenException
    {
        //System.err.println("startRelativeLocationPath()");
        pushFrame();

        push( getXPathFactory().createRelativeLocationPath() );
    }

    public void endRelativeLocationPath() throws JaxenException
    {
        //System.err.println("endRelativeLocationPath()");
        endLocationPath();
    }

    protected void endLocationPath() throws JaxenException
    {
        LocationPath path = (LocationPath) peekFrame().removeFirst();

        addSteps( path,
                  popFrame().iterator() );

        push( path );
    }

    protected void addSteps(LocationPath locationPath,
                          Iterator stepIter)
    {
        while ( stepIter.hasNext() )
        {
            locationPath.addStep( (Step) stepIter.next() );
        }
    }

    public void startNameStep(int axis,
                              String prefix,
                              String localName) throws JaxenException
    {
        //System.err.println("startNameStep(" + axis + ", " + prefix + ", " + localName + ")");
        pushFrame();

        push( getXPathFactory().createNameStep( axis,
                                               prefix,
                                               localName ) );
    }

    public void endNameStep() throws JaxenException
    {
        //System.err.println("endNameStep()");
        endStep();
    }
    
    public void startTextNodeStep(int axis) throws JaxenException
    {
        //System.err.println("startTextNodeStep()");
        pushFrame();
        
        push( getXPathFactory().createTextNodeStep( axis ) );
    }
    
    public void endTextNodeStep() throws JaxenException
    {
        //System.err.println("endTextNodeStep()");
        endStep();
    }

    public void startCommentNodeStep(int axis) throws JaxenException
    {
        //System.err.println("startCommentNodeStep()");
        pushFrame();

        push( getXPathFactory().createCommentNodeStep( axis ) );
    }

    public void endCommentNodeStep() throws JaxenException
    {
        //System.err.println("endCommentNodeStep()");
        endStep();
    }
        
    public void startAllNodeStep(int axis) throws JaxenException
    {
        //System.err.println("startAllNodeStep()");
        pushFrame();

        push( getXPathFactory().createAllNodeStep( axis ) );
    }

    public void endAllNodeStep() throws JaxenException
    {
        //System.err.println("endAllNodeStep()");
        endStep();
    }

    public void startProcessingInstructionNodeStep(int axis,
                                                   String name) throws JaxenException
    {
        //System.err.println("startProcessingInstructionStep()");
        pushFrame();

        push( getXPathFactory().createProcessingInstructionNodeStep( axis,
                                                                    name ) );
    }
    
    public void endProcessingInstructionNodeStep() throws JaxenException
    {
        //System.err.println("endProcessingInstructionStep()");
        endStep();
    }

    protected void endStep()
    {
        Step step = (Step) peekFrame().removeFirst();

        addPredicates( step,
                       popFrame().iterator() );

        push( step );
    }
    
    public void startPredicate() throws JaxenException
    {
        //System.err.println("startPredicate()");
        pushFrame();
    }
    
    public void endPredicate() throws JaxenException
    {
        //System.err.println("endPredicate()");
        Predicate predicate = getXPathFactory().createPredicate( (Expr) pop() );

        popFrame();

        push( predicate );
    }

    public void startFilterExpr() throws JaxenException
    {
        //System.err.println("startFilterExpr()");
        pushFrame();
    }

    public void endFilterExpr() throws JaxenException
    {
        //System.err.println("endFilterExpr()");
        Expr expr = (Expr) peekFrame().removeFirst();
        
        FilterExpr filter = getXPathFactory().createFilterExpr( expr );

        Iterator predIter = popFrame().iterator();

        addPredicates( filter,
                       predIter );

        push( filter );
    }

    protected void addPredicates(Predicated obj,
                               Iterator predIter)
    {
        while ( predIter.hasNext() )
        {
            obj.addPredicate( (Predicate) predIter.next() );
        }
    }

    protected void returnExpr()
    {
        Expr expr = (Expr) pop();
        popFrame();
        push( expr );
    }

    public void startOrExpr() throws JaxenException
    {
        //System.err.println("startOrExpr()");
    }

    public void endOrExpr(boolean create) throws JaxenException
    {
        //System.err.println("endOrExpr()");

        if ( create )
        {
            //System.err.println("makeOrExpr");
            Expr rhs = (Expr) pop();
            Expr lhs = (Expr) pop();

            push( getXPathFactory().createOrExpr( lhs,
                                                 rhs ) );
        }
    }

    public void startAndExpr() throws JaxenException
    {
        //System.err.println("startAndExpr()");
    }

    public void endAndExpr(boolean create) throws JaxenException
    {
        //System.err.println("endAndExpr()");

        if ( create )
        {
            //System.err.println("makeAndExpr");

            Expr rhs = (Expr) pop();
            Expr lhs = (Expr) pop();

            push( getXPathFactory().createAndExpr( lhs,
                                                  rhs ) );
        }
    }

    public void startEqualityExpr() throws JaxenException
    {
        //System.err.println("startEqualityExpr()");
    }

    public void endEqualityExpr(int operator) throws JaxenException
    {
        //System.err.println("endEqualityExpr(" + operator + ")");

        if ( operator != Operator.NO_OP )
        {
            //System.err.println("makeEqualityExpr");
            
            Expr rhs = (Expr) pop();
            Expr lhs = (Expr) pop();
            
            push( getXPathFactory().createEqualityExpr( lhs,
                                                        rhs,
                                                        operator ) );
        }
    }

    public void startRelationalExpr() throws JaxenException
    {
        //System.err.println("startRelationalExpr()");
    }

    public void endRelationalExpr(int operator) throws JaxenException
    {
        //System.err.println("endRelationalExpr(" + operator + ")");

        if ( operator != Operator.NO_OP )
        {
            //System.err.println("makeRelationalExpr");

            Expr rhs = (Expr) pop();
            Expr lhs = (Expr) pop();

            push( getXPathFactory().createRelationalExpr( lhs,
                                                         rhs,
                                                         operator ) );
        }
    }

    public void startAdditiveExpr() throws JaxenException
    {
        //System.err.println("startAdditiveExpr()");
    }

    public void endAdditiveExpr(int operator) throws JaxenException
    {
        //System.err.println("endAdditiveExpr(" + operator + ")");

        if ( operator != Operator.NO_OP )
        {
            //System.err.println("makeAdditiveExpr");
            
            Expr rhs = (Expr) pop();
            Expr lhs = (Expr) pop();
            
            push( getXPathFactory().createAdditiveExpr( lhs,
                                                        rhs,
                                                        operator ) );
        }
    }

    public void startMultiplicativeExpr() throws JaxenException
    {
        //System.err.println("startMultiplicativeExpr()");
    }

    public void endMultiplicativeExpr(int operator) throws JaxenException
    {
        //System.err.println("endMultiplicativeExpr(" + operator + ")");

        if ( operator != Operator.NO_OP )
        {
            //System.err.println("makeMulitiplicativeExpr");

            Expr rhs = (Expr) pop();
            Expr lhs = (Expr) pop();
            
            push( getXPathFactory().createMultiplicativeExpr( lhs,
                                                             rhs,
                                                             operator ) );
        }
    }

    public void startUnaryExpr() throws JaxenException
    {
        //System.err.println("startUnaryExpr()");
    }

    public void endUnaryExpr(int operator) throws JaxenException
    {
        //System.err.println("endUnaryExpr(" + operator + ")");

        if ( operator != Operator.NO_OP )
        {
            push( getXPathFactory().createUnaryExpr( (Expr) pop(),
                                                    operator ) );
        }
    }

    public void startUnionExpr() throws JaxenException
    {
        //System.err.println("startUnionExpr()");
    }

    public void endUnionExpr(boolean create) throws JaxenException
    {
        //System.err.println("endUnionExpr()");

        if ( create )
        {
            //System.err.println("makeUnionExpr");

            Expr rhs = (Expr) pop();
            Expr lhs = (Expr) pop();

            push( getXPathFactory().createUnionExpr( lhs,
                                                    rhs ) );
        }
    }

    public void number(int number) throws JaxenException
    {
        //System.err.println("number(" + number + ")");
        push( getXPathFactory().createNumberExpr( number ) );
    }

    public void number(double number) throws JaxenException
    {
        //System.err.println("number(" + number + ")");
        push( getXPathFactory().createNumberExpr( number ) );
    }

    public void literal(String literal) throws JaxenException
    {
        push( getXPathFactory().createLiteralExpr( literal ) );
    }

    public void variableReference(String prefix,
                                  String variableName) throws JaxenException
    {
        push( getXPathFactory().createVariableReferenceExpr( prefix,
                                                             variableName ) );
    }

    public void startFunction(String prefix,
                              String functionName) throws JaxenException
    {
        pushFrame();
        push( getXPathFactory().createFunctionCallExpr( prefix,
                                                        functionName ) );
    }

    public void endFunction() throws JaxenException
    {
        FunctionCallExpr function = (FunctionCallExpr) peekFrame().removeFirst();

        addParameters( function,
                       popFrame().iterator() );

        push( function );
    }

    protected void addParameters(FunctionCallExpr function,
                               Iterator paramIter)
    {
        while ( paramIter.hasNext() )
        {
            function.addParameter( (Expr) paramIter.next() );
        }
    }

    protected int stackSize()
    {
        return peekFrame().size();
    }

    protected void push(Object obj)
    {
        peekFrame().addLast( obj );

        //System.err.println("push(" + this.stack.size() + "/" + peekFrame().size() + ") == " + obj );
    }

    protected Object pop()
    {
        //System.err.println("pop(" + this.stack.size() + "/" + peekFrame().size() + ")");
        return peekFrame().removeLast();
    }

    protected boolean canPop()
    {
        return ( peekFrame().size() > 0 );
    }

    protected void pushFrame()
    {
        this.stack.addLast( new LinkedList() );
        //System.err.println("pushFrame(" + this.stack.size() + ")");
    }

    protected LinkedList popFrame()
    {
        //System.err.println("popFrame(" + this.stack.size() + ")");
        return (LinkedList) this.stack.removeLast();
    }

    protected LinkedList peekFrame()
    {
        return (LinkedList) this.stack.getLast();
    }
}

/*
 * (c) Copyright 2009 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package dev;

import java.util.ArrayList ;
import java.util.List ;

import com.hp.hpl.jena.sparql.algebra.Op ;
import com.hp.hpl.jena.sparql.algebra.TransformCopy ;
import com.hp.hpl.jena.sparql.algebra.op.OpDisjunction ;
import com.hp.hpl.jena.sparql.algebra.op.OpFilter ;
import com.hp.hpl.jena.sparql.algebra.op.OpUnion ;
import com.hp.hpl.jena.sparql.algebra.opt.TransformEqualityFilter ;
import com.hp.hpl.jena.sparql.expr.E_LogicalOr ;
import com.hp.hpl.jena.sparql.expr.Expr ;
import com.hp.hpl.jena.sparql.expr.ExprList ;
import com.hp.hpl.jena.sparql.util.ExprUtils ;

/**Filter disjunction.
 * Merge with TransformFilterImprove
 */

public class TransformFilterDisjunction extends TransformCopy
{
    public TransformFilterDisjunction() {}
    
    // TODO Equality only.
    
    @Override
    public Op transform(OpFilter opFilter, Op subOp)
    {
        //ExprUtils.isSubstitutionSafe
        
        ExprList exprList = opFilter.getExprs() ;
        // If disjunction, rewrite to union.
        if ( exprList.size() == 1 )
        {
            // Need to iterate on LHS and RHS.
            Expr expr = exprList.get(0) ;
            Op op2 = expandDisjunction(expr, subOp) ;
            if ( op2 != null )
                return op2 ;
            // Fall thorugh.
        }
        
        // Do nothing special.
        //return OpFilter.filter(exprList, subOp) ;
        return super.transform(opFilter, subOp) ;
    }
    
    public static Op expandDisjunction(Expr expr, Op subOp)
    {
        if ( !( expr instanceof E_LogicalOr ) )
            return null ;

        List<Expr> exprList = explodeDisjunction(null, expr) ;
        // Need op UnionN?
        if ( exprList == null )
            return null ;
        
        Op op = null ;
        for ( Expr e : exprList )
        {
            
            Op op2 = TransformEqualityFilter.processFilterOrOpFilter(e, subOp) ;
            op = OpDisjunction.create(op, op2) ;
        }
        return op ;
    }

    /** Explode a expr into a list of disjunction */
    private static List<Expr> explodeDisjunction(List<Expr> exprList, Expr expr)
    {
        if ( !( expr instanceof E_LogicalOr ) )
        {
            exprList.add(expr) ;
            return exprList ;
        }
        
        E_LogicalOr exprOr = (E_LogicalOr)expr ;
        Expr e1 =  exprOr.getArg1() ;
        Expr e2 =  exprOr.getArg2() ;
        if ( exprList == null )
            exprList = new ArrayList<Expr>() ;
        explodeDisjunction(exprList, e1) ; 
        explodeDisjunction(exprList, e2) ;
        return exprList ;
    }
}

/*
 * (c) Copyright 2009 Hewlett-Packard Development Company, LP
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
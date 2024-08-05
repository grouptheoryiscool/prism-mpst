//==============================================================================
//	
//	Copyright (c) 2002-
//	Authors:
//	* Dave Parker <d.a.parker@cs.bham.ac.uk> (University of Birmingham/Oxford)
//	
//------------------------------------------------------------------------------
//	
//	This file is part of PRISM.
//	
//	PRISM is free software; you can redistribute it and/or modify
//	it under the terms of the GNU General Public License as published by
//	the Free Software Foundation; either version 2 of the License, or
//	(at your option) any later version.
//	
//	PRISM is distributed in the hope that it will be useful,
//	but WITHOUT ANY WARRANTY; without even the implied warranty of
//	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//	GNU General Public License for more details.
//	
//	You should have received a copy of the GNU General Public License
//	along with PRISM; if not, write to the Free Software Foundation,
//	Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//	
//==============================================================================

package parser.ast;

import java.util.ArrayList;
import parser.visitor.ASTVisitor;
import parser.visitor.DeepCopy;
import prism.PrismLangException;

// Class representing Selection

public class Branching extends ProbSessType {
    protected int nodes = -1; 
    protected ArrayList<RecvBranch> branches;
    protected String role;

    public Branching(ArrayList<RecvBranch> branches, String role) {
        this.branches = branches;
        this.role = role;
    }

    public int getNodes() {
        if (nodes == -1) {
            nodes = 1;
            for (RecvBranch b : branches) {
                nodes += b.getNodes();
            }
        }
        return nodes;
    }   

    public String toString() {
        String s = role + "&{";
        for (RecvBranch b : branches) {
            s += b.toString();
            s += ";";
        }
        return s + "}";
    }

    /* change all this */
    public Object accept(ASTVisitor v) throws PrismLangException {
        return this;
    }
    
    public ASTElement deepCopy(DeepCopy copier) throws PrismLangException {    
        return this;
    }

}

// ------------------------------------------------------------------------------

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
import parser.ast.Module;
import parser.type.TypeBool;
import parser.type.TypeInt;
import parser.visitor.ASTVisitor;
import parser.visitor.DeepCopy;
import prism.PrismLangException;
import prism.PrismTranslationException;

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

    public String getRole() {
        return role;
    } 

    public String toString() {
        String s = role + "&{";
        for (RecvBranch b : branches) {
            s += b.toString();
            s += ";";
        }
        return s + "}";
    }

    public ArrayList<RecvBranch> getOptions() {
        return branches;
    }

    public Module toModule(String sessRole, ExpressionIdent endVar) throws PrismTranslationException{
        Module module = new Module(sessRole);
        // LATER: this session role should probably be a field of the class
        projectCommands(module, 0, -1, sessRole, endVar);
        return module;
    }

    public void projectCommands(Module m, int k, int r, String sessRole, ExpressionIdent endVar) throws PrismTranslationException{
        ExpressionLiteral trueVal = new ExpressionLiteral(TypeBool.getInstance(), Boolean.valueOf(true));
        ExpressionIdent stateVar = new ExpressionIdent("s_" + role);
        ExpressionLiteral stateVal = new ExpressionLiteral(TypeInt.getInstance(), Integer.valueOf(k));
        ExpressionBinaryOp stateEq = new ExpressionBinaryOp(5, stateVar, stateVal);
        // add a single command for every message choice 
        for (int i = 0; i < branches.size(); i++) {
            RecvBranch b = branches.get(i);
            Command c = new Command();
            String synch = sessRole + "!" + role + "_" + b.getLabel();
            c.setSynch(synch);
            c.setGuard(stateEq);
            Updates updates = new Updates();
            updates.setParent(c);
            Update update = new Update();
            update.setParent(updates);
            int stateAfterChoiceI;
            // LATER: can be made more efficient
            if (!(b.getContinuation() instanceof RecVar)) {
                // set new state
                int toAdd = 0;
                for (int j = 0; j < i; j++) {
                    toAdd += branches.get(j).getNodes();
                }
                stateAfterChoiceI = toAdd + 1 + k + branches.size();
                // if continuation is end then set end to true
                if (b.getContinuation() instanceof TypeEnd) {
                    UpdateElement updateElementEnd = new UpdateElement(endVar, trueVal);
                    update.addElement(updateElementEnd);
                } else {
                    // if continuation is not end or rec we need to project commands
                    int finalState = k + branches.size() + 1 + toAdd;
                    b.getContinuation().projectCommands(m, finalState, r, sessRole, endVar);
                }
            } else {
                stateAfterChoiceI = r;
            }
            ExpressionLiteral stateAfterChoiceIVal = new ExpressionLiteral(TypeInt.getInstance(), Integer.valueOf(stateAfterChoiceI));
            UpdateElement updateElementState = new UpdateElement(stateVar, stateAfterChoiceIVal);
            update.addElement(updateElementState);
            updates.addUpdate(null, update);
            c.setUpdates(updates);
            m.addCommand(c);
        }
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

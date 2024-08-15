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

public class ProbSel extends ProbSessType {
    protected int nodes = -1; 
    protected ArrayList<SelBranch> branches;
    protected String role;

    public ProbSel(ArrayList<SelBranch> branches, String role) {
        this.branches = branches;
        this.role = role;
    }

    public ArrayList<SelBranch> getBranches() {
        return branches;
    }

    public String getRole() {
        return role;
    }

    public int getNodes() {
        if (nodes == -1) {
            nodes = 1;
            for (SelBranch b : branches) {
                nodes += b.getNodes();
            }
        }
        return nodes;
    }   

    public String toString() {
        String s = role + "+{";
        for (SelBranch b : branches) {
            s += b.toString();
            s += ";";
        }
        return s + "}";
    }

    public Module toModule(String sessRole, ExpressionIdent endVar) throws PrismTranslationException{
        Module module = new Module(sessRole);
        // LATER: this session role should probably be a field of the class
        projectCommands(module, 0, -1, sessRole, endVar);
        return module;
    }

    public void projectCommands(Module m, int k, int r, String sessRole, ExpressionIdent endVar) throws PrismTranslationException {
        ExpressionLiteral trueVal = new ExpressionLiteral(TypeBool.getInstance(), Boolean.valueOf(true));
        Command c = new Command();
        ExpressionIdent stateVar = new ExpressionIdent("s_" + role);
        ExpressionLiteral stateVal = new ExpressionLiteral(TypeInt.getInstance(), Integer.valueOf(k));
        ExpressionBinaryOp stateEq = new ExpressionBinaryOp(5, stateVar, stateVal);
        c.setGuard(stateEq);
        //first step that chooses which branch to take
        Updates updates = new Updates();
        updates.setParent(c);
        /* add a single command where the choice is made
        and for every choice, add a command that synchronizes with the choice */
        for (int i = 0; i < branches.size(); i++) {
            SelBranch b = branches.get(i);
            ExpressionInterval interval = b.getInterval();
            int newState = k + i;
            ExpressionLiteral newStateVal = new ExpressionLiteral(TypeInt.getInstance(), Integer.valueOf(newState));
            Update update = new Update();
            update.setParent(updates);
            UpdateElement updateElement = new UpdateElement(stateVar, newStateVal);
            update.addElement(updateElement);
            updates.addUpdate(interval, update);
            // second step that synchronizes with recv branch
            Command c2 = new Command();
            String synch = sessRole + "!" + role + "_" + b.getLabel();
            ExpressionBinaryOp guard = new ExpressionBinaryOp(5, stateVar, newStateVal);
            c2.setGuard(guard);
            Updates updates2 = new Updates();
            updates2.setParent(c2);
            Update update2 = new Update();
            update2.setParent(updates2);
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
                    update2.addElement(updateElementEnd);
                } else {
                    // if continuation is not end or rec we need to project commands
                    int finalState = k + branches.size() + 1 + toAdd;
                    b.getContinuation().projectCommands(m, finalState, r, sessRole, endVar);
                }
            } else {
                stateAfterChoiceI = r;
            }
            ExpressionLiteral stateAfterChoiceIVal = new ExpressionLiteral(TypeInt.getInstance(), Integer.valueOf(stateAfterChoiceI));
            UpdateElement updateElementState2 = new UpdateElement(stateVar, stateAfterChoiceIVal);
            update2.addElement(updateElementState2);
            updates2.addUpdate(null, update2);
            c2.setUpdates(updates2);
            m.addCommand(c2);
        }
        // first step
        c.setUpdates(updates);
        m.addCommand(c);
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

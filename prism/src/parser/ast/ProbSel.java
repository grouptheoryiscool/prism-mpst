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

    public Module toModule(ExpressionIdent parentRole, ExpressionIdent endVar) throws PrismTranslationException{
        // setting module name
        Module module = new Module(parentRole.getName());
        module.setNameASTElement(parentRole);
        // creating state variable
        String stateVarString = "s_" + parentRole.getName();
        ExpressionIdent stateVarIdent = new ExpressionIdent(stateVarString);
        // determine the last state of the module
        int maxState = projectCommands(module, 0, -1, stateVarIdent, endVar, parentRole.getName());
        ExpressionLiteral low = new ExpressionLiteral(TypeInt.getInstance(), Integer.valueOf(0));
        ExpressionLiteral high = new ExpressionLiteral(TypeInt.getInstance(), Integer.valueOf(maxState));
        DeclarationInt declType = new DeclarationInt(low, high); 
        module.addDeclaration(new Declaration(stateVarString, declType));
        // add an end variable to the module
        String endVarString = endVar.getName();
        ExpressionLiteral falseVal = new ExpressionLiteral(TypeBool.getInstance(), Boolean.valueOf(false));
        Declaration endDecl = new Declaration(endVarString, new DeclarationBool());
        endDecl.setStart(falseVal);
        module.addDeclaration(endDecl);
        return module;
    }

    public int projectCommands(
        Module m, 
        int k, 
        int r, 
        ExpressionIdent stateVar, 
        ExpressionIdent endVar, 
        String parent
    ) throws PrismTranslationException {
        ExpressionLiteral trueVal = new ExpressionLiteral(TypeBool.getInstance(), Boolean.valueOf(true));
        Command c = new Command();
        ExpressionLiteral stateVal = new ExpressionLiteral(TypeInt.getInstance(), Integer.valueOf(k));
        ExpressionBinaryOp stateEq = new ExpressionBinaryOp(5, stateVar, stateVal);
        c.setGuard(stateEq);
        //first step that chooses which branch to take
        Updates updates = new Updates();
        updates.setParent(c);
        int finalState = 0; // the max value s_p can take
        int stateAfterChoiceI = k + branches.size() + 1;
        /* add a single command where the choice is made
        and for every choice, add a command that synchronizes with the choice */
        for (int i = 0; i < branches.size(); i++) {
            SelBranch b = branches.get(i);
            ExpressionInterval interval = b.getInterval();
            int newState = k + i + 1;
            ExpressionLiteral newStateVal = new ExpressionLiteral(TypeInt.getInstance(), Integer.valueOf(newState));
            Update update = new Update();
            update.setParent(updates);
            UpdateElement updateElement = new UpdateElement(stateVar, newStateVal);
            update.addElement(updateElement);
            updates.addUpdate(interval, update);
            // second step that synchronizes with recv branch
            Command c2 = new Command();
            ExpressionBinaryOp guard = new ExpressionBinaryOp(5, stateVar, newStateVal);
            c2.setGuard(guard);
            c2.setSynch(parent + "!" + role + "_" + b.getLabel());
            Updates updates2 = new Updates();
            updates2.setParent(c2);
            Update update2 = new Update();
            update2.setParent(updates2);
            if (!(b.getContinuation() instanceof RecVar)) {
                // if continuation is end then set end to true
                if (b.getContinuation() instanceof TypeEnd) {
                    UpdateElement updateElementEnd = new UpdateElement(endVar, trueVal);
                    update2.addElement(updateElementEnd);
                    finalState = stateAfterChoiceI;
                } else {
                    // if continuation is not end or rec we need to project commands
                    finalState = b.getContinuation().projectCommands(m, stateAfterChoiceI, r, stateVar, endVar, parent);
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
            // determine node number for next branch
            stateAfterChoiceI = finalState + 1;
        }
        // first step
        c.setUpdates(updates);
        m.addCommand(c);
        return finalState;
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

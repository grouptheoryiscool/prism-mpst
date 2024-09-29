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
import java.util.HashMap;
import java.util.List;

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

    public Module toModule(
            ExpressionIdent parentRole,
            HashMap<String, Integer> labelsEncoding,
            int[] numLabels,
            ArrayList<Expression> sendStates,
            ArrayList<Expression> pendingStates,
            HashMap<String, ArrayList<Expression>> endStates
    ) throws PrismTranslationException {
        // setting module name
        Module module = new Module(parentRole.getName());
        module.setNameASTElement(parentRole);
        // creating state variable
        String stateVarString = "s_" + parentRole.getName();
        ExpressionIdent stateVarIdent = new ExpressionIdent(stateVarString);
        // creating msglabel variable
        String messageLabelString = "m_" + parentRole.getName();
        //ExpressionIdent msgLabIdent = new ExpressionIdent(messageLabelString);
        // creating msgtype variable
        String messageTypeString = "mtype_" + parentRole.getName();
        //ExpressionIdent msgTyIdent = new ExpressionIdent(messageTypeString);
        // determine the last state of the module
        int maxState = projectCommands(module, 0, -1, stateVarIdent, parentRole.getName(), labelsEncoding, numLabels, sendStates, pendingStates, endStates);
        ExpressionLiteral low = new ExpressionLiteral(TypeInt.getInstance(), Integer.valueOf(0));
        ExpressionLiteral high = new ExpressionLiteral(TypeInt.getInstance(), Integer.valueOf(maxState));
        DeclarationInt declType = new DeclarationInt(low, high);
        // declaration for message label and message type vars
        DeclarationInt declmsgLabel = new DeclarationInt(
                new ExpressionLiteral(TypeInt.getInstance(), Integer.valueOf(-1)),
                new ExpressionLiteral(TypeInt.getInstance(), Integer.valueOf(numLabels[0])));
        DeclarationInt declmsgType = new DeclarationInt(
                new ExpressionLiteral(TypeInt.getInstance(), Integer.valueOf(1)),
                new ExpressionLiteral(TypeInt.getInstance(), Integer.valueOf(4)));
        module.addDeclaration(new Declaration(stateVarString, declType));
        module.addDeclaration(new Declaration(messageLabelString, declmsgLabel));
        module.addDeclaration(new Declaration(messageTypeString, declmsgType));
        return module;
    }

    public int projectCommands (
        Module m, 
        int k, 
        int r, 
        ExpressionIdent stateVar,
        String parent,
        HashMap<String, Integer> labelsEncoding,
        int[] numLabels,
        ArrayList<Expression> sendStates,
        ArrayList<Expression> pendingStates,
        HashMap<String, ArrayList<Expression>> endStates
    ) throws PrismTranslationException {
        String messageLabelString = "m_" + parent;
        ExpressionIdent msgLab = new ExpressionIdent(messageLabelString);
        // creating msgtype variable
        String messageTypeString = "mtype_" + parent;
        ExpressionIdent msgTy = new ExpressionIdent(messageTypeString);
        // ExpressionLiteral trueVal = new ExpressionLiteral(TypeBool.getInstance(), Boolean.valueOf(true));
        Command c = new Command();
        ExpressionLiteral stateVal = new ExpressionLiteral(TypeInt.getInstance(), Integer.valueOf(k));
        ExpressionBinaryOp stateEq = new ExpressionBinaryOp(5, stateVar, stateVal);
        ArrayList<Expression> sendAndPending = new ArrayList<>(List.of(this.pendingFalse, this.sendFalse));
        sendAndPending.add(stateEq);
        Expression commGuard = TypeEnv.createFormulaClause(null, sendAndPending, 4);
        c.setGuard(commGuard);
        c.setSynch(parent + "_" + role);
        //first step that chooses which branch to take
        Updates updates = new Updates();
        int finalState = 0; // the max value s_p can take
        int stateAfterChoiceI = k + branches.size() + 1;
        /* add a single command where the choice is made
        and for every choice, add a command that synchronizes with the choice */
        for (int i = 0; i < branches.size(); i++) {
            SelBranch b = branches.get(i);
            int labelNum = this.getLabelNum(b, labelsEncoding, numLabels);
            int msgTypeInt = 0 ;
            MessageType msgType = b.getMsgType();
            if (!(msgType instanceof BaseType)) {
                throw new PrismTranslationException("Unfortunately, our implementation does not support passing of channels yet. :(");
            } else {
                BaseType basemsgtype = (BaseType) msgType;
                msgTypeInt = basemsgtype.toInt();
            }
            ExpressionLiteral labelNumVal = new ExpressionLiteral(TypeInt.getInstance(), Integer.valueOf(labelNum));
            ExpressionLiteral msgTypeNum = new ExpressionLiteral(TypeInt.getInstance(), Integer.valueOf(msgTypeInt));
            ExpressionInterval interval = b.getInterval();
            int newState = k + i + 1;
            ExpressionLiteral newStateVal = new ExpressionLiteral(TypeInt.getInstance(), Integer.valueOf(newState));
            // mark this as a sending state
            sendStates.add(new ExpressionBinaryOp(5, stateVar, newStateVal));
            Update update = new Update();
            UpdateElement updateElement = new UpdateElement(stateVar, newStateVal);
            // add the message label number and type
            UpdateElement msgLabelUpdate = new UpdateElement(msgLab, labelNumVal);
            UpdateElement msgTyUpdate = new UpdateElement(msgTy, msgTypeNum);
            update.addElement(updateElement);
            update.addElement(msgLabelUpdate);
            update.addElement(msgTyUpdate);
            updates.addUpdate(interval, update);
            // second step that synchronizes with recv branch
            Command c2 = new Command();
            ExpressionBinaryOp guard = new ExpressionBinaryOp(5, stateVar, newStateVal);
            c2.setGuard(guard);
            c2.setSynch(parent + "_" + role + "_" + b.getLabel());
            Updates updates2 = new Updates();
            Update update2 = new Update();
            ExpressionLiteral stateAfterChoiceIVal = new ExpressionLiteral(TypeInt.getInstance(), Integer.valueOf(stateAfterChoiceI));
            if (!(b.getContinuation() instanceof RecVar)) {
                // if continuation is end then mark this as end state
                if (b.getContinuation() instanceof TypeEnd) {
                    ArrayList<Expression> endStatesList;
                    if (endStates.containsKey(parent)) {
                        endStatesList = endStates.get(parent);
                    } else { endStatesList = new ArrayList<>(); endStates.put(parent, endStatesList); }
                    endStatesList.add(new ExpressionBinaryOp(5, stateVar, stateAfterChoiceIVal));
                    finalState = stateAfterChoiceI;
                } else {
                    // if continuation is not end or rec we need to project commands
                    finalState = b.getContinuation().projectCommands(m, stateAfterChoiceI, r, stateVar, parent, labelsEncoding, numLabels, sendStates, pendingStates, endStates);
                }
            } else {
                finalState = stateAfterChoiceI - 1;
                stateAfterChoiceI = r;
            }
            stateAfterChoiceIVal = new ExpressionLiteral(TypeInt.getInstance(), Integer.valueOf(stateAfterChoiceI));
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

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

import java.nio.channels.Channel;
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

    public Module toModule(
            ExpressionIdent parentRole,
            HashMap<String, Integer> labelsEncoding,
            int[] numLabels,
            ArrayList<Expression> sendStates,
            ArrayList<Expression> pendingStates,
            HashMap<String, ArrayList<Expression>> endStates) throws PrismTranslationException {
        Module module = new Module(parentRole.getName());
        module.setNameASTElement(parentRole);
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
        module.addDeclaration(new Declaration(stateVarString, declType));
        // declaration for message label and message type vars
        DeclarationInt declmsgLabel = new DeclarationInt(
                new ExpressionLiteral(TypeInt.getInstance(), Integer.valueOf(-1)),
                new ExpressionLiteral(TypeInt.getInstance(), Integer.valueOf(numLabels[0])));
        DeclarationInt declmsgType = new DeclarationInt(
                new ExpressionLiteral(TypeInt.getInstance(), Integer.valueOf(1)),
                new ExpressionLiteral(TypeInt.getInstance(), Integer.valueOf(4)));
        module.addDeclaration(new Declaration(messageLabelString, declmsgLabel));
        module.addDeclaration(new Declaration(messageTypeString, declmsgType));
        return module;
    }

    public int projectCommands(
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
            // creating msglabel variable
            String messageLabelString = "m_" + role;
            ExpressionIdent msgLab = new ExpressionIdent(messageLabelString);
            // creating msgtype variable
            String messageTypeString = "mtype_" + role;
            ExpressionIdent msgTy = new ExpressionIdent(messageTypeString);
            ExpressionLiteral stateVal = new ExpressionLiteral(TypeInt.getInstance(), Integer.valueOf(k));
            ExpressionBinaryOp stateEq = new ExpressionBinaryOp(5, stateVar, stateVal);
            // the first command that syncs with selection
            Command c1 = new Command();
            c1.setSynch(role + "_" + parent);
            ExpressionLiteral stateValAfterSync = new ExpressionLiteral(TypeInt.getInstance(), Integer.valueOf(k+1));
            ExpressionBinaryOp stateEqAfterSync = new ExpressionBinaryOp(5, stateVar, stateValAfterSync);
            ArrayList<Expression> sendAndPending = new ArrayList<>(List.of(this.pendingFalse, this.sendFalse));
            sendAndPending.add(stateEq);
            Expression commGuard = TypeEnv.createFormulaClause(null, sendAndPending, 4);
            c1.setGuard(commGuard);
            Updates updates1 = new Updates();
            Update update1 = new Update();
            UpdateElement updateElementState1 = new UpdateElement(stateVar, stateValAfterSync);
            update1.addElement(updateElementState1);
            updates1.addUpdate(null, update1);
            c1.setUpdates(updates1);
            m.addCommand(c1);
            // mark this as a pending state
            pendingStates.add(stateEqAfterSync);
            // add a single command for every message choice
            int finalState = 0; // the max value s_p can take
            int stateAfterChoiceI = k + 2;
            for (int i = 0; i < branches.size(); i++) {
                RecvBranch b = branches.get(i);
                Command c = new Command();
                int labelNum = this.getLabelNum(b, labelsEncoding, numLabels);
                ExpressionLiteral labelNumVal = new ExpressionLiteral(TypeInt.getInstance(), Integer.valueOf(labelNum));
                Expression msglabguard = new ExpressionBinaryOp(5, msgLab, labelNumVal);
                int msgTypeInt = 0 ;
                MessageType msgType = b.getMsgType();
                if (!(msgType instanceof BaseType)) {
                    throw new PrismTranslationException("Unfortunately, our implementation does not support passing of channels yet. :(");
                } else {
                    BaseType basemsgtype = (BaseType) msgType;
                    msgTypeInt = basemsgtype.toInt();
                }
                ExpressionLiteral msgTypeVal = new ExpressionLiteral(TypeInt.getInstance(), Integer.valueOf(msgTypeInt));
                Expression msgTypeGuard = new ExpressionBinaryOp(5, msgTy, msgTypeVal);
                ExpressionBinaryOp labType = new ExpressionBinaryOp(4, msgTypeGuard, msglabguard);
                ExpressionBinaryOp guard = new ExpressionBinaryOp(4, labType, stateEqAfterSync);
                c.setSynch(role + "_" + parent + "_" + b.getLabel());
                c.setGuard(guard);
                Updates updates = new Updates();
                // updates.setParent(c);
                Update update = new Update();
                // update.setParent(updates);
                ExpressionLiteral stateAfterChoiceIVal = new ExpressionLiteral(TypeInt.getInstance(), Integer.valueOf(stateAfterChoiceI));
                if (!(b.getContinuation() instanceof RecVar)) {
                    // if continuation is end then mark this as end state
                    if (b.getContinuation() instanceof TypeEnd) {
                        ArrayList<Expression> endStatesList;
                        if (endStates.containsKey(parent)) {
                            endStatesList = endStates.get(parent);
                        } else { endStatesList = new ArrayList<>(); endStates.put(parent, endStatesList);}
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
                UpdateElement updateElementState = new UpdateElement(stateVar, stateAfterChoiceIVal);
                update.addElement(updateElementState);
                updates.addUpdate(null, update);
                c.setUpdates(updates);
                m.addCommand(c);
                // update node number for next branch
                stateAfterChoiceI = finalState + 1;
            }
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

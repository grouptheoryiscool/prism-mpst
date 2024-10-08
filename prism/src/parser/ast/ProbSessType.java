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

import parser.ast.Module;
import prism.PrismTranslationException;
import java.util.ArrayList;
import java.util.HashMap;

// Class representing Probabilistic Session Types 

public abstract class ProbSessType extends MessageType {
    protected int nodes = -1;
    public static final ExpressionUnaryOp sendFalse = new ExpressionUnaryOp(1, new ExpressionIdent("send"));
    public static final ExpressionUnaryOp pendingFalse = new ExpressionUnaryOp(1, new ExpressionIdent("pending"));

    public abstract int getNodes();
    public abstract String toString();
    public abstract Module toModule(ExpressionIdent parentRole,
                                    // ExpressionIdent endVar,
                                    HashMap<String, Integer> labelsEncoding,
                                    int[] numLabels,
                                    ArrayList<Expression> sendStates,
                                    ArrayList<Expression> pendingStates,
                                    HashMap<String, ArrayList<Expression>> endStates)
            throws PrismTranslationException;
    public abstract int projectCommands(Module m, int currentState, int recState, ExpressionIdent parentRole,
                                        //ExpressionIdent endVar,
                                        String parent,
                                        HashMap<String, Integer> labelsEncoding,
                                        int[] numLabels,
                                        ArrayList<Expression> sendStates,
                                        ArrayList<Expression> pendingStates,
                                        HashMap<String, ArrayList<Expression>> endStates) throws PrismTranslationException;

    public int getLabelNum(SelBranch b, HashMap<String, Integer> labelsEncoding, int[] numLabels) {
        String label = b.getLabel();
        if (labelsEncoding.containsKey(label)) {
            return labelsEncoding.get(label);
        } else {
            numLabels[0] = numLabels[0] + 1;
            labelsEncoding.put(label, numLabels[0]);
            return numLabels[0];
        }
    }

    public int getLabelNum(RecvBranch b, HashMap<String, Integer> labelsEncoding, int[] numLabels) {
        String label = b.getLabel();
        if (labelsEncoding.containsKey(label)) {
            return labelsEncoding.get(label);
        } else {
            numLabels[0] = numLabels[0] + 1;
            labelsEncoding.put(label, numLabels[0]);
            return numLabels[0];
        }
    }

}

// ------------------------------------------------------------------------------

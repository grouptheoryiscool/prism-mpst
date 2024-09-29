package parser.ast;

import parser.ast.Module;
import parser.type.TypeBool;
import parser.type.TypeInt;
import parser.visitor.ASTVisitor;
import parser.visitor.DeepCopy;
import prism.PrismLangException;
import prism.PrismTranslationException;

import java.util.ArrayList;
import java.util.HashMap;

public class TypeEnd extends ProbSessType {

    public String toString() {
        return "END";
    }

    public int getNodes() {
        return 1;
    }

    public Module toModule(
            ExpressionIdent parentRole,
            // ExpressionIdent endVar,
            HashMap<String, Integer> labelsEncoding,
            int[] numLabels,
            ArrayList<Expression> sendStates,
            ArrayList<Expression> pendingStates,
            HashMap<String, ArrayList<Expression>> endStates
    ) throws PrismTranslationException {
        throw new PrismTranslationException("Invalid translation, EndType does not have commands to project");
    }

    public int projectCommands (Module m, int currentState, int recState, ExpressionIdent parentRole,
                                //ExpressionIdent endVar,
                                String parent,
                                HashMap<String, Integer> labelsEncoding,
                                int[] numLabels,
                                ArrayList<Expression> sendStates,
                                ArrayList<Expression> pendingStates,
                                HashMap<String, ArrayList<Expression>> endStates) throws PrismTranslationException {
        // This should never be called.
        throw new PrismTranslationException("Invalid translation, EndType does not have commands to project");
    }
    /* change all this */
    public Object accept(ASTVisitor v) {
        return this;
    }
    
    public ASTElement deepCopy(DeepCopy copier) throws PrismLangException {    
        return this;
    }

}
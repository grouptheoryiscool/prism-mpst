package parser.ast;

import parser.ast.Module;
import parser.visitor.ASTVisitor;
import parser.visitor.DeepCopy;
import prism.PrismLangException;
import prism.PrismTranslationException;

import java.util.ArrayList;
import java.util.HashMap;

public class RecVar extends ProbSessType {
    protected String recvar;

    public RecVar(String recvar) {
        this.recvar = recvar;
    }

    public String getRecVar() {
        return this.recvar;
    }

    public String toString() {
        return this.recvar;
    }

    public int getNodes() {
        return 0;
    }

    public Module toModule(
            ExpressionIdent parentRole,
            // ExpressionIdent endVar,
            HashMap<String, Integer> labelsEncoding,
            int numLabels,
            ArrayList<ExpressionBinaryOp> sendStates,
            ArrayList<ExpressionBinaryOp> pendingStates,
            ArrayList<ExpressionBinaryOp> endStates) throws PrismTranslationException {
        // This should never be called.
        throw new PrismTranslationException("Invalid translation, RecVar cannot be translated to module");
    }

    public int projectCommands(Module m,
                               int k,
                               int r,
                               ExpressionIdent stateVar,
                               String parent,
                               HashMap<String, Integer> labelsEncoding,
                               int numLabels,
                               ArrayList<ExpressionBinaryOp> sendStates,
                               ArrayList<ExpressionBinaryOp> pendingStates,
                               ArrayList<ExpressionBinaryOp> endStates) throws PrismTranslationException {
        // This should never be called.
        throw new PrismTranslationException("Invalid translation, RecVar cannot be translated to module");
    }

    /* change all this */
    public Object accept(ASTVisitor v) throws PrismLangException {
        return this;
    }
    
    public ASTElement deepCopy(DeepCopy copier) throws PrismLangException {    
        return this;
    }
}
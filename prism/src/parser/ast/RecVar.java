package parser.ast;

import parser.ast.Module;
import parser.visitor.ASTVisitor;
import parser.visitor.DeepCopy;
import prism.PrismLangException;
import prism.PrismTranslationException;

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

    public Module toModule(String parentRole, ExpressionIdent endVar) throws PrismTranslationException {
        // This should never be called.
        throw new PrismTranslationException("Invalid translation, RecVar cannot be translated to module");
    }

    public void projectCommands(Module m, int k, int r, String sessRole, ExpressionIdent endVar) throws PrismTranslationException {
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
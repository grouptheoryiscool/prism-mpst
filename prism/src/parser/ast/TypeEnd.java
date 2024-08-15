package parser.ast;

import parser.ast.Module;
import parser.visitor.ASTVisitor;
import parser.visitor.DeepCopy;
import prism.PrismLangException;
import prism.PrismTranslationException;

public class TypeEnd extends ProbSessType {

    public String toString() {
        return "END";
    }

    public int getNodes() {
        return 0;
    }

    public Module toModule(String parentRole, ExpressionIdent endVar) throws PrismTranslationException {
        Module module = new Module(parentRole);
        return module;
    }

    public void projectCommands (Module m, int k, int r, String sessRole, ExpressionIdent endVar) throws PrismTranslationException {
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
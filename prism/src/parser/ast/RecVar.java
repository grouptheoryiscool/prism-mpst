package parser.ast;

import parser.visitor.ASTVisitor;
import parser.visitor.DeepCopy;
import prism.PrismLangException;

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

    /* change all this */
    public Object accept(ASTVisitor v) throws PrismLangException {
        return this;
    }
    
    public ASTElement deepCopy(DeepCopy copier) throws PrismLangException {    
        return this;
    }
}
package parser.ast;

import parser.visitor.ASTVisitor;
import parser.visitor.DeepCopy;
import prism.PrismLangException;

public class RecSessType extends ProbSessType {
    protected RecVar recvar;
    protected ProbSessType body;

    public RecSessType(RecVar recvar, ProbSessType body) {
        this.recvar = recvar;
        this.body = body;
    }

    public RecVar getRecVar() {
        return this.recvar;
    }

    public ProbSessType body() {
        return this.recvar;
    }

    public String toString() {
        return "nu " + this.recvar.toString() + ".(" + this.body.toString() + ")";
    }

    public int getNodes() {
        return this.body.getNodes();
    }

    /* change all this */
    public Object accept(ASTVisitor v) throws PrismLangException {
        return this;
    }
    
    public ASTElement deepCopy(DeepCopy copier) throws PrismLangException {    
        return this;
    }

}
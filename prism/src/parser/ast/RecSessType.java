package parser.ast;

import parser.ast.Module;
import parser.visitor.ASTVisitor;
import parser.visitor.DeepCopy;
import prism.PrismLangException;
import prism.PrismTranslationException;

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

    public ProbSessType getBody() {
        return this.body;
    }

    public String toString() {
        return "nu " + this.recvar.toString() + ".(" + this.body.toString() + ")";
    }

    public int getNodes() {
        return this.body.getNodes();
    }

    public Module toModule(String parentRole, ExpressionIdent endVar) throws PrismTranslationException {
        Module module = new Module(parentRole);
        projectCommands(module, 0, 0, parentRole, endVar);
        return module;
    }

    public void projectCommands(Module m, int k, int r, String parentRole, ExpressionIdent endVar) throws PrismTranslationException {
        this.body.projectCommands(m, k, k, parentRole, endVar);
    }

    /* change all this */
    public Object accept(ASTVisitor v) throws PrismLangException {
        return this;
    }
    
    public ASTElement deepCopy(DeepCopy copier) throws PrismLangException {    
        return this;
    }

}
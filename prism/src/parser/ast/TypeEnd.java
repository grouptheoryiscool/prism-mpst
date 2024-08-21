package parser.ast;

import parser.ast.Module;
import parser.type.TypeBool;
import parser.type.TypeInt;
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

    public Module toModule(ExpressionIdent parentRole, ExpressionIdent endVar) throws PrismTranslationException {
        // set module name
        Module module = new Module(parentRole.getName());
        module.setNameASTElement(parentRole);
        // state variable for parent role
        String stateVarString = "s_" + parentRole.getName();
        ExpressionIdent stateVarIdent = new ExpressionIdent(stateVarString);
        ExpressionLiteral stateVal = new ExpressionLiteral(TypeInt.getInstance(), Integer.valueOf(0));
        DeclarationInt declType = new DeclarationInt(stateVal, stateVal);
        module.addDeclaration(new Declaration(stateVarString, declType));
        // add an end variable to the module
        String endVarString = endVar.getName();
        ExpressionLiteral trueVal = new ExpressionLiteral(TypeBool.getInstance(), Boolean.valueOf(true));
        Declaration endDecl = new Declaration(endVarString, new DeclarationBool());
        endDecl.setStart(trueVal);
        module.addDeclaration(endDecl);
        return module;
    }

    public int projectCommands (Module m, int k, int r, ExpressionIdent sessRole, ExpressionIdent endVar, String parent) throws PrismTranslationException {
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
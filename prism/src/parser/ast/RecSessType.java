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

    public Module toModule(
            ExpressionIdent parentRole,
            // ExpressionIdent endVar,
            HashMap<String, Integer> labelsEncoding,
            int numLabels,
            ArrayList<ExpressionBinaryOp> sendStates,
            ArrayList<ExpressionBinaryOp> pendingStates,
            ArrayList<ExpressionBinaryOp> endStates) throws PrismTranslationException {
        // set module name
        Module module = new Module(parentRole.getName());
        module.setNameASTElement(parentRole);
        // set state variable for parent role
        String stateVarString = "s_" + parentRole.getName();
        ExpressionIdent stateVarIdent = new ExpressionIdent(stateVarString);
        // determine the last state of the module
        int maxState = projectCommands(module, 0, 0, stateVarIdent, parentRole.getName(), labelsEncoding,
                numLabels, sendStates, pendingStates, endStates);
        ExpressionLiteral low = new ExpressionLiteral(TypeInt.getInstance(), Integer.valueOf(0));
        ExpressionLiteral high = new ExpressionLiteral(TypeInt.getInstance(), Integer.valueOf(maxState));
        DeclarationInt declType = new DeclarationInt(low, high); 
        module.addDeclaration(new Declaration(stateVarString, declType));
        // add end variable
//        String endVarString = endVar.getName();
//        ExpressionLiteral falseVal = new ExpressionLiteral(TypeBool.getInstance(), Boolean.valueOf(false));
//        Declaration endDecl = new Declaration(endVarString, new DeclarationBool());
//        endDecl.setStart(falseVal);
//        module.addDeclaration(endDecl);
        return module;
    }

    public int projectCommands(
        Module m, 
        int k, 
        int r, 
        ExpressionIdent stateVar,
        // ExpressionIdent endVar,
        String parent,
        HashMap<String, Integer> labelsEncoding,
        int numLabels,
        ArrayList<ExpressionBinaryOp> sendStates,
        ArrayList<ExpressionBinaryOp> pendingStates,
        ArrayList<ExpressionBinaryOp> endState
    ) throws PrismTranslationException {
        return this.body.projectCommands(m, k, k, stateVar, parent, labelsEncoding, numLabels, sendStates, pendingStates, endState);
    }

    /* change all this */
    public Object accept(ASTVisitor v) throws PrismLangException {
        return this;
    }
    
    public ASTElement deepCopy(DeepCopy copier) throws PrismLangException {    
        return this;
    }

}
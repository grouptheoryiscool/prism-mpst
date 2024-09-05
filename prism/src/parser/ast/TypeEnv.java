package parser.ast;

import prism.PrismLangException;
import parser.visitor.ASTVisitor;
import parser.visitor.DeepCopy;
import java.util.HashMap;
import java.util.Map;
import prism.ModelType;
import prism.PrismTranslationException;
import java.util.ArrayList;

import parser.ast.ExpressionIdent;

public class TypeEnv extends ASTElement {
    protected HashMap<ChannelType, ProbSessType> entries = new HashMap<ChannelType, ProbSessType>();

    public TypeEnv(HashMap<ChannelType, ProbSessType> entries) {
        this.entries = entries;
    }

    public HashMap<ChannelType, ProbSessType> getEntries() {
        return this.entries;
    }

    public int getSize() {
        return entries.size();
    }

    public String toString() {
        String s = "";
        for (HashMap.Entry<ChannelType, ProbSessType> entry : entries.entrySet()) {
            s += entry.getKey().toString() + ":" + entry.getValue().toString();
            s += ",\n";
        }
        return s;
    }

    public void addEntry(ChannelType c, ProbSessType p) {
        entries.put(c, p);
    }

    public ModulesFile toModulesFile() throws PrismLangException, PrismTranslationException {
        ModulesFile modulesFile = new ModulesFile();
        modulesFile.setModelType(ModelType.IMDP);
        HashMap<String, Integer> labelsEncoding = new HashMap<>();
        int numLabels = 0;
        ArrayList<ExpressionBinaryOp> sendStates = new ArrayList<ExpressionBinaryOp>();
        ArrayList<ExpressionBinaryOp> pendingStates = new ArrayList<ExpressionBinaryOp>();
        ArrayList<ExpressionBinaryOp> endStates = new ArrayList<ExpressionBinaryOp>();
        for (Map.Entry<ChannelType, ProbSessType> entry : this.entries.entrySet()) {
            String role = entry.getKey().getRole();
            ExpressionIdent parentRole = new ExpressionIdent(role);
            Module module = entry.getValue().toModule(parentRole, labelsEncoding, numLabels, sendStates, pendingStates, endStates);
            modulesFile.addModule(module);
        }
        FormulaList formulas = new FormulaList();
        formulas.addFormula(new ExpressionIdent("send"), createFormulaClause(null, sendStates, 3));
        formulas.addFormula(new ExpressionIdent("pending"), createFormulaClause(null, pendingStates, 3));
        formulas.addFormula(new ExpressionIdent("end"), createFormulaClause(null, endStates, 4));
        modulesFile.setFormulaList(formulas);
        return modulesFile;
    }

    public Expression createFormulaClause(
            Expression current,
            ArrayList<ExpressionBinaryOp> states,
            int op
            ) throws PrismTranslationException {
        if (states.size() == 0) {
            return current;
        }
        if (current == null) {
            if (states.size() == 1) {
                return states.get(0);
            } else if (states.size() > 1) {
                current = new ExpressionBinaryOp(op, states.get(0), states.get(1));
                states.remove(0);
                states.remove(0);
                return this.createFormulaClause(current, states, op);
            }
        }
        ExpressionBinaryOp ret = new ExpressionBinaryOp(op, current, states.get(0));
        states.remove(0);
        return this.createFormulaClause(ret, states, op);
    }

    /* change all this */
    public Object accept(ASTVisitor v) throws PrismLangException {
        return this;
    }
    
    public ASTElement deepCopy(DeepCopy copier) throws PrismLangException {    
        return this;
    }
}
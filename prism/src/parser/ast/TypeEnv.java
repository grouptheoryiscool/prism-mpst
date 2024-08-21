package parser.ast;

import prism.PrismLangException;
import parser.visitor.ASTVisitor;
import parser.visitor.DeepCopy;
import java.util.HashMap;
import java.util.Map;
import prism.ModelType;
import prism.PrismTranslationException;

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
        for (Map.Entry<ChannelType, ProbSessType> entry : this.entries.entrySet()) {
            String role = entry.getKey().getRole();
            ExpressionIdent parentRole = new ExpressionIdent(role);
            Module module = entry.getValue().toModule(parentRole, entry.getKey().getEndVar());
            modulesFile.addModule(module);
        }
        return modulesFile;
    }

    /* change all this */
    public Object accept(ASTVisitor v) throws PrismLangException {
        return this;
    }
    
    public ASTElement deepCopy(DeepCopy copier) throws PrismLangException {    
        return this;
    }
}
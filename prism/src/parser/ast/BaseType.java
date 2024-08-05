package parser.ast;

import prism.PrismLangException;
import parser.visitor.DeepCopy;
import parser.visitor.ASTVisitor;

public class BaseType extends MessageType {
    public enum basictype { INT, BOOL, FLOAT }
    private basictype basictype;

    public BaseType(basictype type) {
        this.basictype = basictype;
    }

    public basictype getBasicType() {
        return this.basictype;
    }
    
    public String toString() {
        switch(this.basictype) {
            case INT:
                return "int";
            case BOOL:
                return "bool";
            case FLOAT:
                return "float";
            default:
                return "unknown";
        }
    }

    // change later 

	public BaseType deepCopy(DeepCopy copier) throws PrismLangException {
        return this;
    }

    public Object accept(ASTVisitor v) throws PrismLangException {
        return this;
    }

}
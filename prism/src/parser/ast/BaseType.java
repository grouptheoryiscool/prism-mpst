package parser.ast;

import parser.visitor.ASTVisitor;
import parser.visitor.DeepCopy;
import prism.PrismLangException;

public class BaseType extends MessageType {
    public enum basictype { INT, BOOL, FLOAT, STR }
    private basictype basictype;

    public BaseType(basictype mybasictype) {
        this.basictype = mybasictype;
    }

    public basictype getBasicType() {
        return this.basictype;
    }
    
    public String toString() {
        return this.basictype.toString();
    }

    public int toInt() {
        switch (this.basictype) {
            case INT:
                return 1;
            case BOOL:
                return 2;
            case FLOAT:
                return 3;
            case STR:
                return 4;
            default:
                throw new IllegalArgumentException("Unknown basictype: " + type);
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
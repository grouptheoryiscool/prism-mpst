package parser.ast;

import parser.visitor.ASTVisitor;
import parser.visitor.DeepCopy;
import prism.PrismLangException;

public class BaseType extends MessageType {
    public enum basictype { INT, BOOL, FLOAT }
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

    // change later 

	public BaseType deepCopy(DeepCopy copier) throws PrismLangException {
        return this;
    }

    public Object accept(ASTVisitor v) throws PrismLangException {
        return this;
    }

}
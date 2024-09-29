package parser.ast;

import java.util.ArrayList;

public class MPSTproperties {

    private boolean safe = false;
    private boolean df = false;

    public MPSTproperties() {}

    public void setSafe(boolean safe) { this.safe = safe; }
    public void setDF(boolean df) { this.df = df; }


    public String toPropertiesString() {
        String s = "";
        if (this.safe) {
            s += "P<=0[F \"deadlock\" & \"pending\"]";
            if (this.df) { s += "; "; }
        }
        if (this.df) {
            s += "P<=0[F \"deadlock\" & !\"end\"]";
        }
        System.out.println(s);
        return s;
    }


//    public  propertiesFile toPropertiesFile(ModulesFile modelInfo) {
//        propertiesFile propFile = new propertiesFile();
//        propFile.modelInfo = propFile.modulesFile = (ModulesFile) modelInfo;
//        if (this.safe) {
//            ExpressionUnaryOp notsend = new ExpressionUnaryOp(ExpressionUnaryOp.NOT, new ExpressionIdent("send"));
//            ExpressionBinaryOp deadlockAndPending = new ExpressionBinaryOp(ExpressionBinaryOp.AND, new ExpressionIdent("deadlock"), new ExpressionIdent("pending"));
//            ExpressionBinaryOp err = new ExpressionBinaryOp(ExpressionBinaryOp.AND, notsend, deadlockAndPending);
//            ExpressionTemporal fErr = new ExpressionTemporal(ExpressionTemporal.P_F, err, null);
//            Property prop = new Property(new ExpressionProb(fErr, "minmin", 0));
//
//        }
//    }
}

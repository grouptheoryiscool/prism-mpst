package parser.ast;

public class ChannelType {

    protected String session;
    protected String role;
    protected ExpressionIdent endVar;

    public ChannelType(String session, String role) {
        this.session = session;
        this.role = role;
        this.endVar = new ExpressionIdent("end_" + role);

    }

    public String getRole() {
        return role;
    }

    public String getSession() {
        return session;
    }

    public ExpressionIdent getEndVar() {
        return endVar;
    }

    public String toString() {
        return session + "[" + role + "]";
    }
}
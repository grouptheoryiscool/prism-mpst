package parser.ast;

public class ChannelType {

    protected String session;
    protected String role;

    public ChannelType(String session, String role) {
        this.session = session;
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public String getSession() {
        return session;
    }

    public String toString() {
        return session + "[" + role + "]";
    }
}
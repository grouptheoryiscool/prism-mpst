package parser.ast;

public class RecvBranch {
    protected String label;
    protected MessageType msgType;
    protected ProbSessType continuation;

    public RecvBranch(String label, MessageType msgType, ProbSessType continuation) {
        this.msgType = msgType;
        this.label = label;
        this.continuation = continuation;
    }

    public String getLabel() {
        return label;
    } 

    public ProbSessType getContinuation() {
        return continuation;
    }

    public MessageType getMsgType() {
        return msgType;
    }

    public String toString() {
        return this.label + "(" + this.msgType.toString() + ")." + this.continuation.toString();
    }

    public int getNodes() {
        return continuation.getNodes();
    }
}
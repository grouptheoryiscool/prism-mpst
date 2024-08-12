package parser.ast;

public class SelBranch {
    protected ExpressionInterval interval;
    protected String label;
    protected MessageType msgType;
    protected ProbSessType continuation;

    public SelBranch(ExpressionInterval interval, String label, MessageType msgType, ProbSessType continuation) {
        this.interval = interval;
        this.msgType = msgType;
        this.label = label;
        this.continuation = continuation;
    }

    public ExpressionInterval getInterval() {
        return interval;
    }

    public MessageType getMsgType() {
        return msgType;
    }

    public String getLabel() {
        return label;
    }

    public ProbSessType getContinuation() {
        return continuation;
    }

    public String toString() {
        return this.interval.toString() + ": " + this.label + "(" + this.msgType.toString() + ")." + this.continuation.toString();
    }

    public int getNodes() {
        return continuation.getNodes();
    } 
}
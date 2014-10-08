package fr.inria.diversify.analyzerPlugin;

/**
 * Created by marodrig on 04/09/2014.
 */
public class CodePosition {

    protected String source;

    protected String position;
    private long registerTime;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return getPosition();
    }

    /**
     * Millis when the test begin. help to identify multi-threaded TP calls in different log files
     * @return
     */
    public long getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(long registerTime) {
        this.registerTime = registerTime;
    }

}

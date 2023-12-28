public class TimeTracker {
    double time;
    public TimeTracker(){
        time = System.nanoTime();
    }

    //Time in seconds
    public double timeFromLast(){
        double prev = time;
        time = System.nanoTime();
        return (time - prev)/1000000000;
    }
}

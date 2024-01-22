package util;

public class TimeTracker {
    private double time;

    public TimeTracker() {
        time = System.nanoTime();
    }

    //Description: Gets the time elapsed from the last method call
    //Parameters: None
    //Return: the time elapsed in seconds
    public double timeFromLast() {
        double prev = time;
        time = System.nanoTime();
        return (time - prev) / 1000000000;
    }

    // A Getter for the time property (time is in nanoseconds)
    public double getTime() {
        return time;
    }
}

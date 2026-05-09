package utils;

public class Time {
    // Because all static variables get initialized AS SOON AS application runs,
    // its gives us an opportunity to measure time right at its beginning
    public static final long timeStarted = System.nanoTime();

    // Time elapsed since the application has started
    public static float getTime() {
        return (float)(
            (System.nanoTime() - (timeStarted))  * 1E-9
        ); // conversion from nanoseconds to seconds
    }
}

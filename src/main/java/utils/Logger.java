package utils;

// * Development utility
public class Logger {
    private final String envClassName;

    public Logger(Class<?> envClass) {
        this.envClassName = envClass.getName();
    }

    // Logging to the console with the
    // environmental reference from where logger was called
    public void log(String msg) {
        if (msg.isBlank()) return;
        msg = String.valueOf(msg);

        System.out.format(
            "\n[%s] %s\n",
            envClassName, msg
        );
    }

    // Method overloading
    public void log(float msg) { log(String.valueOf(msg)); }
    public void log(int msg) { log(String.valueOf(msg)); }
    public void log(double msg) { log(String.valueOf(msg)); }
    public void log(long msg) { log(String.valueOf(msg)); }
}

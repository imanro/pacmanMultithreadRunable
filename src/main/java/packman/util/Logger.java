package packman.util;

public class Logger {
    private Boolean debugEnabled = false;
    private Boolean errorEnabled = true;
    public void debug(String string) {
        if(debugEnabled) {
            System.out.println(string);
        }
    }

    public void error(String string) {
        if(errorEnabled) {
            System.out.println(string);
        }
    }
}

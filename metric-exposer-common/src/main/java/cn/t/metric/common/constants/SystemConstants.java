package cn.t.metric.common.constants;

public class SystemConstants {
    public static final String OS_NAME = System.getProperty("os.name");
    public static final boolean IS_WINDOWS = OS_NAME.toLowerCase().startsWith("windows");
    public static final String CONSOLE_ENCODING = System.getProperty("sun.jnu.encoding");
    public static final String LINE_SEPARATOR = System.lineSeparator();
    public static final int LINE_SEPARATOR_LENGTH = LINE_SEPARATOR.length();
}

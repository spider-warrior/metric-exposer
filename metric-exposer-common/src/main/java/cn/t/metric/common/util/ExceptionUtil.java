package cn.t.metric.common.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtil {
    public static String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }

    public static Throwable getCausedBy(Throwable throwable) {
        if(throwable.getCause() != null) {
            return getCausedBy(throwable.getCause());
        } else {
            return throwable;
        }
    }

    public static String getErrorMessage(Throwable throwable) {
        if(throwable.getCause() != null && throwable.getCause().getMessage() != null) {
            return getErrorMessage(throwable.getCause());
        } else {
            return throwable.getMessage();
        }
    }
}

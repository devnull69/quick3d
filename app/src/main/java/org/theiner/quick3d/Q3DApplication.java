package org.theiner.quick3d;

import android.app.Application;

/**
 * Created by TheineT on 21.10.2014.
 */
public class Q3DApplication extends Application {
    private String trace;

    public String getTrace() {
        return trace;
    }

    public void setTrace(String s) {
        trace = s;
    }

    public void appendTrace(String s) {
        trace += s;
    }

    public void prependTrace(String s) { trace = s + trace;}
}

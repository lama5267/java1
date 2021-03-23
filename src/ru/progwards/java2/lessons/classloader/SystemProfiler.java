package progwards.java2.lessons.classloader;

import java.lang.instrument.Instrumentation;

public class SystemProfiler {
    public static void premain (String args, Instrumentation instrumentation) {
        String [] classes = args.split(";");
        instrumentation.addTransformer(new ProfileTransformer(classes));
    }
}
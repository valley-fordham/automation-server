package com.glenfordham.utils.process;

import com.glenfordham.webserver.Log;

public class ProcessWrapper implements AutoCloseable {

    Process process;

    public ProcessWrapper(Process process) {
        this.process = process;
    }

    public Process getProcess() {
        return process;
    }

    @Override
    public void close() {
        try {
            process.getErrorStream().close();
        } catch (Exception e) {
            Log.error("Unable to close ErrorStream", e);
        }
        try {
            process.getOutputStream().close();
        } catch (Exception e) {
            Log.error("Unable to close OutputStream", e);
        }
        try {
            process.getInputStream().close();
        } catch (Exception e) {
            Log.error("Unable to close InputStream", e);
        }
        try {
            process.destroy();
        } catch (Exception e) {
            Log.error("Unable to destroy process", e);
        }
    }
}
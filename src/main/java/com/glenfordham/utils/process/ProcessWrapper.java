package com.glenfordham.utils.process;

import com.glenfordham.webserver.logging.Log;

public class ProcessWrapper implements AutoCloseable {

    Process process;

    /**
     * Wraps a Process object, provides AutoCloseable functionality
     *
     * @param process the Process to be made AutoCloseable
     */
    public ProcessWrapper(Process process) {
        this.process = process;
    }

    /**
     * Gets the actual process that's been wrapped
     *
     * @return the wrapped process
     */
    public Process getProcess() {
        return process;
    }

    /**
     * Closes all streams and ensures the process is terminated
     */
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

    private ProcessWrapper() {
    }
}
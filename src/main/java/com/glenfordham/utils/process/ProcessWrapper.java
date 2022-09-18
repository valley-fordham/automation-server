package com.glenfordham.utils.process;


/**
 * ProcessWrapper wraps a Process class in order to add auto stream closing behaviour.
 */
public class ProcessWrapper implements AutoCloseable {

    Process process;

    /**
     * Wraps a Process object, provides AutoCloseable functionality.
     *
     * @param process The Process to be made AutoCloseable.
     */
    public ProcessWrapper(Process process) {
        this.process = process;
    }

    /**
     * Gets the actual process that's been wrapped.
     *
     * @return The wrapped Process.
     */
    public Process getProcess() {
        return process;
    }

    /**
     * Closes all streams and ensures the process is terminated.
     */
    @Override
    public void close() {
        try {
            process.getErrorStream().close();
        } catch (Exception ignore) {}
        try {
            process.getOutputStream().close();
        } catch (Exception ignore) {}
        try {
            process.getInputStream().close();
        } catch (Exception ignore) {}
        try {
            process.destroy();
        } catch (Exception ignore) {}
    }

    private ProcessWrapper() {
    }
}

package com.glenfordham.utils.process.cmd;

import com.glenfordham.utils.StreamUtils;
import com.glenfordham.utils.process.ProcessWrapper;
import com.glenfordham.webserver.logging.Log;

import java.io.IOException;

public class CmdLine {

    private String commandLineToRun;

    public CmdLine(String commandLineToRun) {
        this.commandLineToRun = commandLineToRun;
    }

    /**
     * Executes a new process with the provided command-line
     *
     * @return the output of the command line process
     * @throws CmdLineException if an error occurs when attempting to invoke the process
     */
    public String exec() throws CmdLineException {
        if (commandLineToRun.isEmpty()) {
            throw new CmdLineException("Unable to invoke empty command");
        }
        // Invoke the executable using ProcessWrapper to ensure all streams and the process are closed.
        // Wait for the process to complete and log error if an error code is returned
        Log.debug("Executing process: " + commandLineToRun);
        try (ProcessWrapper processWrapper = new ProcessWrapper(
                Runtime.getRuntime().exec(commandLineToRun))) {
            if (processWrapper.getProcess().waitFor() != 0) {
                Log.error(StreamUtils.getString(processWrapper.getProcess().getErrorStream()));
            } else {
                return StreamUtils.getString(processWrapper.getProcess().getInputStream());
            }
        } catch (InterruptedException iE) {
            Log.error("Interrupted execution of process", iE);
            Thread.currentThread().interrupt();
            throw new CmdLineException(iE.getMessage(), iE);
        } catch (IOException e) {
            throw new CmdLineException(e.getMessage(), e);
        }
        return null;
    }

    /**
     * Gets the current command line value
     *
     * @return the command line set against the CmdLine
     */
    public String getCommandLineToRun() {
        return commandLineToRun;
    }

    /**
     * Sets the command line to the provided String
     *
     * @param commandLineToRun String containing the command line to run
     */
    public void setCommandLineToRun(String commandLineToRun) {
        this.commandLineToRun = commandLineToRun;
    }
}
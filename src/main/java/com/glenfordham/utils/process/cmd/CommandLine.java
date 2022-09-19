package com.glenfordham.utils.process.cmd;

import com.glenfordham.utils.StreamUtils;
import com.glenfordham.utils.process.ProcessWrapper;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Arrays;

/**
 * The CommandLine class represents the hosts' Command Prompt or Terminal.
 */
public class CommandLine {

    private static final Logger logger = LogManager.getLogger();

    private String commandLineToRun;

    public CommandLine(String commandLineToRun) {
        this.commandLineToRun = commandLineToRun;
    }

    /**
     * Executes a new process with the provided command-line.
     *
     * @return A String output of the command line process.
     * @throws CmdLineException If an error occurs when attempting to invoke the process.
     */
    public String exec() throws CmdLineException {
        if (commandLineToRun.isEmpty()) {
            throw new CmdLineException("Unable to invoke empty command");
        }

        // When running under Linux/Mac, run using bash to ensure that execution behaviour matches that of the terminal
        String[] osSafeCmdLine;
        if (SystemUtils.IS_OS_WINDOWS) {
            osSafeCmdLine = new String[]{commandLineToRun};
        } else if (SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_MAC) {
            osSafeCmdLine = new String[]{"bash", "-c", commandLineToRun};
        } else {
            throw new CmdLineException("Unsupported Operating System");
        }

        // Invoke the executable using ProcessWrapper to ensure all streams and the process are closed.
        // Wait for the process to complete and log error if an error code is returned
        logger.debug("Executing process: {}", Arrays.toString(osSafeCmdLine));
        try (ProcessWrapper processWrapper = new ProcessWrapper(new ProcessBuilder(osSafeCmdLine).start()))  {
            final int processReturnValue = processWrapper.getProcess().waitFor();
            logger.debug("Process returned: {}", processReturnValue);
            if (processReturnValue != 0) {
                logger.error(StreamUtils.getString(processWrapper.getProcess().getErrorStream()));
            } else {
                String processOutput = StreamUtils.getString(processWrapper.getProcess().getInputStream());
                logger.debug("Process stdout: {}", processOutput);
                return processOutput;
            }
        } catch (InterruptedException iE) {
            Thread.currentThread().interrupt();
            throw new CmdLineException(iE.getMessage(), iE);
        } catch (IOException e) {
            throw new CmdLineException(e.getMessage(), e);
        }
        return null;
    }
}

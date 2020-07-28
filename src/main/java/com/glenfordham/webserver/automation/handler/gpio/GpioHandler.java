package com.glenfordham.webserver.automation.handler.gpio;

import com.glenfordham.utils.process.cmd.CmdLine;
import com.glenfordham.utils.process.cmd.CmdLineException;
import com.glenfordham.webserver.automation.Parameter;
import com.glenfordham.webserver.automation.config.AutomationConfig;
import com.glenfordham.webserver.automation.config.AutomationConfigException;
import com.glenfordham.webserver.automation.handler.Handler;
import com.glenfordham.webserver.automation.handler.HandlerException;
import com.glenfordham.webserver.automation.jaxb.*;
import com.glenfordham.webserver.logging.Log;
import com.glenfordham.webserver.servlet.parameter.ParameterException;
import com.glenfordham.webserver.servlet.parameter.ParameterMap;

import java.io.IOException;
import java.io.OutputStream;

public class GpioHandler implements Handler {
    /**
     * Processes a GPIO type request. Matches request against configuration XML and triggers configured GPIO action
     *
     * @param parameterMap complete ParameterMap object, containing both parameter keys and values
     * @param clientOutput client OutputStream, for writing a response
     * @throws AutomationConfigException if unable to get configuration
     * @throws HandlerException          if a generic Exception occurs when handling the request
     * @throws ParameterException        if unable to get request name from parameter
     */
    @Override
    public void start(ParameterMap parameterMap, OutputStream clientOutput) throws AutomationConfigException, HandlerException, ParameterException {
        String incomingRequestName = parameterMap.get(Parameter.REQUEST_NAME.get()).getFirst();
        Config config = AutomationConfig.get();

        // Check if the incoming request matches a configured request name
        GpioRequest request = config.getGpio().getRequests().stream()
                .filter(requestEntry -> incomingRequestName.equalsIgnoreCase(requestEntry.getName()))
                .findFirst()
                .orElse(null);

        if (request == null || request.isCarportOnly()) {
            Log.error("Invalid request name: " + incomingRequestName);
            return;
        }

        // Process request and get response from Gpio command
        String gpioResponse = processRequest(request);
        // If no read was requested, the response will be null
        if (gpioResponse != null) {
            try {
                clientOutput.write(gpioResponse.getBytes());
            } catch (IOException e) {
                throw new HandlerException("Unable to write response", e);
            }
        }
    }

    /**
     * Processes the GPIO request
     *
     * @param request the GpioRequest to be processed
     * @return a String response if a Gpio read was performed, or null if only a write was performed
     * @throws HandlerException if a generic Exception occurs when handling the request
     */
    private String processRequest(GpioRequest request) throws HandlerException {
        String response = null;
        if (request.getRead() != null) {
            response = processRead(request.getRead());
        }
        if (request.getWrite() != null) {
            processWrite(request.getWrite());
        }
        return response;
    }

    /**
     * Processes a GpioRead request
     *
     * @param readRequest the GpioRead request to be processed
     * @return a String response of the read request
     * @throws HandlerException if a generic Exception occurs when handling the request
     */
    private String processRead(GpioRead readRequest) throws HandlerException {
        GpioReadBehaviour readBehaviour = readRequest.getBehaviour();
        if (readBehaviour.equals(GpioReadBehaviour.READ)) {
            return execute(Constant.GPIO_READ, readRequest.getPin(), null);
        } else {
            throw new HandlerException("For Gpio read, only READ behaviour supported");
        }
    }

    /**
     * Processes a GpioWrite request
     *
     * @param writeRequest the GpioWrite request to be processed
     * @throws HandlerException if an exception occurs when attempting to sleep the thread
     */
    private void processWrite(GpioWrite writeRequest) throws HandlerException {
        GpioWriteBehaviour writeBehaviour = writeRequest.getBehaviour();
        if (writeBehaviour.equals(GpioWriteBehaviour.WRITE)) {
            execute(Constant.GPIO_WRITE, writeRequest.getPin(), writeRequest.getValue());
        } else if (writeRequest.getWaitTimeBeforeReset() != null) {
            // Execute the write action...
            execute(Constant.GPIO_WRITE, writeRequest.getPin(), writeRequest.getValue());
            // ..then sleep for the configured time in millseconds...
            try {
                Thread.sleep(writeRequest.getWaitTimeBeforeReset());
            } catch (InterruptedException e) {
                Log.error("Interrupted execution of process", e);
                Thread.currentThread().interrupt();
                throw new HandlerException(e.getMessage(), e);
            }
            // ..then write the opposite value that was originally written to the pin
            if (writeRequest.getValue().equals(GpioWriteValue.ZERO)) {
                execute(Constant.GPIO_WRITE, writeRequest.getPin(), GpioWriteValue.ONE);
            } else {
                execute(Constant.GPIO_WRITE, writeRequest.getPin(), GpioWriteValue.ZERO);
            }
        } else {
            throw new HandlerException("write and reset requested but 'wait time before reset' not provided");
        }
    }

    /**
     * Executes a provided Gpio Command
     *
     * @param gpioCommand the Gpio command to be performed, either 'read' or 'write'
     * @param pin         the pin to perform the command on
     * @param writeValue  the value to write, if a write is being performed
     * @return String response of Gpio if a read is performed
     * @throws HandlerException if an error occurs when running the Gpio process
     */
    private String execute(Constant gpioCommand, int pin, GpioWriteValue writeValue) throws HandlerException {
        // Invoke the GPIO executable and return the response
        try {
            return new CmdLine("gpio " + gpioCommand.getText() + " " + pin + " " + (writeValue != null ? writeValue.value() : "")).exec();
        } catch (CmdLineException e) {
            throw new HandlerException("Error occurred when executing gpio process", e);
        }
    }
}
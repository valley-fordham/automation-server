package com.glenfordham.webserver.automation.handler.gpio;

import com.glenfordham.utils.process.cmd.CmdLineException;
import com.glenfordham.utils.process.cmd.CommandLine;
import com.glenfordham.webserver.automation.handler.HandlerException;
import com.glenfordham.webserver.automation.jaxb.*;
import com.glenfordham.webserver.logging.Log;

/**
 * Defines behaviour for the various types of GpioRequest.
 */
public class GpioPinControl {
	/**
	 * Processes the GPIO request.
	 *
	 * @param request GpioRequest to be processed.
	 * @return A String if a Gpio read was performed, or null if only a write was performed.
	 * @throws HandlerException If a generic Exception occurs when handling the request.
	 */
	public static String process(GpioRequest request) throws HandlerException {
		String response = null;
		if (request.getRead() != null) {
			response = process(request.getRead());
		}
		if (request.getWrite() != null) {
			process(request.getWrite());
		}
		return response;
	}

	/**
	 * Processes a GpioRead request.
	 *
	 * @param readRequest GpioRead request to be processed.
	 * @return A String with the response of the read request.
	 * @throws HandlerException If a generic Exception occurs when handling the request.
	 */
	private static String process(GpioRead readRequest) throws HandlerException {
		GpioReadBehaviour readBehaviour = readRequest.getBehaviour();
		if (readBehaviour.equals(GpioReadBehaviour.READ)) {
			return execute(Constant.GPIO_READ, readRequest.getPin(), null);
		} else {
			throw new HandlerException("For Gpio read, only READ behaviour supported");
		}
	}

	/**
	 * Processes a GpioWrite request.
	 *
	 * @param writeRequest GpioWrite request to be processed.
	 * @throws HandlerException If an exception occurs when attempting to sleep the thread.
	 */
	private static void process(GpioWrite writeRequest) throws HandlerException {
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
	 * Executes a provided Gpio Commandl
	 *
	 * @param gpioCommand Gpio command to be performed, either 'read' or 'write'.
	 * @param pin         Pin to perform the command on.
	 * @param writeValue  Value to write, if a write is being performed.
	 * @return A String with the response of the Gpio pin, if a read is performed.
	 * @throws HandlerException If an error occurs when running the Gpio process.
	 */
	private static String execute(Constant gpioCommand, int pin, GpioWriteValue writeValue) throws HandlerException {
		// Invoke the GPIO executable and return the response
		try {
			return new CommandLine("gpio " + gpioCommand.get() + " " + pin + " " + (writeValue != null ? writeValue.value() : "")).exec();
		} catch (CmdLineException e) {
			throw new HandlerException("Error occurred when executing gpio process", e);
		}
	}

	// Hide default constructor
	private GpioPinControl() {}
}

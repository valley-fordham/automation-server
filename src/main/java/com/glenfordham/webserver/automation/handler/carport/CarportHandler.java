package com.glenfordham.webserver.automation.handler.carport;

import com.glenfordham.webserver.automation.Parameter;
import com.glenfordham.webserver.automation.config.AutomationConfig;
import com.glenfordham.webserver.automation.config.AutomationConfigException;
import com.glenfordham.webserver.automation.handler.Handler;
import com.glenfordham.webserver.automation.handler.HandlerException;
import com.glenfordham.webserver.automation.handler.gpio.GpioPinControl;
import com.glenfordham.webserver.automation.jaxb.CarportAction;
import com.glenfordham.webserver.automation.jaxb.CarportRequest;
import com.glenfordham.webserver.automation.jaxb.Config;
import com.glenfordham.webserver.automation.jaxb.GpioRequest;
import com.glenfordham.webserver.logging.Log;
import com.glenfordham.webserver.servlet.parameter.ParameterException;
import com.glenfordham.webserver.servlet.parameter.ParameterMap;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * CarportHandler relies on the GpioHandler and GPIO configuration to be present. Use dedicated Gpio requests for each
 * action, using the 'Carport Only' flag. This handler is compatible with Raspberry Pi's only.
 */
public class CarportHandler implements Handler {

	static final int TRIGGER = 0;
	static final int READ = 1;

	private OutputStream clientOutput = null;

	/**
	 * Processes a Carport type request. Carport requests are used to control a carport door through a Raspberry PI
	 * GPIO interface.
	 *
	 * @param parameterMap Complete ParameterMap object, containing both parameter keys and values.
	 * @param clientOutput Client OutputStream, for writing a response.
	 * @throws AutomationConfigException If unable to get configuration.
	 * @throws HandlerException          If a generic Exception occurs when handling the request.
	 * @throws ParameterException        If unable to get request name from parameter.
	 */
	@Override
	public void start(ParameterMap parameterMap, OutputStream clientOutput) throws AutomationConfigException, HandlerException, ParameterException {
		String incomingRequestName = parameterMap.get(Parameter.REQUEST_NAME.get()).getFirst();
		Config config = AutomationConfig.get();
		this.clientOutput = clientOutput;

		// Ensure Carport element is present in config file
		if (config.getCarport() == null) {
			throw new HandlerException("No Carport configuration in configuration XML");
		}

		// Check if the incoming request matches a configured request name
		CarportRequest carportRequest = config.getCarport().getRequests().stream()
				.filter(requestEntry -> incomingRequestName.equalsIgnoreCase(requestEntry.getName()))
				.findFirst()
				.orElse(null);

		if (carportRequest == null) {
			Log.error("Invalid request name");
			return;
		}

		// Check if wait time is configured for open/close actions
		CarportAction action = carportRequest.getAction();
		if ((action == CarportAction.OPEN || action == CarportAction.CLOSE) && (carportRequest.getWaitTime() == null || carportRequest.getDoorClosedValue() == null)) {
			Log.error("Wait time required for open/close action");
			return;
		}

		// Get all carport only Gpio requests
		List<GpioRequest> carportGpioRequests = config.getGpio().getRequests().stream().filter(GpioRequest::isCarportOnly).toList();
		processRequest(carportRequest, carportGpioRequests);
	}

	/**
	 * Attempts to process the carport request.
	 *
	 * Retrieves the linked GpioRequest configuration for door trigger and read status, and uses these to perform the
	 * required Carport Door commands.
	 *
	 * @param carportRequest CarportRequest to be processed.
	 * @param carportGpioRequests List of configured 'Carport Only' GpioRequests.
	 * @throws HandlerException If an error occurs invoking the Gpio process.
	 */
	void processRequest(CarportRequest carportRequest, List<GpioRequest> carportGpioRequests) throws HandlerException {
		// Retrieve the Gpio request to use for triggering the door
		GpioRequest triggerRequest = carportGpioRequests.stream().filter(e->carportRequest.getGpioRequestName().get(TRIGGER).equalsIgnoreCase(e.getName())).findFirst().orElse(null);
		if (triggerRequest == null) {
			Log.error("Invalid Trigger Gpio Request: " + carportRequest);
			return;
		}

		// Retrieve the Gpio request to use for reading the current door status (eg. open/closed)
		GpioRequest readRequest = carportGpioRequests.stream().filter(e->carportRequest.getGpioRequestName().get(READ).equalsIgnoreCase(e.getName())).findFirst().orElse(null);
		if (readRequest == null) {
			Log.error("Invalid Read Gpio Request: " + carportRequest);
			return;
		}

		// Invoke appropriate carport action
		switch (carportRequest.getAction()) {
			case TRIGGER -> sendDoorTrigger(triggerRequest);
			case STATUS -> readDoorStatus(readRequest);
			case OPEN -> openDoor(triggerRequest, readRequest, carportRequest.getDoorClosedValue());
			case CLOSE ->
					closeDoor(triggerRequest, readRequest, carportRequest.getDoorClosedValue(), carportRequest.getWaitTime());
		}
	}

	/**
	 * Closes the carport door. Multiple attempts are made such that if the door is almost closed but was previously
	 * on the way down, it will be re-opened fully and then fully closed. The wait time should represent how long it
	 * takes for the door to go from fully open to fully closed.
	 *
	 * @param triggerRequest GpioRequest to be used for triggering the door.
	 * @param readRequest GpioRequest to be used for checking if the door is open/closed.
	 * @param doorClosedValue Value expected to be returned from the readRequest if the door is closed.
	 * @param waitTime Time to wait in milliseconds between sending a trigger request and performing another door status check.
	 * @throws HandlerException If an error occurs while processing GpioRequests or if the sleeping thread is interrupted.
	 */
	private void closeDoor(GpioRequest triggerRequest, GpioRequest readRequest, String doorClosedValue, int waitTime) throws HandlerException {
		int attempts = 0;
		while (attempts <=3 && !GpioPinControl.process(readRequest).equalsIgnoreCase(doorClosedValue)) {
			GpioPinControl.process(triggerRequest);
			try {
				Thread.sleep(waitTime);
			} catch (InterruptedException e) {
				Log.error("Interrupted execution of process", e);
				Thread.currentThread().interrupt();
				throw new HandlerException(e.getMessage(), e);
			}
			attempts++;
		}
	}

	/**
	 * Opens the carport door. Checks if the door is already open before triggering the option action.
	 *
	 * @param triggerRequest GpioRequest to be used for triggering the door.
	 * @param readRequest GpioRequest to be used for checking if the door is open/closed.
	 * @param doorClosedValue Value expected to be returned from the readRequest if the door is closed.
	 * @throws HandlerException If an error occurs while processing GpioRequests.
	 */
	private void openDoor(GpioRequest triggerRequest, GpioRequest readRequest, String doorClosedValue) throws HandlerException {
		if (!GpioPinControl.process(readRequest).equalsIgnoreCase(doorClosedValue)) {
			GpioPinControl.process(triggerRequest);
		}
	}

	/**
	 * Checks the current status of the carport door and returns the value.
	 *
	 * @param readRequest GpioRequest to be used for checking if the door is open/closed.
	 * @throws HandlerException If an error occurs while processing the GpioRequest.
	 */
	private void readDoorStatus(GpioRequest readRequest) throws HandlerException {
		try {
			clientOutput.write(GpioPinControl.process(readRequest).getBytes());
		} catch (IOException e) {
			throw new HandlerException("Error occurred when returning response", e);
		}
	}

	/**
	 * Sends the door trigger request, equivalent to pressing the carport door remote button.
	 *
	 * @param triggerRequest GpioRequest to be used for triggering the door.
	 * @throws HandlerException If an error occurs while processing the GpioRequest.
	 */
	private void sendDoorTrigger(GpioRequest triggerRequest) throws HandlerException {
		GpioPinControl.process(triggerRequest);
	}
}

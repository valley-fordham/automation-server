package com.glenfordham.webserver.automation.handler.carport;

import com.glenfordham.webserver.automation.Parameter;
import com.glenfordham.webserver.automation.config.AutomationConfig;
import com.glenfordham.webserver.automation.config.AutomationConfigException;
import com.glenfordham.webserver.automation.handler.Handler;
import com.glenfordham.webserver.automation.handler.HandlerException;
import com.glenfordham.webserver.automation.jaxb.CarportRequest;
import com.glenfordham.webserver.automation.jaxb.Config;
import com.glenfordham.webserver.automation.jaxb.GpioRequest;
import com.glenfordham.webserver.logging.Log;
import com.glenfordham.webserver.servlet.parameter.ParameterException;
import com.glenfordham.webserver.servlet.parameter.ParameterMap;

import java.io.OutputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CarportHandler
 *
 * CarportHandler relies on the GpioHandler and GPIO configuration to be present. Set up a dedicated Gpio Request
 * with set as 'Carport Only' for both triggering the door and reading the current status.
 */
public class CarportHandler implements Handler {

	static final int TRIGGER = 0;
	static final int READ = 1;

	/**
	 * Processes a Carport type request. Carport requests are used to control a carport door through a Raspberry PI
	 * GPIO interface.
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
			Log.error("Invalid request name: " + incomingRequestName);
			return;
		}

		// Get all carport only Gpio requests
		List<GpioRequest> carportGpioRequests = config.getGpio().getRequests().stream().filter(GpioRequest::isCarportOnly).collect(Collectors.toList());
		processRequest(carportRequest, carportGpioRequests);
	}

	/**
	 * Attempts to process the carport request.
	 *
	 * Retrieves the linked GpioRequest configuration for door trigger and read status, and uses these to perform the
	 * required Carport Door commands.
	 *
	 * @param carportRequest the CarportRequest to be processed
	 * @param carportGpioRequests the list of configured 'Carport Only' GpioRequests
	 */
	void processRequest(CarportRequest carportRequest, List<GpioRequest> carportGpioRequests) {
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
			case TRIGGER:
				sendDoorTrigger(triggerRequest);
				break;
			case STATUS:
				readDoorStatus(readRequest);
				break;
			case OPEN:
				openDoor(triggerRequest, readRequest);
				break;
			case CLOSE:
				closeDoor(triggerRequest, readRequest);
				break;
		}
	}

	private void closeDoor(GpioRequest triggerRequest, GpioRequest readRequest) {
		// TODO: write me
	}

	private void openDoor(GpioRequest triggerRequest, GpioRequest readRequest) {
		// TODO: write me
	}

	private void readDoorStatus(GpioRequest readRequest) {
		// TODO: write me
	}

	private void sendDoorTrigger(GpioRequest triggerRequest) {
		// TODO: write me
	}
}

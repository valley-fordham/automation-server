package com.glenfordham.webserver.automation.handler.proxy;

import com.glenfordham.webserver.automation.Parameter;

/**
 * Defines mappings between Proxy URL parameters and standard URL parameters as defined in
 * {@link com.glenfordham.webserver.automation.Parameter}.
 */
public enum ProxyParameterMapping {
	PROXY_AUTHENTICATION_TOKEN(
			"proxy_authentication_token",
			Parameter.AUTHENTICATION_TOKEN
	),
	PROXY_REQUEST_TYPE(
			"proxy_request_type",
			Parameter.REQUEST_TYPE
	),
	PROXY_REQUEST_NAME(
			"proxy_request_name",
			Parameter.REQUEST_NAME
	);

	private final String text;
	private final Parameter parameter;

	ProxyParameterMapping(String text, Parameter parameter) {
		this.text = text;
		this.parameter = parameter;
	}

	/**
	 * Gets the text value of the Proxy Parameter Mapping..
	 *
	 * @return The text value of the Proxy Parameter Mapping..
	 */
	public String getText() {
		return text;
	}

	/**
	 * Gets the standard URL Parameter of the Proxy Parameter Mapping..
	 *
	 * @return The standard Parameter of the Proxy Parameter Mapping.
	 */
	public Parameter getParameter() {
		return parameter;
	}
}
package com.glenfordham.webserver.automation.jaxb;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Super class for all generated JAXB objects to allow easy overriding of default Java methods.
 */
public class JaxbObject {

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}


	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}
		return this.getClass().equals(object.getClass()) && ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE).equals(object.toString());
	}


	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
}

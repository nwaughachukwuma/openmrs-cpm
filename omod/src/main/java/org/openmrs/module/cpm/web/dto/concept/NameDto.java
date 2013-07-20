package org.openmrs.module.cpm.web.dto.concept;

import org.openmrs.api.ConceptNameType;
import org.openmrs.module.cpm.web.dto.Dto;

public class NameDto implements Dto {

	private String name;

	private ConceptNameType type;

	private String locale;

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public ConceptNameType getType() {
		return type;
	}

	public void setType(final ConceptNameType type) {
		this.type = type;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(final String locale) {
		this.locale = locale;
	}
}

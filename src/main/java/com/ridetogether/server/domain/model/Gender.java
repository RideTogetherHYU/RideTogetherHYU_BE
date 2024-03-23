package com.ridetogether.server.domain.model;

import static java.util.Locale.ENGLISH;

public enum Gender {
	MALE, FEMALE;

	public static Gender fromName(String type) {
		return Gender.valueOf(type.toUpperCase(ENGLISH));
	}

}

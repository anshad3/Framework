package com.ca.util;

public enum LogMode {
    FATAL(1), ERROR(3), INFO(7), WARN(15), DEBUG(31);

    private int value;

    LogMode(int value) {
	this.setValue(value);
    }

    /**
     * @return the value
     */
    public int getValue() {
	return value;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue(int value) {
	this.value = value;
    }
}
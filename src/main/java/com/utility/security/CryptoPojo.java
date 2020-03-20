package com.utility.security;

public class CryptoPojo {

	private String storage = null;
	private String algorithm = null;
	private String key = null;
	private String data = null;

	public CryptoPojo(String storage, String algorithm, String key, String data) {
		this.storage = storage;
		this.algorithm = algorithm;
		this.key = key;
		this.data = data;
	}

	public String getStorage() {
		return storage;
	}

	public void setStorage(String storage) {
		this.storage = storage;
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

}

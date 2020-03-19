package com.ca.base.kafka;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

public class ProducerPojoSerialiser<MessageContentPojo> implements Serializer<MessageContentPojo> {

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	public byte[] serialize(String arg0, MessageContentPojo arg1) {
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try(ObjectOutputStream out = new ObjectOutputStream(baos)) {
			out.writeObject(arg1);
		} catch (IOException e) {
			throw new SerializationException("Failed to serialize object.",e);
		}
		return baos.toByteArray();
		
	}

}

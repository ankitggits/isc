package no.sample.isc.binder.servicebus.util;

import no.sample.isc.core.domain.GenericComponent;
import no.sample.isc.core.domain.MessageEntity;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import java.io.*;

/**
 * Created by AB75448 on 13.01.2017.
 */
public class MessageUtility {

    public static byte[] serialize(MessageEntity obj) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ObjectOutputStream os = new ObjectOutputStream(out);
            os.writeObject(obj);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return out.toByteArray();
    }

    public static MessageEntity deserialize(byte[] data) {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        try {
            ObjectInputStream is = new ObjectInputStream(in);
            return (MessageEntity) is.readObject();
        } catch (IOException|ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static MessageEntity getEntity( BytesMessage message ) throws JMSException {
        int messageLength = (int) message.getBodyLength();
        if (messageLength != 0) {
            byte[] data = new byte[messageLength];
            message.readBytes(data);
            return deserialize(data);
        }
        return null;
    }
}

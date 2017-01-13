package no.sample.isc.core.util;

import no.sample.isc.core.domain.GenericComponent;
import no.sample.isc.core.domain.MessageEntity;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import java.io.*;

/**
 * Created by AB75448 on 13.01.2017.
 */
public class MessageUtility {

    public static byte[] serialize(MessageEntity obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(obj);
        return out.toByteArray();
    }

    public static MessageEntity deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return (MessageEntity) is.readObject();
    }

    public static MessageEntity getEntity( BytesMessage message ) throws JMSException {
        int messageLength = (int) message.getBodyLength();
        if (messageLength != 0) {
            byte[] data = new byte[messageLength];
            message.readBytes(data);
            try {
                return deserialize(data);
            } catch (IOException e) {
                throw new JMSException(e.getMessage());
            } catch (ClassNotFoundException e) {
                throw new JMSException(e.getMessage());
            }
        }
        return null;
    }
}

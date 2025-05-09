package MiniSocial.Messaging;

import MiniSocial.Model.NotificationEvent;
import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.jms.*;

@MessageDriven(
        activationConfig = {
                @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue"),
                @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "java:/jms/queue/NotificationQueue")
        }
)
public class NotificationSubscriber implements MessageListener {

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof ObjectMessage) {
                ObjectMessage objMsg = (ObjectMessage) message;
                NotificationEvent event = (NotificationEvent) objMsg.getObject();
                System.out.println("Notification Received: " + event.toString());
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}

package MiniSocial.Messaging;

import MiniSocial.Model.NotificationEvent;
import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import jakarta.jms.*;

@Stateless
public class NotificationPublisher {

    @Resource(lookup = "java:/jms/queue/NotificationQueue") // Adjust based on JBoss config
    private Queue notificationQueue;

    @Resource(lookup = "java:/ConnectionFactory")
    private ConnectionFactory connectionFactory;

    public void sendNotification(NotificationEvent event) throws JMSException {
        try (JMSContext context = connectionFactory.createContext()) {
            ObjectMessage message = context.createObjectMessage(event);
            context.createProducer().send(notificationQueue, message);
        }
    }
}

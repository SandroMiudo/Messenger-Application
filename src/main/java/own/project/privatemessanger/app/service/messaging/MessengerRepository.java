package own.project.privatemessanger.app.service.messaging;

import own.project.privatemessanger.domain.messaging.Message;
import own.project.privatemessanger.dto.MessageInfo;

import java.util.List;

public interface MessengerRepository {
    public void addMessage(Message message);
    public void removeMessage(Message message);
    public List<MessageInfo> getConversation(Integer userIn, Integer userOut); // id in db
    public List<MessageInfo> getAllMessagesOfUser(Integer userID);
    public void readMessage(Message message);
}

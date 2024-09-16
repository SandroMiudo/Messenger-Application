package own.project.privatemessanger.app.service.messaging;

import org.springframework.stereotype.Service;
import own.project.privatemessanger.app.service.UserRepository;
import own.project.privatemessanger.domain.messaging.Message;
import own.project.privatemessanger.domain.messaging.MessageSort;
import own.project.privatemessanger.dto.MessageInfo;
import own.project.privatemessanger.spring.MessageUserMapper;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class MessengerService {

    private MessengerRepository messengerRepository;
    private UserRepository userRepository;

    public MessengerService(MessengerRepository messengerRepository, UserRepository userRepository) {
        this.messengerRepository = messengerRepository;
        this.userRepository = userRepository;
    }

    public void sendMessage(String from, String to, String message){
        if(message.isEmpty()){
            return;
        }
        Integer usersID_From = userRepository.getUsersID(from);
        Integer usersID_To = userRepository.getUsersID(to);
        Message finalMessage = new Message.MessageBuilder().sender(usersID_From).receiver(usersID_To)
                .message(message).build();
        messengerRepository.addMessage(finalMessage);
    }

    public void removeMessage(String from , String to, String message){
        Integer usersID_From = userRepository.getUsersID(from);
        Integer usersID_To = userRepository.getUsersID(to);
        Message finalMessage = new Message.MessageBuilder().sender(usersID_From).receiver(usersID_To)
                .message(message).build();
        messengerRepository.removeMessage(finalMessage);
    }

    public List<MessageUserMapper> getAllMessagesFromUser(String user){
        Integer usersID = userRepository.getUsersID(user);
        List<MessageInfo> allMessagesOfUser = messengerRepository.getAllMessagesOfUser(usersID);
        List<MessageInfo> messageInfos = Message.filterMessages(allMessagesOfUser);
        return buildMessageUser(messageInfos);
    }

    public List<MessageInfo> getConversation(String userIn, String userOut){
        Integer userIn_ID = userRepository.getUsersID(userIn);
        Integer userOut_ID = userRepository.getUsersID(userOut);
        List<MessageInfo> conversation = messengerRepository.getConversation(userIn_ID, userOut_ID);
        List<MessageInfo> conversation1 = messengerRepository.getConversation(userOut_ID, userIn_ID);
        conversation.addAll(conversation1);
        return MessageSort.sortMessagesAscending(conversation);
    }

    private List<MessageUserMapper> buildMessageUser(List<MessageInfo> messageInfos){
        List<MessageUserMapper> mapper = new ArrayList<>();
        for(MessageInfo m : messageInfos){
            String modifiedName = userRepository.getModifiedName(m.userin(), m.userout());
            if(modifiedName == null){
                continue;
            }
            if(modifiedName.equals("unknown")){
                mapper.add(new MessageUserMapper(userRepository.getUsersName(m.userout()),m,
                        userRepository.getUsersName(m.userout())));
            }
            else{
                mapper.add(new MessageUserMapper(userRepository.getUsersName(m.userout()),m,modifiedName));
            }
        }
        return mapper;
    }

    public Map<LocalDateTime,List<MessageInfo>> seperateMessagePerDay(List<MessageInfo> messages){
        TreeMap<LocalDateTime,List<MessageInfo>> map = new TreeMap<>();
        for(MessageInfo m : messages){
            map.computeIfAbsent(m.aDay(), k -> new ArrayList<>());
            List<MessageInfo> messagesForThisDay = map.get(m.aDay());
            messagesForThisDay.add(m);
            map.put(m.aDay(),messagesForThisDay);
        }
        return map;
    }
}

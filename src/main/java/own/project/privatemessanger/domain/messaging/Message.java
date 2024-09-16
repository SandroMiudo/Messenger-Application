package own.project.privatemessanger.domain.messaging;

import own.project.privatemessanger.dto.MessageInfo;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Message{
    String message;
    LocalDateTime localDateTime = LocalDateTime.now();
    boolean unread = true;
    Sender sender;
    Receiver receiver;

    private Message(String message, Receiver receiver, Sender sender){
        this.message = message;
        this.receiver = receiver;
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public boolean isUnread() {
        return unread;
    }

    public int getSender() {
        return sender.senderID();
    }

    public int getReceiver() {
        return receiver.receiverID();
    }

    public void setUnread(){
        unread = false;
    }

    public static class MessageBuilder{
        String message;
        Integer receiverID;
        Integer senderID;

        public MessageBuilder receiver(Integer receiverID){
            this.receiverID = receiverID;
            return this;
        }

        public MessageBuilder sender(Integer senderID){
            this.senderID = senderID;
            return this;
        }

        public MessageBuilder message(String message){
            this.message = message;
            return this;
        }

        public Message build(){
            Receiver receiver = new Receiver(this.receiverID);
            Sender sender = new Sender(this.senderID);
            String message = this.message;
            return new Message(message,receiver,sender);
        }
    }

    public static List<MessageInfo> filterMessages(List<MessageInfo> messages){
        Map<Integer, MessageInfo> messageMap = new HashMap<>();
        for(MessageInfo m : messages){
            if(messageMap.get(m.userout()) == null){
                messageMap.put(m.userout(),m);
            }
            else if(messageMap.get(m.userout()) != null){
                MessageInfo currentMessageInMap = messageMap.get(m.userout());
                if(currentMessageInMap.time().isBefore(m.time())){
                    messageMap.put(m.userout(),m);
                }
            }
        }
        return messageMap.values().stream().sorted((x,y) -> {
            if(x.time().isAfter(y.time())){
                return -1;
            }
            else if(x.time().isEqual(y.time())){
                return 0;
            }
            return 1;
        }).collect(Collectors.toList());
    }
}

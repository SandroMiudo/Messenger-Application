package own.project.privatemessanger.domain.messaging;

import own.project.privatemessanger.dto.MessageInfo;
import own.project.privatemessanger.spring.MessageUserMapper;

import java.util.List;
import java.util.stream.Collectors;

public class MessageSort {

    // vorne ist die Message die als letztes versendet wurde
    public static List<MessageInfo> sortMessagesAscending(List<MessageInfo> messages){
        return messages.stream().sorted((x,y) -> {
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

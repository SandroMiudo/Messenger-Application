package own.project.privatemessanger.spring;

import own.project.privatemessanger.dto.MessageInfo;

public record MessageUserMapper(String username, MessageInfo message, String modifiedName) {
}

package own.project.privatemessanger.app.service;

import java.nio.file.Path;

public interface MailProvider {
    public void sendEmail(String email,String username,String randomNumber);
    public void setPath(Path path);
}

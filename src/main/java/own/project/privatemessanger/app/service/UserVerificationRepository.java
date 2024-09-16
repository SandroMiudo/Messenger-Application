package own.project.privatemessanger.app.service;

import java.util.List;


public interface UserVerificationRepository {
    List<String> getIPs(String user);
}

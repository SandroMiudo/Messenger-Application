package own.project.privatemessanger.dto;

import java.nio.file.Path;

public record UserInfo(String name, String email, String password, String status, Path imgpath ,boolean verified){

    public boolean validateUser(){
        return email != null && password != null && !name.equals("None");
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", status='" + status + '\'' +
                ", imgPath=" + imgpath +
                '}';
    }
}

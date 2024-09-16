package own.project.privatemessanger.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record MessageInfo(String message, LocalDateTime time, boolean unread, Integer userin, Integer userout) {

    public String displayTime(){
        LocalDateTime currentTime = LocalDateTime.now();
        if(time.getDayOfWeek().compareTo(currentTime.getDayOfWeek()) != 0){
            return time.getDayOfWeek().name();
        }
        return time.format(DateTimeFormatter.ISO_LOCAL_TIME);
    }

    public String displayHour(){
        return String.format("%02d",time.getHour()) + ":" +String.format("%02d",time.getMinute());
    }

    public LocalDateTime aDay(){
        return LocalDateTime.of(time.getYear(),time.getMonth(),time.getDayOfMonth(),0,0);
    }
}

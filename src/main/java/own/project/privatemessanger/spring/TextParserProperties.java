package own.project.privatemessanger.spring;

import eu.bitwalker.useragentutils.UserAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TextParserProperties {

    Map<String,String> strings = new HashMap<>();
    @Autowired
    HttpServletRequest request;

    public void insertProperties(List<String> props){
        String header = request.getHeader("User-Agent");
        splitHeaderInformation(header);
        if(props.size() == 1){
            strings.put("user",props.get(0));
        }
        else if(props.size() == 2){
            strings.put("user",props.get(0));
            strings.put("numbers",props.get(1));
        }
    }

    public Map<String,String> getProps() {
        return strings;
    }

    private void splitHeaderInformation(String header){
        UserAgent userAgent = UserAgent.parseUserAgentString(header);
        String browser = userAgent.getBrowser().getName();
        String device = userAgent.getOperatingSystem().getDeviceType().getName();
        device += " " + userAgent.getOperatingSystem().getName();
        String ip = request.getRemoteAddr();
        strings.put("browser",browser);
        strings.put("device",device);
        strings.put("ip",ip);
    }
}

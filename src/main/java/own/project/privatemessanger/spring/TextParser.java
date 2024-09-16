package own.project.privatemessanger.spring;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.UserAgent;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Component
public class TextParser {

    TextParserProperties textParserProperties;
    HttpServletRequest request;
    Path path;

    public TextParser(TextParserProperties textParserProperties, HttpServletRequest request) {
        this.textParserProperties = textParserProperties;
        this.request = request;
    }

    public TextParser setPath(Path path){
        this.path = path;
        return this;
    }

    public String build() throws IOException {
        File file = new File(path.toString());
        return replaceMessage(file);
    }

    private String replaceMessage(File file) throws IOException {
        String string = FileUtils.readFileToString(file, "utf-8");
        Map<String, String> props = textParserProperties.getProps();
        if(string.contains("$ip")){
            string = string.replace("$ip",props.get("ip"));
        }
        if(string.contains("$device")){
            string = string.replace("$device",props.get("device"));
        }
        if(string.contains("$browser")){
            string = string.replace("$browser",props.get("browser"));
        }
        if(props.get("numbers") != null){
            String numbers = props.get("numbers");
            string = replaceNumbers(string,numbers);
        }
        if(string.contains("$user")){
            string = string.replace("$user",props.get("user"));
        }
        return string;
    }

    private String replaceNumbers(String message, String numbers){
        String [] splitNumbers = numbers.split(",");
        int inc = 1;
        for(String s : splitNumbers){
            message = message.replace("$"+inc,s);
            inc++;
        }
        return message;
    }
}

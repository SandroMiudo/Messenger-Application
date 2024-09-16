package own.project.privatemessanger.domain.settings;

public class Settings {
    
    public boolean isValidChange(Boolean dark, Boolean light){
        if(dark == null && light == null){
            return false;
        }
        if(dark != null && light != null){
            return false;
        }
        return true;
    }

    public MODE getMode(Boolean dark){
        if(dark == null){
            dark = false;
        }
        if(dark){
            return MODE.DARK_MODE;
        }
        return MODE.LIGHT_MODE;
    }
}

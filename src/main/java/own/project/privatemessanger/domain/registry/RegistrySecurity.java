package own.project.privatemessanger.domain.registry;

import java.util.List;

class RegistrySecurity {

    List<String> ips;
    String newIP;

    RegistrySecurity(List<String> ips, String newIP){
        this.ips = ips;
        this.newIP = newIP;
    }


}

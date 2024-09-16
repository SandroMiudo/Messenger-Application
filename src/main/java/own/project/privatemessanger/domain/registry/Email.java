package own.project.privatemessanger.domain.registry;

class Email {
    String characters;

    Email(String characters) {
        this.characters = characters;
    }

    boolean valid(){
        if(characters.isEmpty()){
            return false;
        }
        String [] split = characters.split("[@]");
        for(EMAILREG i : EMAILREG.values()){
            if(split[1].equals(i.name().toLowerCase()+".com") || split[1].equals(i.name().toLowerCase()+".de")){
                return true;
            }
        }
        return false;
    }

    void changeEmail(String newEmail){
        String oldEmail = characters;
        characters = newEmail;
        if(!valid()){
            characters = oldEmail;
        }
    }
}

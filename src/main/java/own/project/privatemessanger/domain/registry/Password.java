package own.project.privatemessanger.domain.registry;

import own.project.privatemessanger.exceptions.InvalidPasswordException;

class Password {

    String characters;
    private static final int failureCounter = 5;

    Password(String characters) {
        this.characters = characters;
    }

    boolean min_Length(){
        return characters.length() >= 8;
    }

    boolean exclude_Characters(){
        for(char c : characters.toCharArray()){
            if(c < 48 || c > 57 && c < 65|| c > 90 && c < 97 || c > 122){
                try {
                    throw new InvalidPasswordException("Invalid Password!");
                } catch (InvalidPasswordException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    static int comparePasswords(String a , String b, int counter){
        if(a.equals(b)){
            return 0;
        }
        counter += 1;
        if(failureCounter == counter){
            return -1;
        }
        return 1;
    }

    public void changePassword(String x) {
        String oldPassword = characters;
        characters = x;
        if(!exclude_Characters() || !min_Length()){
            characters = oldPassword;
        }
    }
}

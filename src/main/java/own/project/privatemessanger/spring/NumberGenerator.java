package own.project.privatemessanger.spring;

import java.util.Random;

public class NumberGenerator {

    public static String generateRandomNumber(){
        int [] pin = new int[6];
        Random randomInteger = new Random();
        for(int i = 0; i < pin.length; i++){
            pin[i] = randomInteger.nextInt(0,9);
        }
        return number_as_String(pin);
    }

    private static String number_as_String(int [] ints){
        int counter = 0;
        String finalString = "";
        for(int i : ints){
            finalString += i;
            if(counter < ints.length-1){
                finalString += ",";
            }
            counter++;
        }
        return finalString;
    }
}

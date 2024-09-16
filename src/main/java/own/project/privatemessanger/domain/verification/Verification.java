package own.project.privatemessanger.domain.verification;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Verification {

    public boolean registryVerification(List<Integer> inputs, String code){
        List<Integer> splitCode = splitCode(code);
        return checkSameValues(splitCode,inputs);
    }

    private List<Integer> splitCode(String code){
        String[] splitCode = code.split(",");
        return Arrays.stream(splitCode).map(Integer::parseInt).collect(Collectors.toList());
    }

    private boolean checkSameValues(List<Integer> splitCode, List<Integer> code){
        return splitCode.equals(code);
    }

    public boolean containsIP(List<String> iPs, String clientIP){
        long count = iPs.stream().filter(x -> x.equals(clientIP)).count();
        return count != 0;
    }
}

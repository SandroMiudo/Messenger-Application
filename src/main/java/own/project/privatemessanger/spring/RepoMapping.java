package own.project.privatemessanger.spring;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import own.project.privatemessanger.dto.UserInfo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class RepoMapping {

    public static int mapID(ResultSet rs) throws SQLException {
        if(rs.next()){
            return rs.getInt(1);
        }
        return 0;
    }

    public static boolean mapIsInContacts(ResultSet rs) throws SQLException {
        return rs.next();
    }

    public static String mapIDsToModifiedName(ResultSet rs) throws SQLException {
        if(rs.next()){
            return rs.getString(1);
        }
        return null;
    }

    public static String mapIDToUsername(ResultSet resultSet) throws SQLException {
        if(resultSet.next()){
            return resultSet.getString(1);
        }
        return null;
    }

    public static Set<GrantedAuthority> mapAuthorities(ResultSet resultSet) throws SQLException {
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        boolean isUser = false;
        boolean isLeader = false;
        boolean isAdmin = false;
        if(resultSet.next()){
            isUser = resultSet.getBoolean(1);
            isLeader = resultSet.getBoolean(2);
            isAdmin = resultSet.getBoolean(3);
        }

        if(isUser){
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }
        if(isLeader){
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_LEADER"));
        }
        if(isAdmin){
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        return grantedAuthorities;
    }

    public static UserInfo mapUser(ResultSet rs) throws SQLException {
        if(rs.next()){
            String name = rs.getString(1);
            String email = rs.getString(2);
            String password = rs.getString(3);
            return new UserInfo(name,email,password,null,null,false);
        }
        return null;
    }

    public static UserInfo mapUsername(ResultSet resultSet) throws SQLException {
        if(resultSet.next()){
            return new UserInfo(resultSet.getString(1),null,null,null,null,true);
        }
        return null;
    }

    public static UserInfo mapAllUsers(ResultSet rs, int i) throws SQLException {
        return new UserInfo(rs.getString(1),null,null,null,null,true);
    }
}

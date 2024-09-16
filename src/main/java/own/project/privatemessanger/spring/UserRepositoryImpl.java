package own.project.privatemessanger.spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import own.project.privatemessanger.app.service.LoginAttemptService;
import own.project.privatemessanger.domain.settings.MODE;
import own.project.privatemessanger.dto.UserInfo;
import org.springframework.stereotype.Repository;
import own.project.privatemessanger.app.service.UserRepository;
import javax.servlet.http.HttpServletRequest;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class UserRepositoryImpl implements UserRepository, UserDetailsService {

    LoginAttemptService loginAttemptService;
    HttpServletRequest request;
    String adminAccount;
    List<String> leaderAccounts;
    JdbcTemplate jdbcTemplate;

    public UserRepositoryImpl(JdbcTemplate jdbcTemplate, LoginAttemptService loginAttemptService,
                              HttpServletRequest request, @Value("admin") String adminAccount,
                              @Value("leaders") List<String> leaderAccounts) {
        this.jdbcTemplate = jdbcTemplate;
        this.loginAttemptService = loginAttemptService;
        this.request = request;
        this.adminAccount = adminAccount;
        this.leaderAccounts = leaderAccounts;
    }

    @Override
    public List<UserInfo> searchByName(String name) {
        String sql = """
                select * from messanger.User where name = ?;
                """;
        return jdbcTemplate.query(sql,x -> x.setString(1,name),new DataClassRowMapper<>(UserInfo.class));
    }

    @Override
    public UserInfo addUser(UserInfo userInfo) {
        String sql = """
                INSERT INTO messanger.User (name, email, password, status, imgPath, verified) VALUES (?,?,?,null,null,false)
                """;
        jdbcTemplate.update(sql,x -> {
            x.setString(1,userInfo.name());
            x.setString(2,userInfo.email());
            x.setString(3,userInfo.password());
        });
        Integer usersID = getUsersID(userInfo.name());
        String sqlEinstellung = """
                INSERT INTO messanger.Einstellungen(user, LIGTH_MODE, DARK_MODE) values (?,1,0)
        """;
        jdbcTemplate.update(sqlEinstellung,x -> x.setInt(1,usersID));
        setAuthoritiesFor(userInfo.name());
        return userInfo;
    }

    private void setAuthoritiesFor(String username) {
        boolean isAdmin = false;
        boolean isLeader = false;
        if (username.equals(adminAccount)) {
            isAdmin = true;
        }
        if(leaderAccounts.contains(username)){
            isLeader = true;
        }
        String sql = """
                select id from messanger.User where User.name = ?;
                """;
        final Integer userID = jdbcTemplate.query(sql,x -> x.setString(1,username),RepoMapping::mapID);
        String updateSql = """
                insert into messanger.Authorities (USER, LEADER, ADMIN, usr) VALUES (true,?,?,?)
                """;
        boolean finalIsLeader = isLeader;
        boolean finalIsAdmin = isAdmin;
        jdbcTemplate.update(updateSql, x -> {
            x.setBoolean(1, finalIsLeader);
            x.setBoolean(2,finalIsAdmin);
            x.setInt(3,userID);
        });
    }

    @Override
    public String verifyUser(String name) {
        String sql = """
                update messanger.User set verified = true where name = ?
                """;
        jdbcTemplate.update(sql, x -> x.setString(1, name));
        return name;
    }

    @Override
    public String setNewEmail(String email,String username) {
        String sql = """
                update messanger.User set email = ? where name = ?
                """;
        jdbcTemplate.update(sql,x -> {
            x.setString(1,email);
            x.setString(2,username);
        });
        return email;
    }

    @Override
    public void addIP(String username) {
        String clientIP = getClientIP();
        String searchUser = """
                select id from messanger.User where name = ?
                """;
        Integer userID = jdbcTemplate.query(searchUser, x -> x.setString(1, username),RepoMapping::mapID);

        String sql = """
                insert into messanger.Config (ip_address, user) values(?,?)
                """;
        jdbcTemplate.update(sql,x -> {
            x.setString(1,clientIP);
            x.setInt(2,userID);
        });
    }

    @Override
    public UserInfo getUser(String username) {
        String sql = """
                select name from messanger.User where name = ?
                """;
        return jdbcTemplate.query(sql, x -> x.setString(1,username),
                RepoMapping::mapUsername);
    }

    @Override
    public Integer getUsersID(String username) {
        String sql= """
                select id from messanger.User where name = ?
                """;
        return jdbcTemplate.query(sql, x -> x.setString(1, username), RepoMapping::mapID);
    }

    @Override
    public List<UserInfo> getByRegEX(String regex, String user) {
        String param = "%" + regex + "%";
        String sql = """
                select * from messanger.User where name like ? and name not in(?)
                """;
        return jdbcTemplate.query(sql,x -> {
            x.setString(1,param);
            x.setString(2,user);
        },new DataClassRowMapper<>(UserInfo.class));
    }

    @Override
    public String getUsersName(Integer ID) {
        String sql = """
                select name from messanger.User where id = ?;
                """;
        return jdbcTemplate.query(sql,x -> x.setInt(1,ID),RepoMapping::mapIDToUsername);
    }

    @Override
    public String getModifiedName(Integer userIn, Integer userOut) {
        String sql = """
                select name from messanger.Contacts where userIn = ? and userOut = ?;
                """;
        return jdbcTemplate.query(sql,x -> {
            x.setInt(1,userIn);
            x.setInt(2,userOut);
        },RepoMapping::mapIDsToModifiedName);
    }

    @Override
    public void changeSettings(String username, MODE mode) {
        String sql = "";
        Integer usersID = getUsersID(username);
        if(mode.name().equals(MODE.DARK_MODE.name())){
            System.out.println(usersID);
            sql = """
            update messanger.Einstellungen set DARK_MODE = 1,LIGTH_MODE = 0 where user = ?
            """;
            jdbcTemplate.update(sql,x -> x.setInt(1,usersID));
        }
        if(mode.name().equals(MODE.LIGHT_MODE.name())){
            sql = """
            update messanger.Einstellungen set LIGTH_MODE = 1,DARK_MODE = 0 where user = ?
            """;
            jdbcTemplate.update(sql, x -> x.setInt(1,usersID));
        }
    }

    private static MODE mapMode(ResultSet rs) throws SQLException {
        if(rs.next()){
            boolean mode = rs.getBoolean(1);
            if(!mode){
                return MODE.DARK_MODE;
            }
            return MODE.LIGHT_MODE;
        }
        return null;
    }

    @Override
    public MODE loadSettings(String username) {
        Integer usersID = getUsersID(username);
        String sql = """
                select LIGTH_MODE from messanger.Einstellungen where user = ?;
                """;
        return jdbcTemplate.query(sql,x -> x.setInt(1,usersID),UserRepositoryImpl::mapMode);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String clientIP = getClientIP();
        if(loginAttemptService.isBlocked(clientIP)){
            throw new RuntimeException("blocked client");
        }
        String sql = """
                select name,email,password from User where name = ?
                """;

        String sql2 = """
                select USER,LEADER,ADMIN from User,Authorities where User.id = Authorities.usr and User.name = ?
                """;
        UserInfo usr = jdbcTemplate.query(sql, x -> x.setString(1, username), RepoMapping::mapUser);

        Set<GrantedAuthority> grantedAuthorities = jdbcTemplate.query(sql2, x -> x.setString(1, username), RepoMapping::mapAuthorities);
        if(usr == null || grantedAuthorities == null){
            throw new UsernameNotFoundException("User was not found!");
        }
        return new User(usr.name(),usr.password(), grantedAuthorities);
    }

    private String getClientIP() {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null){
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}

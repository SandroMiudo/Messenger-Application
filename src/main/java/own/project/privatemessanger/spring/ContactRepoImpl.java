package own.project.privatemessanger.spring;

import org.springframework.data.relational.core.sql.In;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;
import own.project.privatemessanger.app.service.ContactRepository;
import own.project.privatemessanger.dto.UserInfo;
import java.util.List;

@Repository
public class ContactRepoImpl implements ContactRepository {

    private final JdbcTemplate jdbcTemplate;

    public ContactRepoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addContact(UserInfo from, UserInfo to) {
        String insertQuery = """
                insert into messanger.Contacts (userIn, userOut) VALUES (?,?)
                """;
        List<Integer> ids = queryID(from, to);
        List<UserInfo> contacts = getContactsOf(from);
        jdbcTemplate.update(insertQuery,x -> {
            x.setInt(1,ids.get(0));
            x.setInt(2,ids.get(1));
        });
    }

    @Override
    public List<UserInfo> getContactsOf(UserInfo user) {
        String sql = """
                select id from messanger.User where name = ?
                """;
        final Integer userID = jdbcTemplate.query(sql, x -> x.setString(1, user.name()), RepoMapping::mapID);
        String sqlContactQuery = """
                select User.name from messanger.Contacts,messanger.User where User.id = Contacts.userOut and userIn = ?
                """;
        return jdbcTemplate.query(sqlContactQuery, x -> x.setInt(1,userID),RepoMapping::mapAllUsers);
    }

    @Override
    public boolean removeContact(UserInfo user, UserInfo removeUser) {
        String removeQuery = """
                delete from messanger.Contacts where userIn = ? and userOut = ?
                """;
        List<Integer> ids = queryID(user, removeUser);
        return 0 != jdbcTemplate.update(removeQuery,x -> {
            x.setInt(1,ids.get(0));
            x.setInt(2,ids.get(1));
        });
    }

    @Override
    public String addNameToContact(String givenName, UserInfo from, UserInfo to) {
        String sql = """
                update messanger.Contacts set Contacts.name = ? where userIn = ? and userOut = ?
                """;
        List<Integer> ids = queryID(from, to);

        jdbcTemplate.update(sql,x -> {
            x.setString(1,givenName);
            x.setInt(2,ids.get(1));
            x.setInt(3,ids.get(0));
        });
        return givenName;
    }

    @Override
    public boolean searchContactByNames(Integer userIn, Integer userOut) {
        String sql = """
                select Contacts.name from messanger.Contacts where userIn = ? and userOut = ?;
                """;
        return Boolean.TRUE.equals(jdbcTemplate.query(sql, x -> {
            x.setInt(1, userIn);
            x.setInt(2, userOut);
        }, RepoMapping::mapIsInContacts));
    }


    private List<Integer> queryID(UserInfo from, UserInfo to){
        String sqlUserFrom = """
                select id from messanger.User where name = ?
                """;
        String sqlUserTo = """
                select id from messanger.User where name = ?
                """;
        Integer fromID = jdbcTemplate.query(sqlUserFrom, x -> x.setString(1, from.name()),
                RepoMapping::mapID);
        Integer toID = jdbcTemplate.query(sqlUserTo, x -> x.setString(1, to.name()), RepoMapping::mapID);
        assert fromID != null && toID != null;
        return List.of(fromID,toID);
    }
}

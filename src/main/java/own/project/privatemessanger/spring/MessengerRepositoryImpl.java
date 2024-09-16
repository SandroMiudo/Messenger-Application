package own.project.privatemessanger.spring;

import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import own.project.privatemessanger.app.service.messaging.MessengerRepository;
import own.project.privatemessanger.domain.messaging.Message;
import own.project.privatemessanger.dto.MessageInfo;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class MessengerRepositoryImpl implements MessengerRepository {

    private JdbcTemplate jdbcTemplate;

    public MessengerRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addMessage(Message message) {
        String sql = """
                insert into messanger.Messages (userIn, userOut, message, time, unread) values(?,?,?,?,true)
                """;
        jdbcTemplate.update(sql,x -> {
            x.setInt(1,message.getSender());
            x.setInt(2,message.getReceiver());
            x.setString(3,message.getMessage());
            x.setTimestamp(4, Timestamp.valueOf(message.getLocalDateTime()));
        });
    }

    @Override
    public void removeMessage(Message message) {
        String sql = """
                delete from messanger.Messages where userOut = ? and userIn = ? and message = ? and time = ?
                """;
        jdbcTemplate.update(sql, x -> {
            x.setInt(1,message.getSender());
            x.setInt(2,message.getReceiver());
            x.setString(3,message.getMessage());
            x.setTimestamp(4, Timestamp.valueOf(message.getLocalDateTime()));
        });
    }


    @Override
    public List<MessageInfo> getConversation(Integer userIn, Integer userOut) {
        String sql = """
                select message,time,unread,userIn,userOut from messanger.Messages where userIn = ? and userOut = ?;
                """;
        return jdbcTemplate.query(sql,x -> {
            x.setInt(1,userIn);
            x.setInt(2,userOut);
        },new DataClassRowMapper<>(MessageInfo.class));
    }

    @Override
    public List<MessageInfo> getAllMessagesOfUser(Integer userID) {
        String sql = """
                select message,time,unread,userIn,userOut from messanger.Messages where userIn = ?;
                """;
        return jdbcTemplate.query(sql,x -> x.setInt(1,userID),new DataClassRowMapper<>(MessageInfo.class));
    }

    @Override
    public void readMessage(Message message) {
        String sql = """
                update messanger.Messages set unread = true where message = ? and userIn = ? and userOut = ?
                and time = ?
                """;
        jdbcTemplate.update(sql,x -> {
            x.setString(1,message.getMessage());
            x.setInt(2,message.getSender());
            x.setInt(3,message.getReceiver());
            x.setTimestamp(4,Timestamp.valueOf(message.getLocalDateTime()));
        });
    }
}

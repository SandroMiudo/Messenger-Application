package own.project.privatemessanger.spring;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import own.project.privatemessanger.app.service.UserVerificationRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class UserVerificationRepoImpl implements UserVerificationRepository {

    private JdbcTemplate jdbcTemplate;

    public UserVerificationRepoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static String mapIPs(ResultSet resultSet, int i) throws SQLException {
        return resultSet.getString(1);
    }

    @Override
    public List<String> getIPs(String user) {
        String sql = """
                select ip_address from messanger.User,messanger.Config where User.id = Config.user and User.name = ?
                """;
        return jdbcTemplate.query(sql, x -> x.setString(1, user),
                UserVerificationRepoImpl::mapIPs);
    }
}

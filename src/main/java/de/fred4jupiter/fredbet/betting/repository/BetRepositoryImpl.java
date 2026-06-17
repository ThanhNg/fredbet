package de.fred4jupiter.fredbet.betting.repository;

import de.fred4jupiter.fredbet.props.FredbetProperties;
import de.fred4jupiter.fredbet.ranking.UsernamePoints;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;

class BetRepositoryImpl implements BetRepositoryCustom {

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    private final FredbetProperties fredbetProperties;

    public BetRepositoryImpl(NamedParameterJdbcOperations namedParameterJdbcOperations, FredbetProperties fredbetProperties) {
        this.namedParameterJdbcOperations = namedParameterJdbcOperations;
        this.fredbetProperties = fredbetProperties;
    }

    @Override
    public List<UsernamePoints> calculateRanging() {
        final String sql = """
                Select user_name, sum(total) as sum_all
                from (
                  (Select user_name, sum(points) as total
                   from bet
                   where user_name not like :username
                   group by user_name order by total desc)
                union all
                  (Select user_name, sum(points_one + points_two + points_three) as total
                   from extra_bet
                   where user_name not like :username
                   group by user_name order by total desc)
                  ) as complete
                group by user_name order by sum_all desc
                """;
        Map<String, Object> params = new HashMap<>();
        params.put("username", fredbetProperties.adminUsername());

        return namedParameterJdbcOperations.query(sql, params, (ResultSet rs, int rowNum) -> new UsernamePoints(rs.getString(1), rs.getInt(2)));
    }

    @Override
    public List<UsernamePoints> calculateRangingByDate(LocalDateTime from, LocalDateTime to) {
        final String sql = """
                Select b.user_name, sum(b.points) as sum_all
                from bet b LEFT JOIN matches m on b.match_id = m.match_id
                where b.user_name not like :username
                  and m.kick_off_date >= :from
                  and m.kick_off_date < :to
                group by b.user_name order by sum_all desc
                """;
        Map<String, Object> params = new HashMap<>();
        params.put("username", fredbetProperties.adminUsername());
        params.put("from", from);
        params.put("to", to);

        return namedParameterJdbcOperations.query(sql, params, (ResultSet rs, int rowNum) -> new UsernamePoints(rs.getString(1), rs.getInt(2)));
    }

}

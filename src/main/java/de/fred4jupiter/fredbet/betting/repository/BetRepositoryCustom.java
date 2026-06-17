package de.fred4jupiter.fredbet.betting.repository;
import de.fred4jupiter.fredbet.ranking.UsernamePoints;
import java.util.List;
import java.time.LocalDateTime;

interface BetRepositoryCustom {

    List<UsernamePoints> calculateRanging();

    List<UsernamePoints> calculateRangingByDate(LocalDateTime from, LocalDateTime to);
}

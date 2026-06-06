package de.fred4jupiter.fredbet.betting;

import de.fred4jupiter.fredbet.betting.repository.BetRepository;
import de.fred4jupiter.fredbet.domain.Joker;
import de.fred4jupiter.fredbet.points.PointsConfigService;
import de.fred4jupiter.fredbet.points.PointsConfiguration;
import org.springframework.stereotype.Service;

@Service
public class JokerService {

    private final BetRepository betRepository;

    private final PointsConfigService pointsConfigService;

    public JokerService(BetRepository betRepository, PointsConfigService pointsConfigService) {
        this.betRepository = betRepository;
        this.pointsConfigService = pointsConfigService;
    }

    public Joker getJokerForUser(String userName) {
        Integer numberOfJokersUsed = betRepository.countNumberOfJokerUsed(userName);
        PointsConfiguration pointsConfig = pointsConfigService.loadPointsConfig();
        return new Joker(numberOfJokersUsed, pointsConfig.getJokerMaxCount());
    }

    public boolean isSettingJokerAllowed(String userName, Long matchId) {
        Joker joker = getJokerForUser(userName);
        if (joker.numberOfJokersUsed() < joker.max()) {
            return true;
        }

        // This bet is one of the bets with the previous set joker. So you can edit this
        return betRepository.findBetsOfGivenMatchWithJokerSet(userName, matchId) != null;
    }
}

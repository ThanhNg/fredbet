package de.fred4jupiter.fredbet.points;

import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.entity.Bet;
import de.fred4jupiter.fredbet.domain.entity.Match;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
 class PointsCalculationUtil {

    private static final int JOKER_MULTIPLIER = 2;

    private final PointsConfigService pointsConfigService;

    public PointsCalculationUtil(PointsConfigService pointsConfigService) {
        this.pointsConfigService = pointsConfigService;
    }

    public int calculatePointsFor(Match match, Bet bet) {
        final int standardPoints = calculateStandardPointsFor(match, bet);
        final int penaltyPoints = calculatePenaltyPointsFor(match, bet);

        final int subtotal = standardPoints + penaltyPoints;
        final int multiplier = getMultiplierForMatch(match);

        if (bet.isJoker()) {
            if (subtotal == 0) {
                // Wrong bet -> deduct max points for this match (with multiplier)
                return pointsConfigService.loadPointsConfig().getPointsCorrectResult() * multiplier * -1;
            }
            else {
                // joker points only if there are points to multiply
                return subtotal * JOKER_MULTIPLIER * multiplier;
            }
        }

        return subtotal * multiplier;
    }

    private int calculatePenaltyPointsFor(Match match, Bet bet) {
        if (match.isGroupMatch()) {
            return 0;
        }

        if (match.isUndecidedResult() && bet.isUndecidedBetting()) {
            if (match.isPenaltyWinnerOne() && bet.isPenaltyWinnerOne()) {
                return 1;
            }

            if (!match.isPenaltyWinnerOne() && !bet.isPenaltyWinnerOne()) {
                return 1;
            }
        }

        return 0;
    }

    private int calculateStandardPointsFor(Match match, Bet bet) {
        if (isSameGoalResult(match, bet)) {
            return pointsConfigService.loadPointsConfig().getPointsCorrectResult();
        }

        if (isSameGoalDifference(match, bet)) {
            return pointsConfigService.loadPointsConfig().getPointsSameGoalDifference();
        }

        if (isCorrectWinner(match, bet)) {
            return pointsConfigService.loadPointsConfig().getPointsCorrectWinner();
        }

        if (isCorrectTotalNumberOfGoals(match, bet)) {
            return pointsConfigService.loadPointsConfig().getPointsCorrectTotalNumberOfGoals();
        }

        // if (isCorrectNumberOfGoalsOfOneTeam(match, bet)) {
        //     return pointsConfigService.loadPointsConfig().getPointsCorrectNumberOfGoalsOneTeam();
        // }
        return 0;
    }

    private boolean isCorrectWinner(Match match, Bet bet) {
        if (match.isKnockoutMatch() && match.isUndecidedResult()) {

            if ((match.isPenaltyWinnerOne() && bet.isTeamOneWinner()) || (!match.isPenaltyWinnerOne() && bet.isTeamTwoWinner())) {
                return true;
            }

            return false;
        }
        return (match.isTeamOneWinner() && bet.isTeamOneWinner()) || (match.isTeamTwoWinner() && bet.isTeamTwoWinner());
    }

    private boolean isSameGoalDifference(Match match, Bet bet) {
        if (match.isTeamOneWinner() && bet.isTeamTwoWinner()) {
            return false;
        }
        if (match.isTeamTwoWinner() && bet.isTeamOneWinner()) {
            return false;
        }

        return match.getGoalDifference().intValue() == bet.getGoalDifference().intValue();
    }

    private boolean isSameGoalResult(Match match, Bet bet) {
        Assert.notNull(match.getGoalsTeamOne(), "no goals team one given");
        Assert.notNull(match.getGoalsTeamTwo(), "no goals team two given");
        return match.getGoalsTeamOne().equals(bet.getGoalsTeamOne()) && match.getGoalsTeamTwo().equals(bet.getGoalsTeamTwo());
    }

    // private boolean isCorrectNumberOfGoalsOfOneTeam(Match match, Bet bet) {
    //     Assert.notNull(match.getGoalsTeamOne(), "no goals team one given");
    //     Assert.notNull(match.getGoalsTeamTwo(), "no goals team two given");
    //     return match.getGoalsTeamOne().equals(bet.getGoalsTeamOne()) || match.getGoalsTeamTwo().equals(bet.getGoalsTeamTwo());
    // }

    private boolean isCorrectTotalNumberOfGoals(Match match, Bet bet) {
        Assert.notNull(match.getGoalsTeamOne(), "no goals team one given");
        Assert.notNull(match.getGoalsTeamTwo(), "no goals team two given");
        return match.getGoalsTeamOne() + match.getGoalsTeamTwo() == bet.getGoalsTeamOne() + bet.getGoalsTeamTwo();
    }

    private int getMultiplierForMatch(Match match) {

        if (match.isGroupMatch()) {
            return 1;
        }

        PointsConfiguration pointsConfig = pointsConfigService.loadPointsConfig();

        if (match.isGroup(Group.ROUND_OF_THIRTY_TWO)) {
            return pointsConfig.getMultiplier_ROUND_OF_THIRTY_TWO();
        }

        if (match.isGroup(Group.ROUND_OF_SIXTEEN)) {
            return pointsConfig.getMultiplier_ROUND_OF_SIXTEEN();
        }

        if (match.isGroup(Group.QUARTER_FINAL)) {
            return pointsConfig.getMultiplier_QUARTER_FINAL();
        }

        if (match.isGroup(Group.SEMI_FINAL)) {
            return pointsConfig.getMultiplier_SEMI_FINAL();
        }

        if (match.isGroup(Group.FINAL)) {
            return pointsConfig.getMultiplier_FINAL();
        }

        if (match.isGroup(Group.GAME_FOR_THIRD)) {
            return pointsConfig.getMultiplier_GAME_FOR_THIRD();
        }

        return 1;
    }
}

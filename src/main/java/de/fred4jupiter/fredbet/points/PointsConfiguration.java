package de.fred4jupiter.fredbet.points;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.NonNull;

public class PointsConfiguration {

    private ExtraPointsConfiguration extraPointsConfig;

    @NotNull
    @Min(value = 0)
    private Integer pointsCorrectResult = 3;

    @NotNull
    @Min(value = 0)
    private Integer pointsSameGoalDifference = 2;

    @NotNull
    @Min(value = 0)
    private Integer pointsCorrectWinner = 1;

    @Min(value = 1)
    private Integer multiplier_ROUND_OF_THIRTY_TWO = 2;

    @Min(value = 1)
    private Integer multiplier_ROUND_OF_SIXTEEN = 2;

    @Min(value = 1)
    private Integer multiplier_QUARTER_FINAL = 3;

    @Min(value = 1)
    private Integer multiplier_SEMI_FINAL = 3;

    @Min(value = 1)
    private Integer multiplier_FINAL = 5;

    @Min(value = 1)
    private Integer multiplier_GAME_FOR_THIRD = 5;

    @NotNull
    @Min(value = 0)
    private Integer pointsCorrectNumberOfGoalsOneTeam = 0;

    @NotNull
    @Min(value = 0)
    private Integer pointsCorrectTotalNumberOfGoals = 0;

    @NotNull
    @Min(value = 0)
    private Integer jokerMaxCount = 3;

    private Boolean jokerPenalty = true;

    public static PointsConfiguration withDefaults() {
        PointsConfiguration pointsConfig = new PointsConfiguration();
        pointsConfig.setPointsCorrectResult(3);
        pointsConfig.setPointsSameGoalDifference(2);
        pointsConfig.setPointsCorrectWinner(1);
        pointsConfig.setPointsCorrectNumberOfGoalsOneTeam(0);
        pointsConfig.setPointsCorrectTotalNumberOfGoals(0);
        pointsConfig.setMultiplier_ROUND_OF_THIRTY_TWO(2);
        pointsConfig.setMultiplier_ROUND_OF_SIXTEEN(2);
        pointsConfig.setMultiplier_QUARTER_FINAL(3);
        pointsConfig.setMultiplier_SEMI_FINAL(3);
        pointsConfig.setMultiplier_FINAL(5);
        pointsConfig.setMultiplier_GAME_FOR_THIRD(5);
        pointsConfig.setJokerMaxCount(3);
        pointsConfig.setJokerPenalty(true);
        return pointsConfig;
    }

    public ExtraPointsConfiguration getExtraPointsConfig() {
        if (this.extraPointsConfig == null) {
            this.extraPointsConfig = createWithDefaults();
        }
        return extraPointsConfig;
    }

    private @NonNull ExtraPointsConfiguration createWithDefaults() {
        ExtraPointsConfiguration extraPointsConfig = new ExtraPointsConfiguration();
        extraPointsConfig.setPointsFinalWinner(10);
        extraPointsConfig.setPointsSemiFinalWinner(5);
        extraPointsConfig.setPointsThirdFinalWinner(2);
        return extraPointsConfig;
    }

    public void setExtraPointsConfig(ExtraPointsConfiguration extraPointsConfig) {
        this.extraPointsConfig = extraPointsConfig;
    }

    public Integer getPointsCorrectResult() {
        return pointsCorrectResult;
    }

    public void setPointsCorrectResult(Integer pointsCorrectResult) {
        this.pointsCorrectResult = pointsCorrectResult;
    }

    public Integer getPointsSameGoalDifference() {
        return pointsSameGoalDifference;
    }

    public void setPointsSameGoalDifference(Integer pointsSameGoalDifference) {
        this.pointsSameGoalDifference = pointsSameGoalDifference;
    }

    public Integer getPointsCorrectWinner() {
        return pointsCorrectWinner;
    }

    public void setPointsCorrectWinner(Integer pointsCorrectWinner) {
        this.pointsCorrectWinner = pointsCorrectWinner;
    }

    public Integer getPointsCorrectNumberOfGoalsOneTeam() {
        return pointsCorrectNumberOfGoalsOneTeam;
    }

    public void setPointsCorrectNumberOfGoalsOneTeam(Integer pointsCorrectNumberOfGoalsOneTeam) {
        this.pointsCorrectNumberOfGoalsOneTeam = pointsCorrectNumberOfGoalsOneTeam;
    }

    public Integer getPointsCorrectTotalNumberOfGoals() {
        return pointsCorrectTotalNumberOfGoals;
    }

    public void setPointsCorrectTotalNumberOfGoals(Integer pointsCorrectTotalNumberOfGoals) {
        this.pointsCorrectTotalNumberOfGoals = pointsCorrectTotalNumberOfGoals;
    }

    public Integer getMultiplier_ROUND_OF_THIRTY_TWO() {
        return multiplier_ROUND_OF_THIRTY_TWO;
    }

    public void setMultiplier_ROUND_OF_THIRTY_TWO(Integer multiplier_ROUND_OF_THIRTY_TWO) {
        this.multiplier_ROUND_OF_THIRTY_TWO = multiplier_ROUND_OF_THIRTY_TWO;
    }

    public Integer getMultiplier_ROUND_OF_SIXTEEN() {
        return multiplier_ROUND_OF_SIXTEEN;
    }

    public void setMultiplier_ROUND_OF_SIXTEEN(Integer multiplier_ROUND_OF_SIXTEEN) {
        this.multiplier_ROUND_OF_SIXTEEN = multiplier_ROUND_OF_SIXTEEN;
    }

    public Integer getMultiplier_QUARTER_FINAL() {
        return multiplier_QUARTER_FINAL;
    }

    public void setMultiplier_QUARTER_FINAL(Integer multiplier_QUARTER_FINAL) {
        this.multiplier_QUARTER_FINAL = multiplier_QUARTER_FINAL;
    }

    public Integer getMultiplier_SEMI_FINAL() {
        return multiplier_SEMI_FINAL;
    }

    public void setMultiplier_SEMI_FINAL(Integer multiplier_SEMI_FINAL) {
        this.multiplier_SEMI_FINAL = multiplier_SEMI_FINAL;
    }

    public Integer getMultiplier_FINAL() {
        return multiplier_FINAL;
    }

    public void setMultiplier_FINAL(Integer multiplier_FINAL) {
        this.multiplier_FINAL = multiplier_FINAL;
    }

    public Integer getMultiplier_GAME_FOR_THIRD() {
        return multiplier_GAME_FOR_THIRD;
    }

    public void setMultiplier_GAME_FOR_THIRD(Integer multiplier_GAME_FOR_THIRD) {
        this.multiplier_GAME_FOR_THIRD = multiplier_GAME_FOR_THIRD;
    }

    public Integer getJokerMaxCount() {
        return jokerMaxCount;
    }

    public void setJokerMaxCount(Integer jokerMaxCount) {
        this.jokerMaxCount = jokerMaxCount;
    }

    public Boolean getJokerPenalty() {
        return jokerPenalty;
    }

    public void setJokerPenalty(Boolean jokerPenalty) {
        this.jokerPenalty = jokerPenalty;
    }
}

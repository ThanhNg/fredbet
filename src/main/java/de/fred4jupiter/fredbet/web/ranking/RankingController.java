package de.fred4jupiter.fredbet.web.ranking;
import de.fred4jupiter.fredbet.domain.RankingSelection;
import de.fred4jupiter.fredbet.ranking.RankingService;
import de.fred4jupiter.fredbet.ranking.UsernamePoints;
import de.fred4jupiter.fredbet.match.MatchService;
import de.fred4jupiter.fredbet.betting.BettingService;
import de.fred4jupiter.fredbet.util.ResponseEntityUtil;
import de.fred4jupiter.fredbet.util.Validator;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import de.fred4jupiter.fredbet.web.matches.MatchCommand;
import de.fred4jupiter.fredbet.web.matches.MatchCommandMapper;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.ArrayList;

@Controller
@RequestMapping("/ranking")
public class RankingController {

    private static final String CONTENT_TYPE_PDF = "application/pdf";

    private static final String PAGE_RANKING = "ranking/list";

    private static final String PAGE_RANKING_USER = "ranking/user";

    private final RankingService rankingService;

    private final BettingService bettingService;

    private final WebMessageUtil messageUtil;

    private final MatchCommandMapper matchCommandMapper;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMyy_HHmmss");

    public RankingController(RankingService rankingService, BettingService bettingService, WebMessageUtil messageUtil, MatchCommandMapper matchCommandMapper) {
        this.rankingService = rankingService;
        this.bettingService = bettingService;
        this.messageUtil = messageUtil;
        this.matchCommandMapper = matchCommandMapper;
    }

    @GetMapping
    public String list(Model model, @RequestParam(required = false, defaultValue = "mixed") String mode,
                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekDate) {

        final RankingSelection rankingSelection = RankingSelection.fromMode(mode);
        List<UsernamePoints> rankings = rankingService.calculateCurrentRanking(rankingSelection);
        if (Validator.isEmpty(rankings) && RankingSelection.MIXED.equals(rankingSelection)) {
            messageUtil.addInfoMsg(model, "ranking.noRankings");
            model.addAttribute("rankings", rankings);
            model.addAttribute("rankingSelection", rankingSelection);
            return PAGE_RANKING;
        }

        for (int i = 0; i < rankings.size(); i++) {
            rankings.get(i).setCssRankClass(getCssRankingClassForPosition(i));
        }

        ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");

        LocalDate baseDate = (weekDate != null) ? weekDate : LocalDate.now(zoneId);
        ZonedDateTime lastMondayNoon = baseDate.atStartOfDay(zoneId).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).with(LocalTime.NOON);
        ZonedDateTime nextMondayNoon = lastMondayNoon.plusWeeks(1);

        List<UsernamePoints> weekRankings = rankingService.calculateThisWeekRanking(rankingSelection, lastMondayNoon.toLocalDateTime(), nextMondayNoon.toLocalDateTime());
        for (int i = 0; i < weekRankings.size(); i++) {
            weekRankings.get(i).setCssRankClass(getCssRankingClassForPosition(i));
        }

        model.addAttribute("rankings", rankings);
        model.addAttribute("weekRankings", weekRankings);
        model.addAttribute("weekStart", lastMondayNoon.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        model.addAttribute("weekEnd", nextMondayNoon.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        model.addAttribute("weekDate", baseDate.toString());
        model.addAttribute("rankingSelection", rankingSelection);
        return PAGE_RANKING;
    }

    @GetMapping(value = "/pdf", produces = CONTENT_TYPE_PDF)
    public ResponseEntity<byte[]> exportAllBets(@RequestParam(required = false, defaultValue = "mixed") String mode) {
        final RankingSelection rankingSelection = RankingSelection.fromMode(mode);
        final String fileName = createFilename(mode);
        byte[] fileContent = this.rankingService.exportBetsToPdf(LocaleContextHolder.getLocale(), rankingSelection);
        if (fileContent == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntityUtil.createResponseEntity(fileName, fileContent, CONTENT_TYPE_PDF);
    }

    @GetMapping("/user")
    public String listUserBets(Model model,
        @RequestParam(required = true) String username,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekDate) {

        List<MatchCommand> allUserMatches = matchCommandMapper.findUserMatches(username, MatchService::findAllPastMatches);
        List<MatchCommand> weekMatches = new ArrayList<>(allUserMatches);

        if (weekDate != null) {
            ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");

            LocalDate baseDate = (weekDate != null) ? weekDate : LocalDate.now(zoneId);
            ZonedDateTime lastMondayNoon = baseDate.atStartOfDay(zoneId).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).with(LocalTime.NOON);
            ZonedDateTime nextMondayNoon = lastMondayNoon.plusWeeks(1);

            // Filter user bets for the specified week using loop
            weekMatches.removeIf(matchCommand -> {
                LocalDateTime kickOffDate = matchCommand.getMatch().getKickOffDate();
                if (kickOffDate == null) {
                    return true; // Remove matches with null kick-off date
                }
                return kickOffDate.isBefore(lastMondayNoon.toLocalDateTime()) || kickOffDate.isAfter(nextMondayNoon.toLocalDateTime());
            });

            model.addAttribute("weekStart", lastMondayNoon.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            model.addAttribute("weekEnd", nextMondayNoon.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            model.addAttribute("weekDate", baseDate.toString());
        }

        model.addAttribute("userMatches", weekMatches);
        model.addAttribute("username", username);
        return PAGE_RANKING_USER;
    }

    private String createFilename(String mode) {
        return "%s_%s_fredbet_ranking.pdf".formatted(dateTimeFormatter.format(LocalDateTime.now()), mode);
    }

    private String getCssRankingClassForPosition(int position) {
        return switch (position) {
            case 0 -> "label-success";
            case 1 -> "label-primary";
            case 2 -> "label-warning";
            case 3 -> "label-rank4";
            case 4 -> "label-rank5";
            case 5 -> "label-rank6";
            default -> "label-default";
        };
    }
}

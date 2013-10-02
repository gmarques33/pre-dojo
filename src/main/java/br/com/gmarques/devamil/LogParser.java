package br.com.gmarques.devamil;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import br.com.gmarques.devamil.bean.Action;
import br.com.gmarques.devamil.bean.EventType;
import br.com.gmarques.devamil.bean.Match;
import br.com.gmarques.devamil.bean.Player;

public class LogParser {

	public static final String EXAMPLE_TEST = "23/04/2013 15:34:22 - New match 11348965 has started";

	private static final String DATE_PATTERN = "((?:(?:[0-2]?\\d{1})|(?:[3][01]{1}))[-:\\/.](?:[0]?[1-9]|[1][012])[-:\\/.](?:(?:[1]{1}\\d{1}\\d{1}\\d{1})|(?:[2]{1}\\d{3})))(?![\\d])";
	private static final String TIME24HOURS_PATTERN = "([01]?[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])";
	private static final String SEPARATOR_PATTERN = " - ";
	private static final String LINE_HEADER_PATTERN = DATE_PATTERN + " "
			+ TIME24HOURS_PATTERN + SEPARATOR_PATTERN;

	private static final String NEW_MATCH_PATTERN = "New match .* has started";
	private static final String PLAYER_KILL_PATTERN = ".*killed.*using.*";
	private static final String WORLD_KILL_PATTERN = "<WORLD> killed.*by.*";
	private static final String END_MATCH_PATTERN = "Match .* has ended";

	// Como os atributos abaixo sao muito usados, foram deixados globais pro
	// questoes de performance
	private static final String DATE_FORMAT = "dd/MM/yyyy kk:mm:ss";
	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
			DATE_FORMAT);
	private Pattern datePattern = Pattern.compile(DATE_PATTERN + " "
			+ TIME24HOURS_PATTERN);

	private List<Match> matches = new ArrayList<Match>();
	private Match currentMatch; // For performance

	public LogParser() {
	}

	public List<Match> getMatches() {
		return matches;
	}

	public void parseFile(String filePath) throws IOException {

		LineIterator it = FileUtils.lineIterator(new File(filePath), "UTF-8");
		try {

			while (it.hasNext()) {
				String line = it.nextLine();
				EventType et = identifyEventType(line);
				if(et != null) {
					switch (et) {
					case MATCH_START:
						processMatchStart(line);
						break;
					case PLAYER_KILL:
						processPlayerKill(line);
						break;
					case WORLD_KILL:
						processWorldKill(line);
						break;
					case MATCH_END:
						processMatchEnd(line);
						break;
					default:
						// TODO: log4j de linha invalida
						break;
					}
				}
			}
		} finally {
			it.close();
		}

	}

	private void processMatchStart(String line) {
		int id = Integer.parseInt(line.substring(line.indexOf("match")
				+ "match ".length(), line.indexOf(" has")));
		try {
			currentMatch = new Match(id, getDateFromLine(line));
			matches.add(currentMatch);
		} catch (ParseException e) {
			// TODO log4j
			e.printStackTrace();
		}
	}

	private void processMatchEnd(String line) {
		try {
			currentMatch.setEnd(getDateFromLine(line));
		} catch (ParseException e) {
			// TODO log4j
			e.printStackTrace();
		}
		currentMatch = null;
	}

	private void processPlayerKill(String line) {
		Action action = new Action();
		try {
			action.setTime(getDateFromLine(line));
			line = removeDateFromLine(line);

			String playerKiller = line.substring(0, line.indexOf(" killed"));
			String playerKilled = line.substring(line.indexOf("killed")
					+ "killed ".length(), line.indexOf(" using"));
			String weapon = line.substring(line.indexOf("using")
					+ "using ".length());
			action.setWeapon(weapon);

			if (currentMatch == null) {
				// TODO: log4j/exception de log incorreto
				return;
			}

			// Adiciona informacao ao player que matou
			Player player = currentMatch.findPlayer(playerKiller);
			if (player == null) {
				player = new Player(playerKiller);
				currentMatch.getPlayers().add(player);
			}
			player.addKill(action);

			// Adiciona informacao ao player morto
			player = currentMatch.findPlayer(playerKilled);
			if (player == null) {
				player = new Player(playerKilled);
				currentMatch.getPlayers().add(player);
			}
			player.addDeath(action);
		} catch (ParseException e) {
			// TODO log4j
			e.printStackTrace();
		}

	}

	private void processWorldKill(String line) {
		Action action = new Action();
		try {
			action.setTime(getDateFromLine(line));
			line = removeDateFromLine(line);

			String playerKilled = line.substring(line.indexOf("killed")
					+ "killed ".length(), line.indexOf(" by"));
			String weapon = "<WORLD> "
					+ line.substring(line.indexOf("by") + "by ".length());
			action.setWeapon(weapon);
			
			if (currentMatch == null) {
				// TODO: log4j/exception de log incorreto
				return;
			}
			
			Player player = currentMatch.findPlayer(playerKilled);
			if (player == null) {
				player = new Player(playerKilled);
				currentMatch.getPlayers().add(player);
			}
			player.addDeath(action);
		} catch (ParseException e) {
			// TODO log4j
			e.printStackTrace();
		}

	}

	private String removeDateFromLine(String line) {
		Pattern replace = Pattern.compile(LINE_HEADER_PATTERN);
		Matcher matcher = replace.matcher(line);
		return matcher.replaceAll("");

	}

	private Date getDateFromLine(String line) throws ParseException {
		Matcher matcher = datePattern.matcher(line);
		Date date = null;

		if (matcher.find()) {
			date = simpleDateFormat.parse(line);
		}

		return date;
	}

	private EventType identifyEventType(String line) {
		if (line.matches(LINE_HEADER_PATTERN + NEW_MATCH_PATTERN)) {
			return EventType.MATCH_START;
		} else if (line.matches(LINE_HEADER_PATTERN + PLAYER_KILL_PATTERN)) {
			return EventType.PLAYER_KILL;
		} else if (line.matches(LINE_HEADER_PATTERN + WORLD_KILL_PATTERN)) {
			return EventType.WORLD_KILL;
		} else if (line.matches(LINE_HEADER_PATTERN + END_MATCH_PATTERN)) {
			return EventType.MATCH_END;
		} else {
			return null;
		}
	}
}
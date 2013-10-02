package br.com.gmarques.devamil;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import br.com.gmarques.devamil.bean.Match;
import br.com.gmarques.devamil.bean.Player;

public class RankingTest {

	final String logPath = "src/test/resources/log.txt";
	
    @Test
    public void testNumberMatches() throws IOException {
    	LogParser logParser = new LogParser();
    	
    	logParser.parseFile(logPath);
		List<Match> matches = logParser.getMatches();
		assertEquals(2, matches.size());
    }

    @Test
    public void testRomanKills() throws IOException {
    	int kills = 0;
    	
    	LogParser logParser = new LogParser();
    	
    	logParser.parseFile(logPath);
		Match match = logParser.getMatches().get(0);
		for(Player player : match.getPlayers()) {
			if(player.getName().equalsIgnoreCase("Roman")) {
				kills = player.getKills().size();
			}
		}
		assertEquals(1, kills);
    }
    
    @Test
    public void testNickDeaths() throws IOException {
    	int kills = 0;
    	
    	LogParser logParser = new LogParser();
    	
    	logParser.parseFile(logPath);
		Match match = logParser.getMatches().get(0);
		for(Player player : match.getPlayers()) {
			if(player.getName().equalsIgnoreCase("Nick")) {
				kills = player.getDeaths().size();
			}
		}
		assertEquals(2, kills);
    }


}

package br.com.gmarques.devamil;

import java.io.IOException;
import java.util.List;

import br.com.gmarques.devamil.bean.Match;
import br.com.gmarques.devamil.bean.Player;

public class RankingSystem {
    
	public static void main(String[] args) {
		if(args.length < 1) {
			System.out.println("Usage: java -jar {jar} logFilePath");
			return;
		}
		
		LogParser logParser = new LogParser();
		try {
			logParser.parseFile(args[0]);
			List<Match> matches = logParser.getMatches();
			
			for(Match match : matches) {
				System.out.println("\nMatch " + match.getId() + "\n");
				System.out.println("Player Name | Kills | Deaths | Streak | Awards | Weapon");
				for(Player player : match.getPlayers()) {
					System.out.println(player.getName() + " | " + player.getKills().size() + " | " + player.getDeaths().size() + " | " + player.getStreak() + " | " + player.getAwards().size() + " | " + player.favoriteWeapon());
				}
			}
			
		} catch (IOException e) {
			// TODO log4j
			e.printStackTrace();
		}
	}

}
package br.com.gmarques.devamil.bean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Player {
	
	private String name;
	private List<AwardType> awards;
	private List<Action> kills;	
	private List<Action> deaths;
	private int streak;
	
	public Player(String name) {
		this.name = name;
		streak = 0;
		awards = new ArrayList<AwardType>();
		kills = new ArrayList<Action>();
		deaths = new ArrayList<Action>();
	}
	//TODO: remover getters e setters desnecessarios
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public List<AwardType> getAwards() {
		return awards;
	}

	public void setAwards(List<AwardType> awards) {
		this.awards = awards;
	}

	public List<Action> getKills() {
		return kills; // TODO: Proteger para evitar inserçoes. Usar apenas o metodo addKill
	}
	public void setKills(List<Action> kills) {
		this.kills = kills;
	}
	public List<Action> getDeaths() {
		return deaths;
	}
	public void setDeaths(List<Action> deaths) {
		this.deaths = deaths;
	}
	public int getStreak() {
		return streak;
	}
	
	public void addDeath(Action death) {
		deaths.add(death);
		streak = 0;
	}
	
	public void addKill(Action kill) {
		kills.add(kill);
		streak++;
		int nKills = kills.size();
		if(nKills >= 5) {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.MINUTE, -1);
			
			if(kills.get(nKills -5).getTime().after(calendar.getTime())) {
				awards.add(AwardType.FIVE_KILLS_ONE_MIN);
			}
		}
	}
	
	public String favoriteWeapon() {
		TreeMap<String, Integer> weaponMap = new TreeMap<String, Integer>(); 
		int bigger = 0;
		String weapon = "";

		for(Action action : kills) {
			Integer c = weaponMap.get(action.getWeapon());
			if(c == null) {
				c = 1;
			} else {
				c = c + 1;
			}			
			weaponMap.put(action.getWeapon(), c);
		}

		for(String actualWeapon : weaponMap.keySet()) {
			if(weaponMap.get(actualWeapon) > bigger) {
				bigger = weaponMap.get(actualWeapon);
				weapon = actualWeapon;
			}
		}

		return weapon;
	}
		
	@Override
    public int hashCode() {
        return name.hashCode();
    }
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof Player))
            return false;
        
        return ((Player) obj).getName().equals(this.name);
	}

}

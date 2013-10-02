package br.com.gmarques.devamil.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Match {
	
	private Integer id;
	private Date start;
	private Date end;
	private List<Player> players;
		
	public Match(Integer id, Date start) {
		this.id = id;
		this.start = start;
		players = new ArrayList<Player>();
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getStart() {
		return start;
	}
	
	public void setStart(Date start) {
		this.start = start;
	}
	
	public Date getEnd() {
		return end;
	}
	
	public void setEnd(Date end) {
		this.end = end;
	}
	
	public List<Player> getPlayers() {
		return players;
	}
	
	public void setPlayers(List<Player> players) {
		this.players = players;
	}	
	
	public Player findPlayer(String name) {
		int position = players.indexOf(new Player(name));
		return (position == -1) ? null : players.get(position);
	}
}

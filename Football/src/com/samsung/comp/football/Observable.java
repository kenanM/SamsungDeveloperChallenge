package com.samsung.comp.football;


public interface Observable {
	public void attach(Observer observer);
	public void remove(Observer observer);
	public void notifyObserver();
}

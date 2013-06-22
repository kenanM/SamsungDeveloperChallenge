package com.samsung.comp.events;


public interface ButtonPressEvent {
	public void subscribe(ButtonPressListener observer);
	public void unsubscribe(ButtonPressListener observer);
	public void buttonPressFired();
}

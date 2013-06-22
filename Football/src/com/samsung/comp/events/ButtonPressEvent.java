package com.samsung.comp.events;


public interface ButtonPressEvent {
	public void setListener(ButtonPressListener observer);
	public void clearListener(ButtonPressListener observer);
	public void buttonPressFired();
}

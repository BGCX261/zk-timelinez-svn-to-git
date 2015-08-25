package org.zkforge.timeline.event;

import org.zkforge.timeline.data.OccurEvent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;

public class OccurEventSelectEvent extends Event {
	private OccurEvent _evt;

	public OccurEventSelectEvent(String name, Component target, OccurEvent evt) {
		super(name, target);
		// TODO Auto-generated constructor stub
		this._evt = evt;
	}

	public OccurEvent getOccurEvent() {
		return _evt;
	}

}

package org.zkforge.timeline.event;

import java.util.Date;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;

public class BandScrollEvent extends Event {
	private String _name;

	private Date _min;

	private Date _max;
	
	private Date _center;

	private Component _band;

	public BandScrollEvent(String name, Component target, Date min, Date max, Date center) {
		super(name, target);
		// TODO Auto-generated constructor stub
		_min = min;
		_max = max;
		_band = target;
		_name = name;
		_center = center;
	}

	public Component getBand() {
		return _band;
	}

	public void setBand(Component band) {
		this._band = band;
	}

	public Date getMax() {
		return _max;
	}

	public void setMax(Date max) {
		this._max = max;
	}

	public Date getMin() {
		return _min;
	}

	public void setMin(Date min) {
		this._min = min;
	}
	
	public Date getCenter() {
		return _center;
	}
	
	public void setCenter(Date center) {
		_center = center;
	}

}

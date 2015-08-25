/* Hotzone.java

	Purpose:
		
	Description:
		
	History:
		Fri Oct  23 16:00:44 TST 2009, Created by Jimmy

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
 */

package org.zkforge.timeline;

import java.io.IOException;
import java.util.Date;

import org.zkforge.timeline.util.TimelineUtil;
import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zul.Image;
import org.zkoss.zul.impl.XulElement;

/**
 * The hotzone component.
 *
 * <p>See also <a href="http://simile.mit.edu/timeline">MIT Timeline</a>
 *
 * @author Jimmy
 */
public class Hotzone extends XulElement {
	private Date _start = new Date();

	private Date _end = new Date();

	private int _magnify = 7;// default value

	private String _unit = "week";// default value

	private int _multiple = 1;// default value	
	
	/** Returns the start date.
	 */
	public Date getStart() {
		return _start;
	}
	/** Sets the start date.
	 */
	public void setStart(Date start) {
		if (!_start.equals(start)) {
			_start = start;
			smartUpdate("start", TimelineUtil.formatDateTime(start));
		}
	}
	
	/** Returns the end date.
	 */
	public Date getEnd() {
		return _end;
	}

	/** Sets the end date.
	 */
	public void setEnd(Date end) {
		if (!_end.equals(end)) {
			_end = end;
			smartUpdate("end", TimelineUtil.formatDateTime(end));
		}
	}

	/**
	 * 
	 * @return Magnify
	 */
	public int getMagnify() {
		return _magnify;
	}

	/**
	 * set magnify of the hotzone
	 * @param magnify
	 */
	public void setMagnify(int magnify) {
		if (_magnify != magnify) {
			_magnify = magnify;
			smartUpdate("magnify", magnify);
		}
	}

	/**
	 * @return Unit of the hotzone
	 */
	public String getUnit() {
		return _unit;
	}

	/**
	 * set Unit of the hotzone
	 * @param unit
	 */
	public void setUnit(String unit) {
		if (!Objects.equals(_unit, unit)) {
			_unit = unit;
			smartUpdate("unit", unit);
		}
	}

	/**
	 * 
	 * @return Multiple of the hotzone
	 */
	public int getMultiple() {
		return _multiple;
	}

	/**
	 * set multiple of the hotzone
	 * @param multiple
	 */
	public void setMultiple(int multiple) {
		if (_multiple != multiple) {
			_multiple = multiple;
			smartUpdate("multiple", multiple);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.zkoss.zk.ui.AbstractComponent#insertBefore(org.zkoss.zk.ui.Component, org.zkoss.zk.ui.Component)
	 */
	public boolean insertBefore(Component child, Component insertBefore) {
		 throw new UiException("Unsupported child for timeline");
	}
	/*
	 * (non-Javadoc)
	 * @see org.zkoss.zk.ui.AbstractComponent#setParent(org.zkoss.zk.ui.Component)
	 */
	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Bandinfo))
			throw new UiException("Unsupported parent for hotzone: " + parent);
		super.setParent(parent);
	}

	/*
	 * (non-Javadoc)
	 * @see org.zkoss.zul.impl.XulElement#renderProperties(org.zkoss.zk.ui.sys.ContentRenderer)
	 */
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
			throws IOException {
		super.renderProperties(renderer);
		render(renderer, "start", TimelineUtil.formatDateTime(_start));
		render(renderer, "end", TimelineUtil.formatDateTime(_end));
		if (_magnify != 7)
			renderer.render("magnify", _magnify);
		if (!"week".equals(_unit))
			render(renderer, "unit", _unit);
		if (_multiple != 1)
			renderer.render("multiple", _multiple);
	}

	
}

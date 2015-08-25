/* Timeline.java

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

import org.zkforge.json.simple.*;
import org.zkoss.lang.*;
import org.zkoss.zk.ui.*;
import org.zkoss.zul.impl.XulElement;

/**
 * The timeline component.
 * 
 * <p>
 * See also <a href="http://simile.mit.edu/timeline">MIT Timeline</a>
 * 
 * @author Jimmy
 */
public class Timeline extends XulElement {
	private String _orient = "horizontal";// default

	private String _height = "150px";// default

	private String _width = "100%";// default

	/**
	 * Returns the orientation of {@link Timeline}.
	 */
	public String getOrient() {
		return _orient;
	}

	/**
	 * Sets the orientation of {@link Timeline}.
	 */
	public void setOrient(String orient) throws WrongValueException {		
		if (!"horizontal".equals(orient) && !"vertical".equals(orient))
			throw new WrongValueException(orient);

		if (!Objects.equals(_orient, orient)) {
			_orient = orient;
			smartUpdate("orient", orient);
		}
	}

	/**
	 * Returns the height.
	 * 
	 * @return the height
	 */
	public String getHeight() {
		return _height;
	}

	/**
	 * Sets the height.
	 * 
	 * @param height
	 *            the height to set
	 */
	public void setHeight(String height) {
		if (!Objects.equals(_height, height)) {
			_height = height;
			smartUpdate("height", height);
		}
	}

	/**
	 * Returns the width
	 */
	public String getWidth() {
		return _width;
	}

	/**
	 * Sets the width.
	 */
	public void setWidth(String width) {
		if (!Objects.equals(_width, width)) {
			_width = width;
			smartUpdate("width", width);
		}
	}
	
	/**
	 * set filterText to filter occur event
	 * @param filterText
	 */
	public void performFiltering(String filterText) {
		smartUpdate("filter", filterText);
	}

	/**
	 * clear occur event Filter
	 */
	public void clearFilter() {
		performFiltering("");
	}

	/**
	 * set highlightText to highlight some occur event,Max setting only four occur event
	 * @param highlightText
	 */
	public void performHighlitht(String highlightText[]) {
		JSONArray matchers = new JSONArray();
		for (int i = 0; i < highlightText.length; i++) {
			matchers.add(highlightText[i]);
		}
		smartUpdate("highlight", matchers.toString());
	}

	/**
	 * clear occur event Highlight
	 */
	public void clearHighlight() {
		String highlightText[] ={""};
		performHighlitht(highlightText);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.zkoss.zk.ui.AbstractComponent#insertBefore(org.zkoss.zk.ui.Component, org.zkoss.zk.ui.Component)
	 */
	public boolean insertBefore(Component child, Component insertBefore) {
		 if (!(child instanceof Bandinfo))
		 throw new UiException("Unsupported child for timeline: " + child);
		return super.insertBefore(child, insertBefore);
	}

	/*
	 * (non-Javadoc)
	 * @see org.zkoss.zul.impl.XulElement#renderProperties(org.zkoss.zk.ui.sys.ContentRenderer)
	 */
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
			throws IOException {
		super.renderProperties(renderer);
		if (!"horizontal".equals(_orient))
			render(renderer, "orient", _orient);
		if (!"150px".equals(_height))
			render(renderer, "height", _height);
		if (!"100%".equals(_width))
			render(renderer, "width", _width);
	}
	

}

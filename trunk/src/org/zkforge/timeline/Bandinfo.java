/* Bandinfo.java

	Purpose:
		
	Description:
		
	History:
		Fri Oct  23 16:00:44 TST 2009, Created by Jimmy

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
 */

package org.zkforge.timeline;


import java.io.*;
import java.util.*;
import java.text.*;

import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.out.AuScript;
import org.zkoss.zk.au.out.AuSetAttribute;
import org.zkforge.timeline.data.*;
import org.zkforge.timeline.decorator.HighlightDecorator;
import org.zkforge.timeline.event.*;
import org.zkforge.timeline.util.*;
import org.zkoss.json.JSONArray;
import org.zkoss.lang.*;
import org.zkoss.zk.mesg.*;
import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.*;
import org.zkoss.zul.event.*;
import org.zkoss.zul.impl.XulElement;

/**
 * The Bandinfo component.
 * 
 * <p>
 * See also <a href="http://simile.mit.edu/timeline">MIT Timeline</a>
 * 
 * @author Jimmy
 */
public class Bandinfo extends XulElement {
	private String _width = "70%";
	private String _intervalUnit = "month";
	private int _intervalPixels = 100;
	private String _syncWith;
	private String _eventSourceUrl;
	private boolean _overview = false;
	private TimeZone _timeZone = TimeZone.getDefault();
	private Date _date = new Date();
	
	private ListModel _model;
	private SortedSet _events = new TreeSet();//
	private LinkedList _eventList = new LinkedList();// store the event's index;
	private Date _min;
	private Date _max;
	private transient ListDataListener _dataListener;
	private transient BandScrollListener _bandScrollListener;
	
	private boolean _bubbleVisible = true;
	
	private List _addEvtList, _mdyEvtList, _rmEvtList;

	private static final String ATTR_ON_ADD_EVENT_RESPONSE =
		"org.zkforge.timeline.onAddEventResponse";
	private static final String ATTR_ON_REMOVE_EVENT_RESPONSE =
		"org.zkforge.timeline.onRemoveEventResponse";
	private static final String ATTR_ON_MODIFY_EVENT_RESPONSE =
		"org.zkforge.timeline.onModifyEventResponse";	
	
	static {		
		addClientEvent(Bandinfo.class, "onOccurEventSelect", CE_IMPORTANT);
		addClientEvent(Bandinfo.class, "onBandScroll", CE_IMPORTANT);
	}	
	
	/**
	 * @return the width
	 */
	public String getWidth() {
		return _width;
	}

	/**
	 * @param width
	 *            the width to set
	 */
	public void setWidth(String width) {
		if (!Objects.equals(_width, width)) {
			_width = width;
			smartUpdate("width", width);
		}
	}
	/**
	 * @return the intervalUnit
	 */
	public String getIntervalUnit() {
		return _intervalUnit;
	}
	/**
	 * @param intervalUnit
	 *            the intervalUnit to set
	 */
	public void setIntervalUnit(String intervalUnit) {
		if (!Objects.equals(_intervalUnit, intervalUnit)) {
			_intervalUnit = intervalUnit;
			smartUpdate("intervalUnit", intervalUnit);
		}
	}
	/**
	 * @return the intervalPixels
	 */
	public int getIntervalPixels() {		
		return _intervalPixels;
	}
	/**
	 * @param intervalPixels
	 *            the intervalPixels to set
	 */
	public void setIntervalPixels(int intervalPixels) {
		if (_intervalPixels != intervalPixels) {
			_intervalPixels = intervalPixels;
			smartUpdate("intervalPixels", intervalPixels);
		}
	}
	/**
	 * @param syncWith
	 *            the _syncWith to set
	 */
	public void setSyncWith(String syncWith) {
		if (!Objects.equals(_syncWith, syncWith)) {
			_syncWith = syncWith;
			smartUpdate("syncWith", syncWith);
		}
	}
	/**
	 * @return the _syncWith
	 */
	public String getSyncWith() {
		return _syncWith;
	}
	/**
	 * set URL of the XML 
	 * @param eventSourceUrl
	 */
	public void setEventSourceUrl(String eventSourceUrl) {
		if (!Objects.equals(_eventSourceUrl, eventSourceUrl)) {
			_eventSourceUrl = eventSourceUrl;
			smartUpdate("eventSourceUrl", new EncodedURL(eventSourceUrl));
		}
	}

	/**
	 * URL of the XML
	 * @return String
	 */
	public String getEventSourceUrl() {
		return _eventSourceUrl;
	}
	/**
	 * set layout of the bandinfo to overview
	 * @param overview
	 */
	public void setOverview(boolean overview) {
		if ( _overview != overview) {
			_overview = overview;
			smartUpdate("overview", overview);
		}
	}

	/**
	 * layout of the bandinfo is overview
	 * @return boolean
	 */
	public boolean getOverview() {		
		return _overview;
	}
	
	/**
	 * set timeZone to bandinfo
	 * @param timeZone
	 */
	public void setTimeZone(TimeZone timeZone) {
		if (!Objects.equals(_timeZone, timeZone)) {
			_timeZone = timeZone;
			smartUpdate("timeZone", timeZone.getRawOffset() / (1000 * 60 * 60));
		}
	}

	/**
	 * get timeZone of the bandinfo
	 * @return TimeZone
	 */
	public TimeZone getTimeZone() {
		return _timeZone;
	}
	/**
	 * set date of the bandinfo
	 * @param date
	 */
	public void setDate(Date date) {
		if (!_date.equals(date)) {
			_date = date;
			smartUpdate("date", TimelineUtil.formatDateTime(date));
		}
	}

	/**
	 * get date of the bandinfo
	 * @return Date
	 */
	public Date getDate() {
		return _date;
	}	
	/**
	 * set Visible of Bubble massage 
	 * @param bubbleVisible
	 */
	public void setBubbleVisible(boolean bubbleVisible) {
		if (_bubbleVisible != bubbleVisible) {
			_bubbleVisible = bubbleVisible;
			smartUpdate("bubbleVisible", _bubbleVisible);
		}
	}

	/**
	 * Visible of Bubble massage
	 * @return boolean
	 */
	public boolean isBubbleVisible() {
		return _bubbleVisible;
	}
	
	/**
	 * scroll to target date position
	 * @param date
	 */
	public void scrollToCenter(Date date) {
		if (date == null) return;
		String uuid = getUuid();		
		response("scrollToCenter", new AuScript(this,
				"zk.Widget.$(\""+ uuid +"\").scrollToCenter(\"" + uuid + "\"" + ",\""
						+ date.toString() + "\")"));
	}

	/**
	 * add a HighlightDecorator to bandinfo
	 * @param HighlightDecorator
	 */
	public void addHighlightDecorator(HighlightDecorator hd) {
		if (hd == null) return;
		String uuid = getUuid();		
		response("addHighlightDecorator" + hd.getId(), new AuScript(this,
				"zk.Widget.$(\""+uuid+"\").addHighlightDecorator(\"" + uuid + "\"" + ","
						+ hd.toString() + ")"));
	}

	/**
	 * remove a HighlightDecorator from bandinfo
	 * @param HighlightDecorator
	 */
	public void removeHighlightDecorator(HighlightDecorator hd) {
		// decorators.remove(hd);
		if (hd == null) return;
		String uuid = getUuid();
		response("removeHighlightDecorator" + hd.getId(), new AuScript(this,
				"zk.Widget.$(\""+ uuid +"\").removeHighlightDecorator(\"" + uuid + "\""
						+ "," + hd.getId() + ")"));

	}

	/**
	 * set Loading Message showing
	 * @param boolean
	 */
	public void showLoadingMessage(boolean show) {
		String uuid = getUuid();
		String parentId = getParent().getUuid();
		if (show) {
			response("showLoadingMessage", new AuScript(this,
					"zk.Widget.$(\""+ uuid+"\").showLoadingMessage(\"" + parentId
							+ "\"" + ")"));
		} else {
			response("hideLoadingMessage", new AuScript(this,
					"zk.Widget.$(\""+ uuid +"\").hideLoadingMessage(\"" + parentId
							+ "\"" + ")"));
		}

	}
	
	/**
	 * add an OccurEvent to bandinfo
	 * @param OccurEvent
	 */
	public void addOccurEvent(OccurEvent event) {		
		if (event == null) return;
		if (_addEvtList == null)
			_addEvtList = new LinkedList();
		_addEvtList.add(event);	
		
		if (getAttribute(ATTR_ON_ADD_EVENT_RESPONSE) == null) {
			setAttribute(ATTR_ON_ADD_EVENT_RESPONSE, Boolean.TRUE);
			Events.postEvent(-20000, "onAddEventResponse", this, null);
		}
	}

	/**
	 * remove an OccurEvent from bandinfo
	 * @param OccurEvent
	 */
	public void removeOccurEvent(OccurEvent event) {
		if (event == null) return;
		
		if (_rmEvtList == null)
			_rmEvtList = new LinkedList();
		_rmEvtList.add(event);			
		
		if (getAttribute(ATTR_ON_REMOVE_EVENT_RESPONSE) == null) {
			setAttribute(ATTR_ON_REMOVE_EVENT_RESPONSE, Boolean.TRUE);
			Events.postEvent(-20000, "onRemoveEventResponse", this, null);
		}
	}

	/**
	 * modify an OccurEvent in bandinfo
	 * @param event
	 */
	public void modifyOccurEvent(OccurEvent event) {
		if (event == null) return;
		
		if (_mdyEvtList == null)
			_mdyEvtList = new LinkedList();
		_mdyEvtList.add(event);
		if (getAttribute(ATTR_ON_MODIFY_EVENT_RESPONSE) == null) {
			setAttribute(ATTR_ON_MODIFY_EVENT_RESPONSE, Boolean.TRUE);
			Events.postEvent(-20000, "onModifyEventResponse", this, null);
		}
	}
	
	/**
	 * add a group OccurEvent to bandinfo
	 * @param iter
	 */
	public void addManyOccurEvents(Iterator iter) {
		if (iter == null || !iter.hasNext()) return;		
		if (_addEvtList == null)
			_addEvtList = new LinkedList();
		while (iter.hasNext()) {	
			_addEvtList.add(iter.next());
		}					
		if (getAttribute(ATTR_ON_ADD_EVENT_RESPONSE) == null) {
			setAttribute(ATTR_ON_ADD_EVENT_RESPONSE, Boolean.TRUE);			
			Events.postEvent(-20000, "onAddEventResponse", this, null);
		}
	}
	
	private String getJSONResponse(List list) {
		final StringBuffer sb =	new StringBuffer().append('[');
		for (Iterator it = list.iterator(); it.hasNext();) {
			sb.append(it.next()).append(',');
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append(']');
		list.clear();
		return sb.toString();
	}
	
	public void onAddEventResponse() {
		removeAttribute(ATTR_ON_ADD_EVENT_RESPONSE);		
		response("addOccurEvent" + getUuid(), new AuSetAttribute(this,"addOccurEvent",getJSONResponse(_addEvtList)));		
	}
	
	public void onRemoveEventResponse() {
		removeAttribute(ATTR_ON_REMOVE_EVENT_RESPONSE);
		response("removeOccurEvent" + getUuid(), new AuSetAttribute(this,"removeOccurEvent",getJSONResponse(_rmEvtList)));		
	}
	
	public void onModifyEventResponse() {
		removeAttribute(ATTR_ON_MODIFY_EVENT_RESPONSE);
		response("modifyOccurEvent" + getUuid(), new AuSetAttribute(this,"modifyOccurEvent",getJSONResponse(_mdyEvtList)));
	}
	
	/** Initializes _dataListener and register the listener to the model
	 */
	private void initDataListener() {
		if (_dataListener == null)
			_dataListener = new ListDataListener() {
				public void onChange(ListDataEvent event) {
					onListDataChange(event);
				}
			};

		_model.addListDataListener(_dataListener);
	}
	
	/**
	 * @return the model
	 */
	public ListModel getModel() {
		return _model;
	}
	/**
	 * @param model
	 *            the model to set
	 */
	public void setModel(ListModel model) {
		if (model == null || Objects.equals(_model, model)) return;
		
		if (_model != null){
			_model.removeListDataListener(_dataListener);				
			for (int i = 0, j = _eventList.size(); i < j; i++) {
				OccurEvent oe = (OccurEvent)_eventList.get(0);
				_events.remove(oe);
				_eventList.remove(oe);
				this.removeOccurEvent(oe);
			}
		}		
		
		_model = model;
		
		if (_model != null) {
			
			initDataListener();
			
			for (int i = 0; i < _model.getSize(); i++) {
				Object o = _model.getElementAt(i);
				_events.add(o);
				_eventList.add(o);
			}		
		}
		// listening band scroll event
		if(_bandScrollListener == null)
			_bandScrollListener = new BandScrollListener();
		
		addEventListener("onBandScroll", _bandScrollListener);
		
		if(_max != null)
			syncModel(_min,_max);
	}

	private class BandScrollListener implements EventListener {

		public boolean isAsap() {
			return true;
		}

		public void onEvent(Event event) {			
			BandScrollEvent e = (BandScrollEvent) event;
			Date newmin = e.getMin();
			Date newmax = e.getMax();
			if (_min == null && _max == null) {
				_min = newmin;
				_max = newmax;
				syncModel(_min, _max);
			}

			if (newmin.compareTo(_min) < 0) {
				syncModel(newmin, _min);
				_min = newmin;
			}

			if (newmax.compareTo(_max) > 0) {
				syncModel(_max, newmax);
				_max = newmax;
			}

		}
	};

	protected void syncModel(Date min, Date max) {
		OccurEvent e1 = new OccurEvent();
		e1.setStart(min);
		OccurEvent e2 = new OccurEvent();
		e2.setStart(max);
		SortedSet ss = _events.subSet(e1, e2);
		Iterator iter = ss.iterator();		
		addManyOccurEvents(iter);
	}

	protected void onListDataChange(ListDataEvent event) {
		int lower = event.getIndex0();
		int upper = event.getIndex1();
		OccurEvent e1 = new OccurEvent();
		e1.setStart(_min);
		OccurEvent e2 = new OccurEvent();
		e2.setStart(_max);
		switch (event.getType()) {
		case ListDataEvent.INTERVAL_ADDED:
			for (int i = lower; i <= upper; i++) {
				OccurEvent oe = (OccurEvent) _model.getElementAt(i);
				_events.add(oe);
				_eventList.add(oe);
				if (oe.compareTo(e1) >= 0 && oe.compareTo(e2) <= 0)// lazy-load
					this.addOccurEvent(oe);// if the event is in [_min,_max]
				// then display it.
			}
			break;
		case ListDataEvent.INTERVAL_REMOVED:
			int count = upper - lower + 1,nowCount = 0;
			for (ListIterator it = _eventList.listIterator(upper+1); it.hasPrevious();) {
				OccurEvent oe = (OccurEvent) it.previous();				
				if(count == nowCount)break;
				it.remove();
				_events.remove(oe);
				_eventList.remove(oe);
				this.removeOccurEvent(oe);
				oe = null;
				nowCount++;
			}
			break;
		case ListDataEvent.CONTENTS_CHANGED:
			ListIterator it = _eventList.listIterator(lower + 1);
			for (int i = lower; i <= upper ; it.hasNext(),i++) {
				OccurEvent oe = (OccurEvent) _model.getElementAt(i);
				OccurEvent e = (OccurEvent) it.next();
				_eventList.set(i, oe);
				_events.remove(e);
				_events.add(oe);
				this.removeOccurEvent(e);
				if (oe.compareTo(e1) >= 0 && oe.compareTo(e2) <= 0)
					// if the event is in [_min,_max]
					this.addOccurEvent(oe);// then display it.
			}
			break;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.zkoss.zk.ui.AbstractComponent#setParent(org.zkoss.zk.ui.Component)
	 */
	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Timeline))
			throw new UiException("Unsupported parent for bandinfo: " + parent);
		super.setParent(parent);
	}
	/*
	 * (non-Javadoc)
	 * @see org.zkoss.zk.ui.AbstractComponent#insertBefore(org.zkoss.zk.ui.Component, org.zkoss.zk.ui.Component)
	 */
	public boolean insertBefore(Component child, Component insertBefore) {
		if (!(child instanceof Hotzone))
			throw new UiException("Unsupported child for bandinfo: " + child);
		return super.insertBefore(child, insertBefore);
	}	
	
	private String getString(Object obj){
		if(obj != null)return obj.toString();
		return "";
	}
	/*
	 * (non-Javadoc)
	 * @see org.zkoss.zk.ui.HtmlBasedComponent#service(org.zkoss.zk.au.AuRequest, boolean)
	 */
	public void service(AuRequest request, boolean everError) {		

		SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
		final JSONArray data = (JSONArray) request.getData().get("data");
		
		final Component comp = request.getComponent();
		if (comp == null)
			throw new UiException(MZk.ILLEGAL_REQUEST_COMPONENT_REQUIRED, this);
		if (!(comp instanceof Bandinfo))
			throw new UiException(MZk.ILLEGAL_REQUEST_COMPONENT_REQUIRED, this);		
		
		final String cmd = request.getCommand();
		
		if (cmd.equals("onOccurEventSelect")) {

			OccurEvent evt = new OccurEvent();
			evt.setId(getString(data.get(0)));
			try {
				evt.setStart(sdf.parse(getString(data.get(1))));
				evt.setEnd(sdf.parse(getString(data.get(2))));
				evt.setLatestStart(sdf.parse(getString(data.get(3))));
				evt.setEarliestEnd(sdf.parse(getString(data.get(4))));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			evt.setText(getString(data.get(5)));
			evt.setDescription(getString(data.get(6)));
			evt.setImageUrl(getString(data.get(7)));
			evt.setLinkUrl(getString(data.get(8)));
			evt.setIconUrl(getString(data.get(9)));
			evt.setColor(getString(data.get(10)));
			evt.setTextColor(getString(data.get(11)));
			evt.setWikiUrl(getString(data.get(12)));
			evt.setWikiSection(getString(data.get(13)));

			Events.postEvent(new OccurEventSelectEvent("onOccurEventSelect", comp, evt));		
			
		} else if (cmd.equals("onBandScroll")) {
			if (data == null || data.size() < 2)
				throw new UiException(MZk.ILLEGAL_REQUEST_WRONG_DATA,
						new Object[] { Objects.toString(data), this });

			Date min = null;
			try {
				min = sdf.parse(getString(data.get(0)));
			} catch (ParseException e) {
			}
			Date max = null;
			try {
				max = sdf.parse(getString(data.get(1)));
			} catch (ParseException e) {
			}
			Date center = null;
			try {
				center = sdf.parse(getString(data.get(2)));
			} catch (ParseException e) {
			}
			Events.postEvent(new BandScrollEvent("onBandScroll", comp, min, max,center));

		} else{
			super.service(request, everError);
		}
	}
	/*
	 * (non-Javadoc)
	 * @see org.zkoss.zul.impl.XulElement#renderProperties(org.zkoss.zk.ui.sys.ContentRenderer)
	 */
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
			throws IOException {
		super.renderProperties(renderer);
		if (!"70%".equals(_width))
			render(renderer, "width", _width);
		if (!"month".equals(_intervalUnit))
			render(renderer, "intervalUnit", _intervalUnit);
		if (_intervalPixels != 100)
			renderer.render("intervalPixels", _intervalPixels);
		if(_overview)
			render(renderer, "overview", _overview);
		if (!Objects.equals(_timeZone, TimeZone.getDefault()))
			renderer.render("timeZone", _timeZone.getRawOffset() / (1000 * 60 * 60));
		render(renderer, "date", TimelineUtil.formatDateTime(_date));
		render(renderer, "syncWith", _syncWith);
		render(renderer, "eventSourceUrl", getEncodedURL(_eventSourceUrl));
		if(!_bubbleVisible)
			renderer.render("bubbleVisible", _bubbleVisible);
	}
	private String getEncodedURL(String url) {
		  final Desktop dt = getDesktop(); //it might not belong to any desktop
		  return dt != null ? dt.getExecution().encodeURL(url): "";			 
	}
	private class EncodedURL implements org.zkoss.zk.ui.util.DeferredValue {
		private String url;
		
		public EncodedURL(String url) {
			super();
			this.url = url;
		}

		public Object getValue() {
			return getEncodedURL(this.url);
		}
	}
}

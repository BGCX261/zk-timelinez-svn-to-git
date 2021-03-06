package org.zkforge.timeline.data;

import java.util.Date;

import org.zkforge.json.simple.JSONObject;
import org.zkforge.timeline.util.TimelineUtil;

public class OccurEvent implements Comparable {
	private static int count = 0;

	// @Override
	public String toString() {
		// TODO Auto-generated method stub
		JSONObject json = new JSONObject();
		// json.put("id", new Integer(_id));
		json.put("id", _id);
		if (_start != null)
			json.put("start", TimelineUtil.formatDateTime(_start));
		if (_end != null)
			json.put("end", TimelineUtil.formatDateTime(_end));
		if (_latestStart != null)
			json.put("latestStart", TimelineUtil.formatDateTime(_latestStart));
		if (_earliestEnd != null)
			json.put("earliestEnd", TimelineUtil.formatDateTime(_earliestEnd));
		json.put("duration", Boolean.valueOf(_duration));
		if (_text != null)
			json.put("text", _text);
		if (_description != null)
			json.put("description", _description);
		if (_imageUrl != null)
			json.put("image", _imageUrl);
		if (_linkUrl != null)
			json.put("link", _linkUrl);
		if (_iconUrl != null)
			json.put("icon", _iconUrl);
		if (_wikiUrl != null)
			json.put("wikiUrl", _wikiUrl);
		if (_wikiSection != null)
			json.put("wikiSection", _wikiSection);

		if (_color != null)
			json.put("color", _color);
		if (_textColor != null)
			json.put("textColor", _textColor);
		return json.toString();
	}

	private Date _start = new Date();

	private Date _end = null;

	private Date _latestStart = null;

	private Date _earliestEnd = null;

	private String _text = "";

	private String _description = "";

	private String _imageUrl = "";

	private String _linkUrl = "";

	private String _iconUrl = "";

	private String _wikiUrl = null;

	private String _wikiSection = null;

	private boolean _duration = true;

	private String _color = null;

	private String _textColor = null;

	private String _id = String.valueOf(count++);

	public void setId(String id) {
		_id = id;
	}

	public String getColor() {
		return _color;
	}

	public void setColor(String color) {
		this._color = color;
	}

	public String getDescription() {
		return _description;
	}

	public void setDescription(String description) {
		this._description = description;
	}

	public boolean isDuration() {
		return _duration;
	}

	public void setDuration(boolean duration) {
		this._duration = duration;
	}

	public Date getEarliestEnd() {
		return _earliestEnd;
	}

	public void setEarliestEnd(Date earliestEnd) {
		this._earliestEnd = earliestEnd;
	}

	public Date getEnd() {
		return _end;
	}

	public void setEnd(Date end) {
		this._end = end;
	}

	public String getImageUrl() {
		return _imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this._imageUrl = imageUrl;
	}

	public Date getLatestStart() {
		return _latestStart;
	}

	public void setLatestStart(Date latestStart) {
		this._latestStart = latestStart;
	}

	public String getLinkUrl() {
		return _linkUrl;
	}

	public void setLinkUrl(String linkUrl) {
		this._linkUrl = linkUrl;
	}

	public Date getStart() {
		return _start;
	}

	public void setStart(Date start) {
		this._start = start;
	}

	public String getText() {
		return _text;
	}

	public void setText(String text) {
		this._text = text;
	}

	public String getTextColor() {
		return _textColor;
	}

	public void setTextColor(String textColor) {
		this._textColor = textColor;
	}

	public String getWikiUrl() {
		return _wikiUrl;
	}

	public void setWikiUrl(String wikiUrl) {
		this._wikiUrl = wikiUrl;
	}

	public String getIconUrl() {
		return _iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this._iconUrl = iconUrl;
	}

	public String getId() {
		return String.valueOf(_id);
	}

	public int compareTo(Object target) {
		// TODO Auto-generated method stub
		// System.out.println(this);
		// System.out.println(target);
		int ret;
		if (target == null)
			ret = 1;
		else {
			OccurEvent evt = (OccurEvent) target;
			int sc;// result of start date to compare
			int ec;// result of end date to compare
			if (getStart() == null && evt.getStart() != null)
				sc = -1;
			else if (getStart() != null && evt.getStart() == null)
				sc = 1;
			else if (getStart() == null && evt.getStart() == null)
				sc = 0;
			else
				sc = getStart().compareTo(evt.getStart());

			if (getEnd() == null && evt.getEnd() != null)
				ec = -1;
			else if (getEnd() == null && evt.getEnd() == null)
				ec = 0;
			else if (getEnd() != null && evt.getEnd() == null)
				ec = 1;
			else
				ec = getEnd().compareTo(evt.getEnd());

			if ((sc < 0) || (sc == 0 && ec < 0))
				ret = -1;
			else if ((sc > 0) || (sc == 0 && ec > 0))
				ret = 1;
			else
				ret = 0;
		}
		// System.out.println(ret);
		return ret;
	}

	/**
	 * @return the _wikiSection
	 */
	public String getWikiSection() {
		return _wikiSection;
	}

	/**
	 * @param wikiSection
	 *            the _wikiSection to set
	 */
	public void setWikiSection(String wikiSection) {
		_wikiSection = wikiSection;
	}
}

/* Timeline.js
 Purpose:
 
 Description:
 
 History:
 Fri Oct  23 16:00:44 TST 2009, Created by Jimmy
 Copyright (C) 2009 Potix Corporation. All Rights Reserved.
 This program is distributed under GPL Version 3.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 */
timelinez.Timeline = zk.$extends(zul.Widget, {
    _orient: Timeline.HORIZONTAL,
    _height: "150px",
    _width: "100%",
    _defaultDS: "defaultDS",
	
    $define: {
        orient: function() {
            if (this._orient == "vertical") {
                this._orient = Timeline.VERTICAL;
            }else {
                this._orient = Timeline.HORIZONTAL;
            }
            this.refresh(true);
        },
        height: function() {
            var node = this.$n();
            if (!node)return;
            jq(node).height(this._height);
            var timeline = this.tl;
            if (timeline) 
                timeline.layout();
        },
        width: function() {
            var node = this.$n();
            if (!node)return;
            jq(node).width(this._width);
            var timeline = this.tl;
            if (timeline) 
                timeline.layout();
        }
    },
    
    bind_: function() {
        this.$supers('bind_', arguments);
		zWatch.listen({onShow: this});
        this._bandInfos = [];
        this._eventSources = {};
		if (zk(this.$n()).isRealVisible())
        	this._init(true);
    },
    unbind_: function() {
		this.tl.dispose();
        this.tl = this._bandInfos = this._eventSources = null;
		zWatch.unlisten({onShow : this});
        this.$supers('unbind_', arguments);
    },
	
	onShow: function() {
		this.refresh();
    },
    
    _init: function(load) {
        var list = [], 
			selfIndex = 0;
        
        for (var child = this.firstChild; child; child = child.nextSibling) {
            var childEventSourceUrl = child.getEventSourceUrl(),
				eventSourceUrl = childEventSourceUrl ? childEventSourceUrl: this._defaultDS, 
				syncWith = child.getSyncWith();
            
            this._bandInfos.push(child.getBandInfos());
            
            if (!this._eventSources[eventSourceUrl]) {// if eventSource not the same 
            	this._eventSources[eventSourceUrl] = new Timeline.DefaultEventSource();
            	list.push(child);
            }
            this._bandInfos[selfIndex].eventSource = this._eventSources[eventSourceUrl];
            
            
            if (syncWith) {//add syncWith
                var syncWithNode = this.$f(syncWith);
                if (!syncWithNode)return;
                this._bandInfos[selfIndex].syncWith = syncWithNode.getChildIndex();
                this._bandInfos[selfIndex].highlight = true;
            }
            selfIndex++;
        }
        
        this.tl = Timeline.create(this.$n(), this._bandInfos, this._orient);
        if	(load)
        	this.$class._loadAllXML(this, list);
        
        selfIndex = 0;
        for (var child = this.firstChild; child; child = child.nextSibling) {
            child.setBandInfos(this.tl.getBand(selfIndex));
			if (!child.getBubbleVisible())
				child.showBubbleVisible(false);			
            selfIndex++;
        }
    },
    
    refresh: function(load) {// repaint
    	var node = jq(this.$n());
        node.children().remove();
       	node.removeAttr("class");
        this._bandInfos = [];
        this._eventSources = {};
        
        for (var child = this.firstChild; child; child = child.nextSibling) {
            if (child.firstChild) {
                for (var greatChild = child.firstChild; greatChild; greatChild = greatChild.nextSibling) {
                    greatChild.setNewZone();
                }
            }
            child.setNewBandInfos();
        }        
        this._init(load);
    },
    
    setFilter: function(filterText) {
        var matcher = filterText;
        matcher.replace(/^\s+/, '').replace(/\s+$/, '');
        var regex = new RegExp(matcher, "i");
        var filterMatcher = function(evt) {
            return (regex.test(evt._text) || regex.test(evt._description));
        };
        for (var i = this.tl.getBandCount(); i--;) {
            this.tl.getBand(i).getEventPainter().setFilterMatcher(filterMatcher);
        }
        this.tl.paint();
    },
    
    setHighlight: function(matcherValues) {
        var matchers = eval('(' + matcherValues + ')'), 
        	regexes = [];
        for (var i = matchers.length; i--;) {
            var input = matchers[i];
            var text2 = input.replace(/^\s+/, '').replace(/\s+$/, '');
            if (text2.length) 
				regexes.push(new RegExp(text2, "i"));
            else regexes.push(null);
        }
        
        var highlightMatcher = function(evt) {        
            var text = evt.getText(),
				description = evt.getDescription();
            for (var i = regexes.length; i--;) {
                var regex = regexes[i];
                if (regex && (regex.test(text) || regex.test(description))) {
                    return i;
                }
            }
            return -1;
        };
        for (var i = this.tl.getBandCount(); i--;) {
            this.tl.getBand(i).getEventPainter().setHighlightMatcher(highlightMatcher);
        }
        this.tl.paint();
    }
}, {
    _loadAllXML: function(widget, list) {
        for (var i = list.length; i--;) {
            var band = list[i], 
            	eventSourceUrl = band.getEventSourceUrl(), 
				func = "var callback=function (xml, url){widget._eventSources[eventSourceUrl].loadXML(xml,url);}",
				path = eventSourceUrl;
            
            if(!eventSourceUrl){
            	func = "var callback=function (xml, url){widget._eventSources[widget._defaultDS].loadXML(xml,url);}";
            	path = Timeline.urlPrefix + "defaultDSXML.xml";
            }
            
            eval(func);
            widget.tl.loadXML(path, callback);
        }
    },
	getUnit: function(unit) {
        switch(unit){
			case "millisecond": return SimileAjax.DateTime.MILLISECOND;
			case "second": return SimileAjax.DateTime.SECOND;
			case "minute": return SimileAjax.DateTime.MINUTE;
			case "hour": return SimileAjax.DateTime.HOUR;
			case "day": return SimileAjax.DateTime.DAY;
			case "week": return SimileAjax.DateTime.WEEK;
			case "month": return SimileAjax.DateTime.MONTH;
			case "year": return SimileAjax.DateTime.YEAR;
			case "decade": return SimileAjax.DateTime.DECADE;
			case "century": return SimileAjax.DateTime.CENTURY;
			default: return SimileAjax.DateTime.MILLENNIUM;
		}
    }
});

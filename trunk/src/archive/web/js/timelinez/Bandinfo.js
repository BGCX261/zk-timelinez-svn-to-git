/* Bandinfo.js
 Purpose:
 
 Description:
 
 History:
 Fri Oct  23 16:00:44 TST 2009, Created by Jimmy
 Copyright (C) 2009 Potix Corporation. All Rights Reserved.
 This program is distributed under GPL Version 3.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 */
(function () {
	function _addScrollListener(wgt) {
		var doScroll = function(band) {
            band.currentCenterVisiableDate = band.getCenterVisibleDate();
            if (band.currentMinVisiableDate > band.getMinVisibleDate()) {
                var val = [
							band.getMinVisibleDate().toGMTString(),//min
 							band.currentMaxVisiableDate.toGMTString(),//max
 							band.currentCenterVisiableDate.toGMTString()
					];
                band.currentMinVisiableDate = band.getMinVisibleDate();
                zk.Widget.$(band.uuid).fire("onBandScroll", {data: val}, {toServer: true});
            } else if (band.currentMaxVisiableDate < band.getMaxVisibleDate()) {
				var val = [
							band.currentMinVisiableDate.toGMTString(),//min
 							band.getMaxVisibleDate().toGMTString(),//max
 							band.currentCenterVisiableDate.toGMTString()
					];
				band.currentMaxVisiableDate = band.getMaxVisibleDate();
				zk.Widget.$(band.uuid).fire("onBandScroll", {data: val}, {toServer: true});
			} else if (band.currentCenterVisiableDate != band.getCenterVisibleDate()) {
				var val = [
							band.currentMinVisiableDate.toGMTString(),//min
 							band.currentMaxVisiableDate.toGMTString(),//max
 							band.currentCenterVisiableDate.toGMTString()
					];
				zk.Widget.$(band.uuid).fire("onBandScroll", {data: val}, {toServer: true});}
		};
        var bandInfos = wgt.getBandInfos();
        bandInfos.addOnScrollListener(doScroll);
        
        if (!bandInfos.currentMinVisiableDate) 
            bandInfos.currentMinVisiableDate = bandInfos.getMinVisibleDate();
        if (!bandInfos.currentMaxVisiableDate) 
            bandInfos.currentMaxVisiableDate = bandInfos.getMaxVisibleDate();
        if (!bandInfos.currentCenterVisiableDate) 
            bandInfos.currentCenterVisiableDate = bandInfos.getCenterVisibleDate();
        var val = [
					bandInfos.currentMinVisiableDate.toGMTString(),//min
 					bandInfos.currentMaxVisiableDate.toGMTString(),//max
 					bandInfos.currentCenterVisiableDate.toGMTString()
			];
        zk.Widget.$(bandInfos.uuid).fire("onBandScroll", {data: val}, {toServer: true});
	}
	
	function _createHotZoneBandInfo(wgt) {
		var zones = [];
        for (var child = wgt.firstChild; child; child = child.nextSibling) {
            zones.push(child.getZone());
        }
        
        return Timeline.createHotZoneBandInfo({//add HotZoneBand    
            zones: 			zones,
            width: 			wgt.getWidth(),
            intervalUnit: 	wgt.getIntervalUnit(),
            intervalPixels: wgt.getIntervalPixels(),
            overview: 		wgt.getOverview(),
            timeZone: 		wgt.getTimeZone(),
            date: 			wgt.getDate()
        });
	}
    
	function _createBandInfo(wgt) {
		return Timeline.createBandInfo({//add band            	
            width: 			wgt.getWidth(),
            intervalUnit: 	wgt.getIntervalUnit(),
            intervalPixels: wgt.getIntervalPixels(),
            overview:		wgt.getOverview(),
            timeZone: 		wgt.getTimeZone(),
            date: 			wgt.getDate()
        });
	}
	

	timelinez.Bandinfo = zk.$extends(zk.Widget, {
	    _width: "70%",
	    _intervalUnit: SimileAjax.DateTime.MONTH,
	    _intervalPixels: 100,
	    _overview: false,
	    _date: new Date(),
	    _timeZone: 0,
	    _bubbleVisible: true,
	    
	    $define: {
	        width: null,
	        intervalUnit: function() {
				this._intervalUnit = timelinez.Timeline.getUnit(this._intervalUnit);
	        },
	        intervalPixels: null,
	        syncWith: null,
	        eventSourceUrl: function() {
				if (!this._bandinfo) return;
				
		        var eventSource = this._bandinfo._eventSource;
		        eventSource.clear();
		        this._bandinfo._timeline.loadXML(this._eventSourceUrl, function(xml, url) {eventSource.loadXML(xml, url);});
	            
	        },
	        overview: function() {
				if (!this._bandinfo) return;
	            this.parent.refresh(true);
	        },
	        date: null,
	        timeZone: null,
	        bubbleVisible: function() {
				if (!this._bandinfo) return;
	            this.showBubbleVisible(this._bubbleVisible);
	        }
	    },
	    
		showBubbleVisible: function(v) {		
	        if (!v) {
	            this._bandinfo._eventPainter._showBubble = function(x, y, evt) {//override showBubble
	                var val = [
						evt._id, 
						evt._start.toGMTString(), 
						evt._end.toGMTString(), 
						evt._latestStart.toGMTString(), 
						evt._earliestEnd.toGMTString(), 
						evt._text, 
						evt._description, 
						evt._image, 
						evt._link, 
						evt._icon, 
						evt._color, 
						evt._textColor, 
						evt._wikiURL, 
						evt._wikiSection
					];
	                zk.Widget.$(this._band.uuid).fire("onOccurEventSelect", {data: val}, {toServer: true});
	            };
	        } else if (this._oriShowBubble) {
				this._bandinfo._eventPainter._showBubble = this._oriShowBubble;//set to default function 							
			}
	    },
		
	    setNewBandInfos: function() {
	        if (this.firstChild) {
	            this._bandinfo = _createHotZoneBandInfo(this);
	        } else {
	            this._bandinfo = _createBandInfo(this);
	        }
	    },
	    
	    setBandInfos: function(bandInfos) {//reset after attach to timeline 
	        this._bandinfo = bandInfos;
	        this._bandinfo.uuid = this.uuid;
	        _addScrollListener(this);
	        
	        if (this._decorators) {//repaint decorators after reset
	            this._bandinfo._decorators = this._decorators;
	            for (var i = this._decorators.length; i--;) {
	                this._decorators[i].initialize(this._bandinfo, this._bandinfo._timeline);
	                this._decorators[i].paint();
	            }
	        }        
	    },
	    
	    getBandInfos: function() {
	        return this._bandinfo;
	    },
	    
	    setAddOccurEvent: function(params) {
	        params = jq.evalJSON(params);
	        this.addManyOccurEvent(params);
	    },
	    
	    setRemoveOccurEvent: function(params) {
	        if (!this._bandinfo._eventSource._events || !this._dynaEvents)return;
	        params = jq.evalJSON(params);
	        for (var i = 0, j = params.length; i < j; i++) {
	            var e = this._dynaEvents["dynaEvent" + params[i].id];
	            this._bandinfo._eventSource._events._events.remove(e);
	        }        
	        this._bandinfo._timeline.paint();
	    },
	    
	    setModifyOccurEvent: function(params) {
	        params = jq.evalJSON(params);
	        for (var i = 0, j = params.length; i < j; i++) {
	            if (!this._dynaEvents["dynaEvent" + params[i].id])continue;
	            var oe = [];
	            oe[0] = this._dynaEvents["dynaEvent" + params[i].id];
	            this.setRemoveOccurEvent(oe);
	            this.setAddOccurEvent(oe);
	        }
	    },
	    
	    addManyOccurEvent: function(data) {
	        if (!data.length) return;
	        
	        var timeline = this._bandinfo._timeline, 
				eventSource = this._bandinfo._eventSource, 
				eventPainter = this._bandinfo._eventPainter;
	        
	        timeline.showLoadingMessage();
	        var events = [];
	        if (!this._dynaEvents) 
	            this._dynaEvents = {};
	        for (var i = 0, j = data.length; i < j; i++) {
	            var evt = this.newEvent(this._bandinfo, data[i]);
	            this._dynaEvents[evt._id] = evt;
	            var iter = eventSource.getAllEventIterator();
	            var found = false;
	            while (iter.hasNext()) {
	                var e = iter.next();
	                if (e._id == evt._id) {
	                    found = true;
	                    break;
	                }
	            }
	            if (!found) 
	                events[events.length] = evt;
	        }
	        
	        eventSource.addMany(events);
	        if (!eventPainter._tracks) 
	            eventPainter._tracks = [];//for IE
	        //		 eventPainter.getLayout()._layout();//not find the function yet
	        timeline.hideLoadingMessage();
	    },
	    
	    newEvent: function(cmp, params) {
	        var evt = new Timeline.DefaultEventSource.Event({
	            id: 			"dynaEvent" + params.id,
	            start: 			this.parseDateTime(params.start),
	            end: 			this.parseDateTime(params.end),
	            latestStart: 	this.parseDateTime(params.latestStart),
	            earliestEnd: 	this.parseDateTime(params.earliestEnd),
	            instant: 		!params.duration, // instant
	            text: 			params.text, // text
	            description:	params.description,
	            image: 			cmp.getEventSource()._resolveRelativeURL(params.image, ""),
	            link: 			cmp.getEventSource()._resolveRelativeURL(params.link, ""),
	            icon: 			cmp.getEventSource()._resolveRelativeURL(params.icon, ""),
	            color: 			params.color,
	            textColor: 		params.textColor
	            //hoverText: 	bindings["hoverText"], new version only, old one unimplement
	            //caption: 		bindings["caption"],
	            //classname:	bindings["classname"],
	            //tapeImage: 	bindings["tapeImage"],
	            //tapeRepeat: 	bindings["tapeRepeat"],
	            //eventID: 		bindings["eventID"],
	            //trackNum:		bindings["trackNum"]
	        });
	        
	        params.title = params.text; //it will call getProperty("title") to get text
	        evt._params = params;
	        
	        evt.getProperty = function(name) {
	            return this._params[name];
	        };
	        if (params.wikiUrl) 
	            evt.setWikiInfo(params.wikiUrl, params.wikiSection);
	        return evt;
	    },
	    
	    parseDateTime: function(dateString) {
	        if (!dateString) return null;
	        try {
	            return new Date(Date.parse(dateString));
	        } catch (e) {
	            return null;
	        }
	    },
	    
	    scrollToCenter: function(uuid, dateString) {
	        var currentDate = new Date(Date.parse(dateString));
	        this._bandinfo.scrollToCenter(currentDate);
	    },
	    
	    showLoadingMessage: function(uuid) {
	        this._bandinfo._timeline.showLoadingMessage();
	    },
	    
	    hideLoadingMessage: function(uuid) {
	        this._bandinfo._timeline.hideLoadingMessage();
	    },
	    
	    addHighlightDecorator: function(uuid, params) {
	        var bandinfo = this._bandinfo;
	        var timeline = this._bandinfo._timeline;
	        
	        if (this._decorators == null) {
	            this._decorators = [];
	            bandinfo._decorators = this._decorators;
	        }
	        if (params.HighlightDecoratorName == "SpanHighlightDecorator") {
	            var shd = new Timeline.SpanHighlightDecorator({
	                startDate:	params.startDate,
	                endDate:	params.endDate,
	                color:		params.color,
	                opacity: 	parseInt(params.opacity),
	                startLabel: params.startLabel,
	                endLabel: 	params.endLabel,
	                theme: 		Timeline.getDefaultTheme()
	            });
	            
	            this._decorators[this._decorators.length] = shd;
	            shd.initialize(bandinfo, timeline);
	            shd._id = params.id;
	            shd.paint();
	            
	        } else {
	            var phd = new Timeline.PointHighlightDecorator({
	                date: 		params.date,
	                color: 		params.color,
	                opacity: 	parseInt(params.opacity),
	                theme: 		Timeline.getDefaultTheme()
	            });
	            this._decorators[this._decorators.length] = phd;
	            phd.initialize(bandinfo, timeline);
	            phd._id = params.id;
	            phd.paint();
	        }//end if
	    },
	    
	    removeHighlightDecorator: function(uuid, decoratorId) {
	        var bandinfo = this._bandinfo;
	        var timeline = this._bandinfo._timeline;
	        if (!this._decorators)return;
	        for (var i = this._decorators.length; i--;) {
	            var d = this._decorators[i];
	            if (d._id == decoratorId) {
	                this._decorators.$remove(d);
	                bandinfo.removeLayerDiv(d._layerDiv);
	            }
	        }
	        bandinfo.paint();
	    },
	    
	    bind_: function() {
	        this.$supers('bind_', arguments);
	        this._oriShowBubble = Timeline.OriginalEventPainter.prototype._showBubble;
	        if (this.firstChild) {
	            this._bandinfo = _createHotZoneBandInfo(this);
	        }else {
	            this._bandinfo = _createBandInfo(this);
	        }
	    },
	    unbind_: function() {
	        this._oriShowBubble = this._bandinfo = this._dynaEvents = this._decorators = null;
	        this.$supers('unbind_', arguments);
	    }
	});
})();
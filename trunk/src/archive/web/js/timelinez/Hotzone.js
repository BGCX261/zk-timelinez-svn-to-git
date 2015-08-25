/* Hotzone.js
 Purpose:
 
 Description:
 
 History:
 	Fri Oct  23 16:00:44 TST 2009, Created by Jimmy
 Copyright (C) 2009 Potix Corporation. All Rights Reserved.
 This program is distributed under GPL Version 3.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 */
(function () {
	function _createZone(wgt) {
		return {
            start: wgt._start,
            end: wgt._end,
            magnify: wgt._magnify,
            unit: wgt._unit,
            multiple: wgt._multiple
        };
	}
	
	
	timelinez.Hotzone = zk.$extends(zk.Widget, {
	    _start: new Date(),
	    _end: new Date(),
	    _magnify: 7,// default value
	    _unit: SimileAjax.DateTime.WEEK,// default value
	    _multiple: 1,// default value    
	    
	    $define: {
	        start: null,
	        end: null,
	        magnify: null,
	        unit: function(){
				this._unit = timelinez.Timeline.getUnit(this._unit);
			 },
	        multiple: null
	    },
	    
		setNewZone: function(){		
			 this._zone = _createZone(this);
		},
		
	    getZone: function(){		
			return this._zone;
		},
		
	    bind_: function() {
	        this.$supers('bind_', arguments);
	        this._zone = _createZone(this);
	    },
	    unbind_: function() {        
			this._zone = null;
			this.$supers('unbind_', arguments);
	    }
	});
})();
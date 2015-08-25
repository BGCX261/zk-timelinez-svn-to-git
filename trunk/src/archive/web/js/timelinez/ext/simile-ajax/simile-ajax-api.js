/*==================================================
 *  Simile Ajax API
 *
 *  Include this file in your HTML file as follows:
 *
 *    <script src="http://simile.mit.edu/ajax/api/simile-ajax-api.js" type="text/javascript"></script>
 *
 *==================================================
 */
var packagePath = zk.ajaxURI('web/js/timelinez/ext/simile-ajax/', {desktop: this.desktop,au: true});
packagePath = packagePath.substr(0, packagePath.lastIndexOf("/") + 1);
if (typeof SimileAjax == "undefined") {
    window.SimileAjax = {
        loaded:                 true,
        loadingScriptsCount:    0,
        error:                  null,
        params:                 { bundle:"true" }
    };
    
    SimileAjax.Platform = {};
    SimileAjax.urlPrefix = packagePath;
	
	var getHead = function(doc) {
        return doc.getElementsByTagName("head")[0];
    };
	
	SimileAjax.includeJavascriptFile = function(doc, url, onerror, charset) {
        onerror = onerror || "";
        if (doc.body == null) {
            try {
                var q = "'" + onerror.replace( /'/g, '&apos' ) + "'"; // "
                doc.write("<script src='" + url + "' onerror="+ q +
                          (charset ? " charset='"+ charset +"'" : "") +
                          " type='text/javascript'>"+ onerror + "</script>");
                return;
            } catch (e) {
                // fall through
            }
        }

        var script = doc.createElement("script");
        if (onerror) {
            try { script.innerHTML = onerror; } catch(e) {}
            script.setAttribute("onerror", onerror);
        }
        if (charset) {
            script.setAttribute("charset", charset);
        }
        script.type = "text/javascript";
        script.language = "JavaScript";
        script.src = url;
        return getHead(doc).appendChild(script);
    };
    SimileAjax.includeJavascriptFiles = function(doc, urlPrefix, filenames) {
        for (var i = 0; i < filenames.length; i++) {
            SimileAjax.includeJavascriptFile(doc, urlPrefix + filenames[i]);
        }
        SimileAjax.loadingScriptsCount += filenames.length;
        SimileAjax.includeJavascriptFile(doc, SimileAjax.urlPrefix + "scripts/signal.js?" + filenames.length);
    };
    SimileAjax.includeCssFile = function(doc, url) {
        if (doc.body == null) {
            try {
                doc.write("<link rel='stylesheet' href='" + url + "' type='text/css'/>");
                return;
            } catch (e) {
                // fall through
            }
        }
        
        var link = doc.createElement("link");
        link.setAttribute("rel", "stylesheet");
        link.setAttribute("type", "text/css");
        link.setAttribute("href", url);
        getHead(doc).appendChild(link);
    };
    SimileAjax.includeCssFiles = function(doc, urlPrefix, filenames) {
        for (var i = 0; i < filenames.length; i++) {
            SimileAjax.includeCssFile(doc, urlPrefix + filenames[i]);
        }
    };
	var cssFiles = [
        "graphics.css"
    ];
	SimileAjax.includeCssFiles(document, SimileAjax.urlPrefix + "styles/", cssFiles);
}
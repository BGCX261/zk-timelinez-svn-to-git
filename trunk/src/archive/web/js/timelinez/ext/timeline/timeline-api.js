/*==================================================
 *  Timeline API
 *
 *  This file will load all the Javascript files
 *  necessary to make the standard timeline work.
 *  It also detects the default locale.
 *
 *  To run from the MIT copy of Timeline:
 *  Include this file in your HTML file as follows:
 *
 *    <script src="http://api.simile-widgets.org/timeline/2.3.1/timeline-api.js" 
 *     type="text/javascript"></script>
 *
 *
 * To host the Timeline files on your own server:
 *   1) Install the Timeline and Simile-Ajax files onto your webserver using
 *      timeline_libraries.zip or timeline_source.zip
 * 
 *   2) Set global js variables used to send parameters to this script:
 *        Timeline_ajax_url -- url for simile-ajax-api.js
 *        Timeline_urlPrefix -- url for the *directory* that contains timeline-api.js
 *          Include trailing slash
 *        Timeline_parameters='bundle=true'; // you must set bundle to true if you are using
 *                                           // timeline_libraries.zip since only the
 *                                           // bundled libraries are included
 *      
 * eg your html page would include
 *
 *   <script>
 *     Timeline_ajax_url="http://YOUR_SERVER/javascripts/timeline/timeline_ajax/simile-ajax-api.js";
 *     Timeline_urlPrefix='http://YOUR_SERVER/javascripts/timeline/timeline_js/';       
 *     Timeline_parameters='bundle=true';
 *   </script>
 *   <script src="http://YOUR_SERVER/javascripts/timeline/timeline_js/timeline-api.js"    
 *     type="text/javascript">
 *   </script>
 *
 * SCRIPT PARAMETERS
 * This script auto-magically figures out locale and has defaults for other parameters 
 * To set parameters explicity, set js global variable Timeline_parameters or include as
 * parameters on the url using GET style. Eg the two next lines pass the same parameters:
 *     Timeline_parameters='bundle=true';                    // pass parameter via js variable
 *     <script src="http://....timeline-api.js?bundle=true"  // pass parameter via url
 * 
 * Parameters 
 *   timeline-use-local-resources -- 
 *   bundle -- true: use the single js bundle file; false: load individual files (for debugging)
 *   locales -- 
 *   defaultLocale --
 *   forceLocale -- force locale to be a particular value--used for debugging. Normally locale is determined
 *                  by browser's and server's locale settings.
 *================================================== 
 */
var packagePath = zk.ajaxURI('web/js/timelinez/ext/timeline/', {desktop: this.desktop,au: true});
packagePath = packagePath.substr(0, packagePath.lastIndexOf("/") + 1);

(function() {
       var loadMe = function() {
        if ("Timeline" in window) return;
        
        window.Timeline = {};
        window.Timeline.DateTime = SimileAjax.DateTime; // for backward compatibility
    
        var localizedJavascriptFiles = [
            "timeline.js",
            "labellers.js"
        ];
        
        // ISO-639 language codes, ISO-3166 country codes (2 characters)
        var supportedLocales = [
            "cs",       // Czech
            "de",       // German
            "en",       // English
            "es",       // Spanish
            "fr",       // French
            "it",       // Italian
            "nl",       // Dutch (The Netherlands)
            "ru",       // Russian
            "se",       // Swedish
            "tr",       // Turkish
            "vi",       // Vietnamese
            "zh"        // Chinese
        ];
        
        try {
            var desiredLocales = [ "en" ],
                defaultServerLocale = "en",
                forceLocale = null;
            
            Timeline.urlPrefix = packagePath;
            
            var includeJavascriptFiles = function(urlPrefix, filenames) {
                SimileAjax.includeJavascriptFiles(document, urlPrefix, filenames);
            }
            
            /*
             *  Include localized files
             */
            var loadLocale = [];
            loadLocale[defaultServerLocale] = true;
            
            var tryExactLocale = function(locale) {
                for (var l = 0; l < supportedLocales.length; l++) {
                    if (locale == supportedLocales[l]) {
                        loadLocale[locale] = true;
                        return true;
                    }
                }
                return false;
            }
            var tryLocale = function(locale) {
                if (tryExactLocale(locale)) {
                    return locale;
                }
                
                var dash = locale.indexOf("-");
                if (dash > 0 && tryExactLocale(locale.substr(0, dash))) {
                    return locale.substr(0, dash);
                }
                
                return null;
            }
            
            for (var l = 0; l < desiredLocales.length; l++) {
                tryLocale(desiredLocales[l]);
            }
            
            var defaultClientLocale = defaultServerLocale;
            var defaultClientLocales = ("language" in navigator ? navigator.language : navigator.browserLanguage).split(";");
            for (var l = 0; l < defaultClientLocales.length; l++) {
                var locale = tryLocale(defaultClientLocales[l]);
                if (locale != null) {
                    defaultClientLocale = locale;
                    break;
                }
            }
            
            for (var l = 0; l < supportedLocales.length; l++) {
                var locale = supportedLocales[l];
                if (loadLocale[locale]) {
                    includeJavascriptFiles(Timeline.urlPrefix + "scripts/l10n/" + locale + "/", localizedJavascriptFiles);
                }
            }
            
            if (forceLocale == null) {
              Timeline.serverLocale = defaultServerLocale;
              Timeline.clientLocale = defaultClientLocale;
            } else {
              Timeline.serverLocale = forceLocale;
              Timeline.clientLocale = forceLocale;
            }            	
        } catch (e) {
            alert(e);
        }
    };
    
    loadMe();
})();

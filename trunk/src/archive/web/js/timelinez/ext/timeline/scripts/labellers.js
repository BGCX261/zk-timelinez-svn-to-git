/*==================================================
 *  Gregorian Date Labeller
 *==================================================
 */

Timeline.GregorianDateLabeller = function(locale, timeZone) {
    this._locale = locale;
    this._timeZone = timeZone;
};

Timeline.GregorianDateLabeller.monthNames = [];
Timeline.GregorianDateLabeller.dayNames = [];
Timeline.GregorianDateLabeller.labelIntervalFunctions = [];
Timeline.GregorianDateLabeller.monthNames["en"]=["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
Timeline.GregorianDateLabeller.dayNames["en"]=["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];
Timeline.GregorianDateLabeller.monthNames["zh"]=["1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"];
Timeline.GregorianDateLabeller.monthNames["vi"]=["Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6", "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"];
Timeline.GregorianDateLabeller.monthNames["tr"]=["Ock", "Åžbt", "Mrt", "Nsn", "Mys", "Hzr", "Tem", "AÄŸs", "Eyl", "Ekm", "Ksm", "Arl"];
Timeline.GregorianDateLabeller.monthNames["se"]=["Jan", "Feb", "Mar", "Apr", "Maj", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec"];
Timeline.GregorianDateLabeller.dayNames["se"]=["Sön", "Mån", "Tis", "Ons", "Tors", "Fre", "Lör"];
Timeline.GregorianDateLabeller.monthNames["ru"]=["Янв", "Фев", "Мар", "Апр", "Май", "Июн", "Июл", "Авг", "Сен", "Окт", "Ноя", "Дек"];
Timeline.GregorianDateLabeller.monthNames["nl"]=["jan", "feb", "mrt", "apr", "mei", "jun", "jul", "aug", "sep", "okt", "nov", "dec"];
Timeline.GregorianDateLabeller.monthNames["it"]=["Gen", "Feb", "Mar", "Apr", "Mag", "Giu", "Lug", "Ago", "Set", "Ott", "Nov", "Dic"];
Timeline.GregorianDateLabeller.monthNames["fr"]=["jan", "fev", "mar", "avr", "mai", "jui", "jui", "aou", "sep", "oct", "nov", "dec"];
Timeline.GregorianDateLabeller.monthNames["es"]=["Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"];
Timeline.GregorianDateLabeller.monthNames["de"]=["Jan", "Feb", "Mrz", "Apr", "Mai", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dez"];
Timeline.GregorianDateLabeller.monthNames["cs"]=["Leden", "Únor", "Bøezen", "Duben", "Kvìten", "Èerven", "Èervenec", "Srpen", "Záøí", "Øíjen", "Listopad", "Prosinec"];
Timeline.GregorianDateLabeller.dayNames["cs"]=["Ne", "Po", "Út", "St", "Èt", "Pá", "So"];

Timeline.GregorianDateLabeller.getMonthName = function(month, locale) {
    return Timeline.GregorianDateLabeller.monthNames[locale][month];
};

Timeline.GregorianDateLabeller.prototype.labelInterval = function(date, intervalUnit) {
    var f = Timeline.GregorianDateLabeller.labelIntervalFunctions[this._locale];
    if (f == null) {
        f = Timeline.GregorianDateLabeller.prototype.defaultLabelInterval;
    }
    return f.call(this, date, intervalUnit);
};

Timeline.GregorianDateLabeller.prototype.labelPrecise = function(date) {
    return SimileAjax.DateTime.removeTimeZoneOffset(
        date, 
        this._timeZone //+ (new Date().getTimezoneOffset() / 60)
    ).toUTCString();
};

Timeline.GregorianDateLabeller.prototype.defaultLabelInterval = function(date, intervalUnit) {
    var text;
    var emphasized = false;
    
    date = SimileAjax.DateTime.removeTimeZoneOffset(date, this._timeZone);
    
    switch(intervalUnit) {
    case SimileAjax.DateTime.MILLISECOND:
        text = date.getUTCMilliseconds();
        break;
    case SimileAjax.DateTime.SECOND:
        text = date.getUTCSeconds();
        break;
    case SimileAjax.DateTime.MINUTE:
        var m = date.getUTCMinutes();
        if (m == 0) {
            text = date.getUTCHours() + ":00";
            emphasized = true;
        } else {
            text = m;
        }
        break;
    case SimileAjax.DateTime.HOUR:
        text = date.getUTCHours() + "hr";
        break;
    case SimileAjax.DateTime.DAY:
        text = Timeline.GregorianDateLabeller.getMonthName(date.getUTCMonth(), this._locale) + " " + date.getUTCDate();
        break;
    case SimileAjax.DateTime.WEEK:
        text = Timeline.GregorianDateLabeller.getMonthName(date.getUTCMonth(), this._locale) + " " + date.getUTCDate();
        break;
    case SimileAjax.DateTime.MONTH:
        var m = date.getUTCMonth();
        if (m != 0) {
            text = Timeline.GregorianDateLabeller.getMonthName(m, this._locale);
            break;
        } // else, fall through
    case SimileAjax.DateTime.YEAR:
    case SimileAjax.DateTime.DECADE:
    case SimileAjax.DateTime.CENTURY:
    case SimileAjax.DateTime.MILLENNIUM:
        var y = date.getUTCFullYear();
        if (y > 0) {
            text = date.getUTCFullYear();
        } else {
            text = (1 - y) + "BC";
        }
        emphasized = 
            (intervalUnit == SimileAjax.DateTime.MONTH) ||
            (intervalUnit == SimileAjax.DateTime.DECADE && y % 100 == 0) || 
            (intervalUnit == SimileAjax.DateTime.CENTURY && y % 1000 == 0);
        break;
    default:
        text = date.toUTCString();
    }
    return { text: text, emphasized: emphasized };
}


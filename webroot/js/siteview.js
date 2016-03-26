function createTable(obj) {
    var result = "";
    var i = 0;
    result += "<table style='width: 100%; padding: 2px; border-spacing: 0px;'>";
    $.each(obj, function( key, value ) {
        if (i % 2 == 0) {
        	result += "<tr style='background: #dfedfb;'>";
        } else {
        	result += "<tr style='background: #ffffff;'>";
        }
    	result += "<td style='text-align: left;'>";
    	result += key;
    	result += "</td>";
    	result += "<td style='text-align: right;'>";
    	result += value;
    	result += "</td>";		
    	result += "</tr>";
    	i++;
    });
    result += "</table>";
    return (result);
}

$(function () {
    Highcharts.setOptions({
        global: {
            useUTC: false
        },
        chart: {
        	style: {
        		fontFamily: 'Unica One'
    		}       
    	}     
    });
    $('#chart').highcharts({
        chart: {
            type: 'areaspline',
            animation: Highcharts.svg
        },
        title: {
            text: 'Live Hits'
        },
        subtitle: {
        	text: 'Past 30 seconds'
        },
        credits: {
        	enabled: false
        },
        xAxis: {
            type: 'datetime',
            tickPixelInterval: 150
        },
        yAxis: {
            title: {
                text: 'Hits/s'
            },
            plotLines: [{
                value: 0,
                width: 1,
                color: '#808080'
            }]
        },
        tooltip: {
            formatter: function () {
                return '<b>' + this.series.name + '</b><br/>' +
                    Highcharts.dateFormat('%H:%M:%S', this.x) + '<br/>' +
                    Highcharts.numberFormat(this.y, 0);
            }
        },
        legend: {
            enabled: false
        },
        exporting: {
            enabled: false
        },
        plotOptions: {
        	areaspline: {
            	fillOpacity: 0.25
        	}
    	},
        series: [{
            name: 'Hits',
            data: (function () {
                var data = [],
                    time = (new Date()).getTime(),
                    i;
                for (i = -29; i <= 0; i += 1) {
                    data.push({
                        x: time + i * 1000,
                        y: 0
                    });
                }
                return data;
            }())
        }]
    });  
});
  
var eventBus = new EventBus("/event/");
eventBus.onopen = function () {
    eventBus.registerHandler("event.to.client", function (err, msg) {
        var json = jQuery.parseJSON(msg.body);
        var x = +json.time;
        var y = +json.hits;
        $('#chart').highcharts().series[0].addPoint([x, y], true, true);
    	$('#uris').html(createTable(json.uris));
    	$('#countries').html(createTable(json.countries));
    	$('#languages').html(createTable(json.languages));		
    	$('#operatingSystems').html(createTable(json.operatingSystems));  
    	$('#browsers').html(createTable(json.browsers));
    });
};
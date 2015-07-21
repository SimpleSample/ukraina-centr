$(function () {
    var DATA_ARRAY = 'dataArray';
    var MILISS_DATA_ARRAY = 'milissDataArray';
    var PASSENGERS_DATA = 'passengersData';
    var PASSENGERS_LOCAL_OBJ = {
        forth: {
            dataArray : [],
            milissDataArray: []
        },
        back: {
            dataArray : [],
            milissDataArray: []
        }
    };
    // global vars
    var chosenTripId;
    var passengersData = localStorage? localStorage.getItem(PASSENGERS_DATA): null;
    var printableTitle = '';
    if (passengersData) {
        passengersData = JSON.parse(passengersData);
    } else {
        passengersData = PASSENGERS_LOCAL_OBJ;
        if (localStorage) {
            localStorage.setItem(PASSENGERS_DATA, JSON.stringify(passengersData));
        }
    }

    function showPassengersData(passengersData) {
        $('#column-chart-container').highcharts({
            chart: {
                type: 'column'
            },
            title: {
                text: 'Кількість пасажирів за останні три місяці',
                style: {
                    fontSize: '20px',
                    fontFamily: 'Roboto, sans-serif'
                }
            },
            xAxis: {
                type: 'category',
                labels: {
                    rotation: -45,
                    style: {
                        fontSize: '13px',
                        fontFamily: 'Roboto, sans-serif'
                    }
                }
            },
            plotOptions: {
                series: {
                    color: '#93D600'
                }
            },
            yAxis: {
                min: 0,
                title: {
                    text: 'Кількість пасажирів'
                }
            },
            legend: {
                enabled: false
            },
            tooltip: {
                pointFormat: 'Кількість пасажирів: <b>{point.y}</b>'
            },
            series: [{
                name: 'Кількість',
                data: passengersData,
                dataLabels: {
                    enabled: true,
                    color: '#FFFFFF',
                    align: 'center',
                    format: '{point.y}',
                    y: 20,
                    style: {
                        fontSize: '13px',
                        fontFamily: 'Roboto, sans-serif',
                        textShadow: 'none'
                    }
                }
            }]
        });
    }

    function loadPassengersData(tripType, startDate, endDate, onLoad) {
        var reqData = {
            tripType : tripType,
            startDate : startDate,
            endDate : endDate
        };
        new Request('countPassengers', reqData).send(function(data){
            var dataArray = data[DATA_ARRAY];
            var milissDataArray = data[MILISS_DATA_ARRAY];
            var typedPassengersData = passengersData[tripType];
            typedPassengersData[DATA_ARRAY] = typedPassengersData[DATA_ARRAY].concat(dataArray);
            typedPassengersData[MILISS_DATA_ARRAY] = typedPassengersData[MILISS_DATA_ARRAY].concat(milissDataArray);
            if (localStorage) {
                localStorage.setItem(PASSENGERS_DATA, JSON.stringify(passengersData));
            }
            onLoad(typedPassengersData[DATA_ARRAY]);
        });
    }

    function showChartForRange(tripType, startDateMiliss, endDateMiliss) {
        var typedPassengersData = passengersData[tripType];
        var milissDataArray = typedPassengersData[MILISS_DATA_ARRAY];
        var dataArray = typedPassengersData[DATA_ARRAY];

        if (milissDataArray.length) {
            for(var i = 0; milissDataArray[i] < startDateMiliss && i < milissDataArray.length; i++) {}

            var loadingNeeded = false;
            if(!i) {
                showPassengersData(dataArray);
            } else if (i != milissDataArray.length) {
                var nowDelta = startDateMiliss - milissDataArray[0] - 60000; //jic
                startDateMiliss = endDateMiliss - nowDelta;
                milissDataArray.splice(0, i);
                dataArray.splice(0, i);
                loadingNeeded = true;
            } else {
                typedPassengersData[MILISS_DATA_ARRAY] = [];
                typedPassengersData[DATA_ARRAY] = [];
                loadingNeeded = true;
            }
            if (loadingNeeded) {
                loadPassengersData(tripType, startDateMiliss, endDateMiliss, function(data) {
                    showPassengersData(data);
                });
            }
        } else {
            loadPassengersData(tripType, startDateMiliss, endDateMiliss, function(data) {
                showPassengersData(data);
            });
        }
    }

    function showPieChart(passCount, allCount) {
        var percents = 100;
        if (allCount > 0) {
            percents = Math.round((passCount/allCount)*100);
        }
        var pieChartAngle = percents * 3.6 + 90;

        $('#pie-chart-container').highcharts({
            chart: {
                plotBackgroundColor: null,
                plotBorderWidth: 0,
                plotShadow: false
            },
            title: {
                text: 'Кількість<br>пасажирів<br>по рейсу<br>',
                align: 'center',
                verticalAlign: 'middle',
                y:-20
            },
            tooltip: {
                headerFormat:'<span style="font-size: 14px">Кіровоград - Рим</span><br/>',
                pointFormat: 'Заповнення автобусу: <b>{point.y}%</b><br/>'
            },
            plotOptions: {
                pie: {
                    dataLabels: {
                        enabled: true,
                        distance: -40,
                        style: {
                            fontWeight: 'bold',
                            color: 'white',
                            textShadow: 'none',
                            fontSize: '40px'
                        }
                    },
                    startAngle: 90,
                    endAngle: pieChartAngle,
                    center: ['50%', '50%']
                }
            },
            series: [{
                type: 'pie',
                innerSize: '60%',
                data: [
                    [passCount+"", percents]
                ]
            }]
        });
    }

    function onTripChanged(tripId) {
        var chosenTrip = dataStore.get(tripId);
        //changing header value
        var headerDropdown = $('.header-dropdown');
        var dropdownValue = headerDropdown.find(' .dropdown-value');
        dropdownValue.text(chosenTrip.stringData);

        //changing column chart
        var now = new Date();
        var threeMonthAgo = new Date();
        threeMonthAgo.setMonth(now.getMonth()-3);
        var startDateMiliss = threeMonthAgo.getTime();
        var endDateMiliss = now.getTime();
        showChartForRange(chosenTrip.type, startDateMiliss, endDateMiliss);

        //changing pie chart
        var passCount = chosenTrip.passCount || 0;
        var allCount = chosenTrip.allCount || 0;
        showPieChart(parseInt(passCount), parseInt(allCount));
        TablesawUtils.renderTable($('.history-table>table>tbody'),
                                    chosenTrip.tickets,
                                    uc.adminTicketRecordTemplate);
        printableTitle = chosenTrip.startDate + " " + chosenTrip.stringData;
    }

    $(document).ready(function(){
        TablesawUtils.bindCommonHandlers($('.history-table>table'), 'Ticket');

        new Request('closestTripData').send(function(data){
            var forthTrip = data.forthTrip;
            forthTrip.type = 'forth';
            dataStore.set(forthTrip, 'Trip');
            var backTrip = data.backTrip;
            backTrip.type = 'back';
            dataStore.set(backTrip, 'Trip');
            var currentTrip = forthTrip.startDate > backTrip.startDate? backTrip : forthTrip;
            var headerDropdown = $('.header-dropdown');
            var dropdownOptions = headerDropdown.find('.dropdown-menu');
            dropdownOptions.append('<li><a id="'+forthTrip.id+'" href="#">'+forthTrip.stringData+'</a></li>');
            dropdownOptions.append('<li><a id="'+backTrip.id+'"href="#">'+backTrip.stringData+'</a></li>');
            dropdownOptions.on('click', 'a', function(ev) {
                ev.preventDefault();
                headerDropdown.removeClass('open');
                var choice = $(this).attr('id');
                if (chosenTripId == choice) return false;

                chosenTripId = choice;
                onTripChanged(chosenTripId);
                return false;
            });
            onTripChanged(currentTrip.id);
        });

        $('#print-table').on('click', function(event){
            printTablesaw($('.history-table>table'), printableTitle);
        });
    });
});

if ($.urlParam('barcode')) {
    var barcode = $.urlParam('barcode');
    new Popup('Результат сканування', '<div>'+barcode+'</div>').show();
}

if (!uc) var uc = {};
window.uc.adminTicketRecordTemplate = function(ticket) {
    var note = ticket['note'] || '';
    return '<tr id="'+ticket['id']+'">' +
        '<td>'+ticket['passenger']+'</td>' +
        '<td>'+ticket['phones']+'</td>' +
        '<td class="ticket-trip">'+ticket['trip']+'</td>' +
        '<td class="ticket-date">'+ticket['startDate']+'</td>' +
        '<td class="ticket-seat">'+ticket['seat']+'</td>' +
        '<td>'+ticket['price']+'</td>' +
        '<td>'+note+'</td>' +
        '<td><a class="action-glyph-link" href="#"><span class="glyphicon glyphicon-trash" title="Видалити" aria-hidden="true"></span></a></td>' +
        '</tr>';
};


$(function () {
    $(document).ready(function(){
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
                data: [
                    ['23 Липня', 40],
                    ['16 Липня', 15],
                    ['9 Липня', 19],
                    ['2 Липня', 38],
                    ['25 червня', 33],
                    ['18 червня', 34],
                    ['11 червня', 12],
                    ['4 червня', 27],
                    ['28 травня', 40],
                    ['21 травня', 30],
                    ['14 травня', 22],
                    ['7 травня', 36]
                ],
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
                    endAngle: 440,
                    center: ['50%', '50%']
                }
            },
            series: [{
                type: 'pie',
                innerSize: '60%',
                data: [
                    ['13',   28]
                ]
            }]
        });


        var sidebar = $('.sidebar');
        var swipeOptions = { dragLockToAxis: true, dragBlockHorizontal: true };
        var touchControl = new Hammer($('body')[0], swipeOptions);
        touchControl.on("swipeleft swiperight", function(ev) {
            if (ev.type == 'swipeleft') {
                if (!sidebar.hasClass('sidebar-swiped')) {
                    sidebar.addClass('sidebar-swiped');
                }
            } else if (ev.type == 'swiperight') {
                if (sidebar.hasClass('sidebar-swiped')) {
                    sidebar.removeClass('sidebar-swiped');
                }
            }
        });

        sidebar.find('#toggle-sidebar-link').on('click', function(e) {
            e.preventDefault();
            sidebar.toggleClass('sidebar-swiped');
            return false;
        });
    });
});


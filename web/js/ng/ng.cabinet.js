(function(){
var clientBundle = window.clientBundle || {};
    $(document).on('ready', function() {
        if (!isAuthorized()) {
            var popup = new Popup(clientBundle.error, '<div>'+clientBundle.you_are_not_authorized_you_can+' <a href="#" id="sug-login" class="italic">'+clientBundle.login+'</a> '+clientBundle.or+' <a href="#" id="sug-register" class="italic">'+clientBundle.register+'</a></div>', '', function(popupId){
                var $popupEl = $(popupId);
                $popupEl.find('#sug-login').on('click', function(event) {
                    popup.destroy();
                    login(function(){window.location.reload();});
                });
                $popupEl.find('#sug-register').on('click', function(event) {
                    popup.destroy();
                    register(function(){window.location.reload();});
                });
            });
            popup.show();
            return;
        }

        var $table = $('.history-table tbody');
        var pager = new Pager($('.tablesaw-pager'), "allTickets", function(data){
            $table.empty();

            var tickets = data['objects'];
            var isPartner = data['isPartner'];
            for (var i = 0, size = tickets.length; i < size; i++) {
                var ticket = tickets[i];
                $table.append(uc.ticketRecordTemplate(ticket['id'], ticket['passenger'], ticket['phones'], ticket['trip'], ticket['startDate'], ticket['seat'], ticket['status'], isPartner));
            }
        });

//        pager.init(); todo

        EventBus.addEventListener('user_loggedout', function(event) {
            pager.clear();
        });

        EventBus.addEventListener('user_authorized', function(event) {
//            pager.init(); todo
        });

        setTimeout(function () {
            $('.history-table>table>tbody').append('<tr id="ag9zfnVrcmFpbmEtY2VudHJyNgsSBVJvdXRlGICAgICEypsKDAsSBFRyaXAYgICAgO6wmQoMCxIGVGlja2V0GICAgIDurIQJDA"><td>Odmin Admin</td><td></td><td>Чезене - Кіровоград</td><td>24 жовтня, 2015 11:15</td><td>2b</td><td>RESERVED</td></tr><tr id="ag9zfnVrcmFpbmEtY2VudHJyNgsSBVJvdXRlGICAgICEypsKDAsSBFRyaXAYgICAgO6wmQoMCxIGVGlja2V0GICAgIDuwYwJDA">                    <td>Мdmin Admin</td><td></td><td>Вінниця - Кіровоград</td><td>25 квітня, 2015 11:15</td><td>2a</td><td>PROCESSING</td></tr><tr id="ag9zfnVrcmFpbmEtY2VudHJyNgsSBVJvdXRlGICAgICEypsKDAsSBFRyaXAYgICAgO6wmQoMCxIGVGlja2V0GICAgIDu2pUJDA">                    <td>зdmin Admin</td><td class="ticket-trip"></td><td>КРим - Кіровоград</td><td>5 вересня, 2015 11:15</td><td>1a</td><td>RESERVED</td></tr><tr id="ag9zfnVrcmFpbmEtY2VudHJyNgsSBVJvdXRlGICAgICEypsKDAsSBFRyaXAYgICAgO6wmQoMCxIGVGlja2V0GICAgIDurIQKDA">                    <td>trdmin Admin</td><td class="ticket-trip"></td><td>Рим - Кіровоград</td><td>25 жовтня, 2015 11:15</td><td>2d</td><td>PROCESSING</td></tr><tr id="ag9zfnVrcmFpbmEtY2VudHJyNgsSBVJvdXRlGICAgICEypsKDAsSBFRyaXAYgICAgO6wmQoMCxIGVGlja2V0GICAgIDuwYwKDA">                    <td>раdmin Admin</td><td class="ticket-trip"></td><td>СРим - Кіровоград</td><td>25 жовтня, 2015 11:15</td><td>1b</td><td>RESERVED</td></tr><tr id="ag9zfnVrcmFpbmEtY2VudHJyNgsSBVJvdXRlGICAgICEypsKDAsSBFRyaXAYgICAgO6wmQoMCxIGVGlja2V0GICAgIDu2pUKDA">                    <td>Admin Admin</td><td></td><td>Рим - Кіровоград</td><td>25 жовтня, 2015 11:15</td><td>1d</td><td>PROCESSING</td></tr><tr id="ag9zfnVrcmFpbmEtY2VudHJyNgsSBVJvdXRlGICAgICEypsKDAsSBFRyaXAYgICAgO6wmQoMCxIGVGlja2V0GICAgIDurIQLDA">                    <td>Admin Admin</td><td></td><td>Рим - Кіровоград</td><td>25 жовтня, 2015 11:15</td><td>1c</td><td>RESERVED</td></tr><tr id="ag9zfnVrcmFpbmEtY2VudHJyNgsSBVJvdXRlGICAgICEypsKDAsSBFRyaXAYgICAgO6wmQoMCxIGVGlja2V0GICAgIDu2pULDA">                    <td>Admin Admin</td><td></td><td>Рим - Кіровоград</td><td>25 жовтня, 2015 11:15</td><td>2c</td><td>PROCESSING</td></tr><tr id="ag9zfnVrcmFpbmEtY2VudHJyNgsSBVJvdXRlGICAgICEypsKDAsSBFRyaXAYgICAgK6OngoMCxIGVGlja2V0GICAgICumYAJDA">                    <td>Admin Admin</td><td></td><td>Рим - Кіровоград</td><td>18 жовтня, 2015 11:15</td><td>2b</td><td>RESERVED</td></tr><tr id="ag9zfnVrcmFpbmEtY2VudHJyNgsSBVJvdXRlGICAgICEypsKDAsSBFRyaXAYgICAgK6OngoMCxIGVGlja2V0GICAgICu54gJDA">                    <td>Admin Admin</td><td></td><td>Рим - Кіровоград</td><td>18 жовтня, 2015 11:15</td><td>2a</td><td>PROCESSING</td></tr>');
//            $('.history-table table').table().data( "table" ).refresh();
        }, 5000);

//        var $changePass = $('#change-pass');
//        $changePass.find('button').click(function(event) {
//            var $old = $changePass.find('#user-password-old');
//            var oldVal = $old.val();
//            var newVal = $changePass.find('#user-password').val();
//            var isOldPassValid = /^[0-9a-zA-Z]{8,}$/.test(oldVal);
//            var isNewPassValid = /^[0-9a-zA-Z]{8,}$/.test(newVal);
//            if (!isOldPassValid || !isNewPassValid) {
//                new Message(clientBundle.password_should_be_at_least_8_symbols_long, 5000);
//                return;
//            }
//
//            new Request("changePass", {oldPass: oldVal, newPass: newVal}).send(function(data) {
//               if (data && data['newPass']) {
//                   if ($old.hasClass('input-error')) $old.removeClass('input-error');
//                   new Popup(clientBundle.password_change, '<div>'+clientBundle.password_was_changed_successfully+'</div>','').show();
//               } else {
//                   $changePass.find('#user-password-old').addClass('input-error');
//                   new Popup(clientBundle.password_change, '<div>'+clientBundle.old_password_is_not_correct+'</div>', '').show();
//               }
//            });
//        });

        $table.on('click', '.change-ticket-date', function(event) {
            var $targetTr = $(this).parents('tr');
            var ticketId = $targetTr.attr('id');

            new Request('otherTrips', {ticketId: ticketId}).send(function(data) {
                var trips = data['trips'];
                var popupHtml = uc.changeTicketDateTemplate(trips);
                for (var i = 0, size = trips.length; i < size; i++) {
                    var t = trips[i];
                    dataStore.set(t, 'Trip');
                    dataStore.setAll(t['seats'], 'Seat');
                }

                var popup = new Popup(clientBundle.ticket_date_change, popupHtml, '', function(popupId){
                    var $availTrips = $('#avail-trips');
                    var $availSeats = $('#avail-seats');
                    $availTrips.on('select', function(e){
                        var $this = $(this);
                        var tripId = $this.find(":selected").attr('id');
                        var trip = dataStore.get(tripId);
                        var seatOpts = createSelectSeatsOpts(trip['seats']);
                        $availSeats.html(seatOpts);
                    });

                    $('#do-change').click(function(e) {
                        var tripId = $availTrips.find(":selected").attr('id');
                        var seatId = $availSeats.find(":selected").attr('id');
                        new Request('changeDate', {tripId: tripId, seatId: seatId, ticketId: ticketId}).send(function(data) {
                            var trip = dataStore.get(tripId);
                            var tripStr = trip['startCity'] + " - " + trip['endCity'];
                            $targetTr.find('.ticket-trip').text(tripStr);
                            $targetTr.find('.ticket-date').text(trip['startDate']);
                            $targetTr.find('.ticket-seat').text(dataStore.get(seatId)['seatNum']);
                            $targetTr.attr('id', data['newTicketId']);
                            popup.destroy();
                        });
                    });

                });
                popup.show();
            });
        });
        $('#order-history').on('click tap', function(eve){
            eve.preventDefault();

            return false;
        });
        $('#password-change').on('click tap', function(eve){
            eve.preventDefault();
            var popup = new Popup('Зміна паролю', window.uc.changePassTemplate, '', function(popupId) {
                $(popupId + ' #btn-change-pass').click(function(event){
                    event.preventDefault();
                    var popupEl = $(popupId);
                    var $old = popupEl.find('#user-password-old');
                    var oldVal = $old.val();
                    var newVal = popupEl.find('#user-password').val();
                    var isOldPassValid = /^[0-9a-zA-Z]{8,}$/.test(oldVal);
                    var isNewPassValid = /^[0-9a-zA-Z]{8,}$/.test(newVal);
                    if (!isOldPassValid || !isNewPassValid) {
                        new Message(clientBundle.password_should_be_at_least_8_symbols_long, 5000);
                        return;
                    }

                    new Request("changePass", {oldPass: oldVal, newPass: newVal}).send(function(data) {
                        popup.destroy();
                        if (data && data['newPass']) {
                            if ($old.hasClass('input-error')) $old.removeClass('input-error');
                            new Popup(clientBundle.password_change, '<div>'+clientBundle.password_was_changed_successfully+'</div>','').show();
                        } else {
                            $changePass.find('#user-password-old').addClass('input-error');
                            new Popup(clientBundle.password_change, '<div>'+clientBundle.old_password_is_not_correct+'</div>', '').show();
                        }
                    });
                    return false;
                });

            });
            popup.show();
            return false;
        });
        $('#personal-data').on('click tap', function(eve){
            eve.preventDefault();

            return false;
        });
        $('#cabinet-logout').on('click tap', function(eve){
            eve.preventDefault();
            window.logout(function(){
                window.location.href = '/';
            });

            return false;
        });
    });

    window.uc.changePassTemplate =
        '<div class="forms">' +
        '<label><input type="password" name="password" id="user-password-old" placeholder="'+clientBundle.old_password+'"></label>' +
        '<label><input type="password" name="password" id="user-password" placeholder="'+clientBundle.new_one+'"></label>' +
        '<div class="clear-both">' +
        '<button id="btn-change-pass" class="btn btn-left btn-green">'+clientBundle.change_it+'</button>' +
        '</div>' +
        '</div>';

    window.uc.ticketRecordTemplate = function(ticketId, passenger, phones, trip, startDate, seat, status, showAction) {
        var action = '';
        if (showAction) action += '<a href="#" class="change-ticket-date" title="'+clientBundle.change_date+'"></a>';
        return '<tr id="'+ticketId+'"><td>'+passenger+'</td><td>'+phones+'</td><td>'+trip+'</td><td>'+startDate+'</td><td>'+seat+'</td><td>'+status+'</td></tr>'; // todo <td>'+action+'</td>
    }

    window.uc.changeTicketDateTemplate = function(trips) {
        var selectOpts = '';
        for (var i = 0, size = trips.length; i < size; i++) {
            var trip = trips[i];
            var option = trip['startDate'] + ", " + trip['startCity'] + " - " + trip['endCity'] + ", " + trip['reservedSeatsCount'] + "/" + trip['allSeatsCount'];
            selectOpts += '<option id="'+trip['id']+'">' + option + '</option>';
        }
        var selectSeatsOpts = '';
        if (trips.length > 0) {
            var first = trips[0];
            selectSeatsOpts = createSelectSeatsOpts(first['seats']);
        }
        return '<div>'+clientBundle.choose_trip_and_seat+'</div><div><select id="avail-trips">'+selectOpts+'</select><select id="avail-seats">'+selectSeatsOpts+'</select></div>' +
            '<p><button id="do-change" style="margin-top: 10px;" class="btn btn-green width-100 light">'+clientBundle.change_it+'</button></p>';
    }

    function createSelectSeatsOpts(seats) {
        var selectSeatsOpts = '';
        for (var i = 0, size = seats.length; i < size; i++) {
            var seat = seats[i];
            selectSeatsOpts += '<option id="'+seat['id']+'">' + seat['seatNum'] + '</option>';
        }
        return selectSeatsOpts;
    }

})();

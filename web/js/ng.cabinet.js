(function(){

    $(document).on('ready', function() {
        $( "#accordion" ).accordion({
            heightStyle: "content"
        });

        if (!isAuthorized()) {
            var popup = new Popup('Помилка', '<div>Ви не авторизовані в системі. У Вас є можлівисть <a href="#" id="sug-login" class="italic">авторизуватись</a> або <a href="#" id="sug-register" class="italic">зареєструватись</a></div>', 'white', function(popupId){
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
        var $table = $('.table-tickets tbody');
        var pager = new Pager($('.pager'), "allTickets", function(data){
            $table.empty();

            var tickets = data['objects'];
            var isPartner = data['isPartner'];
            for (var i = 0, size = tickets.length; i < size; i++) {
                var ticket = tickets[i];
                $table.append(uc.ticketRecordTemplate(ticket['id'], ticket['passenger'], ticket['phones'], ticket['trip'], ticket['startDate'], ticket['seat'], ticket['status'], isPartner));
            }
        });

        pager.init();

        var $changePass = $('#change-pass');
        $changePass.find('button').click(function(event) {
            var $old = $changePass.find('#user-password-old');
            var oldVal = $old.val();
            var newVal = $changePass.find('#user-password').val();
            var isOldPassValid = /^[0-9a-zA-Z]{8,}$/.test(oldVal);
            var isNewPassValid = /^[0-9a-zA-Z]{8,}$/.test(newVal);
            if (!isOldPassValid || !isNewPassValid) {
                new Message('Пароль повинен мати принаймi 8 символiв', 5000);
                return;
            }

            new Request("changePass", {oldPass: oldVal, newPass: newVal}).send(function(data) {
               if (data && data['newPass']) {
                   if ($old.hasClass('input-error')) $old.removeClass('input-error');
                   new Popup('Зміна паролю', "<div>Пароль був успішно змінений</div>", "white").show();
               } else {
                   $changePass.find('#user-password-old').addClass('input-error');
                   new Popup('Зміна паролю', "<div>Невірно введений старий пароль</div>", "white").show();
               }
            });
        });

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

                var popup = new Popup('Зміна дати квитків', popupHtml, "white", function(popupId){
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
                            $targetTr.find('#ticket-trip').text(tripStr);
                            $targetTr.find('#ticket-date').text(trip['startDate']);
                            $targetTr.find('#ticket-seat').text(dataStore.get(seatId)['seatNum']);
                            $targetTr.attr('id', data['newTicketId']);
                            popup.destroy();
                        });
                    });

                });
                popup.show();
            });


        });
    });

    window.uc.ticketRecordTemplate = function(ticketId, passenger, phones, trip, startDate, seat, status, showAction) {
        var action = '';
        if (showAction) action += '<a href="#" class="change-ticket-date" title="Змінити дату поїздки"></a>';
        return '<tr id="'+ticketId+'"><td>'+passenger+'</td><td>'+phones+'</td><td id="ticket-trip">'+trip+'</td><td id="ticket-date">'+startDate+'</td><td id="ticket-seat">'+seat+'</td><td>'+status+'</td><td>'+action+'</td></tr>';
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
        return '<div>Оберіть поїздку і місце</div><div><select id="avail-trips">'+selectOpts+'</select><select id="avail-seats">'+selectSeatsOpts+'</select></div>' +
            '<p><button id="do-change" style="margin-top: 10px;" class="btn btn-green width-100 light">Змінити</button></p>';
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

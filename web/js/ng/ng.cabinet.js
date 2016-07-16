(function(){
var clientBundle = window.clientBundle || {};

    function reinitTablesaw($table) {
        $table.data( 'table' ).destroy();
        $table.table();
    }

    function changeTicketDate($tableElement, ticketId, onChangedCallback) {
        $tableElement.addClass('active');
        new Request('otherTrips', {ticketId: ticketId}).send(function(data) {
            var trips = data['trips'];
            var popupHtml = uc.changeTicketDateTemplate(trips);
            for (var i = 0, size = trips.length; i < size; i++) {
                var t = trips[i];
                dataStore.set(t, 'Trip');
                dataStore.setAll(t['seats'], 'Seat');
            }

            var popup = new Popup(clientBundle.ticket_date_change, popupHtml, '', function(popupId){
                var $popup = $(popupId);
                var $availTrips = $popup.find('#avail-trips');
                var $availSeats = $popup.find('#avail-seats');
                $availTrips.on('select', function(e){
                    var $this = $(this);
                    var tripId = $this.find(":selected").attr('id');
                    var trip = dataStore.get(tripId);
                    var seatOpts = createSelectSeatsOpts(trip['seats']);
                    $availSeats.html(seatOpts);
                });

                $popup.find('#do-change').click(function(e) {
                    var tripId = $availTrips.find(":selected").attr('id');
                    var seatId = $availSeats.find(":selected").attr('id');
                    new Request('changeDate', {tripId: tripId, seatId: seatId, ticketId: ticketId}).send(function(data) {
                        var trip = dataStore.get(tripId);
                        var tripStr = trip['startCity'] + ' - ' + trip['endCity'];

                        var currentTicket = dataStore.get(ticketId);
                        currentTicket.id = data['newTicketId'];
                        currentTicket.trip = tripStr;
                        currentTicket.startDate = trip['startDate'];
                        currentTicket.seat = dataStore.get(seatId)['seatNum'];
                        if (onChangedCallback) {
                            onChangedCallback(currentTicket);
                        }
                        $tableElement.removeClass('active');
                        popup.destroy();
                    });
                });

            });
            popup.show();
        });
    }

    function removeTicket($tableElement, ticketId, onRemovedCallback) {
        $tableElement.addClass('active');
        var popup = new Popup(clientBundle.ticket_deletion, uc.removeTicketConfirmation(), '', function(popupId){
            var $popupEl = $(popupId);
            $popupEl.find('#do-remove-ticket').on('click', function(event) {
                new Request('removeTicket', {entityId: ticketId}).send(function(event){
                    if (onRemovedCallback) onRemovedCallback();
                    $tableElement.removeClass('active');
                    popup.destroy();
                });
            });
            $popupEl.find('#cancel-remove-ticket').on('click', function(event) {
                $tableElement.removeClass('active');
                popup.destroy();
            });
        });
        popup.show();
    }

    function showCabinetSection(sectionSelectorToShow) {
        var sectionToShow = $(sectionSelectorToShow);
        if (sectionToShow.hasClass('block-hidden')) {
            var notHidden = $('.profile-workplace').not('.block-hidden');
            notHidden.animate({
                opacity: 'toggle'
            }, 200, function() {
                notHidden.addClass('block-hidden');
                sectionToShow.removeClass('block-hidden');
                sectionToShow.animate({
                    opacity: 'toggle'
                }, 300);
            });
        }

    }

    $(document).on('ready', function() {
        var $content = $('.user-board');
        var minHeight = $( window ).height() - $('footer').outerHeight() - $('header').outerHeight();
        $content.css('min-height', minHeight + 'px');
        if (!isAuthorized()) {
            var popup = new Popup(clientBundle.error, uc.suggestRegistration(), '', function(popupId){
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

        var $tablesaw = $('.history-table>table');
        var $tableBody = $('.history-table>table>tbody');
        var pager = new Pager($('.tablesaw-pager'), "allTickets", function(data){
            $tableBody.empty();

            var tickets = data['objects'];
            var isPartner = data['isPartner'];
            for (var i = 0, size = tickets.length; i < size; i++) {
                var ticket = tickets[i];
                $tableBody.append(uc.ticketRecordTemplate(ticket['id'], ticket['passenger'], ticket['phones'], ticket['trip'], ticket['startDate'], ticket['seat'], ticket['status'], isPartner));
            }
            reinitTablesaw($tablesaw);
        });

        $tablesaw.on('click', 'a.action-glyph-link', function (ev) {
            ev.preventDefault();
            var $targetTr = $(this).parents('tr');
            var $actionElement = $(this).find('.glyphicon');
            var ticketId = $targetTr.attr('id');

            if ($actionElement.hasClass('glyphicon-calendar')) {
                changeTicketDate($targetTr, ticketId, function(currentTicket) {
                    $targetTr.find('.ticket-trip').text(currentTicket.trip);
                    $targetTr.find('.ticket-date').text(currentTicket.startDate);
                    $targetTr.find('.ticket-seat').text(currentTicket.seat);
                    $targetTr.attr('id', currentTicket.id);
                    pager.onObjectIdChanged(ticketId, currentTicket.id);
                    reinitTablesaw($tablesaw);
                });
            } else if ($actionElement.hasClass('glyphicon-trash')) {
                removeTicket($targetTr, ticketId, function() {
                    $targetTr.remove();
                    pager.clear();
                    pager.load(1, function() {
                        reinitTablesaw($tablesaw);
                    });
                });
            }
            return false;
        });

        pager.init();

        EventBus.addEventListener('user_loggedout', function(event) {
            pager.clear();
        });

        EventBus.addEventListener('user_authorized', function(event) {
            pager.init();
        });

        var $menu = $('.profile-menu-list');
        var $orderHistory = $('#order-history');
        var $personalData = $('#personal-data');
        var $cabinetLogout = $('#cabinet-logout')
        $orderHistory.on('click tap', function(eve){
            eve.preventDefault();
            showCabinetSection('#order-history-block');
            $menu.find('.active').removeClass('active');
            $orderHistory.addClass('active');

            return false;
        });

        $personalData.on('click tap', function(eve){
            eve.preventDefault();
            showCabinetSection('#personal-info-block');
            $menu.find('.active').removeClass('active');
            $personalData.addClass('active');
            return false;
        });

        $cabinetLogout.on('click tap', function(eve){
            eve.preventDefault();
            $menu.find('.active').removeClass('active');
            $cabinetLogout.addClass('active');
            window.logout(function(){
                window.location.href = '/';
            });

            return false;
        });
        var $passChangeBlock = $('.password-change-block');
        //$('#password-change').on('click tap', function(eve){
        //    eve.preventDefault();
            //var popup = new Popup(clientBundle.password_change, window.uc.changePassTemplate, '', function(popupId) {
        $passChangeBlock.find('#password-change').click(function(event) {
            event.preventDefault();
            //var popupEl = $(popupId);
            var $old = $passChangeBlock.find('#user-password-old');
            var oldVal = $old.val();
            var $new = $passChangeBlock.find('#user-password');
            var newVal = $new.val();
            var isOldPassValid = /^[0-9a-zA-Z]{8,}$/.test(oldVal);
            var isNewPassValid = /^[0-9a-zA-Z]{8,}$/.test(newVal);
            if (!isOldPassValid || !isNewPassValid) {
                new Message(clientBundle.password_should_be_at_least_8_symbols_long, 5000);
                return;
            }

            new Request("changePass", {oldPass: oldVal, newPass: newVal}).send(function(data) {
                if (data && data['newPass']) {
                    //popup.destroy();
                    if ($old.hasClass('input-error')) $old.removeClass('input-error');
                    $old.val('');
                    $new.val('');
                    new Popup(clientBundle.password_change, '<div>'+clientBundle.password_was_changed_successfully+'</div>','').show();
                } else {
                    $old.addClass('input-error');
                    new Message(clientBundle.old_password_is_not_correct, 5000);
                }
            });
            return false;
        });

            //});
            //popup.show();
            //return false;
        //});

        $('#buy-ticket').click(function() {window.location.assign = '/';});

        $('.feedback-circle').on('click tap', function() {
            new Popup(clientBundle.feedback, window.uc.feedbackForm, '', function(popupId) {
                var popup = this;
                $('#btn-submit-feedback').click(function() {
                    var value = $('#feedback-text').val();
                    if (!value) {
                        return false;
                    }
                    new Request('postFeedback', {feedback: value}).send(function(){
                        popup.destroy();
                        new Popup(clientBundle.feedback, '<div>'+clientBundle.thanks_for_posting_feedback+'</div>','').show();
                    });
                });
            }).show();
        });
    });

    window.uc.feedbackForm =
        '<div class="forms">' +
        '<label><textarea id="feedback-text" rows="6" placeholder="'+clientBundle.your_feedback+'"></textarea></label>' +
        '<div class="clear-both">' +
        '<button id="btn-submit-feedback" class="btn btn-left btn-green">'+clientBundle.submit+'</button>' +
        '</div>' +
        '</div>';

    window.uc.changePassTemplate =
        '<div class="forms">' +
        '<label><input type="password" name="password" id="user-password-old" placeholder="'+clientBundle.old_password+'"></label>' +
        '<label><input type="password" name="password" id="user-password" placeholder="'+clientBundle.new_one+'"></label>' +
        '<div class="clear-both">' +
        '<button id="btn-change-pass" class="btn btn-left btn-green">'+clientBundle.change_it+'</button>' +
        '</div>' +
        '</div>';

    window.uc.ticketRecordTemplate = function(ticketId, passenger, phones, trip, startDate, seat, status, isPartner) {
        return '<tr id="'+ticketId+'">' +
                    '<td>'+passenger+'</td>' +
                    '<td>'+phones+'</td>' +
                    '<td class="ticket-trip">'+trip+'</td>' +
                    '<td class="ticket-date">'+startDate+'</td>' +
                    '<td class="ticket-seat">'+seat+'</td>' +
                    '<td>'+status+'</td>' +
                    '<td>' +
                        (isPartner?'<a class="action-glyph-link" href="#"><span class="glyphicon glyphicon-calendar" title="'+clientBundle.change_date+'" aria-hidden="true"></span></a>' : '') +
                        '<a class="action-glyph-link" href="#"><span class="glyphicon glyphicon-trash" title="'+clientBundle.delete_ticket+'" aria-hidden="true"></span></a>' +
                    '</td>' +
                '</tr>';
    };

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
        return '<div>'+clientBundle.choose_trip_and_seat+'</div><div class="forms"><label><select id="avail-trips">'+selectOpts+'</select></label><label><select id="avail-seats">'+selectSeatsOpts+'</select></label>' +
            '<div class="clear-both"><button id="do-change" class="btn btn-left btn-green">'+clientBundle.change_it+'</button></div></div>';
    };

    window.uc.removeTicketConfirmation = function() {
        return '<div class="popup-text-block">'+clientBundle.this_action_will_delete_the_selected_ticket+'<br>'+clientBundle.are_you_sure_you_want_to_continue+'</div>' +
                    '<div class="clear-both">' +
                        '<button id="do-remove-ticket" class="btn btn-left btn-green">'+clientBundle.delete_ticket+'</button>' +
                        '<button id="cancel-remove-ticket" class="btn btn-left btn-green">'+clientBundle.cancel+'</button>' +
                    '</div>';
    };

    window.uc.suggestRegistration = function() {
        return '<div>'+clientBundle.you_are_not_authorized_you_can+' <a href="#" id="sug-login" class="italic">'+clientBundle.login+'</a> '+clientBundle.or+' <a href="#" id="sug-register" class="italic">'+clientBundle.register+'</a></div>';
    };

    function createSelectSeatsOpts(seats) {
        var selectSeatsOpts = '';
        for (var i = 0, size = seats.length; i < size; i++) {
            var seat = seats[i];
            selectSeatsOpts += '<option id="'+seat['id']+'">' + seat['seatNum'] + '</option>';
        }
        return selectSeatsOpts;
    }

})();

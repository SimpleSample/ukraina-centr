(function(){
    // variable to track state of chosen sities and order state
    var STATE = {
        startCity : null,
        endCity : null,
        bothDirections : false,
        currency : 'uah',
        order : {
            tickets:{}
        }
    };


    function cleanSelect($select) {
        $select.empty();
        $select.append('<option></option>');
    }

    function getCitiesNotForCountry(country) {
        var result = [];
        var all = dataStore.getAll('City');
        var idx = 0;
        for (var i = 0; i < all.length; i++) {
            var city = all[i];
            if (city['country'] !== country) result[idx] = city;
            idx = result.length;
        }
        return result;
    }

    $(document).on('ready', function() {
        var isResultsBoardClickHandlerSet = false;
        var isTicketBoardClickHandlerSet = false;

        var $forthSelect = $('#forth-city');
        var $backSelect = $('#back-city');
        var $bothDirs = $('#both-dirs');
        var $backDate = $('#back-date');
        var $tickets = $('.tickets');
        var startCitySelectId = null;
        var endCitySelectId = null;
        var both = false;
        var cachedUsername = null;
        var cachedPhone = null;

        var currentTripResults = null;

        var discounts = null;
        // 1. filling back and forth cities options
        new Request('allCities').send(function(data) {
            var allCities = data['cities'];
            for(var i = 0; i < allCities.length; i++) {
                var city = allCities[i];
                $forthSelect.append('<option id="f'+city.id+'">'+city.name+'</option>');
                $backSelect.append('<option id="b'+city.id+'">'+city.name+'</option>');
                dataStore.set(city, "City");
            }
        });

        var forthDateWidget = getDateField($('#forth-date'), function(date) {
            if (date.getTime() > backDateWidget.getDate().getTime()) {
                backDateWidget.setDate(new Date(date.getTime()+24*60*60*1000));
            }
        });
        var backDateWidget = getDateField($backDate, function(date){
            if (date.getTime() < forthDateWidget.getDate().getTime()) {
                forthDateWidget.setDate(new Date());
            }
        });

        $forthSelect.on('change', function(event) {
            var $this = $(this);
            var id = $this.find(":selected").length === 0? null :
                        $($this.find(":selected")).attr('id').substring(1);
            var startCity = {};
            if (id != null) {
                startCitySelectId = id;
                startCity = dataStore.get(id);
            }
            var backId = null;
            if ($backSelect.val() !== '') {
                backId = $($backSelect.find(":selected")).attr('id').substring(1);
                var backCity = dataStore.get(backId);
                if (id != null && startCity['country'] !== backCity['country']) return;
            }
            var otherCities = getCitiesNotForCountry(startCity['country']);
            cleanSelect($backSelect);
            for (var i = 0; i < otherCities.length; i++) {
                var city = otherCities[i];
                var option = $('<option id="b'+ city.id +'">'+city.name+'</option>');
                if (backId && city.id === backId) option.attr('selected', 'selected');
                $backSelect.append(option);
            }
        });

        $backSelect.on('change', function() {
            var $this = $(this);
            var id = $this.find(":selected").length === 0? null :
                $($this.find(":selected")).attr('id').substring(1);
            if (id != 0) endCitySelectId = id;
        });

        $bothDirs.on('change', function(event) {
            if ($(this).is(':checked')) {
                both = true;
                $backDate.removeAttr('disabled');
            } else {
                both = false;
                $backDate.attr('disabled','disabled');
            }
        });

        var currentForthTrip = null;
        var currentBackTrip = null;
        var isBackChanged = false;
        var chosenSeats = {forth:[], back:[]}; //seatIds

        $('#search-trips').on('click', function(event) {
            event.preventDefault();
            // checking input values
            if ($forthSelect.val() === '' || $backSelect.val() === '') {
                new Message("Одне з міст не вибране", 7000);
                return false;
            }

            var $orderBoard = $('.order-board');

            //cleaning up from previous search
            $orderBoard.removeClass('board-wide');

            STATE.startCity = dataStore.get(startCitySelectId);
            STATE.endCity = dataStore.get(endCitySelectId);
            STATE.bothDirections = both;

            var data = {
                startCityId: STATE.startCity.id,
                endCityId: STATE.endCity.id,
                startDate: forthDateWidget.getDate().getTime(),
                tzOffset: new Date().getTimezoneOffset()/60*(-1)
            };
            if (STATE.bothDirections) {
                data['endDate'] = backDateWidget.getDate().getTime();
            }

            new Request('search', data).send(function(data) {
                currentTripResults = data;
                var $forthDir = $('#forth-direction');
                var $backDir = $('#back-direction');
                var $division = $('#division');
                if (!data['forthTrips']) {
                    $forthDir.hide();
                    $division.hide();
                    $orderBoard.hide();
                    new Message("Не знайдено жодного квитка для даної дати", 10000);
                    return;
                }
                if (STATE.bothDirections && !data['backTrips']) STATE.bothDirections = false;

                $forthDir.empty();
                $tickets.empty();
                $backDir.empty();
                var tripStr = STATE.startCity.name + ' - ' + STATE.endCity.name;
                $forthDir.append($(uc.tripGroupTemplate(tripStr)));
                var $trips = $forthDir.find('.trips');
                var responseForthTrips = data['forthTrips'];
                for (var i = 0, size = responseForthTrips.length; i < size; i++) {
                    var trip = responseForthTrips[i];
                    trip.isForth = true;
                    $trips.append($(uc.tripTemplate(trip)));
                    dataStore.set(trip, 'Trip');
                    var seats = trip['seats'];
                    for (var j = 0; j < seats.length; j++) {
                        var seat = seats[j];
                        if (!dataStore.get(seat.id)) dataStore.set(seat, 'Seat');
                    }
                }
                $forthDir.show();
                $division.show();

                if (data['backTrips']) {
                    var backTripStr = STATE.endCity.name + ' - ' + STATE.startCity.name;
                    $backDir.append($(uc.tripGroupTemplate(backTripStr)));
                    var $backTrips = $backDir.find('.trips');
                    var responseBackTrips = data['backTrips'];
                    for (var i = 0, size = responseBackTrips.length; i < size; i++) {
                        var trip = responseBackTrips[i];
                        trip.isForth = false;
                        $backTrips.append($(uc.tripTemplate(trip)));
                        dataStore.set(trip, 'Trip');
                    }
                    $backDir.show();
                } else {
                    $backDir.hide();
                }
                $orderBoard.show();

                discounts = currentTripResults['discounts'];
                for (var i = 0, size = discounts.length; i < size; i++) {
                    dataStore.set(discounts[i], "Discount");
                }

                if (!isResultsBoardClickHandlerSet) {
                    var $ticketBoard  = $('.ticket-board');

                    $forthDir.on('click', function(event) {
                        event.preventDefault();
                        var $forthTripEl = $(event.target);
                        if ($forthTripEl.prop('tagName') !== 'A' && $ticketBoard.find('.ticket').length === 0) {
                            return false;
                        }
                        if ($forthTripEl.prop('tagName') === 'A' && !isAuthorized()) {
                            new Message('Для здійснення операції авторизуйтесь, будь ласка', 10000);
                            return false;
                        }
                        if(!$forthTripEl.hasClass('trip')) {
                            $forthTripEl = $forthTripEl.parents('.trip');
                            if(!$forthTripEl.length === 0) return false;
                        }
                        var forthTripId = $forthTripEl.attr('id');
                        $forthDir.find('.trip.active').removeClass('active');
                        $forthTripEl.addClass('active');

                        currentForthTrip = dataStore.get(forthTripId);
                        if(STATE.bothDirections) {
                            if (!currentBackTrip) {
                                currentBackTrip = currentTripResults['backTrips'][0];
                                isBackChanged = true;
                            }
                            //adding 'active' class for back trip element
                            var $backTrip = $('#'+currentBackTrip.id);
                            if (!$backTrip.hasClass('active')) $backTrip.addClass('active');
                        }

                        var forthTripCities = STATE.startCity.name+' - '+STATE.endCity.name;
                        var backTripCities = currentBackTrip? STATE.endCity.name + ' - ' + STATE.startCity.name : null;

                        var forthTripDate = currentForthTrip['startDate'];
                        var backTripDate = currentBackTrip? currentBackTrip['startDate'] : null;
                        var backTripId = currentBackTrip? currentBackTrip.id : null;

                        if ($(event.target).prop('tagName') === 'A') {
                            var forthSeat = getNextFreeSeatForTrip(currentForthTrip, true);
                            if (!forthSeat) {
                                new Message("Для поїздки відсутні вільні місця", 10000);
                                return false;
                            }

                            var backSeat = currentBackTrip? getNextFreeSeatForTrip(currentBackTrip, false) : null;
                            if (currentBackTrip && !backSeat) {
                                new Message("Для зворотньої поїздки відсутні вільні місця", 10000);
                                return false;
                            }

                            var ticketTempl = uc.ticketTemplate(getUsername(), getPhone(), currentForthTrip['price'], currentForthTrip['discPrice'], currentForthTrip.id, forthTripCities,
                                forthTripDate, forthSeat,backTripId, backTripCities, backTripDate, backSeat, discounts);
                            $('.ticket.active').toggleClass('active');
                            $ticketBoard.show();
                            if (!$orderBoard.hasClass('board-wide')) {
								$tickets.empty();
								$orderBoard.addClass('board-wide');
							}
                            $tickets.append(ticketTempl);
                            var $ticketsChilds = $tickets.children();
                            var $thisTicket = $($ticketsChilds[$ticketsChilds.length-1]);
                            STATE.order.tickets[$thisTicket.attr('id')] = {price : currentForthTrip['price'], discPrice: currentForthTrip['discPrice'], discount: dataStore.get("NONE")};
                            setDiscountClickHandler($thisTicket);
                            if (!isTicketBoardClickHandlerSet) {
                                setTicketBoardClickHandler();
                                setAddTicketHandler();
                                setRemoveTicketHandler();
                                setReserveTicketHandler();
                                isTicketBoardClickHandlerSet = true;
                            }
                        } else {
                            chosenSeats.forth = [];
                            if (isBackChanged) chosenSeats.back = [];
                            $ticketBoard.find('.ticket').each(function(index, element){
                                var $element = $(element);
                                var newSeat = getNextFreeSeatForTrip(currentForthTrip, true);
                                if (!newSeat) {
                                    $element.remove(); return;
                                }
                                $element.find('.forth-info-trip').text(forthTripCities);
                                $element.find('.forth-info-date').text(forthTripDate);
                                $element.find('.forth-trip-seat').text(newSeat['seatNum']);
                                $element.find('.forth-trip-seat').attr('id', newSeat['id']);
                                if (isBackChanged) {
                                    var newBackSeat = getNextFreeSeatForTrip(currentBackTrip, false);
                                    if (!newBackSeat) {
                                        $element.remove(); return;
                                    }
                                    $element.find('.back-info-trip').text(backTripCities);
                                    $element.find('.back-info-date').text(backTripDate);
                                    $element.find('.back-trip-seat').text(newBackSeat['seatNum']);
                                    $element.find('.back-trip-seat').attr('id',newBackSeat['id']);
                                }
                            });
                        }
                        isBackChanged = false;
                    });

                    $backDir.on('click', function(event) {
                        event.preventDefault();
                        var $backTripEl = $(event.target);
                        if ($backTripEl.hasClass('disabled')) return false;
                        if ($ticketBoard.find('.ticket').length === 0) return false;

                        if(!$backTripEl.hasClass('trip')) {
                            $backTripEl = $backTripEl.parents('.trip');
                            if($backTripEl.length === 0) return false;
                        }

                        var backTripId = $backTripEl.attr('id');
                        if (currentBackTrip && currentBackTrip.id === backTripId) return false;

                        currentBackTrip = dataStore.get(backTripId);
                        $backDir.find('.trip.active').removeClass('active');
                        if (!$backTripEl.hasClass('active')) $backTripEl.addClass('active');

                        var backTripCities = currentBackTrip? STATE.endCity.name + ' - ' + STATE.startCity.name : null;
                        var backTripDate = currentBackTrip? currentBackTrip['startDate'] : null;
                        chosenSeats.back = [];
                        $ticketBoard.find('.ticket').each(function(index, element){
                            var $element = $(element);
                            var newBackSeat = getNextFreeSeatForTrip(currentBackTrip, false);
                            if (!newBackSeat) return;
                            $element.find('.back-info-trip').text(backTripCities);
                            $element.find('.back-info-date').text(backTripDate);
                            $element.find('.back-trip-seat').text(newBackSeat['seatNum']);
                            $element.find('.back-trip-seat').attr('id',newBackSeat['id']);
                        });
                    });
                    isResultsBoardClickHandlerSet = true;
                }

            });

            return false;
        });

        function getUsername() {
            var fromCookies = window.cookies.get('name');
            if (fromCookies) cachedUsername = fromCookies;
            var usernameInput = $('.ticket-input.username')[0];
            var enteredUsername = usernameInput? $(usernameInput).val() : null;
            if (!StringUtils.isEmpty(enteredUsername)) cachedUsername = enteredUsername;
            return cachedUsername;
        }

        function getPhone() {
            var fromCookies = window.cookies.get('phone');
            if (fromCookies) cachedPhone = fromCookies;
            var phoneInput = $('.ticket-input.phone')[0];
            var enteredPhone = phoneInput? $(phoneInput).val() : null;
            if (!StringUtils.isEmpty(enteredPhone)) cachedPhone = enteredPhone;
            return cachedPhone;
        }

        function getNextFreeSeatForTrip(trip, isForth) {
            var seats = trip['seats'];
            var reservedSeats = isForth? chosenSeats.forth : chosenSeats.back;
            for (var i = 0; i < seats.length; i++) {
                if (reservedSeats.indexOf(seats[i].id) < 0) {
                    reservedSeats[reservedSeats.length] = seats[i].id;
                    return seats[i];
                }
            }

            return null;
        }

        function setTicketBoardClickHandler() {
            var $tickets = $('.tickets');
            $tickets.on('click', function(event){
                event.preventDefault();
                var $target = $(event.target);
                if ($target.prop('tagName') === 'BUTTON') {
                    var trip = $target.hasClass('forth')? currentForthTrip : currentBackTrip;
                    var locked = trip.isForth? chosenSeats.forth : chosenSeats.back;
                    showBusSeatsPopup($target, trip.id, trip['seats'], locked);
                } else {
                    var $ticket = $target.parents('.ticket');
                    if (!$ticket.hasClass('active')) {
                        $('.ticket.active').toggleClass('active');
                        $ticket.addClass('active');
                    }
                }
            });
        }

        function setAddTicketHandler() {
            $('#add-ticket').on('click', function(event) {
                var forthSeat = getNextFreeSeatForTrip(currentForthTrip, true);
                var backSeat = currentBackTrip? getNextFreeSeatForTrip(currentBackTrip, false) : null;

                if (!forthSeat || (currentBackTrip && !backSeat)) {
                    new Message("Для поїздки відсутні вільні місця", 10000);
                    return;
                }

                var forthTripCities = STATE.startCity.name+' - '+STATE.endCity.name;
                var backTripCities = currentBackTrip? STATE.endCity.name + ' - ' + STATE.startCity.name : null;

                var forthTripDate = currentForthTrip['startDate'];
                var backTripDate = currentBackTrip? currentBackTrip['startDate'] : null;
                var backTripId = currentBackTrip? currentBackTrip.id : null;
                var ticketTempl = uc.ticketTemplate(getUsername(), getPhone(), currentForthTrip['price'], currentForthTrip['discPrice'], currentForthTrip.id, forthTripCities,
                    forthTripDate, forthSeat,backTripId, backTripCities, backTripDate, backSeat, discounts);
                $('.ticket.active').toggleClass('active');
                var $ticketsDiv = $('.tickets');
                $ticketsDiv.append(ticketTempl);
                var $tickets = $ticketsDiv.children();
                var $thisTicket = $($tickets[$tickets.length-1]);

                STATE.order.tickets[$thisTicket.attr('id')] = {price : currentForthTrip['price'], discPrice: currentForthTrip['discPrice'], discount: dataStore.get("NONE")};
                setDiscountClickHandler($thisTicket);
            });
        }

        function setRemoveTicketHandler() {
            $('#delete-ticket').on('click', function(event) {
                var $active = $('.ticket.active');
                var $seatEl = $active.find('.forth').find('button').find('span');
                var $backSeatEl = $active.find('.back').length === 0? null : $active.find('.back').find('button').find('span');

                var idx = chosenSeats.forth.indexOf($seatEl.attr('id'));
                chosenSeats.forth.splice(idx, 1);

                if ($backSeatEl) {
                    var backIdx = chosenSeats.back.indexOf($backSeatEl.attr('id'));
                    chosenSeats.back.splice(backIdx, 1);
                }
                $active.remove();
                var first = $('.tickets').children()[0];
                if (!first) {
                    $('.order-board.board-wide').removeClass('board-wide');
                } else {
                    $(first).addClass('active');
                    $(first).removeClass('card');
                }
            });
        }

        function showBusSeatsPopup(targetButton, tripId, allSeats, lockedSeats) {
            var choice = targetButton.find('span').attr('id');

            var popup = new Popup('Схема автобуса', setraBus, 'popup-bus', function(popupId){
                for (var i = 0; i < allSeats.length; i++) {
                    var seat = allSeats[i];
                    var $current = $('#ticket-'+seat['seatNum']);
                    $current.attr('seatId', seat.id);
                    if (lockedSeats.indexOf(seat.id) >= 0) continue;

                    $current.removeClass('blocked');
                }
                var $choice = $('[seatid='+choice+']');
                $choice.removeClass('blocked');
                $choice.addClass('active');

                var $popupEl = $(popupId);
                $popupEl.find('.bus-setra').on('click', function(event){
                    var $target = $(event.target);
                    if ($target.prop('tagName') === 'A') {
                        choice = $target.attr('seatId');
                        if (lockedSeats.indexOf(choice) >= 0) return false;

                        var $prevActive = $popupEl.find('a.active');
                        $prevActive.removeClass('active');
                        var seatId = $prevActive.attr('seatId');
                        lockedSeats.splice(lockedSeats.indexOf(seatId), 1);
                        lockedSeats[lockedSeats.length] = $target.attr('seatId');
                        if (!$target.hasClass('active')) $target.addClass('active');
                    }
                });
                $popupEl.find('#bus-ticket-cancel').on('click', function(){
                    popup.destroy();
                });

                $popupEl.find('#bus-ticket-ok').on('click', function(){
                    var $span = targetButton.find('span');
                    var seat = dataStore.get(choice);
                    var prevTicketId = $span.attr('ticketId');
                    var data = {tripId: tripId, seatId: choice};
                    if (prevTicketId) data['unlockTicketId'] = prevTicketId;

                    new Request('lockTicket', data).send(function(data) {
                        $span.attr('ticketId', data['ticketId']);
                        $span.text(seat['seatNum']);
                        $span.attr('id', seat['id']);
                        popup.destroy();
                    });
                })
            });
            popup.show();
        }

        function setReserveTicketHandler() {
            $('#buy-tickets').on('click', function(event) {
                if (!isAuthorized()) {
                    new Message('Для здійснення операції авторизуйтесь, будь ласка', 10000);
                    return false;
                }
                var order = createOrder();
                if (order == null) return;
                order.type = 'RESERVE';

                new Request('order', {order: JSON.stringify(order)}).send(function(data) {
                    var $liqForm = $('#liq-form');
                    if (data && data['order_id']) {
                        for (var param in data) if (data.hasOwnProperty(param)) {
                            $liqForm.find('[name='+param+']').val(data[param]);
                        }
                        loader.show();
                        $liqForm.submit();
                    } else {
                        var p = new Popup('Квитки заброньовано', '<div>Ви успішно придбали квитки, підтвердження надіслано на вашу пошту</div><p><button id="reserve-ready" class="btn btn-green width-100">OK</button></p>', 'white', function(){
                            $('#reserve-ready').on('click', function(){
                                p.destroy();
                                window.location.reload();
                            });
                        });
                        p.show();
                    }
                });
            });
        }

        function setDiscountClickHandler($ticket) {
            var select = $ticket.find('.ticket-input.select').on('change', function(event) {
                var $this = $(this);
                var discountId = $this.find(":selected").attr('id').substring(9);
                var discount = dataStore.get(discountId);
                STATE.order.tickets[$ticket.attr('id')]['discount'] = discount;

                var $fullPrice = $ticket.find('.price-full .price-self');
                var $discPrice = $ticket.find('.price-disc .price-self');

                var fullPrice = STATE.order.tickets[$ticket.attr('id')]['price'];
                var price = STATE.order.tickets[$ticket.attr('id')]['discPrice'];
                var resultPrice = parseInt(StringUtils.isEmpty(fullPrice)? price : fullPrice);
                var result = calculatePrice(resultPrice, discount);
                if (STATE.currency == 'uah') {
                    resultPrice = CurrencyUtils.round(resultPrice * EURUAH, -1);
                    result = CurrencyUtils.round(result * EURUAH, -1);
                }
                $discPrice.text(result);
                $fullPrice.text(resultPrice);
                var $fullPriceDiv = $ticket.find('.price-full');
                if (resultPrice === result) {
                    $fullPriceDiv.addClass('hide');
                } else {
                    if ($fullPriceDiv.hasClass('hide')) $fullPriceDiv.removeClass('hide');
                }
            });

        }

        function calculatePrice(currentPrice, discount) {
            var result = 0;
            if (discount.type === 'SUBTRACT') {
                result = currentPrice - (discount.value*EURUAH);
            } else if (discount.type === 'MULTIPLY') {
                result = currentPrice - (currentPrice*discount.value/100);
            }

            return CurrencyUtils.round(result, -1);
        }

        function createOrder() {
            var order = {tickets:[]};
            var tickets = $('.tickets').children();
            order['forthTripId'] = currentForthTrip.id;
            if (currentBackTrip) order['backTripId'] = currentBackTrip.id;

            for (var i = 0; i < tickets.length; i++) {
                var ticket = {};
                var $ticket = $(tickets[i]);
                var username = $ticket.find('.ticket-input.username').val();
                if (StringUtils.isEmpty(username)) {
                    new Message('В одному з квитків не вказано імені і прізвища пасажира', 10000);
                    return null;
                }
                var phones = $ticket.find('.ticket-input.phone');
                var phone = $(phones[0]).val();
                var phone2 = $(phones[1]).val();
                if (StringUtils.isEmpty(phone) && StringUtils.isEmpty(phone2)) {
                    new Message('В одному з квитків не вказано телефону пасажира', 10000);
                    return null;
                }

                var discountId = $ticket.find('.ticket-input.select').find(':selected').attr('id').substring(9);
                ticket['discountId'] = discountId;

                ticket['passenger'] = username;
                ticket['phone1'] = phone;
                if (!StringUtils.isEmpty(phone2)) ticket['phone2'] = phone2;
                ticket['rawStartDate'] = currentForthTrip['rawStartDate'];
                if (currentBackTrip) ticket['rawBackStartDate'] = currentBackTrip['rawStartDate'];
                ticket['startCity'] = STATE.startCity.id;
                ticket['endCity'] = STATE.endCity.id;

                var $forthSeat = $ticket.find('.btn.forth span');
                var $backSeat = $ticket.find('.btn.back span');
                ticket['forthTicketId'] = $forthSeat.attr('ticketId');
                ticket['forthSeatNum'] = $forthSeat.text();
                ticket['forthSeatId'] = $forthSeat.attr('id');
                if ($backSeat[0]) {
                    ticket['backTicketId'] = $backSeat.attr('ticketId');
                    ticket['backSeatNum'] = $backSeat.text();
                    ticket['backSeatId'] = $backSeat.attr('id');
                }
                order.tickets[order.tickets.length] = ticket;
            }

            return order;

        }
        var locationOrderId = location.getParam('orderId');
        if (locationOrderId) {
            new Request('orderExists', {orderId: locationOrderId}).send(function(data) {
                if (data && data['orderId']) {
                    var p = new Popup('Квитки придбано', '<div>Ви успішно придбали квитки, підтвердження надіслано на вашу пошту</div><p><button id="reserve-ready" class="btn btn-green width-100">OK</button></p>', 'white', function(){
                        $('#reserve-ready').on('click', function(){
                            p.destroy();
                            location.assign(location.protocol + '//' + location.host)
                        });
                    });
                    p.show();
                } else {
                    location.assign(location.protocol + '//' + location.host)
                }
            })
        }

        //changing currency

        $('.change-currency').on('change', function(event) {
            var currencyId = $(this).find(':selected').attr('id');
            if (STATE.currency == currencyId) return;
            STATE.currency = currencyId;
            for (var ticket in STATE.order.tickets) {
                var $ticket = $('#'+ticket);
                var discount = STATE.order.tickets[ticket]['discount'];

                var $fullPrice = $ticket.find('.price-full .price-self');
                var $discPrice = $ticket.find('.price-disc .price-self');
                var $sign = $ticket.find('.price-disc .sign');
                $sign.text($(this).val());

                var fullPrice = STATE.order.tickets[ticket]['price'];
                var price = STATE.order.tickets[ticket]['discPrice'];
                var resultPrice = parseInt(StringUtils.isEmpty(fullPrice)? price : fullPrice);
                var result = calculatePrice(resultPrice, discount);
                if (STATE.currency == 'uah') {
                    resultPrice = CurrencyUtils.round(resultPrice * EURUAH, -1);
                    result = CurrencyUtils.round(result * EURUAH, -1);
                }
                $discPrice.text(result);
                $fullPrice.text(resultPrice);
            }
        });
    });
    //show in popup
    var setraBus = '<div><div class="bus-scheme bus-setra"><table><tbody><tr><td><a id="ticket-1a" class="blocked" href="#">1a</a></td><td><a id="ticket-1b" class="blocked" href="#" class="active">1b</a></td><td><span> </span></td><td><a id="ticket-1c" class="blocked" href="#">1c</a></td><td><a id="ticket-1d" class="blocked" href="#">1d</a></td></tr><tr><td><a id="ticket-2a" class="blocked" href="#" class="blocked">2a</a></td><td><a id="ticket-2b" class="blocked" href="#">2b</a></td><td><span> </span></td><td><a id="ticket-2c" class="blocked" href="#">2c</a></td><td><a id="ticket-2d" class="blocked" href="#">2d</a></td></tr><tr><td><a id="ticket-3a" class="blocked" href="#">3a</a></td><td><a id="ticket-3b" class="blocked" href="#">3b</a></td><td><span> </span></td><td><a id="ticket-3c" class="blocked" href="#">3c</a></td><td><a id="ticket-3d" class="blocked" href="#">3d</a></td></tr><tr><td><a id="ticket-4a" class="blocked" href="#">4a</a></td><td><a id="ticket-4b" class="blocked" href="#">4b</a></td><td><span> </span></td><td><a id="ticket-4c" class="blocked" href="#">4c</a></td><td><a id="ticket-4d" class="blocked" href="#">4d</a></td></tr><tr><td><a id="ticket-5a" class="blocked" href="#">5a</a></td><td><a id="ticket-5b" class="blocked" href="#">5b</a></td><td><span> </span></td><td><a id="ticket-5c" class="blocked" href="#">5c</a></td><td><a id="ticket-5d" class="blocked" href="#">5d</a></td></tr><tr><td><a id="ticket-6a" class="blocked" href="#">6a</a></td><td><a id="ticket-6b" class="blocked" href="#">6b</a></td><td><span> </span></td><td><a id="ticket-6c" class="blocked" href="#">6c</a></td><td><a id="ticket-6d" class="blocked" href="#">6d</a></td></tr><tr><td><a id="ticket-7a" class="blocked" href="#">7a</a></td><td><a id="ticket-7b" class="blocked" href="#">7b</a></td><td><span> </span></td><td><a id="ticket-7c" class="blocked" href="#">7c</a></td><td><a id="ticket-7d" class="blocked" href="#">7d</a></td></tr><tr><td><a id="ticket-8a" class="blocked" href="#">8a</a></td><td><a id="ticket-8b" class="blocked" href="#">8b</a></td><td><span> </span></td><td><span> </span></td><td><span> </span></td></tr><tr><td><a id="ticket-9a" class="blocked" href="#">9a</a></td><td><a id="ticket-9b" class="blocked" href="#">9b</a></td><td><span> </span></td><td><a id="ticket-9c" class="blocked" href="#">9c</a></td><td><a id="ticket-9d" class="blocked" href="#">9d</a></td></tr><tr><td><a id="ticket-10a" class="blocked" href="#">10a</a></td><td><a id="ticket-10b" class="blocked" href="#">10b</a></td><td><span> </span></td><td><a id="ticket-10c" class="blocked" href="#">10c</a></td><td><a id="ticket-10d" class="blocked" href="#">10d</a></td></tr><tr><td><a id="ticket-11a" class="blocked" href="#">11a</a></td><td><a id="ticket-11b" class="blocked" href="#">11b</a></td><td><span> </span></td><td><a id="ticket-11c" class="blocked" href="#">11c</a></td><td><a id="ticket-11d" class="blocked" href="#">11d</a></td></tr><tr><td><a id="ticket-12a" class="blocked" href="#">12a</a></td><td><a id="ticket-12b" class="blocked" href="#">12b</a></td><td><span> </span></td><td><a id="ticket-12c" class="blocked" href="#">12c</a></td><td><a id="ticket-12d" class="blocked" href="#">12d</a></td></tr><tr><td><a id="ticket-13a" class="blocked" href="#">13a</a></td><td><a id="ticket-13b" class="blocked" href="#">13b</a></td><td><span> </span></td><td><a id="ticket-13c" class="blocked" href="#">13c</a></td><td><a id="ticket-13d" class="blocked" href="#">13d</a></td></tr></tbody></table></div><div><div class="clear-both"><button id="bus-ticket-ok" class="btn btn-right btn-green">Обрати</button><button id="bus-ticket-cancel" class="btn btn-right btn-green">Відміна</button></div></div></div>';
    /*HTML Templates*/
    (function(){
        window.uc.tripGroupTemplate = function(trip) {
            return '<div class="direction">'+trip+'</div><div class="trips"></div>';
        };

        window.uc.tripTemplate = function(trip) {
            var tripId = trip.id;
            var startCity = trip['routeFirstCity'];
            var endCity = trip['routeEndCity'];
            var startDate = trip['startDate'];
            var endDate = trip['endDate'];
            var seatCount = trip['seats'].length;
            var isForth = trip.isForth;
            return '<div id='+tripId+' class="trip">' +
                '<div><h4>Рейс '+startCity+' - '+endCity+'</h4>' + '<span class="available-seats">'+seatCount+' місць</span></div>' +
                '<div class="clear-both">' +
                    '<div class="trip-info">' +
                        '<div><span>Відправлення:</span><span>'+startDate+'</span></div>' +
                        '<div><span>Прибуття:</span><span>'+endDate+'</span></div>' +
                    '</div>' +
                    '<div class="buy">'+(isForth? '<a href="#" class="get-ticket-btn">+ Квиток</a>': '')+'</div>' +
                '</div>' +
            '</div>'
        };

        window.uc.ticketTemplate = function(user, phone, price, discPrice, forthTripId, forthTrip, forthDate, forthSeat, backTripId, backTrip, backDate, backSeat, discounts){
            var resultPrice = discPrice? discPrice : price;
            var discountOptions = '';
            for (var i = 0, size = discounts.length; i < size; i++) {
                discountOptions += '<option id="discount-'+discounts[i].id+'" '+
                    (discounts[i].id==='NONE'? 'selected':'')+'>'+discounts[i].text+'</option>';
            }
            if (STATE.currency == 'uah') {
                resultPrice = CurrencyUtils.round(parseInt(resultPrice) * EURUAH, -1);
                if (discPrice) price = CurrencyUtils.round(parseInt(price) * EURUAH, -1);
            }
            var ticketsCount = $('.tickets').children().length;
            var clazz = 'ticket active'+(backTrip? '':' one-way')+ (ticketsCount === 0? '':' card');
            var start =
                '<div id="ticket'+ticketsCount+'" class="' +clazz+ '" style="z-index:'+(ticketsCount+5)+';">'+
                    '<div class="ticket-row">'+
                        '<input type="text" '+(user? 'value="'+user+'"':'')+' placeholder="Ім\'я і прізвище" class="ticket-input username">'+

                        '<div class="price">'+
                            '<div class="price-disc clear-both"><div class="price-right"><span class="sign">₴</span><span class="price-self">'+resultPrice+'</span></div></div>'+
                            '<div class="price-full '+(discPrice?'' : 'hide')+'"><span class="price-self">'+(discPrice? price : '')+'</span></div>'+
                        '</div>'+
                    '</div>'+
                    '<div class="ticket-row">' +
                        '<input type="text" '+(phone? 'value="'+phone+'"':'')+' placeholder="Телефон" class="ticket-input phone">' +
                        '<input type="text" '+(phone? 'value="'+phone+'"':'')+' placeholder="Телефон" class="ticket-input phone second">' +
                    '</div>' +
                    '<div class="ticket-row"><select class="ticket-input select">'+discountOptions+'</select></div>'+
                    '<div class="ticket-row clear-both forth">'+
                        '<div class="forth-info">' +
                            '<span class="forth-info-trip">'+forthTrip+'</span>' +
                            '<span class="forth-info-date">'+forthDate+'</span>'+
                        '</div>'+
                        '<button class="btn btn-green light forth">Місце #<span id="'+forthSeat.id+'" class="forth-trip-seat">'+forthSeat['seatNum']+'</span></button>'+
                    '</div>';
            if (!backTrip) return start + '</div>';

            return start +
                    '<div class="ticket-row clear-both back">'+
                        '<div class="back-info">' +
                            '<span class="back-info-trip">'+backTrip+'</span>' +
                            '<span class="back-info-date">'+backDate+'</span>'+
                        '</div>'+
                        '<button class="btn btn-green light back">Місце #<span id="'+backSeat.id+'" class="back-trip-seat">'+backSeat['seatNum']+'</span></button>'+
                    '</div>'+
                '</div>';
        }
    })();
})();
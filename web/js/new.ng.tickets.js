/**
 * Created by Nagorny on 20.04.2015.
 */

MiscUtils = {
    getTimezoneOffset: function() {
        return new Date().getTimezoneOffset()/60*(-1);
    },

    getSelectedId: function ($selectElement, startIdx) {
        return $($selectElement.find(":selected")).attr('id').substring(startIdx);
    },

    cleanSelect: function ($select) {
        $select.empty();
        $select.append('<option></option>');
    },

    getCitiesNotForCountry: function (country) {
        var result = [];
        var all = dataStore.getAll('City');
        var idx = 0;
        for (var i = 0; i < all.length; i++) {
            var city = all[i];
            if (city['country'] !== country) result[idx] = city;
            idx = result.length;
        }
        return result;
    },

    calculatePrice: function(currentPrice, discount) {
        var result = 0;
        if (discount.type === 'SUBTRACT') {
            var discValue = UserSettings.currency == 'uah'? discount.value*EURUAH : discount.value;
            result = currentPrice - discValue;
        } else if (discount.type === 'MULTIPLY') {
            result = currentPrice - (currentPrice*discount.value/100);
        }

        if (UserSettings.currency == 'uah') {
            result = CurrencyUtils.round(result, -1);
        }
        return result;
    }
};

(function() {
    function getCleanState() {
        return {
            startCity: null,
            endCity: null,
            forthDate: 0,
            backDate: 0,
            bothDirections: false,

            forthTrips: [],
            selectedForthTrip: null,

            backTrips: [],
            selectedBackTrip: null,

            currency: 'uah',
            order: {
                tickets: {}
            }
        }
    }
    // variable to track state of chosen cities and order state
    var State = getCleanState();

    window.UserSettings = {
        lang: 'ua',
        currency: 'uah',
        currencySign: '₴'
    };

    var stateInitialized = false;
    window.StateManager = {

        init : function () {
            if (stateInitialized) return;

            stateInitialized = true;
            if(typeof(Storage) !== "undefined") {
                var stateStr = localStorage.getItem("state");
                if (stateStr) {
                    // TODO use this state to init page
                    this.state = JSON.parse(stateStr);
                    localStorage.removeItem("state");
                }

                var userSettingsStr = localStorage.getItem('userSettings');
                if (userSettingsStr) {
                    UserSettings = JSON.parse(userSettingsStr);
                    localStorage.removeItem("userSettings");
                }
            }

            $(window).unload(function() {
                localStorage.setItem('state', JSON.stringify(State));
                localStorage.setItem('userSettings', JSON.stringify(UserSettings));
            });
        },

        clearState: function() {
            State = getCleanState();
        },

        setStartCity: function(startCity) {
            State.startCity = startCity;
        },

        getStartCity: function() {
            return State.startCity;
        },

        setEndCity: function(endCity) {
            State.endCity = endCity;
        },

        getEndCity: function() {
            return State.endCity;
        },

        setForthDate: function(forthDate) {
            State.forthDate = forthDate;
        },

        getForthDate: function() {
            return State.forthDate;
        },

        setBackDate: function(backDate) {
            State.backDate = backDate;
        },

        getBackDate: function() {
            return State.backDate;
        },

        setBothDirections: function(bothDirections) {
            State.bothDirections = bothDirections;
        },

        getBothDirections: function() {
            return State.bothDirections;
        },

        setForthTrips: function(forthTrips) {
            State.forthTrips = forthTrips;
        },

        getForthTrips: function() {
            return State.forthTrips;
        },

        setBackTrips: function(backTrips) {
            State.backTrips = backTrips;
        },

        getBackTrips: function() {
            return State.backTrips;
        },

        setSelectedForthTrip: function(selectedForthTrip) {
            State.selectedForthTrip = selectedForthTrip;
        },

        getSelectedForthTrip: function() {
            return State.selectedForthTrip;
        },

        setSelectedBackTrip: function(selectedBackTrip) {
            State.selectedBackTrip = selectedBackTrip;
        },

        getSelectedBackTrip: function() {
            return State.selectedBackTrip;
        },

        addTicket: function(ticket) {
            State.order.tickets[ticket.id] = ticket;
        },

        removeTicket: function(ticketId) {
            delete State.order.tickets[ticketId];
        },

        getTicket: function(ticketId) {
            return State.order.tickets[ticketId];
        },

        getOrder: function() {
            return State.order;
        }
    };

    window.SelectionBoard = function ($selectionBoard) {
        this.$startCitySelect = $selectionBoard.find('#forth-city');
        this.$endCitySelect = $selectionBoard.find('#back-city');
        this.$forthDatePicker = $selectionBoard.find('#forth-date');
        this.$backDatePicker = $selectionBoard.find('#back-date');
        this.$bothDirCombobox = $selectionBoard.find('#both-dirs');

        this.orderBoard = new OrderBoard($('.order-board'));

        // todo mb no need
        this.currentStartCityId = null;
        this.currentEndSityId = null;
        this.currentForthDate = null;
        this.currentBackDate = null;
        this.currentBackDirection = false;
    };

    SelectionBoard.prototype = {
        constructor : SelectionBoard,

        init : function() {
            var that = this;
            // 1. filling start and end cities options
            new Request('allCities').send(function(data) {
                var allCities = data['cities'];
                for(var i = 0; i < allCities.length; i++) {
                    var city = allCities[i];
                    that.$startCitySelect.append('<option id="f'+city.id+'">'+city.name+'</option>');
                    that.$endCitySelect.append('<option id="b'+city.id+'">'+city.name+'</option>');
                    dataStore.set(city, "City");
                }

                that.$startCitySelect.on('change', function() {
                    var id = that.$startCitySelect.val()? MiscUtils.getSelectedId(that.$startCitySelect, 1) : null;
                    var newStartCity = id? dataStore.get(id) : null;
                    that.onStartCityChange(newStartCity);
                });

                that.$endCitySelect.on('change', function() {
                    var id = that.$endCitySelect.val()? MiscUtils.getSelectedId(that.$endCitySelect, 1) : null;
                    var newEndCity = id? dataStore.get(id) : null;
                    that.onEndCityChange(newEndCity);
                });

                that.$bothDirCombobox.on('change', function(event) {
                    that.currentBackDirection = $(this).is(':checked');
                    if (that.currentBackDirection) {
                        that.$backDatePicker.removeAttr('disabled');
                    } else {
                        that.$backDatePicker.attr('disabled','disabled');
                    }
                });
            });

            var forthDateWidget = getDateField(this.$forthDatePicker, function(date) {
                that.currentForthDate = date.getTime();
                if (date.getTime() > backDateWidget.getDate().getTime()) {
                    backDateWidget.setDate(new Date(date.getTime()+24*60*60*1000));
                }
            });
            this.currentForthDate = forthDateWidget.getDate().getTime();

            var backDateWidget = getDateField(this.$backDatePicker, function(date) {
                that.currentBackDate = date.getTime();
                if (date.getTime() < forthDateWidget.getDate().getTime()) {
                    forthDateWidget.setDate(new Date());
                }
            });
            this.currentBackDate = backDateWidget.getDate().getTime();

            $('#search-trips').click(function(event) {
                event.preventDefault();
                if (!that.validateInputs()) return false;

                that.orderBoard.searchTrips(that.currentStartCityId,
                                            that.currentEndSityId,
                                            that.currentForthDate,
                            that.currentBackDirection? that.currentBackDate : null,
                                            that.currentBackDirection);

            });
        },

        onStartCityChange: function(newStartCity) {
            this.currentStartCityId = newStartCity.id;

            if (this.currentEndSityId) {
                var backCity = dataStore.get(this.currentEndSityId);
                if (newStartCity != null && newStartCity['country'] !== backCity['country']) return;
            }
            var otherCities = MiscUtils.getCitiesNotForCountry(newStartCity['country']);
            MiscUtils.cleanSelect(this.$endCitySelect);
            for (var i = 0; i < otherCities.length; i++) {
                var city = otherCities[i];
                var option = $('<option id="b'+ city.id +'">'+city.name+'</option>');
                if (this.currentEndSityId && city.id === this.currentEndSityId) {
                    option.attr('selected', 'selected');
                }
                this.$endCitySelect.append(option);
            }
        },

        onEndCityChange: function (newEndCity) {
            this.currentEndSityId = newEndCity.id;
        },

        validateInputs: function() {
            // checking input values
            if (!this.currentStartCityId || !this.currentEndSityId) {
                new Message("Одне з міст не вибране", 7000);
                return false;
            }
            return true;
        }
    };

    window.OrderBoard = function ($orderBoard) {
        this.$orderBoard = $orderBoard;
        this.$division = $('#division');

        this.ticketsBoard = new TicketsBoard();
        this.tripsBoard = new TripsBoard(this.ticketsBoard);

        var that = this;
        EventBus.addEventListener('ticket_added', function(event) {
            if (that.ticketsBoard.getTicketsSize() == 1) {
                that.$orderBoard.addClass('board-wide');
            }
        });
        EventBus.addEventListener('before_ticket_removed', function(event) {
            if (that.ticketsBoard.getTicketsSize() == 1) {
                that.$orderBoard.removeClass('board-wide');
            }
        });
    };

    OrderBoard.prototype = {
        constructor : OrderBoard,

        searchTrips: function (startCityId, endCityId, forthDate, backDate, bothDirections) {
            StateManager.setStartCity(dataStore.get(startCityId));
            StateManager.setEndCity(dataStore.get(endCityId));
            StateManager.setForthDate(forthDate);
            StateManager.setBackDate(backDate);
            StateManager.setBothDirections(bothDirections);

            var that = this;

            var data = {
                startCityId: startCityId,
                endCityId: endCityId,
                startDate: forthDate,
                tzOffset: MiscUtils.getTimezoneOffset()
            };
            if (bothDirections) {
                data['endDate'] = backDate;
            }

            new Request('search', data).send(function(data) {
                that.clean();
                if (!data['forthTrips']) {
                    that.$orderBoard.hide();
                    new Message("Не знайдено жодного квитка для даної дати", 10000);
                    return;
                }
                that.$division.show();
                if (StateManager.getBothDirections() && !data['backTrips']) {
                    StateManager.setBothDirections(false);
                }

                that.tripsBoard.setTrips(data['forthTrips'], data['backTrips']);
                that.ticketsBoard.setDiscounts(data['discounts']);

                that.tripsBoard.drawLayout();
                that.$orderBoard.show();
            });
        },

        clean: function() {
            this.tripsBoard.clean();
            this.ticketsBoard.clean();
        }
    };

    window.TripsBoard = function (ticketsBoard) {

        this.ticketsBoard = ticketsBoard;
        this.trips = {};

        this.$forthTrips = $('#forth-trips');
        this.$backTrips = $('#back-trips');

        this.currentForthTrip = null;
        this.currentBackTrip = null;

        this.init();
    };

    TripsBoard.prototype = {
        constructor : TripsBoard,

        init: function() {
            var that = this;
            this.$forthTrips.on('click', function(event) {
                event.preventDefault();
                var $forthTripEl = $(event.target);
                if ($forthTripEl.prop('tagName') !== 'A' && that.ticketsBoard.hasNoTickets()) {
                    return false;
                }
                if ($forthTripEl.prop('tagName') === 'A' && !isAuthorized()) {
                    new Message('Для здійснення операції авторизуйтесь, будь ласка', 10000);
                    return false;
                }
                if (!$forthTripEl.hasClass('trip')) {
                    $forthTripEl = $forthTripEl.parents('.trip');
                    if (!$forthTripEl.length === 0) return false;
                }

                that.setTripElementActive($forthTripEl);
                that.currentForthTrip = dataStore.get($forthTripEl.attr('id'));
                var isInitial = false;
                if(StateManager.getBothDirections() && !that.currentBackTrip) {
                    that.currentBackTrip = StateManager.getBackTrips()[0];
                    that.setTripElementActive($('#'+that.currentBackTrip.id));
                    isInitial = true;
                }

                if (!StateManager.getSelectedForthTrip() || that.currentForthTrip.id != StateManager.getSelectedForthTrip().id) {
                    that.ticketsBoard.changeTrip(that.currentForthTrip);
                }
                if (StateManager.getBothDirections() && (isInitial || !StateManager.getSelectedBackTrip() || that.currentBackTrip.id != StateManager.getSelectedBackTrip().id)) {
                    that.ticketsBoard.changeTrip(that.currentBackTrip);
                }
                if ($(event.target).prop('tagName') === 'A') {
                    that.ticketsBoard.addTicketElement(that.currentForthTrip, that.currentBackTrip);
                }
            });

            this.$backTrips.on('click', function(event) {
                event.preventDefault();
                var $backTripEl = $(event.target);
                if ($backTripEl.hasClass('disabled')) return false;
                if (that.$ticketBoard.hasNoTickets()) return false;

                if(!$backTripEl.hasClass('trip')) {
                    $backTripEl = $backTripEl.parents('.trip');
                    if($backTripEl.length === 0) return false;
                }

                var backTripId = $backTripEl.attr('id');
                if (that.currentBackTrip && that.currentBackTrip.id === backTripId) return false;

                that.ticketsBoard.changeTrip(that.currentBackTrip);
            });
        },

        setTrips: function(forthTrips, backTrips) {
            function setTrips(trips, isForth) {
                for (var i = 0, size = trips.length; i < size; i++) {
                    var trip = trips[i];
                    trip.isForth = isForth;
                    trip.lockedSeats = [];
                    dataStore.set(trip, 'Trip');
                    var seats = trip['seats'];
                    for (var j = 0; j < seats.length; j++) {
                        var seat = seats[j];
                        if (!dataStore.get(seat.id)) dataStore.set(seat, 'Seat');
                    }
                }
            }

            setTrips(forthTrips, true); StateManager.setForthTrips(forthTrips);
            if (backTrips) {
                setTrips(backTrips, false);
                StateManager.setBackTrips(backTrips);
            }
        },

        drawLayout: function() {
           function drawTripsBlock($tripsBlock, trips, startCity, endCity) {
               var route = startCity.name + ' - ' + endCity.name;
               $tripsBlock.append($(uc.tripGroupTemplate(route)));
               var $trips = $tripsBlock.find('.trips');
               for (var i = 0, size = trips.length; i < size; i++) {
                   var trip = trips[i];
                   $trips.append($(uc.tripTemplate(trip)));
               }
               $tripsBlock.show();
            }

            drawTripsBlock(this.$forthTrips, StateManager.getForthTrips(), StateManager.getStartCity(), StateManager.getEndCity());

            if (StateManager.getBackTrips()) {
                drawTripsBlock(this.$backTrips, StateManager.getBackTrips(), StateManager.getEndCity(), StateManager.getStartCity());
            } else {
                this.$backTrips.hide();
            }
        },

        setTripElementActive: function($tripElement) {
            $tripElement.parent().find('.trip.active').removeClass('active');
            $tripElement.addClass('active');
        },

        clean: function() {
            this.$forthTrips.empty();
            this.$backTrips.empty();

            StateManager.setForthTrips(null);
            StateManager.setBackTrips(null);

            this.currentForthTrip = null;
            this.currentBackTrip = null;
        }
    };

    window.TicketsBoard = function () {
        this.discounts = {};

        this.$ticketBoard = $('.ticket-board');
        this.$tickets = $('#tickets');
        this.init();
    };

    TicketsBoard.prototype = {
        constructor : TicketsBoard,

        init: function() {
            var that = this;
            this.$tickets.click(function(event){
                var $target = $(event.target);
                if ($target.prop('tagName') === 'BUTTON') {
                    var trip = $target.hasClass('forth')? StateManager.getSelectedForthTrip() : StateManager.getSelectedBackTrip();
                    var ticketId = $target.parents('.ticket').attr('id');
                    new BusSchemePopup(StateManager.getTicket(ticketId), trip).show();
                } else {
                    var $ticket = $target.parents('.ticket');
                    if (!$ticket.hasClass('active')) {
                        that.$tickets.find('.ticket.active').removeClass('active');
                        $ticket.addClass('active');
                    }
                }
            });
            this.$tickets.change(function(event) {
                var $changedElement = $(event.target);
                var ticketId = $changedElement.parents('.ticket').attr('id');
                var ticket = StateManager.getTicket(ticketId);
                if ($changedElement.hasClass('username')) {
                    ticket['passenger'] = $changedElement.val();
                } else if ($changedElement.hasClass('phone')) {
                    if ($changedElement.hasClass('second')) {
                        ticket['phone2'] = $changedElement.val();
                    } else {
                        ticket['phone1'] = $changedElement.val();
                    }
                }
            });
            this.$ticketBoard.find('#add-ticket').on('click', function(event) {
                that.addTicketElement(StateManager.getSelectedForthTrip(), StateManager.getSelectedBackTrip());
            });

            this.$ticketBoard.find('#delete-ticket').on('click', function(event) {
                that.removeTicket(that.$tickets.find('.ticket.active'));
            });

            this.$ticketBoard.find('#buy-tickets').on('click', function(event) {
                if (!isAuthorized()) {
                    new Message('Для здійснення операції авторизуйтесь, будь ласка', 10000);
                    return false;
                }
                that.buyTickets();
            });

            var $changeCurrency = this.$ticketBoard.find('.change-currency');
            $changeCurrency.find('#'+UserSettings.currency).attr('selected', 'selected');
            $changeCurrency.change(function(event) {
                var currencyId = MiscUtils.getSelectedId($(this), 0);
                if (UserSettings.currency == currencyId) return;
                UserSettings.currency = currencyId;
                UserSettings.currencySign = $(this).val();
                for (var ticket in StateManager.getOrder().tickets) {
                    var $ticket = $('#'+ticket);
                    that.recalculatePrice($ticket);
                }
            });
        },

        hasNoTickets: function() {
            return this.getTicketsSize() === 0;
        },

        getTicketsSize: function() {
            return this.$ticketBoard.find('.ticket').length;
        },

        setDiscounts: function(discounts) {
            for (var i = 0, size = discounts.length; i < size; i++) {
                dataStore.set(discounts[i], "Discount");
            }
            this.discounts = discounts;
        },

        addTicketElement: function (forthTrip, backTrip) {
            var forthSeat = this.getNextFreeSeatForTrip(forthTrip);
            if (!forthSeat) {
                new Message("Для поїздки відсутні вільні місця", 10000);
                return;
            }

            var backSeat = backTrip? this.getNextFreeSeatForTrip(backTrip) : null;
            if (backTrip && !backSeat) {
                new Message("Для зворотньої поїздки відсутні вільні місця", 10000);
                return;
            }

            var ticketTempl = uc.ticketTemplate(this.getUsername(), this.getPhone(), StateManager.getStartCity(), StateManager.getEndCity(),
                                                            forthTrip, forthSeat, backTrip, backSeat, this.discounts);
            this.$tickets.find('.ticket.active').removeClass('active');
            var isFirstTicket = this.hasNoTickets();
            this.$tickets.append(ticketTempl);
            var $ticketsChilds = this.$tickets.children();
            if (isFirstTicket) {
                this.$ticketBoard.show();
            }
            EventBus.dispatch('ticket_added');
            var $thisTicket = $($ticketsChilds[$ticketsChilds.length-1]);
            StateManager.addTicket(this.createTicketObject($thisTicket.attr('id'), forthTrip, forthSeat, backSeat));
            var that = this;
            $thisTicket.find('.ticket-input.select').on('change', function(event) {
                that.recalculatePrice($thisTicket, MiscUtils.getSelectedId($(this), 9));

            });
        },

        createTicketObject: function(id, trip, forthSeat, backSeat) {
            var ticket = {
                id: id,
                price: trip['price'],
                discPrice: trip['discPrice'],
                discountId: 'NONE',
                forthSeatId: forthSeat.id,
                forthSeatNum: forthSeat['seatNum'],
                startCity: StateManager.getStartCity().id,
                endCity: StateManager.getEndCity().id,
                rawStartDate: StateManager.getSelectedForthTrip()['rawStartDate'],
                passenger: this.getUsername(),
                phone1: this.getPhone(),
                phone2: this.getPhone()
            };

            if (StateManager.getBothDirections()) {
                ticket.rawBackStartDate = StateManager.getSelectedBackTrip()['rawStartDate'];
                ticket.backSeatId = backSeat.id;
                ticket.backSeatNum = backSeat['seatNum'];
            }

            return ticket;
        },

        removeTicket: function($ticketElement) {
            var ticket = StateManager.getTicket($ticketElement.attr('id'));
            var forthSeatId = ticket['forthSeatId'];
            var backSeatId = ticket['backSeatId'];

            var lockedForthSeats = StateManager.getSelectedForthTrip()['lockedSeats'];
            var idx = lockedForthSeats.indexOf(forthSeatId);
            lockedForthSeats.splice(idx, 1);

            if (backSeatId) {
                var lockedBackSeats = StateManager.getSelectedBackTrip()['lockedSeats'];
                idx = lockedBackSeats.indexOf(backSeatId);
                lockedBackSeats.splice(idx, 1);
            }

            EventBus.dispatch('before_ticket_removed');
            if (this.getTicketsSize() == 1) {
                setTimeout(function() {
                    $ticketElement.remove();
                }, 800);
            } else {
                $ticketElement.remove();
                var $first = $(this.$tickets.children()[0]);
                $first.removeClass('card');
                $first.addClass('active');
            }

            EventBus.dispatch('ticket_removed');
        },

        buyTickets: function() {
            var order = StateManager.getOrder();
            order['forthTripId'] = StateManager.getSelectedForthTrip().id;
            if (StateManager.getBothDirections()) order['backTripId'] = StateManager.getSelectedBackTrip().id;

            for (var ticketId in order.tickets) if (order.tickets.hasOwnProperty(ticketId)) {
                var ticket = StateManager.getTicket(ticketId);
                if (StringUtils.isEmpty(ticket['passenger'])) {
                    new Message('В одному з квитків не вказано імені і прізвища пасажира', 10000);
                    return null;
                }
                if (StringUtils.isEmpty(ticket['phone1']) && StringUtils.isEmpty(ticket['phone2'])) {
                    new Message('В одному з квитків не вказано телефону пасажира', 10000);
                    return null;
                }
            }
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
                    var p = new Popup('Квитки заброньовано', '<div>Ви успішно придбали квитки, підтвердження надіслано на вашу пошту</div><p><button id="buy-ready" class="btn btn-green width-100">OK</button></p>', 'white', function(){
                        $('#buy-ready').on('click', function(){
                            p.destroy();
                            StateManager.clearState();
                            window.location.reload();
                        });
                    });
                    p.show();
                }
            });
        },

        changeTrip: function (newTrip) {
            var that = this;
            var route = '';
            var styleClass = '';
            var prevTrip = null;
            if (newTrip.isForth) {
                prevTrip = StateManager.getSelectedForthTrip();
                StateManager.setSelectedForthTrip(newTrip);
                route = StateManager.getStartCity().name + ' - ' + StateManager.getEndCity().name;
                styleClass = 'forth';
            } else {
                prevTrip = StateManager.getSelectedBackTrip();
                StateManager.setSelectedBackTrip(newTrip);
                route = StateManager.getEndCity().name + ' - ' + StateManager.getStartCity().name;
                styleClass = 'back';
            }
            if (prevTrip) prevTrip['lockedSeats'] = [];
            this.$ticketBoard.find('.ticket').each(function(index, element){
                var $element = $(element);
                var newSeat = that.getNextFreeSeatForTrip(newTrip);
                //TODO send bulk request to reserve these seats
                if (!newSeat) {
                    that.removeTicket($element);
                    return;
                }
                var ticket = StateManager.getTicket($element.attr('id'));
                if (newTrip.isForth) {
                    ticket['rawStartDate'] = newTrip['rawStartDate'];
                } else {
                    ticket['rawBackStartDate'] = newTrip['rawStartDate'];
                }
                ticket[styleClass + 'SeatId'] = newSeat.id;
                ticket[styleClass + 'SeatNum'] = newSeat['seatNum'];

                $element.find('.'+styleClass+'-info-trip').text(route);
                $element.find('.'+styleClass+'-info-date').text(newTrip['startDate']);
                $element.find('.'+styleClass+'-trip-seat').text(newSeat['seatNum']);
                $element.find('.'+styleClass+'-trip-seat').attr('id', newSeat['id']);
            });
        },

        getNextFreeSeatForTrip: function(trip) {
            var reservedSeats = trip['lockedSeats'];
            var availableSeats = trip['seats'];
            for (var i = 0; i < availableSeats.length; i++) {
                var availableSeat = availableSeats[i];
                if (reservedSeats.indexOf(availableSeat.id) < 0) {
                    reservedSeats[reservedSeats.length] = availableSeat.id;
                    return availableSeat;
                }
            }

            return null;
        },

        getUsername: function() {
            var result = null;
            var fromCookies = window.cookies.get('name');
            if (fromCookies) result = fromCookies;
            var usernameInput = this.$ticketBoard.find('.ticket-input.username')[0];
            var enteredUsername = usernameInput? $(usernameInput).val() : null;
            if (!StringUtils.isEmpty(enteredUsername)) result = enteredUsername;
            return result;
        },

        getPhone: function() {
            var result = null;
            var fromCookies = window.cookies.get('phone');
            if (fromCookies) result = fromCookies;
            var phoneInput = this.$ticketBoard.find('.ticket-input.phone')[0];
            var enteredPhone = phoneInput? $(phoneInput).val() : null;
            if (!StringUtils.isEmpty(enteredPhone)) result = enteredPhone;
            return result;
        },

        recalculatePrice: function($ticket, discountId) {
            var ticket = StateManager.getTicket($ticket.attr('id'));
            if (!discountId) discountId = ticket.discountId;
            var discount = dataStore.get(discountId);
            ticket.discountId = discountId;

            var $fullPrice = $ticket.find('.price-full .price-self');
            var $discPrice = $ticket.find('.price-disc .price-self');

            var fullPrice = ticket['price'];
            var price = ticket['discPrice'];
            var resultPrice = parseInt(StringUtils.isEmpty(fullPrice)? price : fullPrice);
            var result = MiscUtils.calculatePrice(resultPrice, discount);
            if (UserSettings.currency == 'uah') {
                resultPrice = CurrencyUtils.round(resultPrice * EURUAH, -1);
                result = CurrencyUtils.round(result * EURUAH, -1);
            }
            $discPrice.text(result);
            $fullPrice.text(resultPrice);
            $ticket.find('.price .sign').text(UserSettings.currencySign);
            var $fullPriceDiv = $ticket.find('.price-full');
            if (resultPrice === result) {
                $fullPriceDiv.addClass('hide');
            } else {
                if ($fullPriceDiv.hasClass('hide')) $fullPriceDiv.removeClass('hide');
            }
        },

        clean: function() {
            this.$tickets.empty();
            this.discounts = {};

//            StateManager.getSelectedForthTrip()['lockedSeats'] = [];
//            if (StateManager.getBothDirections()) {
//                StateManager.getSelectedBackTrip()['lockedSeats'] = [];
//            }

            StateManager.getOrder().tickets = {};
        }
    };

    window.BusSchemePopup = function(ticket, trip) {
        var that = this;
        this.allSeats = trip['seats'];
        this.lockedSeats = trip['lockedSeats'];
        this.initialSeatId = trip.isForth? ticket['forthSeatId'] : ticket['backSeatId'];

        this.popup = new Popup('Схема автобуса', setraBus, 'popup-bus', function(popupId) {
            var $popupEl = $(popupId);
            for (var i = 0; i < that.allSeats.length; i++) {
                var seat = that.allSeats[i];
                var $current = $popupEl.find('#seat-'+seat['seatNum']);
                $current.attr('seatId', seat.id);
                if (that.lockedSeats.indexOf(seat.id) >= 0) continue;

                $current.removeClass('blocked');
            }
            var choice = that.initialSeatId;
            var $choice = $('[seatId='+choice+']');
            $choice.removeClass('blocked');
            $choice.addClass('active');


            $popupEl.find('.bus-setra').on('click', function(event){
                var $target = $(event.target);
                if ($target.prop('tagName') === 'A') {
                    var temp = $target.attr('seatId');
                    if (that.lockedSeats.indexOf(temp) >= 0) return false;

                    choice = temp;

                    var $prevActive = $popupEl.find('a.active');
                    $prevActive.removeClass('active');
                    if (!$target.hasClass('active')) $target.addClass('active');
                }
            });
            $popupEl.find('#bus-seat-cancel').on('click', function(){
                that.popup.destroy();
            });

            $popupEl.find('#bus-seat-ok').on('click', function() {
                that.lockedSeats.splice(that.lockedSeats.indexOf(that.initialSeatId), 1);
                that.lockedSeats[that.lockedSeats.length] = choice;

                var seat = dataStore.get(choice);
                var data = {tripId: trip.id, seatId: choice};
                var prevTicketId = trip.isForth? ticket['forthTicketId'] : ticket['backTicketId'];
                if (prevTicketId) data['unlockTicketId'] = prevTicketId;

                new Request('lockSeat', data).send(function(data) {
                    if (trip.isForth) {
                        ticket['forthTicketId'] = data['ticketId'];
                        ticket['forthSeatId'] = seat.id;
                        ticket['forthSeatNum'] = seat['seatNum'];
                    } else {
                        ticket['backTicketId'] = data['ticketId'];
                        ticket['backSeatId'] = seat.id;
                        ticket['backSeatNum'] = seat['seatNum'];
                    }
                    var $seatButton = $('#'+ticket.id).find('#'+that.initialSeatId);
                    $seatButton.text(seat['seatNum']);
                    $seatButton.attr('id', seat['id']);
                    that.popup.destroy();
                });
            })
        });
    };

    BusSchemePopup.prototype = {
        constructor: BusSchemePopup,

        show: function () {
            this.popup.show();
        },

        destroy: function() {
            this.popup.destroy();
        }
    };


    //show in popup
    var setraBus = '<div><div class="bus-scheme bus-setra"><table><tbody><tr><td><a id="seat-1a" class="blocked" href="#">1a</a></td><td><a id="seat-1b" class="blocked" href="#" class="active">1b</a></td><td><span> </span></td><td><a id="seat-1c" class="blocked" href="#">1c</a></td><td><a id="seat-1d" class="blocked" href="#">1d</a></td></tr><tr><td><a id="seat-2a" class="blocked" href="#" class="blocked">2a</a></td><td><a id="seat-2b" class="blocked" href="#">2b</a></td><td><span> </span></td><td><a id="seat-2c" class="blocked" href="#">2c</a></td><td><a id="seat-2d" class="blocked" href="#">2d</a></td></tr><tr><td><a id="seat-3a" class="blocked" href="#">3a</a></td><td><a id="seat-3b" class="blocked" href="#">3b</a></td><td><span> </span></td><td><a id="seat-3c" class="blocked" href="#">3c</a></td><td><a id="seat-3d" class="blocked" href="#">3d</a></td></tr><tr><td><a id="seat-4a" class="blocked" href="#">4a</a></td><td><a id="seat-4b" class="blocked" href="#">4b</a></td><td><span> </span></td><td><a id="seat-4c" class="blocked" href="#">4c</a></td><td><a id="seat-4d" class="blocked" href="#">4d</a></td></tr><tr><td><a id="seat-5a" class="blocked" href="#">5a</a></td><td><a id="seat-5b" class="blocked" href="#">5b</a></td><td><span> </span></td><td><a id="seat-5c" class="blocked" href="#">5c</a></td><td><a id="seat-5d" class="blocked" href="#">5d</a></td></tr><tr><td><a id="seat-6a" class="blocked" href="#">6a</a></td><td><a id="seat-6b" class="blocked" href="#">6b</a></td><td><span> </span></td><td><a id="seat-6c" class="blocked" href="#">6c</a></td><td><a id="seat-6d" class="blocked" href="#">6d</a></td></tr><tr><td><a id="seat-7a" class="blocked" href="#">7a</a></td><td><a id="seat-7b" class="blocked" href="#">7b</a></td><td><span> </span></td><td><a id="seat-7c" class="blocked" href="#">7c</a></td><td><a id="seat-7d" class="blocked" href="#">7d</a></td></tr><tr><td><a id="seat-8a" class="blocked" href="#">8a</a></td><td><a id="seat-8b" class="blocked" href="#">8b</a></td><td><span> </span></td><td><span> </span></td><td><span> </span></td></tr><tr><td><a id="seat-9a" class="blocked" href="#">9a</a></td><td><a id="seat-9b" class="blocked" href="#">9b</a></td><td><span> </span></td><td><a id="seat-9c" class="blocked" href="#">9c</a></td><td><a id="seat-9d" class="blocked" href="#">9d</a></td></tr><tr><td><a id="seat-10a" class="blocked" href="#">10a</a></td><td><a id="seat-10b" class="blocked" href="#">10b</a></td><td><span> </span></td><td><a id="seat-10c" class="blocked" href="#">10c</a></td><td><a id="seat-10d" class="blocked" href="#">10d</a></td></tr><tr><td><a id="seat-11a" class="blocked" href="#">11a</a></td><td><a id="seat-11b" class="blocked" href="#">11b</a></td><td><span> </span></td><td><a id="seat-11c" class="blocked" href="#">11c</a></td><td><a id="seat-11d" class="blocked" href="#">11d</a></td></tr><tr><td><a id="seat-12a" class="blocked" href="#">12a</a></td><td><a id="seat-12b" class="blocked" href="#">12b</a></td><td><span> </span></td><td><a id="seat-12c" class="blocked" href="#">12c</a></td><td><a id="seat-12d" class="blocked" href="#">12d</a></td></tr><tr><td><a id="seat-13a" class="blocked" href="#">13a</a></td><td><a id="seat-13b" class="blocked" href="#">13b</a></td><td><span> </span></td><td><a id="seat-13c" class="blocked" href="#">13c</a></td><td><a id="seat-13d" class="blocked" href="#">13d</a></td></tr></tbody></table></div><div><div class="clear-both"><button id="bus-seat-ok" class="btn btn-right btn-green">Обрати</button><button id="bus-seat-cancel" class="btn btn-right btn-green">Відміна</button></div></div></div>';
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

        window.uc.ticketTemplate = function(user, phone, startCity, endCity, forthTrip, forthSeat, backTrip, backSeat, discounts){
            var price = forthTrip['price'];
            var discPrice = forthTrip['discPrice'];
            var forthRoute = startCity.name + ' - ' + endCity.name; //TODO make a method
            var forthDate = forthTrip['startDate'];

            var resultPrice = discPrice? discPrice : price;
            var discountOptions = '';
            for (var i = 0, size = discounts.length; i < size; i++) {
                discountOptions += '<option id="discount-'+discounts[i].id+'" '+
                    (discounts[i].id==='NONE'? 'selected':'')+'>'+discounts[i].text+'</option>';
            }
            if (UserSettings.currency == 'uah') {
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
                '<div class="price-disc clear-both"><div class="price-right"><span class="sign">'+UserSettings.currencySign+'</span><span class="price-self">'+resultPrice+'</span></div></div>'+
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
                '<span class="forth-info-trip">'+forthRoute+'</span>' +
                '<span class="forth-info-date">'+forthDate+'</span>'+
                '</div>'+
                '<button class="btn btn-green light forth">Місце #<span id="'+forthSeat.id+'" class="forth-trip-seat">'+forthSeat['seatNum']+'</span></button>'+
                '</div>';
            if (!backTrip) return start + '</div>';

            var backRoute = endCity  + ' - ' + startCity;
            var backDate = backTrip['startDate'];
            return start +
                '<div class="ticket-row clear-both back">'+
                '<div class="back-info">' +
                '<span class="back-info-trip">'+backRoute+'</span>' +
                '<span class="back-info-date">'+backDate+'</span>'+
                '</div>'+
                '<button class="btn btn-green light back">Місце #<span id="'+backSeat.id+'" class="back-trip-seat">'+backSeat['seatNum']+'</span></button>'+
                '</div>'+
                '</div>';
        }
    })();

    function showConfirm(locationOrderId) {
        new Request('orderExists', {orderId: locationOrderId}).send(function(data) {
            if (data && data['orderId']) {
                var p = new Popup('Квитки придбано', '<div>Ви успішно придбали квитки, підтвердження надіслано на вашу пошту</div><div><div class="clear-both"><button id="reserve-ready" class="btn btn-right btn-green">OK</button></div></div>', 'white', function(){
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

    $(document).ready(function() {
        StateManager.init();

        var selectionBoard = new SelectionBoard($('.forms-inline-list'));
        selectionBoard.init();

        var locationOrderId = location.getParam('orderId');
        if (locationOrderId) {
            showConfirm(locationOrderId);
        }
    });
})();
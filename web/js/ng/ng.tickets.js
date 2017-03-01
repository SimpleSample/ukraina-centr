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
    },

    adjustWeekDay: function(date, weekDay) {
        var dateWeekDay = date.getDay();
        var daysToAdd = 0;
        if (dateWeekDay < weekDay) {
            daysToAdd = weekDay - dateWeekDay;
        } else {
            daysToAdd = 7 - dateWeekDay + weekDay;
        }

        var miliseconds = date.getTime() + daysToAdd * 24 * 60* 60 * 1000;
        return new Date(miliseconds);
    }
};

(function() {
    var UKRAINE_TEST = "ahFzfm91ci1saXR0bGUtYmFieXIQCxIHQ291bnRyeSIDdWtyDA";
    var UKRAINE_PROD = "ag9zfnVrcmFpbmEtY2VudHJyEAsSB0NvdW50cnkiA3Vrcgw";
    var clientBundle = window.clientBundle;
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
            serverLockedTickets: [],
            order: {
                tickets: {}
            }
        }
    }
    // variable to track state of chosen cities and order state
    var State = getCleanState();

    window.UserSettings = {
        lang: 'ua',
        currency: 'eur',
        currencySign: '€'
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
            var ticket = this.getTicket(ticketId);
            delete State.order.tickets[ticketId];
            return ticket;
        },

        getTicket: function(ticketId) {
            return State.order.tickets[ticketId];
        },

        getTickets: function() {
            return State.order.tickets;
        },

        getOrder: function() {
            return State.order;
        },

        getServerLockedTickets: function() {
            return State.serverLockedTickets;
        },

        addServerLockedTicket: function(tripId, ticketId) {
            State.serverLockedTickets[State.serverLockedTickets.length] = {tripId:tripId, ticketId: ticketId};
        },

        removeServerLockedTicket: function(tripId, ticketId) {
            for (var i = 0; i < State.serverLockedTickets.length; i++) {
                var ticketToUnlock = State.serverLockedTickets[i];
                if (ticketToUnlock.tripId === tripId && ticketToUnlock.ticketId === ticketId) {
                    State.serverLockedTickets.splice(i, 1);
                    return;
                }
            }
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

        this.isCurrentForth = true;
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
                    var temp = newStartCity.country == UKRAINE_PROD || newStartCity.country == UKRAINE_TEST;
                    if (that.isCurrentForth != temp){
                        var forthDayOfWeek = temp? 4 : 0;
                        var backDayOfWeek = temp? 0 : 4;
                        forthDateWidget.setDate(MiscUtils.adjustWeekDay(new Date(), forthDayOfWeek));
                        backDateWidget.setDate(MiscUtils.adjustWeekDay(selectedForthDate, backDayOfWeek));
                    }
                    that.onStartCityChange(newStartCity);
                    that.isCurrentForth = temp;
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

            var forthDayOfWeek = that.isCurrentForth? 4 : 0;
            var backDayOfWeek = that.isCurrentForth? 0 : 4;
            var selectedForthDate = MiscUtils.adjustWeekDay(new Date(), forthDayOfWeek);
            var forthDateWidget =
                new DateField(this.$forthDatePicker)
                        .setSelectedDate(selectedForthDate)
                        .setSelectFunction(function(date) {
                            that.currentForthDate = date.getTime();
                            if (date.getTime() > backDateWidget.getDate().getTime()) {
                                var dayOfWeek = that.isCurrentForth? 0 : 4;
                                backDateWidget.setDate(MiscUtils.adjustWeekDay(date, dayOfWeek));
                            }
                        })
                        .setDisableDayFunction(function(date) {
                            var dayOfWeek = that.isCurrentForth? 4 : 0;
                            if (date.getDay() !== dayOfWeek) return true;
                        })
                        .createPikaday();

            this.currentForthDate = forthDateWidget.getDate().getTime();

            var selectedBackDate = MiscUtils.adjustWeekDay(selectedForthDate, backDayOfWeek);
            var backDateWidget =
                new DateField(this.$backDatePicker)
                        .setSelectedDate(selectedBackDate)
                        .setSelectFunction(function(date) {
                            that.currentBackDate = date.getTime();
                            if (date.getTime() < forthDateWidget.getDate().getTime()) {
                                var dayOfWeek = that.isCurrentForth? 4 : 0;
                                forthDateWidget.setDate(MiscUtils.adjustWeekDay(new Date(), dayOfWeek));
                            }
                        })
                        .setDisableDayFunction(function(date) {
                            var dayOfWeek = that.isCurrentForth? 0 : 4;
                            if (date.getDay() !== dayOfWeek) return true;
                        })
                        .createPikaday();

            this.currentBackDate = backDateWidget.getDate().getTime();

            $('#show-tickets').click(function(event) {
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
                new Message(clientBundle.one_of_cities_is_not_selected, 7000);
                return false;
            }
            return true;
        }
    };

    window.OrderBoard = function ($orderBoard) {
        this.$orderBoard = $orderBoard;

        this.ticketsBoard = new TicketsBoard();
        this.tripsBoard = new TripsBoard($orderBoard, this.ticketsBoard);
    };

    OrderBoard.prototype = {
        constructor : OrderBoard,

        searchTrips: function (startCityId, endCityId, forthDate, backDate, bothDirections) {
            var that = this;
            this.clean(function() {
                StateManager.setStartCity(dataStore.get(startCityId));
                StateManager.setEndCity(dataStore.get(endCityId));
                StateManager.setForthDate(forthDate);
                StateManager.setBackDate(backDate);
                StateManager.setBothDirections(bothDirections);

                var data = {
                    startCityId: startCityId,
                    endCityId: endCityId,
                    startDate: forthDate,
                    tzOffset: MiscUtils.getTimezoneOffset()
                };
                if (bothDirections) {
                    data['endDate'] = backDate;
                }

                new Request('searchForWeekdays', data).send(function(data) {
                    if (!data['forthTrips']) {
                        that.tripsBoard.hide();
                        new Message(clientBundle.could_not_find_ticket_for_chosen_date, 10000);
                        return;
                    }
                    if (StateManager.getBothDirections() && !data['backTrips']) {
                        StateManager.setBothDirections(false);
                    }

                    that.tripsBoard.setTrips(data['forthTrips'], data['backTrips']);
                    that.ticketsBoard.setDiscounts(data['discounts']);

                    that.tripsBoard.drawLayout();
                    that.tripsBoard.show();
                });
            });
        },

        clean: function(callback) {
            var that = this;
            this.tripsBoard.clean();
            this.ticketsBoard.clean(function(){
                that.tripsBoard.hide(function() {
                    StateManager.clearState();
                    if (callback) callback();
                });
            });
        }
    };

    window.TripsBoard = function ($container, ticketsBoard) {

        this.$container = $container;
        this.ticketsBoard = ticketsBoard;
        this.trips = {};

        this.$tripsBoard = $('.results');
        this.$forthTrips = $('#forth-trips');
        this.$backTrips = $('#back-trips');
        this.$createTicketBtn = this.$tripsBoard.find('#create-ticket');

        this.init();
    };

    TripsBoard.prototype = {
        constructor : TripsBoard,

        init: function() {
            var that = this;
            this.$createTicketBtn.click(function(event) {
                event.preventDefault();
                if (!isAuthorized()) {
                    new Message(clientBundle.login_to_perform_this_operation, 10000);
                    return false;
                }

                that.ticketsBoard.addTicketElement(StateManager.getSelectedForthTrip(), StateManager.getSelectedBackTrip());
                that.$createTicketBtn.addClass('btn-hidden');
                that.$tripsBoard.addClass('bordered');
            });
            EventBus.addEventListener('ticket_removed', function(event) {
                if (that.ticketsBoard.getTicketsSize() == 0) {
                    that.$tripsBoard.removeClass('bordered');
                    that.$createTicketBtn.removeClass('btn-hidden');
                }
            });
        },

        setTrips: function(forthTrips, backTrips) {
            function setTrip(trip, isForth) {
                trip.isForth = isForth;
                trip.lockedSeats = [];
                dataStore.set(trip, 'Trip');
                var seats = trip['seats'];
                for (var j = 0; j < seats.length; j++) {
                    var seat = seats[j];
                    if (!dataStore.get(seat.id)) dataStore.set(seat, 'Seat');
                }
            }

            var forthTrip = forthTrips[0];
            setTrip(forthTrip, true); StateManager.setSelectedForthTrip(forthTrip);
            if (backTrips) {
                var backTrip = backTrips[0];
                setTrip(backTrip, false);
                StateManager.setSelectedBackTrip(backTrip);
            }
        },

        drawLayout: function() {
            function drawTripsBlock($tripsBlock, trip, startCity, endCity) {
                var route = startCity.name + ' - ' + endCity.name;
                $tripsBlock.append($(uc.tripGroupTemplate(route)));
                $tripsBlock.find('.trips').append($(uc.tripTemplate(trip)));
                $tripsBlock.show();
            }

            drawTripsBlock(this.$forthTrips, StateManager.getSelectedForthTrip(), StateManager.getStartCity(), StateManager.getEndCity());

            if (StateManager.getSelectedBackTrip()) {
                drawTripsBlock(this.$backTrips, StateManager.getSelectedBackTrip(), StateManager.getEndCity(), StateManager.getStartCity());
            } else {
                this.$backTrips.hide();
            }

        },

        show: function() {
            var that = this;
            this.$container.animate({
                height: "toggle"
            }, 300, function() {
                that.$createTicketBtn.removeClass('btn-hidden');
            });
        },

        hide: function(callback) {
            var that = this;
            this.ticketsBoard.hide(function(){
                if (that.$container.css('display') == 'none') {
                    if (callback) callback();
                    return;
                }

                that.$createTicketBtn.addClass('btn-hidden');
                that.$tripsBoard.removeClass('bordered');
                that.$container.animate({
                    height: "toggle"
                }, 300, function() {
                    if (callback) callback();
                });
            });
        },

        clean: function() {
            this.$forthTrips.empty();
            this.$backTrips.empty();

            StateManager.setSelectedForthTrip(null);
            StateManager.setSelectedBackTrip(null);
        }
    };

    window.TicketsBoard = function () {
        this.discounts = {};
        this.serverLockedTickets = {};

        this.$ticketBoard = $('.ticket-board');
        this.$tickets = $('#tickets');
        this.init();
    };

    TicketsBoard.prototype = {
        constructor : TicketsBoard,

        init: function() {
            var that = this;
            this.$tickets.click(function(event){
                if (event.target === that.$tickets[0]) return;

                var $target = $(event.target);
                var $ticket = $target.parents('.ticket');
                if (!$ticket.hasClass('active')) {
                    that.$tickets.find('.ticket.active').removeClass('active');
                    $ticket.addClass('active');
                }
                if ($target.prop('tagName') === 'BUTTON') {
                    var trip = $target.hasClass('forth')? StateManager.getSelectedForthTrip() : StateManager.getSelectedBackTrip();
                    var ticketId = $target.parents('.ticket').attr('id');
                    new BusSchemePopup(StateManager.getTicket(ticketId), trip).show();
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
                } else if ($changedElement.hasClass('ticket-note')) {
                    ticket['note'] = $changedElement.val();
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
                    new Message(clientBundle.login_to_perform_this_operation, 10000);
                    return false;
                }
                that.buyTickets();
            });

            EventBus.addEventListener('ticket_removed', function(event) {
                that.onTicketPriceChanged();
            });

            EventBus.addEventListener('ticket_added', function(event) {
                that.onTicketPriceChanged();
            });

            //todo restore when uah prices will be appropriate
//            var $changeCurrency = this.$ticketBoard.find('.change-currency');
//            $changeCurrency.find('#'+UserSettings.currency).attr('selected', 'selected');
//            $changeCurrency.change(function(event) {
//                var currencyId = MiscUtils.getSelectedId($(this), 0);
//                if (UserSettings.currency == currencyId) return;
//                UserSettings.currency = currencyId;
//                UserSettings.currencySign = $(this).val();
//                for (var ticket in StateManager.getOrder().tickets) {
//                    var $ticket = $('#'+ticket);
//                    that.recalculatePrice($ticket);
//                }
//            });
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
                new Message(clientBundle.no_available_seats_found_for_selected_trip, 10000);
                return;
            }

            var backSeat = backTrip? this.getNextFreeSeatForTrip(backTrip) : null;
            if (backTrip && !backSeat) {
                new Message(clientBundle.no_available_seats_found_for_selected_back_trip, 10000);
                return;
            }

            var $ticketTempl = $(uc.ticketTemplate(this.getUsername(), this.getPhone(), StateManager.getStartCity(), StateManager.getEndCity(),
                forthTrip, forthSeat, backTrip, backSeat, this.discounts));
            this.$tickets.find('.ticket.active').removeClass('active');
            var isFirstTicket = this.hasNoTickets();
            this.$tickets.append($ticketTempl);
            var $ticketsChilds = this.$tickets.children();
            if (isFirstTicket) {
                this.show(function() {
                    $ticketTempl.animate({
                        opacity: 1
                    }, 300);
                });
            } else {
                $ticketTempl.animate({
                    opacity: 1
                }, 300);
            }
            var $thisTicket = $($ticketsChilds[$ticketsChilds.length-1]);
            StateManager.addTicket(this.createTicketObject($thisTicket.attr('id'), forthTrip, forthSeat, backSeat));
            EventBus.dispatch('ticket_added');
            var that = this;
            $thisTicket.find('.ticket-input.select').on('change', function(event) {
                that.recalculatePrice($thisTicket, MiscUtils.getSelectedId($(this), 9));
            });
        },

        createTicketObject: function(id, trip, forthSeat, backSeat) {
            var ticket = {
                id: id,
                price: trip['price'], // full price for the tickets of this trip
                discPrice: trip['discPrice'], //price including personal discounts
                resultDiscPrice: 0, //calculated on client basing on 'price' or 'discPrice'
                discountId: 'NONE',
                seatId: forthSeat.id,
                seatNum: forthSeat['seatNum'],
                startCity: StateManager.getStartCity().id,
                endCity: StateManager.getEndCity().id,
                rawStartDate: StateManager.getSelectedForthTrip()['rawStartDate'],
                passenger: this.getUsername(),
                phone1: this.getPhone(),
                phone2: this.getPhone(),
                note: null
            };

            if (StateManager.getBothDirections()) {
                ticket.rawBackStartDate = StateManager.getSelectedBackTrip()['rawStartDate'];
                ticket.backSeatId = backSeat.id;
                ticket.backSeatNum = backSeat['seatNum'];
            }

            return ticket;
        },

        removeTicket: function($ticketElement) {
            var ticket = StateManager.removeTicket($ticketElement.attr('id'));
            var forthSeatId = ticket.seatId;
            var backSeatId = ticket.backSeatId;

            var lockedForthSeats = StateManager.getSelectedForthTrip()['lockedSeats'];
            var idx = $.inArray(forthSeatId, lockedForthSeats);
            lockedForthSeats.splice(idx, 1);

            if (backSeatId) {
                var lockedBackSeats = StateManager.getSelectedBackTrip()['lockedSeats'];
                idx = $.inArray(backSeatId, lockedBackSeats);
                lockedBackSeats.splice(idx, 1);
            }

            this.invokeTicketUnlock(ticket);

            var that = this;
            $ticketElement.animate({
                opacity: 0
            }, 300, function() {
                if (that.getTicketsSize() != 1) {
                    $ticketElement.remove();
                    var $first = $(that.$tickets.children()[0]);
                    if ($first) {
                        $first.removeClass('card');
                        $first.addClass('active');
                    }
                    EventBus.dispatch('ticket_removed', ticket);
                } else {
                    that.hide(function () {
                        $ticketElement.remove();
                        EventBus.dispatch('ticket_removed', ticket);
                    });
                }
            });
        },

        buyTickets: function() {
            var order = StateManager.getOrder();
            order['tripId'] = StateManager.getSelectedForthTrip().id;
            if (StateManager.getBothDirections()) order['backTripId'] = StateManager.getSelectedBackTrip().id;

            for (var ticketId in order.tickets) if (order.tickets.hasOwnProperty(ticketId)) {
                var ticket = StateManager.getTicket(ticketId);
                if (StringUtils.isEmpty(ticket['passenger'])) {
                    new Message(clientBundle.there_is_no_passenger_name_in_some_tickets, 10000);
                    return null;
                }
                if (StringUtils.isEmpty(ticket['phone1']) && StringUtils.isEmpty(ticket['phone2'])) {
                    new Message(clientBundle.there_is_no_passenger_phone_in_some_tickets, 10000);
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
                    var p = new Popup(clientBundle.ticket_order, '<div>'+clientBundle.you_have_successfully_order_tickets_please_check_your_email+'</div><p><button id="buy-ready" class="btn btn-green width-100">OK</button></p>', 'white', function(){
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

        invokeTicketUnlock: function(ticket, callback) {
            var ticketsToUnlock = [];
            if (ticket.ticketId) {
                ticketsToUnlock[ticketsToUnlock.length] = {
                    tripId : StateManager.getSelectedForthTrip().id,
                    ticketId : ticket.ticketId
                };
            }
            if (ticket.backTicketId) {
                ticketsToUnlock[ticketsToUnlock.length] = {
                    tripId : StateManager.getSelectedBackTrip().id,
                    ticketId : ticket.backTicketId
                };
            }
            if (ticketsToUnlock.length) {
                new Request('unlockTickets', {tickets: JSON.stringify(ticketsToUnlock)}).send(function() {
                    for (var i = 0; i < ticketsToUnlock.length; i++) {
                        var ticket = ticketsToUnlock[i];
                        StateManager.removeServerLockedTicket(ticket.tripId, ticket.ticketId);
                    }
                    if (callback) callback();
                });
            } else {
                if (callback) callback();
            }
        },

        getNextFreeSeatForTrip: function(trip) {
            var reservedSeats = trip['lockedSeats'];
            var availableSeats = trip['seats'];
            for (var i = 0; i < availableSeats.length; i++) {
                var availableSeat = availableSeats[i];

                if ($.inArray(availableSeat.id, reservedSeats) < 0) {
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
            var resultPrice = fullPrice? fullPrice : price;
            var result = MiscUtils.calculatePrice(resultPrice, discount);
            if (UserSettings.currency == 'uah') {
                resultPrice = CurrencyUtils.round(resultPrice * EURUAH, -1);
                result = CurrencyUtils.round(result * EURUAH, -1);
            }
            ticket.resultDiscPrice = result;
            $discPrice.text(result);
            $fullPrice.text(resultPrice);
            $ticket.find('.price .sign').text(UserSettings.currencySign);
            var $fullPriceDiv = $ticket.find('.price-full');
            if (resultPrice === result) {
                $fullPriceDiv.addClass('hide');
            } else {
                if ($fullPriceDiv.hasClass('hide')) $fullPriceDiv.removeClass('hide');
            }
            this.onTicketPriceChanged();
        },

        onTicketPriceChanged: function() {
            var tickets = StateManager.getTickets();
            var discSum = 0;
            var fullSum = 0;
            for (var ticketId in tickets) if (tickets.hasOwnProperty(ticketId)) {
                var ticket = tickets[ticketId];
                var fullPrice = ticket['price'];
                var price = ticket.resultDiscPrice;
                fullSum += fullPrice;
                discSum += (price? price : fullPrice);
            }

            var $summaryDiscPriceElement = this.$ticketBoard.find('#sum-disc-price');
            var $summaryFullPriceElement = this.$ticketBoard.find('#sum-full-price');
            $summaryDiscPriceElement.text(discSum);
            if (fullSum == discSum) {
                $summaryFullPriceElement.hide();
            } else {
                $summaryFullPriceElement.text(fullSum);
                $summaryFullPriceElement.show();
            }
        },

        show: function(callback) {
            var that = this;
             this.$ticketBoard.animate({
                 height: "toggle"
             }, 400, function() {
                 if (callback) callback();
                 //showing price block
                 that.$ticketBoard.find('.ticket-price-sum').animate({
                     opacity: 1
                 }, 200);
             });
        },

        hide: function(callback) {
            var ticketPriceSum = this.$ticketBoard.find('.ticket-price-sum');
            if (ticketPriceSum.css('opacity') === '0') {
                if (callback) callback();
                return;
            }
            var that = this;
            //hiding price block
            ticketPriceSum.animate({
                opacity: 0
            }, 200, function() {
                that.$ticketBoard.animate({
                    height: "toggle"
                }, 400, function() {
                    if (callback) callback();
                });
            });
        },

        clean: function(callback) {
            this.$tickets.empty();
            this.discounts = {};
            this.serverLockedTickets = {};
            var ticketsToUnlock = StateManager.getServerLockedTickets();
            if (ticketsToUnlock.length) {
                new Request('unlockTickets', {tickets: JSON.stringify(ticketsToUnlock)}).send(function() {
                    if (callback) callback();
                });
            } else {
                if (callback) callback();
            }
            StateManager.getOrder().tickets = {};
        }
    };

    window.BusSchemePopup = function(ticket, trip) {
        var that = this;
        this.allSeats = trip['seats'];
        this.lockedSeats = trip['lockedSeats'];
        this.initialSeatId = trip.isForth? ticket['seatId'] : ticket['backSeatId'];

        this.popup = new Popup(clientBundle.bus_scheme, setraBus, 'popup-bus', function(popupId) {
            var $popupEl = $(popupId);
            for (var i = 0; i < that.allSeats.length; i++) {
                var seat = that.allSeats[i];
                var $current = $popupEl.find('#seat-'+seat['seatNum']);
                $current.attr('seatId', seat.id);

                if ($.inArray(seat.id, that.lockedSeats) >= 0) continue;

                $current.removeClass('blocked');
            }
            var choice = that.initialSeatId;
            var $choice = $('[seatId='+choice+']');
            $choice.removeClass('blocked');
            $choice.addClass('active');


            $popupEl.find('.bus-setra').on('click', function(event){
                var $target = $(event.target);
                if ($target.prop('tagName') === 'A') {
                    event.preventDefault();
                    var temp = $target.attr('seatId');
                    if ($.inArray(temp, that.lockedSeats) >= 0) return false;

                    choice = temp;

                    var $prevActive = $popupEl.find('a.active');
                    $prevActive.removeClass('active');
                    if (!$target.hasClass('active')) $target.addClass('active');
                    return false;
                }
            });
            $popupEl.find('#bus-seat-cancel').on('click', function(){
                that.popup.destroy();
            });

            $popupEl.find('#bus-seat-ok').on('click', function() {
                that.lockedSeats.splice($.inArray(that.initialSeatId, that.lockedSeats), 1);
                that.lockedSeats[that.lockedSeats.length] = choice;

                var seat = dataStore.get(choice);
                var ticketCopy = JSON.parse(JSON.stringify(ticket));
                if (trip.isForth) {
                    ticketCopy['seatId'] = choice;
                    ticketCopy.seatNum = dataStore.get(choice).seatNum;
                } else {
                    ticketCopy['backSeatId'] = choice;
                    ticketCopy.backSeatNum = dataStore.get(choice).seatNum;
                }
                var data = {tripId: trip.id, forth: trip.isForth, ticket: JSON.stringify(ticketCopy)};
                var prevTicketId = trip.isForth? ticket['ticketId'] : ticket['backTicketId'];
                if (prevTicketId) {
                    data.unlockTicketId = prevTicketId;
                }

                new Request('lockSeat', data).send(function(data) {
                    if (trip.isForth) {
                        ticket['ticketId'] = data['ticketId'];
                        ticket['seatId'] = seat.id;
                        ticket['seatNum'] = seat['seatNum'];
                    } else {
                        ticket['backTicketId'] = data['ticketId'];
                        ticket['backSeatId'] = seat.id;
                        ticket['backSeatNum'] = seat['seatNum'];
                    }
                    StateManager.addServerLockedTicket(trip.id, data.ticketId);
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
            return '<div id='+tripId+' class="trip">' +
                '<div><h4>'+clientBundle.trip+' '+startCity+' - '+endCity+'</h4>' + '<span class="available-seats">'+seatCount+' '+clientBundle.seats+'</span></div>' +
                '<div class="clear-both">' +
                '<div class="trip-info">' +
                '<div><span>'+clientBundle.departure+':</span><span class="trip-info-date">'+startDate+'</span></div>' +
                '<div><span>'+clientBundle.arrival+':</span><span class="trip-info-date">'+endDate+'</span></div>' +
                '</div>' +
                '</div>' +
                '</div>'
        };

        window.uc.ticketTemplate = function(user, phone, startCity, endCity, forthTrip, forthSeat, backTrip, backSeat, discounts) {
            var price = forthTrip? forthTrip.price : backTrip.price;
            var discPrice = forthTrip? forthTrip.discPrice : backTrip.discPrice;

            var resultPrice = discPrice ? discPrice : price;
            var discountOptions = '';
            for (var i = 0, size = discounts.length; i < size; i++) {
                discountOptions += '<option id="discount-' + discounts[i].id + '" ' +
                    (discounts[i].id === 'NONE' ? 'selected' : '') + '>' + discounts[i].text + '</option>';
            }
            if (UserSettings.currency == 'uah') {
                resultPrice = CurrencyUtils.round(resultPrice * EURUAH, -1);
                if (discPrice) price = CurrencyUtils.round(price * EURUAH, -1);
            }
            var ticketsCount = $('.tickets').children().length;
            var clazz = 'ticket active' + (forthTrip && backTrip ? '' : ' one-way') + (ticketsCount === 0 ? '' : ' card');
            var start =
                '<div id="ticket' + ticketsCount + '" class="' + clazz + '" style="z-index:' + (ticketsCount + 5) + ';">' +
                '<div class="ticket-row">'+
                '<div class="ticket-title">Квиток #'+(++ticketsCount)+'</div>'+
                    '<div class="price">'+
                    '<div class="price-disc clear-both"><div class="price-right"><span class="sign">'+UserSettings.currencySign+'</span><span class="price-self">'+resultPrice+'</span></div></div>'+
                    '<div class="price-full '+(discPrice?'' : 'hide')+'"><span class="price-self">'+(discPrice? price : '')+'</span></div>'+
                    '</div>'+
                    '</div>'+
                '<div class="ticket-row">'+
                '<input type="text" '+(user? 'value="'+user+'"':'')+' placeholder="'+clientBundle.name_and_surname+'" class="ticket-input username">'+
                '</div>'+
                '<div class="ticket-row">' +
                '<input type="text" '+(phone? 'value="'+phone+'"':'')+' placeholder="'+clientBundle.phone+'" class="ticket-input phone">' +
                '<input type="text" '+(phone? 'value="'+phone+'"':'')+' placeholder="'+clientBundle.phone+'" class="ticket-input phone second">' +
                '</div>' +
                '<div class="ticket-row"><select class="ticket-input select">'+discountOptions+'</select></div>'+
                '<div class="ticket-row">'+
                '<textarea placeholder="примітка" class="ticket-input ticket-note"></textarea>'+
                '</div>'+
                '<div class="ticket-row">'+
                    ticketTripInfo(forthTrip, forthSeat, true);
            if (!backTrip) return start + '</div></div>';
                    return start + ticketTripInfo(backTrip, backSeat, false) + '</div></div>';
        };

        function ticketTripInfo(trip, seat, isForth) {
            var route = trip.routeFirstCity +' - '+ trip.routeEndCity;
            return '<div class="ticket__trip-info">' +
                        '<div>'+route+'</div>' +
                        '<div>'+trip.startDate+'</div>'+
                        '<button class="btn btn-green light '+(isForth?'forth':'back')+'">'+
                            clientBundle.seat+' #<span id="'+seat.id+'">'+seat.seatNum+'</span></button>'+
                    '</div>';
        }
    })();

    function showConfirm(locationOrderId) {
        new Request('orderExists', {orderId: locationOrderId}).send(function(data) {
            if (data && data['orderId']) {
                var p = new Popup(data.title, '<div>'+data.message+'</div><div><div class="clear-both"><button id="reserve-ready" class="btn btn-right btn-green">OK</button></div></div>', 'white', function(){
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

        var selectionBoard = new SelectionBoard($('.c-banner-search-input-container'));
        selectionBoard.init();

        var locationOrderId = location.getParam('orderId');
        if (locationOrderId) {
            showConfirm(locationOrderId);
        }
    });
})();
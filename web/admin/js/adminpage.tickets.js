(function(){
    var ACTIVE_SEAT_CHANGED_EVENT = 'ACTIVE_SEAT_CHANGED_EVENT';
    var TICKET_DISCOUNT_CHANGED_EVENT = 'TICKET_DISCOUNT_CHANGED_EVENT';

    var activeTrip = null;
    var activeTicket = null;
    var changedTickets = {}; //example - {tripId: {tickets}}

    function cacheTicket(ticket) {
        var changedTicketsForTrip = changedTickets[activeTrip.id];
        if (!changedTicketsForTrip) {
            changedTickets[activeTrip.id] = {};
            changedTicketsForTrip = changedTickets[activeTrip.id];
        }
        if (!changedTicketsForTrip[ticket.id]) {
            changedTicketsForTrip[ticket.id] = ticket;
        }
    }

    function findTicketByNumber(tickets, seatNum) {
        if (tickets instanceof Array) {
            for (var i = 0; i < tickets.length; i++) {
                var currentTicket = tickets[i];
                if (currentTicket.seat === seatNum) {
                    return currentTicket;
                }
            }
        } else {
            for (var ticketId in tickets) {
                if (!tickets.hasOwnProperty(ticketId)) {
                    continue;
                }
                var current = tickets[ticketId];
                if (current.seat === seatNum) {
                    return current;
                }
            }
        }
        return null;
    }

    function compareTickets(actual, changed) {
        if (changed.passenger !== actual.passenger) {
            return true;
        }
        if (changed.phones !== actual.phones) {
            return true;
        }
        if (changed.editStatus !== actual.editStatus) {
            return true;
        }
        return false;
    }

    function parsePhones(phonesString) {
        var phones = [];
        if (!phonesString) {
            return phones;
        }
        phones = phonesString.split(',');
        for (var i = 0; i < phones.length; i++) {
            phones[i] = phones[i].trim();
        }
        return phones;
    }
    
    function setPhonesFromString(ticket, phonesString) {
        var phones = parsePhones(phonesString);
        if (phones.length) {
            if (phones.length >= 1) {
                ticket.phone1 = phones[0];
            }
            if (phones.length >= 2) {
                ticket.phone2 = phones[1];
            }
        }
    }
    
    var seatsPromise = new Promise(function (resolve, reject) {
        new Request('getSeats').send(function(data) {
            resolve(data.allSeats);
        }, reject);
    });

    var contactsPromise = new Promise(function(resolve, reject) {
        if ($.urlParam('oauth')) {
            new Request('allContacts').send(function(data) {
                if (data.contacts) {
                    localStorage.setItem('googleContacts', JSON.stringify(data.contacts));
                    localStorage.setItem('googleContactsTimestamp', new Date().toLocaleString());

                    resolve(data.contacts);

                    var clean_uri = location.protocol + "//" + location.host + location.pathname;
                    if (window.history && window.history.replaceState) {
                        window.history.replaceState({}, document.title, clean_uri);
                    } else {
                        window.location.href = clean_uri;
                    }
                } else {
                    Message.show('Не знайдено жодного контакту');
                    reject();
                }
            });
        } else {
            var googleContactsStr = localStorage.getItem('googleContacts');
            var contacts = googleContactsStr? JSON.parse(googleContactsStr) : {};
            resolve(contacts);
        }
    });

    contactsPromise.then(function(){}); //immediately call

    function setActiveTicketValue(prop, val) {
        if (activeTicket == null) {
            return;
        }
        if (activeTicket[prop] !== val) {
            activeTicket.isChanged = true;
            activeTicket[prop] = val;
        }
    }


    var TicketsToolbar = function($toolbar) {
        this.$toolbar = $toolbar;
    };

    TicketsToolbar.prototype = {
        constructor : TicketsToolbar,

        init: function () {
            var that = this;
            this.$toolbar.find('#get-contacts').click(function() {
                that.loadContacts();
            });

            this.$toolbar.find('#save-button').click(function() {
                that.showConfirmSavePopup();
            });
        },

        loadContacts: function() {
            contactsPromise.then(function(contacts) {
                if ($.isEmptyObject(contacts)) {
                    window.location.href = '/retrieveContacts';
                } else {
                    new Popup('Підтвердження',
                        window.uc.loadContactsTemplate(localStorage.getItem('googleContactsTimestamp')), '',
                        function(popupId) {
                            var popup = this;
                            $(popupId).find('#button-ok').click(function() {
                                window.location.href = '/retrieveContacts';
                            });
                            $(popupId).find('#button-cancel').click(function() {
                                popup.destroy();
                            });
                        }).show();
                }
            });
        },

        showConfirmSavePopup: function() {
            var changedTicketsClone = JSON.parse(JSON.stringify(changedTickets));

            for (var tripId in changedTicketsClone) {
                if (!changedTicketsClone.hasOwnProperty(tripId)) {
                    continue;
                }
                var tripTickets = changedTicketsClone[tripId];
                for (var ticketId in tripTickets) {
                    if (!tripTickets.hasOwnProperty(ticketId)) {
                        continue;
                    }
                    var ticket = tripTickets[ticketId];
                    if (this.shouldBeExcluded(ticket)) {
                        delete tripTickets[ticketId];
                    } else {
                        setPhonesFromString(ticket, ticket.phones);
                    }

                    if ($.isEmptyObject(tripTickets)) {
                        delete changedTicketsClone[tripId];
                    }
                }
            }
            if ($.isEmptyObject(changedTicketsClone)) {
                new Popup('Підсумок', '<div>Змін не виявлено</div>').show();
            } else {
                this.showSummary(changedTicketsClone);
            }
        },

        showSummary: function(changedTickets) {
            new Popup('Підсумок', window.uc.changesConfirmationTemplate(changedTickets), '',
                function (popupId) {
                    var that = this;
                    $(popupId).on('click', '.action-glyph-link', function(ev) {
                        ev.preventDefault();
                        var $removed = $(this).parents('tr');
                        var ticketId = $removed.attr('id');
                        var tripId = $removed.attr('data-tripId');

                        delete changedTickets[tripId][ticketId];
                        $removed.remove();

                        return false;
                    });

                    $(popupId).find('#button-ok').click(function() {
                        that.destroy();
                        new Request('saveTickets', {changedTickets: JSON.stringify(changedTickets)})
                            .send(function() {
                                new Popup('Збережено', '<div>Дані успішно збережено</div>').show();
                            });
                    });
                    $(popupId).find('#button-cancel').click(function() {
                        that.destroy();
                    });
                }).show();
        },

        shouldBeExcluded : function(ticket) {
            if (!ticket.isChanged) {
                return true;
            } else if (ticket.id.indexOf('temp-') !== -1) {
                if (ticket.editStatus === 'removed') {
                    return true;
                }
            } else {
                var initialTicket = dataStore.getOfType(ticket.id, 'Ticket');
                if (initialTicket) {
                    var actuallyChanged = compareTickets(initialTicket, ticket);
                    if (!actuallyChanged) {
                        return true;
                    }
                }
            }
            return false;
        }
    };

    var PassengerForm = function($formContainer) {
        this.$nameInput = $formContainer.find('#name-substring-search');
        this.$substringSearchResults = this.$nameInput.siblings('.name-substring-search-dropdown');
        this.$dropdownOptions = this.$substringSearchResults.find('.dropdown-menu');
        this.$phonesInput = $formContainer.find('#passenger-phone');
        this.$noteArea = $formContainer.find('#passenger-note');
        this.$ticketPriceElement = $formContainer.find('#active-ticket-price');
        this.$discountsDropdown = $formContainer.find('.dropdown-discounts');
        this.$discountValueSelect = this.$discountsDropdown.find('.dropdown-value');

        var that = this;
        this.$nameInput.change(function() {
            setActiveTicketValue('passenger', $(this).val());
        });
        this.$phonesInput.change(function() {
            setActiveTicketValue('phones', $(this).val());
        });
        this.$noteArea.change(function() {
            setActiveTicketValue('note', $(this).val());
        });

        this.$discountsDropdown.find('.dropdown-menu').on('click', 'a', function() {
            that.onDiscountChanged($(this));
        });

        contactsPromise.then(function(contacts) {
            that.$nameInput.on('keyup', function() {
                that.performSubstringSearch(contacts);
            });

            that.$nameInput.click(function() {
                if (that.$substringSearchResults.hasClass('open')) {
                    that.$substringSearchResults.removeClass('open');
                }
            });

            that.$dropdownOptions.on('click', 'a', function(ev) {
                ev.preventDefault();
                var contactId = $(this).attr('id');
                that.onContactSelected(contacts[contactId]);
                return false;
            });
        });

        EventBus.addEventListener(TICKET_DISCOUNT_CHANGED_EVENT, function(event, discountId) {
            setActiveTicketValue('discountId', discountId.substring(9));
        });
    };

    PassengerForm.prototype = {
        constructor : PassengerForm,

        performSubstringSearch: function(contacts) {
            if ($.isEmptyObject(contacts)) {
                return;
            }

            var currentValue = this.$nameInput.val();
            if (!currentValue) {
                return;
            }

            var foundContacts = [];
            var currentLCValue = currentValue.toLowerCase();
            for (var id in contacts) {
                if (!contacts.hasOwnProperty(id)) {
                    continue;
                }
                var currentContact = contacts[id];
                if(currentContact.fullName.indexOf(currentValue) !== -1 || (currentValue === currentLCValue && currentContact.fullName.indexOf(currentLCValue) !== -1)) {
                    if (!currentContact.id) {
                        currentContact.id = id;
                    }
                    foundContacts.push(currentContact);
                }
            }

            this.$substringSearchResults.removeClass('open');
            if (!foundContacts.length) {
                return;
            }
            foundContacts.sort(function(c1, c2){
                return c1.fullName.localeCompare(c2.fullName);
            });
            var allResultsCount = foundContacts.length;

            if (allResultsCount > 20) {
                foundContacts = foundContacts.slice(0, 20);
            }
            this.$dropdownOptions.empty();

            var options = '';
            for (var i = 0, size = foundContacts.length; i < size; i++) {
                var contact = foundContacts[i];
                options += '<li><a id="'+contact.id+'" href="#">' + contact.fullName +'</a></li>';
            }
            if (allResultsCount > 20) {
                options += '<li><div class="dropdown-list-summary">' +
                    '<span>...</span><span class="all-results-count">всього '+allResultsCount+' результатів</span>' +
                    '</div></li>'
            }
            this.$dropdownOptions.html(options);
            this.$substringSearchResults.addClass('open');
        },
        
        onContactSelected: function(contact) {
            this.$nameInput.val(contact.fullName);
            this.$nameInput.trigger('change');
            
            if (contact.phoneNumbers) {
                this.$phonesInput.val(contact.phoneNumbers.join(', '));
            } else {
                this.$phonesInput.val('');
            }
            this.$phonesInput.trigger('change');
            
            this.$noteArea.val(contact.note || '');
            this.$noteArea.trigger('change');

            this.$substringSearchResults.removeClass('open');
        },

        onDiscountChanged: function($currentDiscountElement) {
            var selectedId = $currentDiscountElement.attr('id');
            if (selectedId) {
                this.$discountValueSelect.attr('data-selectedId', selectedId);
            }
            this.$discountValueSelect.text($currentDiscountElement.text());

            EventBus.dispatch(TICKET_DISCOUNT_CHANGED_EVENT, null, selectedId);
        },

        fillForm : function (ticket) {
            this.$nameInput.val(ticket.passenger || '');
            this.$phonesInput.val(ticket.phones || '');
            this.$noteArea.val(ticket.note || '');
            this.$ticketPriceElement.text(ticket.price);
            if (ticket.discountId) {
                this.$discountValueSelect.attr('data-selectedId', ticket.discountId);
                this.$discountValueSelect.text($('#'+ticket.discountId).text());
            }
        },
        
        clearForm: function() {
            this.$nameInput.val('');
            this.$phonesInput.val('');
            this.$noteArea.val('');
            this.$ticketPriceElement.text('');
            this.$discountValueSelect.attr('data-selectedId', 'discount-NONE');
            this.$discountValueSelect.text('Без знижки');
        }
    };

    var BusScheme = function($busScheme) {
        this.initialized = false;

        this.$busScheme = $busScheme;

        var that = this;
        $('.bus-scheme').on('click', 'a', function(event) {
            event.preventDefault();
            that.onSeatSelect($(this));
            return false;
        });
    };

    BusScheme.prototype = {
        constructor : BusScheme,

        init: function(seats) {
            if (this.initialized) {
                return;
            }

            for (var i = 0; i < seats.length; i++) {
                var seat = seats[i];
                var $current = this.$busScheme.find('#seat-'+seat['seatNum']);
                $current.attr('seatId', seat.id);
            }
            this.initialized = true;
        },

        setSelected: function(seatNum) {
            this.$busScheme.find('#seat-'+seatNum).addClass('selected');
        },

        onSeatSelect: function($selectedSeat) {
            var seatNum = $selectedSeat.attr('id').substring(5);
            if (!$selectedSeat.hasClass('selected')) {
                this.$busScheme.find('a.selected').removeClass('selected');
                $selectedSeat.addClass('selected');
                
                var ticket = null;
                var changedTicketsForTrip = changedTickets[activeTrip.id];
                if (changedTicketsForTrip) {
                    ticket = findTicketByNumber(changedTicketsForTrip, seatNum);
                }
                if (ticket == null) {
                    ticket = findTicketByNumber(activeTrip.tickets, seatNum);
                    if (ticket) {
                        ticket = JSON.parse(JSON.stringify(ticket)); // cloning from initial tickets
                    }
                }
                if (ticket == null) {
                    ticket = {
                        id: 'temp-client-' + Date.now(),
                        tripId: activeTrip.id,
                        seat : seatNum,
                        price: activeTrip.price,
                        editStatus: 'removed',
                        isChanged: false
                    };
                }
                ticket.seatId = $selectedSeat.attr('seatid');
                EventBus.dispatch(ACTIVE_SEAT_CHANGED_EVENT, null, ticket);
            } else {
                if (!activeTicket.editStatus) {
                    activeTicket.editStatus = 'added';
                }
                if (activeTicket.editStatus === 'added') {
                    $selectedSeat.removeClass('blocked');
                    activeTicket.editStatus = 'removed';
                } else if(activeTicket.editStatus === 'removed') {
                    $selectedSeat.addClass('blocked');
                    activeTicket.editStatus = 'added';
                }
                activeTicket.isChanged = true;
            }
            return false;
        },
        
        markBlockedSeats: function(tickets) {
            for (var i = 0; i < tickets.length; i++) {
                var ticket = tickets[i];
                this.$busScheme.find('#seat-'+ticket.seat).addClass('blocked');
            }
        },

        markEditedSeats: function(tickets) {
            for (var ticketId in tickets) {
                if (!tickets.hasOwnProperty(ticketId)) {
                    continue;
                }
                var ticket = tickets[ticketId];
                var $seat = this.$busScheme.find('#seat-'+ticket.seat);
                if (ticket.editStatus === 'added') {
                    $seat.addClass('blocked');
                } else if (ticket.editStatus === 'removed') {
                    $seat.removeClass('blocked');
                }
            }
        },

        unmarkAll: function() {
            this.$busScheme.find('.selected').removeClass('selected');
            this.$busScheme.find('tbody>tr>td>a').removeClass('blocked');
        }
    };

    $(document).ready(function() {
        new TicketsToolbar($('.tickets-toolbar')).init();
        var passengerForm = new PassengerForm($('#ticket-detailed-info'));
        var busScheme = new BusScheme($('.bus-scheme'));

        seatsPromise.then(function (allSeats) {
            busScheme.init(allSeats);
        });

        EventBus.addEventListener(TRIP_CHANGED_EVENT, function(event, trip) {
            activeTrip = trip;

            new Request('ticketsForTrip', {tripId: trip.id, excludeBlockedTickets: false}).send(function(data) {
                dataStore.setAll(data.tickets, 'Ticket');
                trip.tickets = data.tickets;

                busScheme.unmarkAll();
                busScheme.markBlockedSeats(trip.tickets);

                if (changedTickets[trip.id]) {
                    busScheme.markEditedSeats(changedTickets[trip.id]);
                }
                if (trip.tickets.length) {
                    var firstTicket = trip.tickets[0];
                    firstTicket = JSON.parse(JSON.stringify(firstTicket)); // cloning from initial tickets
                    cacheTicket(firstTicket);
                    busScheme.setSelected(firstTicket.seat);

                    EventBus.dispatch(ACTIVE_SEAT_CHANGED_EVENT, null, firstTicket);
                }
            });
        });

        EventBus.addEventListener(ACTIVE_SEAT_CHANGED_EVENT, function(event, ticket) {
            cacheTicket(ticket);
            //cleaning form
            passengerForm.clearForm();

            if (!ticket.hasOwnProperty('isChanged')) {
                ticket.isChanged = false;
            }
            activeTicket = ticket;
            passengerForm.fillForm(activeTicket);
        });
    });
})();

if (!uc) {
    var uc = {};
}
window.uc.loadContactsTemplate = function(timestamp) {
    return '<div style="width: 300px; white-space: normal;">Контакти вже були завантажені '+timestamp+'. Якщо з цього часу в Вашому списку контактів відбулись будь-які зміни - натисніть кнопку \'Завантажити\'</div>' +
    '<div class="clear-both" style="margin-top: 10px;">' +
        '<button id="button-ok" type="button" style="float: left; margin-right: 10px;margin-bottom: 10px;" class="btn btn-popup"><span class="glyphicon glyphicon-ok" aria-hidden="true"></span>Завантажити</button>'+
        '<button id="button-cancel" type="button" class="btn btn-default"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span>Відміна</button>'+
    '</div>';
};

window.uc.changesConfirmationTemplate = function(changedTickets) {
    var tableBody = '';
    for (var tripId in changedTickets) {
        if (!changedTickets.hasOwnProperty(tripId)) {
            continue;
        }
        var tripTickets = changedTickets[tripId];
        if ($.isEmptyObject(tripTickets)) {
            continue;
        }
        var trip = dataStore.get(tripId);
        tableBody += '<tr class="table-subheader"><td colspan="42">' +trip.stringData+ '</td></tr>';
        for (var ticketId in tripTickets) {
            if (!tripTickets.hasOwnProperty(ticketId)) {
                continue;
            }
            var ticket = tripTickets[ticketId];
            tableBody += '<tr id="'+ticket.id+'" data-tripid="'+tripId+'"><td>' + ticket.passenger +'</td>' +
                '<td>'+(ticket.phones || '')+'</td>' +
                '<td>' + ticket.seat +'</td>' +
                '<td>' + ticket.price +'</td>' +
                '<td>' + (ticket.note || '')+'</td>' +
                '<td class="ticket-status-'+ticket.editStatus+'"><span>' + ticket.editStatus + '</span></td>'+
                '<td><a class="action-glyph-link" href="#"><span class="glyphicon glyphicon-trash" title="Видалити" aria-hidden="true"></span></a></td>' +
                '</tr>';
        }
    }

    return '<div class="summary-table"><table class="tablesaw" data-tablesaw-mode="stack">'+
                '<thead>'+
                    '<tr>'+
                        '<th>Пасажир</th>'+
                        '<th>Телефони</th>'+
                        '<th>Місце</th>'+
                        '<th>Оплата</th>'+
                        '<th>Примітка</th>'+
                        '<th>Статус</th>'+
                        '<th>&nbsp;</th>'+
                    '</tr>'+
                '</thead>'+
                '<tbody>'+ tableBody +'</tbody>'+
            '</table>' +
        '<div class="clear-both" style="margin-top: 10px;">' +
            '<button id="button-ok" type="button" style="float: left; margin-right: 10px;margin-bottom: 10px;" class="btn btn-popup"><span class="glyphicon glyphicon-ok" aria-hidden="true"></span>Зберегти</button>'+
            '<button id="button-cancel" type="button" class="btn btn-default"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span>Відміна</button>'+
        '</div></div>';
};
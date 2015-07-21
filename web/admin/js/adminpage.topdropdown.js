(function(){
    window.TRIP_CHANGED_EVENT = 'TRIP_CHANGED_EVENT';
    var inMemoryAllSeats = [];

    var dropdownPromise = new Promise(function(resolve, reject){
        $(document).ready(function(){
            var $headerDropdown = $('.header-dropdown');
            if ($headerDropdown.length) {
                resolve($headerDropdown);
            } else {
                reject();
            }
        });
    });

    dropdownPromise.then(function($headerDropdown){
        var $dropdownText = $headerDropdown.find(' .dropdown-value');
        $headerDropdown.find('.dropdown-menu').on('click', 'a', function(ev) {
            var $thisAnchor = $(this);
            $dropdownText.text($thisAnchor.find('.drop-value').text());
            var tripId = $thisAnchor.attr('id');
            if (!dataStore.has(tripId)) {
                new Request('getTrip', {tripId: tripId}).send(function(data){
                    dataStore.set(data.trip, 'Trip');
                    data.trip.allSeats = inMemoryAllSeats;
                    EventBus.dispatch(TRIP_CHANGED_EVENT, null, data.trip);
                });
            } else {
                var activeTrip = dataStore.get(tripId);
                activeTrip.allSeats = inMemoryAllSeats;
                EventBus.dispatch(TRIP_CHANGED_EVENT, null, activeTrip);
            }
        });
    });

    new Request('tripsForPeriod').send(function(data){
        var trips = data['trips'];
        if (!trips) {
            new Popup('Помилка', '<div>Не знайдено жодної поїздки</div>').show();
            return;
        }

        dropdownPromise.then(function($headerDropdown){
            dataStore.set(trips[0], 'Trip');
            var $dropdownText = $headerDropdown.find(' .dropdown-value');
            $dropdownText.text(trips[0].stringData);

            inMemoryAllSeats = trips[0].allSeats;
            EventBus.dispatch(TRIP_CHANGED_EVENT, null, trips[0]);
            var $dropdownOptions = $headerDropdown.find('.dropdown-menu');
            for (var i = 0, size = trips.length; i < size; i++) {
                var trip = trips[i];
                var almostFull = trip.passCount/trip.allCount > 0.8;
                var counterClass = 'pass-counter' + almostFull? ' red' : '';
                $dropdownOptions.append(
                    '<li>' +
                    '<a id="'+trip.id+'" href="#">' +
                    '<span class="drop-value">'+trip.stringData+'</span>, ' +
                    '<span class="'+counterClass+'">'+trip.passCount +'/'+trip.allCount+'</span></a>' +
                    '</li>');
            }
        });
    });
})();
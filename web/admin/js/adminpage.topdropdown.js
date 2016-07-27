(function(){
    window.TRIP_CHANGED_EVENT = 'TRIP_CHANGED_EVENT';
    
    function renderTripOption($dropdownOptions, trip, quiteFull) {
        var template =
            '<li><a id="'+trip.id+'" href="#">' +
                    '<span class="drop-value">'+trip.stringData+'</span>, ' +
                    '<span class="pass-counter'+(quiteFull? ' red' : '')+'">'+trip.passCount +'/'+trip.allCount+'</span>' +
            '</a></li>';
        $dropdownOptions.append(template);
    }

    var dropdownPromise = new Promise(function(resolve, reject) {
        $(document).ready(function() {
            var $headerDropdown = $('.header-dropdown');
            if ($headerDropdown.length) {
                resolve($headerDropdown);
            } else {
                reject();
            }
        });
    });

    dropdownPromise.then(function($headerDropdown) {
        var $dropdownText = $headerDropdown.find(' .dropdown-value');
        $headerDropdown.find('.dropdown-menu').on('click', 'a', function() {
            var $thisAnchor = $(this);
            $dropdownText.text($thisAnchor.find('.drop-value').text());
            var tripId = $thisAnchor.attr('id');
            var activeTrip = dataStore.get(tripId);
            
            EventBus.dispatch(TRIP_CHANGED_EVENT, null, activeTrip);
        });
    });

    new Request('tripsForPeriod').send(function(data) {
        var trips = data;
        if (!trips) {
            new Popup('Помилка', '<div>Не знайдено жодної поїздки</div>').show();
            return;
        }

        dataStore.setAll(trips, 'Trip');

        dropdownPromise.then(function($headerDropdown) {
            var firstTrip = trips[0];
            var $dropdownText = $headerDropdown.find(' .dropdown-value');
            $dropdownText.text(firstTrip.stringData);

            var $dropdownOptions = $headerDropdown.find('.dropdown-menu');
            for (var i = 0, size = trips.length; i < size; i++) {
                var trip = trips[i];
                var quiteFull = trip.passCount/trip.allCount > 0.8;

                renderTripOption($dropdownOptions, trip, quiteFull);
            }

            $headerDropdown.find('.dropdown-toggle-hidden').removeClass('dropdown-toggle-hidden');
            EventBus.dispatch(TRIP_CHANGED_EVENT, null, firstTrip)
        });
    });
})();
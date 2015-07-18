(function(){
    window.uc = {};
    var clientBundle = window.clientBundle;

    location.getParam = function (name) {
        name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
        var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
            results = regex.exec(location.search);
        return results == null ? null : decodeURIComponent(results[1].replace(/\+/g, " "));
    }

    $(document).on('ready', function() {

        $('.navbar-header .navbar-toggle').on('click tap', function(e){
            $('#navbar').animate({height: 'toggle'}, 300);
        });

        EventBus.addEventListener('user_authorized', function(event) {
            var user = cookies.get('name');
            $('#cabinet').text(user);
            showUpperRightMenu(true);
        });

        EventBus.addEventListener('user_loggedout', function(event) {
            cookies.removeAll();
            showUpperRightMenu(false);
        });
        if (window.isAuthorized()) {
            EventBus.dispatch('user_authorized');
        } else {
            showUpperRightMenu(false);
        }
        if (window.cookies.get("pass")) {
            var data = {email: window.cookies.get("email"),
                password: window.cookies.get("pass"),
                auto: true};
            new Request("login", data).send(function(data){
                showCurrencyWidget(data['EURUAH']);
            }, function() {}); // quietly fails if server error arises
        } else {
            var req = new Request('welcome');
            req.setShowLoading(false);
            req.send(function(data) {
                showCurrencyWidget(data['EURUAH']);
            }, function() {}); // quietly fails if server error arises
        }
        // setting up content height
        var $content = $('.page-content');
        if ($content[0]) {
            var minHeight = $( window ).height() - $('footer').outerHeight() - $('header').outerHeight()-($content.outerHeight() - $content.height());
            $content.css('min-height', minHeight + 'px');
        }

        location.getParam = function (name) {
            name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
            var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
                results = regex.exec(location.search);
            return results == null ? null : decodeURIComponent(results[1].replace(/\+/g, " "));
        }
    });

    function showCurrencyWidget(rate) {
        window.EURUAH = rate? rate.toFixed(2) : 24.15;
        $('#currency-container').append(uc.currencyWidget(EURUAH));
    }

    function showUpperRightMenu(isLoggedIn) {
        function showAndHide(show1, bind1, show2, bind2, hide1, unbind1, hide2, unbind2){
            show1.show();
            show2.show();
            if (hide1.css('display') !== 'none') {
                hide1.hide();
                hide1.off('click');
                hide2.hide();
                hide2.off('click');
            }
            show1.on('click', function(event){event.preventDefault(); bind1(); return false});
            show2.on('click', function(event){event.preventDefault(); bind2(); return false});
        }
        var $cabinet = $('#cabinet');
        var $logout = $('#logout');
        var $login = $('#login');
        var $register = $('#register');
        if (isLoggedIn) {
            showAndHide($cabinet, cabinet, $logout, logout, $login, login, $register, register);
        } else {
            showAndHide($login, login, $register, register, $cabinet, cabinet, $logout, logout);
        }
    }

    function cabinet() {
        window.location.assign(location.origin + '/cabinet');
    }

    window.isAuthorized = function() {
        return window.cookies.get("email");
    };

    window.register = function(callback) {
        var content = window.uc.registerTemplate;
        var popup = new Popup(clientBundle.registration, content, '', function(popupId) { //TODO localize
            var captchaWidgetId = grecaptcha.render(
                'captcha-form',
                {sitekey:"6LfUKfQSAAAAAN_RtEDkPQnIn9sT5YS0Kp9yK__1", theme: 'light'}
            );

            $(popupId + ' #btn-register').on('click', function(event){
                event.preventDefault();
                var data = {};
                $(popupId + ' .forms input').each(function(index, element){
                    data[element.name] = element.value;
                });
                var errors = '';
                var $forms = $(popupId + ' .forms');
                var name = $forms.find('#reg-username').val();
                var surname = $forms.find('#reg-surname').val();
                var email = $forms.find('#reg-email').val();
                var phone = $forms.find('#reg-phone').val();
                var pass = $forms.find('#reg-pass').val();

                if (name === '') errors += clientBundle.name_field_should_not_be_empty+'<br>';
                if (surname === '') errors += clientBundle.surname_field_should_not_be_empty+'<br>';
                var isValidEmail = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/.test(email);
                if (!isValidEmail) errors += clientBundle.wrong_email_format+'<br>';
                var isPhoneValid = /^[\s()+-]*([0-9][\s()+-]*){6,20}$/.test(phone);
                if (!isPhoneValid) errors += clientBundle.wrong_phone_format+'<br>';
                var isPassValid = /^[0-9a-zA-Z]{8,}$/.test(pass);
                if (!isPassValid) errors += clientBundle.password_should_be_at_least_8_symbols_long +'<br>';

                if (errors !== '') {
                    new Message(errors, 10000);
                    return false;
                }

                data['captcha'] = grecaptcha.getResponse(captchaWidgetId);

                var req = new Request('reg', data);
                req.send(function(data){
                    popup.destroy();
                    onAuthorized(data);
                    if (callback) callback();
                });
                return false;
            });
        });
        popup.show();
    };

    window.login = function(callback) {
        var content = window.uc.loginTemplate;
        var popup = new Popup(clientBundle.login, content, '', function(popupId) { //TODO localize
            $(popupId + ' #btn-login').on('click', function(event){
                event.preventDefault();
                var data = {};
                $(popupId + ' .forms input').each(function(index, element){
                    data[element.name] = element.value;
                });

                var req = new Request('login', data);
                req.send(function(data){
                    popup.destroy();
                    onAuthorized(data);
                    if (callback) callback();
                });
                return false;
            });

            $(popupId + ' #or-register-link').click(function(e){
                e.preventDefault();
                popup.destroy();
                setTimeout(register, 100);
                return false;
            });

            $(popupId + ' #forgot-pass-link').click(function(e) {
                e.preventDefault();
                popup.destroy();
                renewPassword();
                return false;
            });
        });
        popup.show();
    };

    window.renewPassword = function() {
        var content = window.uc.renewPassTemplate;
        var popup = new Popup(clientBundle.password_renewal, content, '', function(popupId) {
            $(popupId + ' #btn-renew-pass').click(function(event){
                event.preventDefault();
                var email = $(popupId).find('#form-email').val();
                var isValidEmail = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/.test(email);
                if (!isValidEmail) {
                    new Message(clientBundle.wrong_email_format, 5000);
                    return false;
                }
                var data = {email: email};
                var req = new Request('renewPass', data);
                req.send(function(data){
                    popup.destroy();
                    new Popup(clientBundle.password_was_generated,
                            '<div>'+clientBundle.generated_password_was_sent_to_your_email_Please_use_it_in_order_to_have_access_to_the_service+'</div>')
                        .show();
                });
                return false;
            });
        });
        popup.show();
    };

    function onAuthorized(data) {
        document.cookie = "user=" + data['name'];
        document.cookie = "name=" + data['name'] + ' ' + data['surname'];
        document.cookie = "pass=" + data['pass'];
        document.cookie = "email=" + data['email'];
        document.cookie = "phone=" + data['phone'];
        EventBus.dispatch('user_authorized');
    }

    window.logout = function(callback) {
        var req = new Request('logout');
        req.send(function(data){
            EventBus.dispatch('user_loggedout');
            if (callback) callback();
        });
    };

    window.getDateField = function ($input, selectedDate, selectFunc, disableDayFn) {
        var now = new Date();
        var maxDate = new Date(now.getTime() + 6*30*24*60*60*1000);
        return new Pikaday({
            field: $input[0],
            firstDay:1,
            defaultDate: selectedDate? selectedDate : now,
            minDate: now,
            maxDate: maxDate,
            setDefaultDate: true,
            i18n: {
                previousMonth : clientBundle.previous_month,
                nextMonth     : clientBundle.next_month,
                months        : clientBundle.months.split(','),
                weekdays      : clientBundle.weekdays.split(','),
                weekdaysShort : clientBundle.weeksdays_short.split(',')
            },
            onSelect: selectFunc,
            disableDayFn: disableDayFn
        });
    }
})();

/*Popup*/
(function(){
    var toBeBlurred = [];
    var wrapperZindex = 1000;
    var popupZindex = 1003;

    function isIE() {
        return window.navigator.userAgent.indexOf("MSIE ") > 0;
    }

    function setMultibrowserProperty($element, property, value) {
        $element.css(property, value);
        $element.css('-webkit-'+property, value);
        $element.css('-ms-'+property, value);
        $element.css('-moz-'+property, value);
        $element.css('-o-'+property, value);
    }

    function getPopupLeft(popup) {
        return ($(window).width() - popup.width())/2;
    }

    function getPopupTop(popup) {
        var top = $(window).scrollTop() + ($(window).height() - popup.height())/2;

        return top > 0? top : 50;
    }

    function addBackgroundBlur() {
        if (!toBeBlurred.length) {
            toBeBlurred = [$('header'), $('.uc-banner'), $('.order-board-wrapper'), $('.page-content'), $('footer')];
        }
        for (var i = 0; i < toBeBlurred.length; i++) {
            setMultibrowserProperty(toBeBlurred[i], 'filter', 'blur(6px)');
        }
    }

    function removeBackgroundBlur() {
        for (var i = 0; i < toBeBlurred.length; i++) {
            setMultibrowserProperty(toBeBlurred[i], 'filter', 'blur(0px)');
        }
    }

    window.Popup = function(title, innerHTML, styles, onCreated) {
        this.id = $.now();
        this.title = title;
        this.styles = styles;
        this.wasShown = false;
        this.isShown = false;
        this.innerHTML = innerHTML;
        this.onCreated = onCreated;
        this.isDestroyed = false;

        this.show = function() {
            if (this.isDestroyed) return;
            if (this.isShown) return;
            if (this.wasShown) {
                this.$popupWrapper.show();
                this.isShown = true;
                return;
            }
            var that = this;
            //rendering canvas of the page
//            html2canvas(document.body, {
//                onrendered: function(canvas) {
                    wrapperZindex += 2;
//                    blurredCanvasZindex += 2;
                    popupZindex += 2;
                    that.$popupWrapper = $('<div id="popup-'+that.id+'"></div>');


                    var back = $('<div class="popup-back" style="width:'+$(document).width()+'px; height:'+$(document).height()+'px; z-index:'+wrapperZindex+'"></div>');
                    addBackgroundBlur();
                    that.$popupWrapper.append(back);
//                    var $canvas = $(canvas);
//                    $canvas.addClass('blurred-page');
//                    $canvas.css('zIndex', blurredCanvasZindex);
//                    that.$popupWrapper.append($canvas);
                    that.$popup = $('<div class="popup-self ' + that.styles + '" style="z-index:'+popupZindex+'"></div>');
                    that.$popupWrapper.append(that.$popup);
                    that.$popup.append($('<div class="popup-header"><span class="popup-title">'+that.title+'</span><div class="popup-close-block"><a href="#" class="popup-close"></a></div></div>'));
                    that.$popupContent = $('<div class="popup-content"><div class="message-container"></div></div>');
                    that.$popupContent.append($(that.innerHTML));
                    that.$popup.append(that.$popupContent);
                    $(document.body).append(that.$popupWrapper);
                    $('#popup-'+that.id + ' .popup-close').on('click', function(event) {event.preventDefault(); that.destroy(); return false;});

                    if (!that.wasShown && that.onCreated) that.onCreated.call(that, '#popup-'+that.id);
                    that.$popup.css({top: getPopupTop(that.$popup),
                                    left: getPopupLeft(that.$popup)});
                    that.isShown = true;
//                }
//            });
        };

        this.hide = function() {
            if (this.isDestroyed) return;
            if (!this.isShown) return;
            this.wasShown = true;
            this.$popupWrapper.hide();
            this.isShown = false;
        };

        this.destroy = function() {
            if (this.isDestroyed) return;
            this.isShown = false;
            this.wasShown = false;
            this.$popupWrapper.remove();
            this.$popupWrapper = null;
            this.$popup = null;
            this.innerHTML = null;
            this.isDestroyed = true;
            removeBackgroundBlur();
        };
    }

})();

(function(){
    window.Message = function(message, seconds) {
        this.element = $('<div class="message" style="display: none;">'+message+'</div>');
        var that = this;
        this.timerId = setTimeout(function(){
            that.element.removeClass('shown');
            clearTimeout(that.timerId);
        }, seconds? seconds : 3000);
        var popup = $('.popup-self');
        if (popup[0]) {
            popup.find('.message-container').append(this.element); //todo
        } else {
            $('#message-container').append(this.element);
        }
        this.element.addClass('shown');
        this.element.show();
    }

})();
/*Ajax*/
(function(){
    window.Request = function(action, params) {
        var reqParams = params || {};
        reqParams.a = action;
        this.settings = {
            url: '/process',
            type: 'POST',
            dataType: 'json',
            data: reqParams
        };
        this.showLoading = true;
    };

    window.Request.prototype.setUrl = function(url) {
        this.settings.url = url;
    };

    window.Request.prototype.setMethod = function(method) {
        this.settings.type = method;
    };

    window.Request.prototype.setShowLoading = function(showLoading) {
        this.showLoading = showLoading;
    };

    window.Request.prototype.send = function(callback, failCallback) {
        if (this.showLoading) loader.show();
        var that = this;
        $.ajax(this.settings)
            .done(function(data){
                if (data.errorMessage) {
                    var popup = $('.popup-self');
                    if (popup[0] && popup.css('display') !== 'none') {
                        new Message(data.errorMessage, 5000);
                    } else {
                        new Popup(clientBundle.error, '<div>' + data.errorMessage + '</div>', 'white', function(){}).show(); //TODO localize
                    }
                } else {
                    callback(data.data);
                }
            })
            .fail(function( jqXHR, textStatus, errorThrown ) {
                //smth wasn't caught by normal action exception handler
                if (failCallback) {
                    failCallback();
                } else {
                    new Popup(clientBundle.error, '<div>'+clientBundle.service_is_temporarily_unavailable+'</div>', '', function(){}).show(); //TODO localize
                }
            })
            .always(function() {
                if (that.showLoading) loader.hide();
            });

    }

    //adding ajax loader

    var showingCount = 0;

    window.loader = {
        isShown: false,
        background: null,
        loader: null,

        show: function(){
            showingCount += 1;
            if (this.isShown) return;

            if (!this.background) {
                $(document.body).append('<div class="popup-back light-op""></div><div class="ajax-loader""></div>');
                this.background = $('.popup-back.light-op');
                this.loader = $('.ajax-loader');
                this.background.css('z-index', 1110);
                this.loader.css('z-index', 1111);
            }
            this.background.show();
            this.background.css('width', $(document).width());
            this.background.css('height', $(document).height());
            this.loader.css('left', ($(window).width()-48)/2);
            this.loader.css('top', ($(window).height()-48)/2);
            this.loader.show();
            this.isShown = true;
        },

        hide: function() {
            showingCount -= 1;
            if (showingCount == 0) {
                this.background.hide();
                this.loader.hide();
                this.isShown = false;
            }
        }
    }
})();

/*Cookies*/
(function(){

    window.cookies = {

        get: function(cname) {
            if (!document.cookie || document.cookie === '') return null;

            var name = cname + '=';
            var ca = document.cookie.split(';');
            for (var i = 0; i < ca.length; i++) {
                var c = $.trim(ca[i]);
                if (c.indexOf(name) == 0) return c.substring(name.length, c.length);
            }
            return null;
        },

        set: function(cname, cvalue, exdays) {
            var d = new Date();
            d.setTime(d.getTime() + (exdays*24*60*60*1000));
            var expires = "expires=" + d.toGMTString();
            document.cookie = cname + '=' + cvalue + '; ' + expires;
        },

        remove: function(cname) {
            document.cookie = cname + '=; expires=Thu, 01 Jan 1970 00:00:00 GMT';
        },

        removeAll: function() {
            if (!document.cookie || document.cookie === '') return;

            var ca = document.cookie.split(';');
            for (var i = 0; i < ca.length; i++) {
                var c = ca[i].trim();
                var pair = c.split('=');
                this.remove(pair[0].trim());
            }
        }
    }
})();

(function(){
    var storage = {all:[{id: 'test'}]};
    window.dataStore = {

        get: function(id) {
            for (var type in storage) if (storage.hasOwnProperty(type)) {
                var objs = storage[type];
                if (!objs) return;
                for (var i in objs) {
                    var obj = objs[i];
                    if (obj.id === id) return obj;
                }
            }
            return null;
        },

        getAll: function(type) {
            return storage[type];
        },

        set: function(object, type) {
            if (!type) {
                storage.all[storage.all.length] = object;
            } else {
                if (!storage[type]) storage[type] = [];
                if (this.get(object.id)) this.remove(object.id);
                storage[type][storage[type].length] = object;
            }
        },

        setAll: function(objects, type) {
            for (var i = 0, size = objects.length; i < size; i++) {
                this.set(objects[i], type);
            }
        },

        remove: function(id) {
            for (var type in storage) if (storage.hasOwnProperty(type)) {
                var objs = storage[type];
                if (!objs) return;
                for (var i in objs) {
                    var obj = objs[i];
                    if (obj.id === id) {
                        objs.splice(i, 1);
                    }
                }
            }
        },

        removeAll: function(type) {
            delete storage[type];
        }
    }
})();

(function(){
    window.StringUtils = {
        isEmpty : function(str) { return !str || str === '' }
    };

    window.CurrencyUtils = {
        round: function(value, places) {
            var factor = Math.pow(10, places);
            value = value * factor;
            var tmp = Math.round(value);
            return tmp / factor;
        }
    };
})();

/*HTML Templates*/
(function(){
    window.uc.registerTemplate =
        '<div class="forms">' +
        '<label><input type="text" id="reg-username" name="username" placeholder="'+clientBundle.name+'"></label>' +
        '<label><input type="text" id="reg-surname" name="surname" placeholder="'+clientBundle.surname+'"></label>' +
        '<label><input type="email" id="reg-email" name="email" placeholder="Email"></label>' +
        '<label><input type="tel" id="reg-phone" name="phone" placeholder="'+clientBundle.phone+'"></label>' +
        '<label><input type="password" id="reg-pass" name="password" placeholder="'+clientBundle.password+'"></label>' +
        '<div id="captcha-form"></div>' +
        '<div class="clear-both"><button id="btn-register" class="btn btn-right btn-green">'+clientBundle.register+'</button></div>' + //TODO localize
        '</div>';

    window.uc.loginTemplate =
        '<div class="forms">' +
        '<label><input type="email" name="email" placeholder="Email"></label>' +
        '<label><input type="password" name="password" placeholder="'+clientBundle.password+'"></label>' +
        '<div class="forgot-pass"><a id="forgot-pass-link" href="#">'+clientBundle.forgot_your_password+'</a></div>'+
        '<div class="clear-both">' +
        '<button id="btn-login" class="btn btn-left btn-green">'+clientBundle.login1+'</button>' +
        '<div class="or-register"><span class="or-key">'+clientBundle.or+'</span><a id="or-register-link" href="#">'+clientBundle.register+'</a></div>' +
        '</div>' +
        '</div>';

    window.uc.renewPassTemplate =
        '<div class="forms">' +
        '<label><input type="email" id="form-email" name="email" placeholder="Email"></label>' +
        '<div class="clear-both">' +
        '<button id="btn-renew-pass" class="btn btn-left btn-green">'+clientBundle.renew+'</button>' +
        '</div>' +
        '</div>';

    window.uc.currencyWidget = function(eurUah){
        return '<div id="currency"><table><tbody><tr><td>'+clientBundle.exchange_rate+':</td><td>1€ -</td><td id="EUR-UAH">'+eurUah+'₴</td></tr><tr></tr></tbody></table></div>';
    }
})();
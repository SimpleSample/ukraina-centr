(function(){
    window.uc = {};

    $(document).on('ready', function() {
        var user = cookies.get('name');
        if (user) {
           showUpperRightMenu(true);
            $('#cabinet').text(user);
        } else {
            showUpperRightMenu(false);
        }
        if (window.cookies.get("pass")) {
            var data = {email: window.cookies.get("email"),
                        password: window.cookies.get("pass"),
                        auto: true};
            new Request("login", data).send(function(data){
                showCurrencyWidget(data['EURUAH']);
            });
        } else {
            var req = new Request('welcome');
            req.setShowLoading(false);
            req.send(function(data) {
                showCurrencyWidget(data['EURUAH']);
            })
        }
        // setting up content height
        var $content = $('#content');
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
        if (rate) {
            window.EURUAH = (rate.toFixed(2));
            $('#currency-container').append(uc.currencyWidget(EURUAH));
        } else {
            $('#currency-container').append(16.09);
        }
    }

    function showUpperRightMenu(isLoggedIn) {
        function showAndHide(show1, bind1, show2, bind2, hide1, unbind1, hide2, unbind2){
            show1.show();
            show2.show();
            if (hide1.css('display') !== 'none') {
                hide1.hide();
                hide1.off('click', unbind1);
                hide2.hide();
                hide2.off('click', unbind2);
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
        window.location.assign(location.origin + '/cabinet.html');
    }

    window.isAuthorized = function() {
        return window.cookies.get("email");
    };

    window.register = function(callback) {
        var  content = window.uc.registerTemplate;
        var popup = new Popup('Реєстрація', content, 'black', function(popupId) { //TODO localize
			Recaptcha.create("6LfUKfQSAAAAAN_RtEDkPQnIn9sT5YS0Kp9yK__1",
				"captcha-form",
				{
					theme: 'white',
					callback: Recaptcha.focus_response_field
				}
			);

            $(popupId + ' .btn-register').on('click', function(event){
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

                if (name === '') errors += 'Поле iменi не повинно бути пустим<br>';
                if (surname === '') errors += 'Поле прiзвища не повинно бути пустим<br>';
                var isValidEmail = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/.test(email);
                if (!isValidEmail) errors += 'Не вiрний формат Email<br>';
                var isPhoneValid = /^[\s()+-]*([0-9][\s()+-]*){6,20}$/.test(phone);
                if (!isPhoneValid) errors += 'Не вiрний формат телефона<br>';
                var isPassValid = /^[0-9a-zA-Z]{8,}$/.test(pass);
                if (!isPassValid) errors += 'Пароль повинен мати принаймi 8 символiв<br>';

                if (errors !== '') {
                    new Message(errors, 10000);
                    return false;
                }
				var captcha = {
					challenge: $('[name=recaptcha_challenge_field]').val(),
					response: $('[name=recaptcha_response_field]').val()
				};

				data['captcha'] = JSON.stringify(captcha);

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
    }

    window.login = function(callback) {
        var  content = window.uc.loginTemplate;
        var popup = new Popup('Вхід', content, 'black', function(popupId) { //TODO localize
            $(popupId + ' .btn-login').on('click', function(event){
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
        });
        popup.show();
    }

    function onAuthorized(data) {
        console.log(data);
		document.cookie = "user=" + data['name'];
		document.cookie = "name=" + data['name'] + ' ' + data['surname'];
		document.cookie = "pass=" + data['pass'];
		document.cookie = "email=" + data['email'];
		document.cookie = "phone=" + data['phone'];
        showUpperRightMenu(true);
    }

    function logout() {
        var req = new Request('logout');
        req.send(function(data){
            cookies.removeAll();
            showUpperRightMenu(false);
        });
    }

    window.getDateField = function (inputEl, selectFunc) {
        var now = new Date();
        var maxDate = new Date(now.getTime() + 3*30*24*60*60*1000);
        return new Pikaday({
            field: inputEl,
            firstDay:1,
            defaultDate: now,
            minDate: now,
            maxDate: maxDate,
            setDefaultDate: true,
            i18n: {
                previousMonth : 'попередній місяць',
                nextMonth     : 'наступний місяць',
                months        : ['січень','лютий','березень','квітень','травень','червень','липень','серпень','вересень','жовтень','листопад','грудень'],
                weekdays      : ['неділя','понеділок','вівторок','середа','четвер','п\'ятниця','субота'],
                weekdaysShort : ['нд','пн','вт','ср','чт','пт','сб']
            },
            onSelect: selectFunc
        });
    }
})();

/*Popup*/
(function(){
    var wrapperZindex = 1000;
    var popupZindex = 1001;

    function getPopupLeft() {
        return ($(window).width() - 400)/2;
    }

    function getPopupTop(popup) {
        var top = ($(window).height() - popup.height())/2;

        return top > 0? top : 50;
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
            wrapperZindex = wrapperZindex + 2;
            popupZindex = popupZindex + 2;
            this.$popupWrapper = $('<div id="popup-'+this.id+'"></div>');
            this.$popupWrapper.append($('<div class="popup-back dark-op" style="width:'+$(document).width()+'px; height:'+$(document).height()+'px; z-index:'+wrapperZindex+'"></div>'));
            this.$popup = $('<div class="popup-self ' + this.styles + ' noise" style="left: '+getPopupLeft()+'px; z-index:'+popupZindex+'"></div>');
            this.$popupWrapper.append(this.$popup);
            this.$popup.append($('<div class="popup-header noise"><span class="popup-title">'+this.title+'</span><a href="#" class="popup-close"></a></div>'));
            this.$popupContent = $('<div class="popup-content"><div class="message-container"></div></div>');
            this.$popupContent.append($(this.innerHTML));
            this.$popup.append(this.$popupContent);
            $(document.body).append(this.$popupWrapper);
            var that = this;
            $('#popup-'+this.id + ' .popup-close').on('click', function(event) {event.preventDefault(); that.destroy(); return false;});

            if (!this.wasShown && this.onCreated) this.onCreated('#popup-'+this.id);
            this.$popup.css('top', getPopupTop(this.$popup));
            this.isShown = true;
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

    window.Request.prototype.send = function(callback) {
        if (this.showLoading) loader.show();
        var that = this;
        $.ajax(this.settings)
            .done(function(data){
                if (data.errorMessage) {
                    var popup = $('.popup-self');
                    if (popup[0] && popup.css('display') !== 'none') {
                        new Message(data.errorMessage, 5000);
                    } else {
                        new Popup('Помилка', '<div>' + data.errorMessage + '</div>', 'white', function(){}).show(); //TODO localize
                    }
                } else {
                    callback(data.data);
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
                var c = ca[i].trim();
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
    }

    window.CurrencyUtils = {
        round: function(value, places) {
            var factor = Math.pow(10, places);
            value = value * factor;
            var tmp = Math.round(value);
            return tmp / factor;
        }
    }
})();

(function(){
    function disableNext($pagerEl) {
        $pagerEl.find('#next-page').addClass('disabled');
    }

    function enableNext($pagerEl) {
        $pagerEl.find('#next-page').removeClass('disabled');
    }

    function disablePrev($pagerEl) {
        $pagerEl.find('#prev-page').addClass('disabled');
    }

    function enablePrev($pagerEl) {
        $pagerEl.find('#prev-page').removeClass('disabled');
    }

    function setPage($pagerEl, pageNum) {
        $pagerEl.find('.page-count').text(pageNum);
    }

    window.Pager = function($pagerEl, action, processData, loadSize) {
        this.objectIds = [];
        this.$pagerEl = $pagerEl;
        this.action = action;
        this.loadSize = loadSize || 10;
        this.requestParams = {};
        this.processData = processData || function(){};
        this.allPossibleCount = -1;
        this.currentPage = -1;
        this.cursor = null;
        this.data = {};

        var that = this;
        $pagerEl.find('#prev-page').click(function(e){
            e.preventDefault();
            if ($(this).hasClass('disabled')) return false;

            var from = (that.currentPage-2)*that.loadSize;
            that.load(from, that.loadSize);
            return false;
        });

        $pagerEl.find('#next-page').click(function(e){
            e.preventDefault();
            if ($(this).hasClass('disabled')) return false;

            var from = (that.currentPage)*that.loadSize;
            that.load(from, that.loadSize);
            return false;
        });

    }

    Pager.prototype.setRequestParams = function(requestParams) {
        this.requestParams = requestParams;
    }

    Pager.prototype.init = function() {
        this.load(0, this.loadSize);
    }

    Pager.prototype.load = function(from, count) {
        this.from = from;
        if (from+count <= this.objectIds.length || this.allPossibleCount != -1) {
            var objects = [];
            for (var i = from; i < from+count; i++) {
                var objId = this.objectIds[i];
                if (!objId) break;

                objects[objects.length] = dataStore.get(this.objectIds[i]);
            }
            this.onAfterResponse(objects);
        } else {
            this.requestParams.from = from;
            this.requestParams.count = count;
            if (this.cursor) this.requestParams.cursor = this.cursor;
            var that = this;
            new Request(this.action, this.requestParams).send(function(data){
                that.data = data;
                var objects = data['objects'];
                dataStore.setAll(objects);
                for (var i = 0, size = objects.length; i < size; i++) {
                    that.objectIds[that.objectIds.length] = objects[i].id;
                }
                that.cursor = data['cursor'];
                delete data['cursor'];
                that.onAfterResponse(objects);
            });
        }
    }

    Pager.prototype.onAfterResponse = function(objects) {
        enableNext(this.$pagerEl);
        enablePrev(this.$pagerEl);
        var allCount = this.objectIds.length;

        if (objects.length < this.loadSize) {
            disableNext(this.$pagerEl);
            this.allPossibleCount = allCount;
        }
        if (this.from == 0) {
            disablePrev(this.$pagerEl);
        }

        this.currentPage = (this.from - this.from%this.loadSize)/this.loadSize;
        if (this.from%this.loadSize != 0) this.currentPage += 1;
        setPage(this.$pagerEl, this.currentPage);

        this.data['objects'] = objects;
        this.processData(this.data);
    }
})();

/*HTML Templates*/
(function(){
    window.uc.registerTemplate =
        '<div class="forms">' +
            '<label><input type="text" id="reg-username" name="username" placeholder="Ім\'я" class="width-100"></label>' +
            '<label><input type="text" id="reg-surname" name="surname" placeholder="Прізвище" class="width-100"></label>' +
            '<label><input type="email" id="reg-email" name="email" placeholder="Email" class="width-100"></label>' +
            '<label><input type="tel" id="reg-phone" name="phone" placeholder="телефон" class="width-100"></label>' +
            '<label><input type="password" id="reg-pass" name="password" placeholder="Пароль" class="width-100"></label>' +
				'<div id="captcha-form"></div>' +
            '<p><button class="btn btn-register btn-green width-100">Зареєструватись</button></p>' + //TODO localize
        '</div>';

    window.uc.loginTemplate =
        '<div class="forms">' +
            '<label><input type="email" name="email" placeholder="Email" class="width-100"></label>' +
            '<label><input type="password" name="password" placeholder="Пароль" class="width-100"></label>' +
            '<p><button class="btn btn-login btn-green width-100">Увійти</button></p>' + //TODO localize
        '</div>';

    window.uc.currencyWidget = function(eurUah){
        return '<div id="currency"><table><tbody><tr><td>Курс:</td><td>1€ -</td><td id="EUR-UAH">'+eurUah+'₴</td></tr><tr></tr></tbody></table></div>';
    }
})();
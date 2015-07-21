(function() {
    function createPagerElement(styleClasses, pageIdx) {
        return '<td><div class="pager-element '+styleClasses+'"><a href="#">'+pageIdx+'</a></div></td>';
    }

    function createPagerLayout($pagerEl) {
        if (!$pagerEl.hasClass('tablesaw-pager')) $pagerEl.addClass('tablesaw-pager');
        $pagerEl.append($('<div class="tablesaw-pager-container">'+
            '<table>'+
            '<tbody>' +
            '<tr>'+
            createPagerElement('pager-prev disabled', 'Prev') +
            createPagerElement('pager-page active', '1') +
            createPagerElement('pager-next disabled', 'Next') +
            '</tr>'+
            '</tbody>' +
            '</table>'+
            '</div>'));
    }

    function createPagerElements($pagerEl, pageCount) {
        var $elemContainer = $pagerEl.find('tr');
        for (var i = 1; i < pageCount; i++) {
            $(createPagerElement('pager-page', i + 1)).insertAfter($($elemContainer.find('.pager-element')[i]).parent());
        }
    }

    function clearPagerElements($pagerEl, pageCount) {
        var $elemContainer = $pagerEl.find('tr');
        for (var i = 1; i < pageCount; i++) {
            $($elemContainer.find('.pager-element')[i+1]).remove();
        }
    }

    function disableNext($pagerEl) {
        $pagerEl.find('.pager-next').addClass('disabled');
    }

    function enableNext($pagerEl) {
        $pagerEl.find('.pager-next').removeClass('disabled');
    }

    function disablePrev($pagerEl) {
        $pagerEl.find('.pager-prev').addClass('disabled');
    }

    function enablePrev($pagerEl) {
        $pagerEl.find('.pager-prev').removeClass('disabled');
    }

    function setPage($pagerEl, pageNum) {
        $pagerEl.find('.pager-element.active').removeClass('active');
        $($pagerEl.find('.pager-element')[pageNum]).addClass('active');
    }

    function createPagedObjectsArray(allPossibleCount, loadSize) {
        var resultArray = [];
        var modulo = allPossibleCount%loadSize;
        var pagesCount = (allPossibleCount-modulo)/loadSize;
        if (modulo != 0) pagesCount++;
        for(var i = 0; i < pagesCount; i++) {
            resultArray[i] = [];
        }
        return resultArray;
    }

    window.Pager = function($pagerEl, action, processData, loadSize) {
        this.pagedObjectIds = [];
        this.$pagerEl = $pagerEl;
        this.action = action;
        this.loadSize = loadSize || 10;
        this.requestParams = {};
        this.processData = processData || function(){};
        this.allPossibleCount = 0;
        this.isInitialLoad = true;
        this.currentPage = 1;
        this.cursor = null;
        this.data = {};

        createPagerLayout($pagerEl);

        var that = this;
        $pagerEl.find('.pager-prev').click(function(e){
            e.preventDefault();
            if (that.currentPage == 1) return false;

            that.load(that.currentPage - 1);
            return false;
        });

        $pagerEl.find('.pager-next').click(function(e){
            e.preventDefault();
            if (that.currentPage*that.loadSize > that.allPossibleCount) return false;

            that.load(that.currentPage + 1);
            return false;
        });
    };

    Pager.prototype.setRequestParams = function(requestParams) {
        this.requestParams = requestParams;
    };

    Pager.prototype.init = function() {
        this.load(1);
    };

    Pager.prototype.load = function(pageIdx) {
        this.currentPage = pageIdx;
        this.from = (pageIdx-1) * this.loadSize;
        var objForCurrentPage = this.pagedObjectIds[pageIdx-1];
        if (objForCurrentPage && objForCurrentPage.length > 0) {
            var objects = [];
            for (var i = 0; i < objForCurrentPage.length; i++) {
                var objId = objForCurrentPage[i];
                if (!objId) break;

                objects[objects.length] = dataStore.get(objId);
            }
            this.onAfterResponse(objects);
        } else {
            this.requestParams.from = this.from;
            this.requestParams.count = this.loadSize;
            this.requestParams.isInitialLoad = this.isInitialLoad;
            if (this.cursor) this.requestParams.cursor = this.cursor;
            var that = this;
            new Request(this.action, this.requestParams).send(function(data){
                that.data = data;
                if (data.allPossibleCount) {
                    that.allPossibleCount = data.allPossibleCount;
                    that.pagedObjectIds = createPagedObjectsArray(that.allPossibleCount, that.loadSize);
                    createPagerElements(that.$pagerEl, that.pagedObjectIds.length);
                    that.$pagerEl.find('.pager-page a').click(function(e){
                        e.preventDefault();
                        var $this = $(this);
                        that.load(parseInt($this.text()));
                        return false;
                    });
                    that.isInitialLoad = false;
                }
                objForCurrentPage = that.pagedObjectIds[pageIdx-1];
                var objects = data['objects'];
                dataStore.setAll(objects);
                for (var i = 0, size = objects.length; i < size; i++) {
                    objForCurrentPage[i] = objects[i].id;
                }
                that.cursor = data['cursor'];
                delete data['cursor'];
                that.onAfterResponse(objects);
            });
        }
    };

    Pager.prototype.onAfterResponse = function(objects) {
        enableNext(this.$pagerEl);
        enablePrev(this.$pagerEl);

        if (this.isInitialLoad || this.currentPage * this.loadSize > this.allPossibleCount) {
            disableNext(this.$pagerEl);
        }
        if (this.from == 0) {
            disablePrev(this.$pagerEl);
        }

        setPage(this.$pagerEl, this.currentPage);

        this.data['objects'] = objects;
        this.processData(this.data);
    };

    Pager.prototype.onObjectIdChanged = function(oldId, newId) {
        for (var i = 0, size = this.pagedObjectIds.length; i < size; i++) {
            var idx = $.inArray(oldId, this.pagedObjectIds[i]);
            if (idx != -1) {
                var current = this.pagedObjectIds[i];
                current[idx] = newId;
            }
        }
    };

    Pager.prototype.clear = function() {
        clearPagerElements(this.$pagerEl, this.allPossibleCount.length);
        this.allPossibleCount = 0;
        this.isInitialLoad = true;
        this.pagedObjectIds = [];
        this.requestParams = {};
        this.currentPage = 1;
        this.cursor = null;
        this.data = {};

        this.onAfterResponse([]);
    };
})();
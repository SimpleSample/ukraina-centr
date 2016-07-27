(function(){
    //Tablesaw utils
    window.TablesawUtils = {

        reinitTable : function($table) {
            $table.data( 'table' ).destroy();
            $table.table();
        },

        renderTable: function($tableBody, dataArray, rowRenderer) {
            $tableBody.empty();

            for (var i = 0, size = dataArray.length; i < size; i++) {
                var entity = dataArray[i];
                $tableBody.append(rowRenderer(entity));
            }
            TablesawUtils.reinitTable($tableBody.parent());
        },

        bindCommonHandlers: function($tablesaw, entityType) {
            $tablesaw.on('click', 'a.action-glyph-link', function (ev) {
                ev.preventDefault();
                var $targetTr = $(this).parents('tr');
                var $actionElement = $(this).find('.glyphicon');
                var entityId = $targetTr.attr('id');

                if ($actionElement.hasClass('glyphicon-trash')) {
                    new Request('remove' + entityType, {entityId: entityId}).send(function(event){
                        $targetTr.remove();
                        TablesawUtils.reinitTable($tablesaw);
                    });
                }
                return false;
            });
        }
    };


    $(document).ready(function() {
        var sidebar = $('.sidebar');
        var swipeOptions = { dragLockToAxis: true, dragBlockHorizontal: true };
        var touchControl = new Hammer($('body')[0], swipeOptions);
        touchControl.on("swipeleft swiperight", function(ev) {
            if (ev.type === 'swipeleft') {
                if (!sidebar.hasClass('sidebar-swiped')) {
                    sidebar.addClass('sidebar-swiped');
                }
            } else if (ev.type === 'swiperight') {
                if (sidebar.hasClass('sidebar-swiped')) {
                    sidebar.removeClass('sidebar-swiped');
                }
            }
        });

        sidebar.find('#toggle-sidebar-link').on('click', function(e) {
            e.preventDefault();
            sidebar.toggleClass('sidebar-swiped');
            return false;
        });
    });
})();
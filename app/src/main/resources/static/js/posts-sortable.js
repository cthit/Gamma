htmx.onLoad(function(content) {
    var sortable = content.querySelector('.sortable');

    if (sortable != null) {
        sortableInstance = new Sortable(sortable, {
            animation: 150,
            handle: '.sortable-item',

            onEnd: function (evt) {
                this.option("disabled", true);
            }
        });
    }
});

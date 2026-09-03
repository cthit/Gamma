htmx.onLoad(function (content) {
    const sortable = content.querySelector('.sortable');

    if (sortable != null) {
        new Sortable(sortable, {
            animation: 150,
            handle: '.sortable-item',

            onEnd: function () {
                this.option("disabled", true);
            }
        });
    }
});

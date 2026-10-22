// Replaces the htmx 1 loading-states extension, which has no htmx 4 version.
// While a request has been in flight longer than body[data-loading-delay] ms,
// [data-loading-disable] elements are disabled and [data-loading] elements get
// their data-loading-class added.
(function () {
    var pending = new WeakSet();
    var activeRequests = 0;
    var timer = null;
    var undo = [];

    function startLoading() {
        document.querySelectorAll("[data-loading-disable]").forEach(function (elt) {
            if (!elt.disabled) {
                elt.disabled = true;
                undo.push(function () { elt.disabled = false; });
            }
        });
        document.querySelectorAll("[data-loading]").forEach(function (elt) {
            var cls = elt.dataset.loadingClass;
            if (cls) {
                elt.classList.add(cls);
                undo.push(function () { elt.classList.remove(cls); });
            }
        });
    }

    function stopLoading() {
        clearTimeout(timer);
        timer = null;
        undo.forEach(function (fn) { fn(); });
        undo = [];
    }

    document.addEventListener("htmx:before:request", function (evt) {
        pending.add(evt.detail.ctx);
        if (activeRequests++ === 0) {
            var delay = parseInt(document.body.dataset.loadingDelay || "0", 10);
            timer = setTimeout(startLoading, delay);
        }
    });

    document.addEventListener("htmx:finally:request", function (evt) {
        if (!pending.delete(evt.detail.ctx)) return;
        if (--activeRequests === 0) stopLoading();
    });

    // htmx 4 always runs <script> tags in swapped content; keep the htmx 1
    // allowScriptTags: false behaviour.
    document.addEventListener("htmx:before:swap", function (evt) {
        evt.detail.tasks.forEach(function (task) {
            task.fragment?.querySelectorAll?.("script").forEach(function (script) {
                script.remove();
            });
        });
    });
})();

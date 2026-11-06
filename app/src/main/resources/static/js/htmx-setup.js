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

    // Shows an error alert the same way as the server rendered common/alert :: error.
    function showErrorAlert(text) {
        var alerts = document.getElementById("alerts");
        if (!alerts) return;
        var alert = document.createElement("div");
        alert.setAttribute("role", "alert");
        alert.className = "error";
        alert.style.opacity = "0";
        alert.textContent = text;
        alert.setAttribute("_", "on load transition *opacity to 1 then wait 7s then transition *opacity to 0 then remove me");
        alerts.prepend(alert);
        if (window._hyperscript) {
            _hyperscript.processNode(alert);
        } else {
            alert.style.opacity = "1";
        }
    }

    // File inputs with data-max-size (in bytes) reject files that are too
    // large before they are uploaded. Runs in the capture phase so that the
    // input's own change handler (which enables the upload button) never sees
    // the rejected file.
    document.addEventListener("change", function (evt) {
        var input = evt.target;
        if (!(input instanceof HTMLInputElement) || input.type !== "file") return;
        var maxSize = parseInt(input.dataset.maxSize || "", 10);
        if (isNaN(maxSize)) return;

        var tooLarge = Array.prototype.some.call(input.files || [], function (file) {
            return file.size > maxSize;
        });
        if (!tooLarge) return;

        evt.stopPropagation();
        input.value = "";
        if (input.form && input.form.id) {
            document.querySelectorAll('button[form="' + input.form.id + '"]').forEach(function (button) {
                button.disabled = true;
            });
        }
        showErrorAlert(input.dataset.maxSizeMessage || "The file is too large.");
    }, true);

    // Gamma answers a too large upload with an alert (HX-Retarget: #alerts).
    // A 413 without that header comes from something in front of Gamma, like a
    // reverse proxy, and its error page must not be swapped into the page.
    document.addEventListener("htmx:after:request", function (evt) {
        var ctx = evt.detail.ctx;
        if (ctx.response && ctx.response.status === 413 && !(ctx.hx && ctx.hx.retarget)) {
            evt.preventDefault();
            showErrorAlert("The file is too large.");
        }
    });

    // A server may close the connection instead of reading a large upload,
    // which makes the request fail without a response.
    document.addEventListener("htmx:error", function (evt) {
        var ctx = evt.detail.ctx;
        if (ctx && !ctx.response && ctx.request && ctx.request.body instanceof FormData) {
            showErrorAlert("The upload failed. Is the file too large?");
        }
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

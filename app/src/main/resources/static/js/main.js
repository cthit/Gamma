document.addEventListener("DOMContentLoaded", function () {
    htmx.config.selfRequestsOnly = true;
    htmx.config.allowScriptTags = false;
    htmx.config.historyCacheSize = 0;
    htmx.config.allowEval = false;
    htmx.config.useTemplateFragments = true;
    htmx.config.includeIndicatorStyles = false;

    document.getElementById('toggle-nav').addEventListener('click', toggleNavFunction);
    document.addEventListener("htmx:afterSwap", () => {
        document.getElementById('toggle-nav').addEventListener('click', toggleNavFunction);
        document.body.classList.remove("show-nav");
    });
})

function toggleNavFunction() {
    document.body.classList.toggle('show-nav');
}
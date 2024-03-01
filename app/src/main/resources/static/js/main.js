document.addEventListener("DOMContentLoaded", function () {
    htmx.config.selfRequestsOnly = true;
    htmx.config.allowScriptTags = false;
    htmx.config.historyCacheSize = 0;
    htmx.config.allowEval = false;
    htmx.config.useTemplateFragments = true;
})
_hyperscript.addCommand('reindex', function(parser, runtime, tokens) {
    if(!tokens.matchToken('reindex')) return

    const expr = parser.requireElement('queryRef', tokens);

    return {
        args: [expr],
        op(context, value) {
            console.log(value);

            return runtime.findNext(this)
        }
    }
})

document.addEventListener("DOMContentLoaded", function () {
    htmx.config.selfRequestsOnly = true;
    //TODO: Enable CSP
    htmx.config.allowScriptTags = true;
    htmx.config.historyCacheSize = 0;
    htmx.config.allowEval = false;
    htmx.config.useTemplateFragments = true;
})
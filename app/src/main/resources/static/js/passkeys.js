(function () {
    function base64urlEncode(buffer) {
        var bytes = new Uint8Array(buffer);
        var binary = "";
        for (var i = 0; i < bytes.length; i++) {
            binary += String.fromCharCode(bytes[i]);
        }
        return window.btoa(binary).replace(/=/g, "").replace(/\+/g, "-").replace(/\//g, "_");
    }

    function base64urlDecode(value) {
        var base64 = value.replace(/-/g, "+").replace(/_/g, "/");
        var binary = window.atob(base64);
        var bytes = new Uint8Array(binary.length);
        for (var i = 0; i < binary.length; i++) {
            bytes[i] = binary.charCodeAt(i);
        }
        return bytes.buffer;
    }

    function csrfHeaders(button) {
        var headers = { "Content-Type": "application/json" };
        if (button.dataset.csrfHeader && button.dataset.csrfToken) {
            headers[button.dataset.csrfHeader] = button.dataset.csrfToken;
        }
        return headers;
    }

    async function post(button, url, body) {
        var response = await fetch(url, {
            method: "POST",
            headers: csrfHeaders(button),
            body: body === undefined ? undefined : JSON.stringify(body),
            credentials: "same-origin"
        });
        if (!response.ok) {
            throw new Error("The server answered " + response.status);
        }
        return response.json();
    }

    async function signIn(button) {
        var options = await post(button, "/webauthn/authenticate/options");
        var credential = await navigator.credentials.get({
            publicKey: Object.assign({}, options, {
                challenge: base64urlDecode(options.challenge),
                allowCredentials: (options.allowCredentials || []).map(function (allowed) {
                    return Object.assign({}, allowed, { id: base64urlDecode(allowed.id) });
                })
            })
        });

        var response = credential.response;
        var result = await post(button, "/login/webauthn", {
            id: credential.id,
            rawId: base64urlEncode(credential.rawId),
            response: {
                authenticatorData: base64urlEncode(response.authenticatorData),
                clientDataJSON: base64urlEncode(response.clientDataJSON),
                signature: base64urlEncode(response.signature),
                userHandle: response.userHandle ? base64urlEncode(response.userHandle) : undefined
            },
            credType: credential.type,
            clientExtensionResults: credential.getClientExtensionResults(),
            authenticatorAttachment: credential.authenticatorAttachment
        });

        if (!result || !result.authenticated) {
            throw new Error("The passkey was not accepted");
        }
        return result.redirectUrl;
    }

    async function register(button, label) {
        var options = await post(button, "/webauthn/register/options");
        var credential = await navigator.credentials.create({
            publicKey: Object.assign({}, options, {
                challenge: base64urlDecode(options.challenge),
                user: Object.assign({}, options.user, { id: base64urlDecode(options.user.id) }),
                excludeCredentials: (options.excludeCredentials || []).map(function (excluded) {
                    return Object.assign({}, excluded, { id: base64urlDecode(excluded.id) });
                })
            })
        });

        var response = credential.response;
        var result = await post(button, "/webauthn/register", {
            publicKey: {
                credential: {
                    id: credential.id,
                    rawId: base64urlEncode(credential.rawId),
                    response: {
                        attestationObject: base64urlEncode(response.attestationObject),
                        clientDataJSON: base64urlEncode(response.clientDataJSON),
                        transports: response.getTransports ? response.getTransports() : []
                    },
                    type: credential.type,
                    clientExtensionResults: credential.getClientExtensionResults(),
                    authenticatorAttachment: credential.authenticatorAttachment
                },
                label: label
            }
        });

        if (!result || !result.success) {
            throw new Error("The passkey could not be added");
        }
    }

    function showError(button, message) {
        var id = button.dataset.passkeyError;
        var element = id ? document.getElementById(id) : null;
        if (element) {
            element.textContent = message;
            element.hidden = false;
        }
    }

    function clearError(button) {
        var id = button.dataset.passkeyError;
        var element = id ? document.getElementById(id) : null;
        if (element) {
            element.textContent = "";
            element.hidden = true;
        }
    }

    function describe(err) {
        if (err && err.name === "NotAllowedError") {
            return "The passkey prompt was closed or timed out. Try again.";
        }
        if (err && err.name === "InvalidStateError") {
            return "That passkey has already been added.";
        }
        return "That didn't work. Try again.";
    }

    async function run(button, action) {
        if (button.disabled) return;
        clearError(button);
        button.disabled = true;
        try {
            await action();
        } catch (err) {
            console.error(err);
            showError(button, describe(err));
            button.disabled = false;
        }
    }

    document.addEventListener("click", function (evt) {
        var signInButton = evt.target.closest("[data-passkey-sign-in]");
        if (signInButton) {
            evt.preventDefault();
            run(signInButton, async function () {
                var redirectUrl = await signIn(signInButton);
                window.location.href = signInButton.dataset.redirect || redirectUrl || "/";
            });
            return;
        }

        var registerButton = evt.target.closest("[data-passkey-register]");
        if (registerButton) {
            evt.preventDefault();
            if (registerButton.dataset.passkeyConfirm && !window.confirm(registerButton.dataset.passkeyConfirm)) {
                return;
            }
            run(registerButton, async function () {
                var input = document.getElementById(registerButton.dataset.labelInput);
                var label = input && input.value.trim() ? input.value.trim() : "Passkey";
                await register(registerButton, label);
                if (registerButton.hasAttribute("data-then-sign-in")) {
                    var redirectUrl = await signIn(registerButton);
                    window.location.href = registerButton.dataset.success || redirectUrl || "/";
                } else {
                    window.location.href = registerButton.dataset.success || window.location.href;
                }
            });
        }
    });

    function markUnsupported() {
        if (window.PublicKeyCredential) return;
        document.querySelectorAll("[data-passkey-sign-in], [data-passkey-register]").forEach(function (button) {
            button.disabled = true;
            showError(button, "This browser doesn't support passkeys.");
        });
    }
    document.addEventListener("DOMContentLoaded", markUnsupported);
    document.addEventListener("htmx:after:swap", markUnsupported);
})();

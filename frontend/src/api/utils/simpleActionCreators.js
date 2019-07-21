export function successfully(type, response) {
    return {
        type,
        error: false,
        payload: {
            data: response.data
        }
    };
}

export function failed(type, error) {
    return {
        type,
        error: true,
        payload: {
            error
        }
    };
}

export function loading(type) {
    return {
        type,
        error: false,
        payload: {}
    };
}

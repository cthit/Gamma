class DeltaError {
    constructor(code, message) {
        this.code = code;
        this.message = message;
    }

    equals(error) {
        return (
            this.code == this._statusCode(error) &&
            this.message == this._statusMessage(error)
        );
    }

    _statusCode(error) {
        return error.response == null ? -1 : error.response.status;
    }

    _statusMessage(error) {
        return error.response == null ? null : error.response.data.message;
    }
}

export default DeltaError;

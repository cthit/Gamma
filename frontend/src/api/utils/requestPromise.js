export function requestPromise(
    request,
    requestSuccessfullyActionCreator,
    requestFailedActionCreator
) {
    return dispatch => {
        return new Promise((resolve, reject) => {
            request()
                .then(response => {
                    dispatch(requestSuccessfullyActionCreator(response));
                    resolve(response);
                })
                .catch(error => {
                    dispatch(requestFailedActionCreator(error));
                    reject(error);
                });
        });
    };
}

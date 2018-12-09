export function requestPromise(
    request,
    requestLoadingActionCreator,
    requestSuccessfullyActionCreator,
    requestFailedActionCreator
) {
    return dispatch => {
        dispatch(requestLoadingActionCreator());
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

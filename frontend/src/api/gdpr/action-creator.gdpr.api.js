import { failed, loading, successfully } from "../utils/simpleActionCreators";
import {
    GDPR_GET_MINIFIED_FAILED,
    GDPR_GET_MINIFIED_LOADING,
    GDPR_GET_MINIFIED_SUCCESSFULLY,
    GDPR_SET_FAILED,
    GDPR_SET_LOADING,
    GDPR_SET_SUCESSFULLY
} from "./actions.gdpr.api";
import { requestPromise } from "../utils/requestPromise";
import { setGDPRValue } from "./put.gdpr.api";
import { getUsersWithGDPRMinified } from "./get.gdpr.api";

export function createSetGDPRRequest(userId, data) {
    return requestPromise(
        () => setGDPRValue(userId, data),
        setGDPRLoading,
        setGDPRSuccessfully,
        setGDPRFailed
    );
}

export function createGetGDPRMinifiedRequest() {
    return requestPromise(
        getUsersWithGDPRMinified,
        getGDPRMinifiedLoading,
        getGDPRMinifiedSuccessfully,
        getGDPRMinifiedFailed
    );
}

function setGDPRSuccessfully(response) {
    return successfully(GDPR_SET_SUCESSFULLY, response);
}

function setGDPRFailed(error) {
    return failed(GDPR_SET_FAILED, error);
}

function setGDPRLoading() {
    return loading(GDPR_SET_LOADING);
}

function getGDPRMinifiedSuccessfully(response) {
    return successfully(GDPR_GET_MINIFIED_SUCCESSFULLY, response);
}

function getGDPRMinifiedFailed(error) {
    return failed(GDPR_GET_MINIFIED_FAILED, error);
}

function getGDPRMinifiedLoading() {
    return loading(GDPR_GET_MINIFIED_LOADING);
}

import {
    SUPER_GROUP_ADD_FAILED,
    SUPER_GROUP_ADD_LOADING,
    SUPER_GROUP_ADD_SUCCESSFULLY,
    SUPER_GROUP_DELETE_FAILED,
    SUPER_GROUP_DELETE_LOADING,
    SUPER_GROUP_DELETE_SUCCESSFULLY,
    SUPER_GROUP_GET_FAILED,
    SUPER_GROUP_GET_SUCCESSFULLY,
    SUPER_GROUPS_LOAD_FAILED,
    SUPER_GROUPS_LOAD_LOADING,
    SUPER_GROUPS_LOAD_SUCCESSFULLY,
    SUPER_GROUP_GET_LOADING,
    SUPER_GROUP_GET_SUB_GROUPS_SUCCESSFULLY,
    SUPER_GROUP_GET_SUB_GROUPS_FAILED,
    SUPER_GROUP_GET_SUB_GROUPS_LOADING,
    SUPER_GROUP_EDIT_SUCCESSFULLY,
    SUPER_GROUP_EDIT_FAILED,
    SUPER_GROUP_EDIT_LOADING
} from "./actions.super-groups.api";
import { successfully, failed, loading } from "../utils/simpleActionCreators";
import { requestPromise } from "../utils/requestPromise";
import {
    getSuperGroup,
    getSuperGroups,
    getSuperGroupSubGroups
} from "./get.super-groups.api";
import { addSuperGroup } from "./post.super-groups.api";
import { editSuperGroup } from "./put.super-groups.api";
import { deleteSuperGroup } from "./delete.super-groups.api";

export function createLoadSuperGroupsAction() {
    return requestPromise(
        getSuperGroups,
        superGroupsLoadLoading,
        superGroupsLoadSuccessfully,
        superGroupsLoadFailed
    );
}

export function createAddSuperGroupAction(superGroupData) {
    return requestPromise(
        () => addSuperGroup(superGroupData),
        superGroupAddLoading,
        superGroupAddSuccessfully,
        superGroupAddFailed
    );
}

export function createEditSuperGroupAction(superGroupId, superGroupData) {
    return requestPromise(
        () => editSuperGroup(superGroupId, superGroupData),
        superGroupEditLoading,
        superGroupEditSuccessfully,
        superGroupEditFailed
    );
}

export function createDeleteSuperGroupAction(superGroupId) {
    return requestPromise(
        () => deleteSuperGroup(superGroupId),
        superGroupDeleteLoading,
        superGroupDeleteSuccessfully,
        superGroupDeleteFailed
    );
}

export function createGetSuperGroupAction(superGroupId) {
    return requestPromise(
        () => getSuperGroup(superGroupId),
        superGroupGetLoading,
        superGroupGetSuccessfully,
        superGroupGetFailed
    );
}

export function createGetSuperGroupSubGroupsAction(superGroupId) {
    return requestPromise(
        () => getSuperGroupSubGroups(superGroupId),
        superGroupGetSubGroupsLoading,
        superGroupGetSubGroupsSuccessfully,
        superGroupGetSubGroupsFailed
    );
}

function superGroupsLoadSuccessfully(response) {
    return successfully(SUPER_GROUPS_LOAD_SUCCESSFULLY, response);
}

function superGroupsLoadFailed(error) {
    return failed(SUPER_GROUPS_LOAD_FAILED, error);
}

function superGroupsLoadLoading() {
    return loading(SUPER_GROUPS_LOAD_LOADING);
}

function superGroupAddSuccessfully(response) {
    return successfully(SUPER_GROUP_ADD_SUCCESSFULLY, response);
}

function superGroupAddFailed(error) {
    return failed(SUPER_GROUP_ADD_FAILED, error);
}

function superGroupAddLoading() {
    return loading(SUPER_GROUP_ADD_LOADING);
}

function superGroupDeleteSuccessfully(response) {
    return successfully(SUPER_GROUP_DELETE_SUCCESSFULLY, response);
}

function superGroupDeleteFailed(error) {
    return failed(SUPER_GROUP_DELETE_FAILED, error);
}

function superGroupDeleteLoading() {
    return loading(SUPER_GROUP_DELETE_LOADING);
}

function superGroupEditSuccessfully(response) {
    return successfully(SUPER_GROUP_EDIT_SUCCESSFULLY, response);
}

function superGroupEditFailed(error) {
    return failed(SUPER_GROUP_EDIT_FAILED, error);
}

function superGroupEditLoading() {
    return loading(SUPER_GROUP_EDIT_LOADING);
}

function superGroupGetSuccessfully(response) {
    return successfully(SUPER_GROUP_GET_SUCCESSFULLY, response);
}

function superGroupGetFailed(error) {
    return failed(SUPER_GROUP_GET_FAILED, error);
}

function superGroupGetLoading() {
    return loading(SUPER_GROUP_GET_LOADING);
}

function superGroupGetSubGroupsSuccessfully(response) {
    return successfully(SUPER_GROUP_GET_SUB_GROUPS_SUCCESSFULLY, response);
}

function superGroupGetSubGroupsFailed(error) {
    return failed(SUPER_GROUP_GET_SUB_GROUPS_FAILED, error);
}

function superGroupGetSubGroupsLoading() {
    return loading(SUPER_GROUP_GET_SUB_GROUPS_LOADING);
}

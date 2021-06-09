import { deleteRequest } from "../utils/api";

export const deleteAuthorityLevel = id =>
    deleteRequest("/admin/authority/level/" + id);

export const deleteUserAuthority = (authorityLevelName, userId) =>
    deleteRequest(
        "/admin/authority/user?authorityLevelName=" +
            authorityLevelName +
            "&userId=" +
            userId
    );

export const deleteSuperGroupAuthority = (authorityLevelName, superGroupId) =>
    deleteRequest(
        "/admin/authority/supergroup?authorityLevelName=" +
            authorityLevelName +
            "&superGroupId=" +
            superGroupId
    );

export const deletePostAuthority = (authorityLevelName, superGroupId, postId) =>
    deleteRequest(
        "/admin/authority/post?authorityLevelName=" +
            authorityLevelName +
            "&superGroupId=" +
            superGroupId +
            "&postId=" +
            postId
    );

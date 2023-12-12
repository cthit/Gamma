import { deleteRequest } from "../utils/api";
import { ADMIN_POSTS_ENDPOINT } from "../utils/endpoints";

export function deletePost(postId) {
    return deleteRequest(ADMIN_POSTS_ENDPOINT + postId);
}

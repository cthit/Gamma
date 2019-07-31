import { putRequest } from "../utils/api";
import { ADMIN_POSTS_ENDPOINT } from "../utils/endpoints";

/**
 * {
 *     post: {
 *         sv: String,
 *         en: String
 *     }
 * }
 */
export function editPost(postId, newPostId) {
    return putRequest(ADMIN_POSTS_ENDPOINT + postId, newPostId);
}

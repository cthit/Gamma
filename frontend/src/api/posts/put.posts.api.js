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
export function editPost(postId, newPostData) {
    return putRequest(ADMIN_POSTS_ENDPOINT + postId, newPostData);
}

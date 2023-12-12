import { postRequest } from "../utils/api";
import { ADMIN_POSTS_ENDPOINT } from "../utils/endpoints";

/**
 * {
 *     post: {
 *         sv: String,
 *         en: String
 *     }
 * }
 */
export function addPost(postData) {
    return postRequest(ADMIN_POSTS_ENDPOINT, postData);
}

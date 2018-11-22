import { postRequest } from "../utils/api";
import { POSTS_ENDPOINT } from "../utils/endpoints";

/**
 * {
 *     post: {
 *         sv: String,
 *         en: String
 *     }
 * }
 */
export function addPost(postData) {
    return postRequest(POSTS_ENDPOINT, postData);
}

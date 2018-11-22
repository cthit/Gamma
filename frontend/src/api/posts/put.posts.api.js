import { putRequest } from "../utils/api";
import { POSTS_ENDPOINT } from "../utils/endpoints";

/**
 * {
 *     post: {
 *         sv: String,
 *         en: String
 *     }
 * }
 */
export function editPost(postId, newPostId) {
  return putRequest(POSTS_ENDPOINT + postId, newPostId);
}

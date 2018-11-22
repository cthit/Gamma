import { deleteRequest } from "../utils/api";
import { POSTS_ENDPOINT } from "../utils/endpoints";

export function deletePost(postId) {
    return deleteRequest(POSTS_ENDPOINT + postId);
}

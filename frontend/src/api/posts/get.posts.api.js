import { getRequest } from "../utils/api";
import { POSTS_ENDPOINT, ADMIN_POSTS_ENDPOINT } from "../utils/endpoints";

export function getPosts() {
    return getRequest(POSTS_ENDPOINT);
}

export function getPost(postId) {
    return getRequest(POSTS_ENDPOINT + postId);
}

export function getPostUsage(postId) {
    return getRequest(ADMIN_POSTS_ENDPOINT + postId + "/usage", input => ({
        data: { usages: input.data }
    }));
}

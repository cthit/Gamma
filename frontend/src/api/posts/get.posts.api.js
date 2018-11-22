import { getRequest } from "../utils/api";
import { POSTS_ENDPOINT } from "../utils/endpoints";

export function getPosts() {
    return getRequest(POSTS_ENDPOINT);
}

export function getPost(postId) {
    return getRequest(POSTS_ENDPOINT + postId);
}

export function getUsagesOfPost(postId) {
    return getRequest(POSTS_ENDPOINT + postId + "/usage");
}

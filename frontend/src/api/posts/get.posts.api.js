import { getRequest } from "../utils/api";
import { POSTS_ENDPOINT } from "../utils/endpoints";

export function getPosts() {
    console.log("yay");
    return getRequest(POSTS_ENDPOINT);
}

export function getPost(postId) {
    return getRequest(POSTS_ENDPOINT + postId);
}

export function getPostUsage(postId) {
    return getRequest(POSTS_ENDPOINT + postId + "/usage", true, input => ({
        data: { usages: input.data }
    }));
}

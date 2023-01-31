import { deleteRequest } from "../utils/api";

export function deleteApproval(clientUid) {
    return deleteRequest("/users/me/approval/" + clientUid);
}

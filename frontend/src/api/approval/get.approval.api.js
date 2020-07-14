import { getRequest } from "../utils/api";

export const getAllApprovalsbyClientId = clientId =>
    getRequest("/admin/users/approval/" + clientId);

export const getApprovals = () => getRequest("/users/approval");

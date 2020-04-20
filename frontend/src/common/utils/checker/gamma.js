import some from "lodash/some";

const inGroup = (user, group) => {
    if (user == null) {
        return false;
    }
    return some(group.groupMembers, ["id", user.id]);
};

export { inGroup };

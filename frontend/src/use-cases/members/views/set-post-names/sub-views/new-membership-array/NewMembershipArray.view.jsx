import React from "react";
import CreateMembership from "../create-membership";

const NewPostsArray = ({ value, replace, posts, groupId }) => {
    return value.map((value, i) => (
        <CreateMembership
            key={value.firstName + value.nick + value.lastName}
            posts={posts}
            value={value}
            onChange={value => {
                replace(i, value);
            }}
        />
    ));
};

export default NewPostsArray;

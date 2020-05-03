import React from "react";
import CreateMembership from "../create-membership";

const NewPostsArray = ({ value, posts, innerInputs }) => {
    return value.map((value, i) => (
        <CreateMembership
            key={value.firstName + value.nick + value.lastName}
            posts={posts}
            innerInputs={innerInputs[i]}
            value={value}
        />
    ));
};

export default NewPostsArray;

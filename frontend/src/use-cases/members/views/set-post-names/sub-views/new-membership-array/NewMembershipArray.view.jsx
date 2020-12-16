import React from "react";
import CreateMembership from "../create-membership";
import { useDigitFormFieldArray } from "@cthit/react-digit-components";

const NewPostsArray = ({ posts }) => {
    const { innerInputs, value } = useDigitFormFieldArray("members", {
        inputs: ["postId", "unofficialPostName"]
    });

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

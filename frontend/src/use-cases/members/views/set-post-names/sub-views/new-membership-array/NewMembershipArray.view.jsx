import React, { useState } from "react";
import CreateMembership from "../create-membership";

const NewPostsArray = ({ groupId, posts, form, replace }) => {
    return (
        <>
            {form.values.members.map((value, i) => (
                <CreateMembership
                    key={value.firstName + value.nick + value.lastName}
                    posts={posts}
                    value={value}
                    onChange={value => {
                        replace(i, value);

                        //This will not add the last modified thing, but it's ok. :upside_smile:
                        sessionStorage.setItem(
                            groupId + ".postNames",
                            JSON.stringify({
                                ...form.values
                            })
                        );
                    }}
                />
            ))}
        </>
    );
};

export default NewPostsArray;
